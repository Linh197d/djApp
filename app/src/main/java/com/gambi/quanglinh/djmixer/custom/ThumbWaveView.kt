package com.gambi.quanglinh.djmixer.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.listener.OnProgressListener
import com.gambi.quanglinh.djmixer.listener.OnSamplingListener
import com.gambi.quanglinh.djmixer.view.MAIN_THREAD
import com.gambi.quanglinh.djmixer.view.Sampler
import com.gambi.quanglinh.djmixer.view.abs
import com.gambi.quanglinh.djmixer.view.clamp
import com.gambi.quanglinh.djmixer.view.dip
import com.gambi.quanglinh.djmixer.view.filterPaint
import com.gambi.quanglinh.djmixer.view.fits
import com.gambi.quanglinh.djmixer.view.flush
import com.gambi.quanglinh.djmixer.view.inCanvas
import com.gambi.quanglinh.djmixer.view.paste
import com.gambi.quanglinh.djmixer.view.rectFOf
import com.gambi.quanglinh.djmixer.view.safeRecycle
import com.gambi.quanglinh.djmixer.view.smoothPaint
import com.gambi.quanglinh.djmixer.view.transform
import com.gambi.quanglinh.djmixer.view.withAlpha

class ThumbWaveView : View {

  constructor(context: Context?) : super(context) {
    setWillNotDraw(false)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    setWillNotDraw(false)
    inflateAttrs(attrs)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
          defStyleAttr) {
    setWillNotDraw(false)
    inflateAttrs(attrs)
  }

  var onProgressListener: OnProgressListener? = null

  var onProgressChanged: (Float, Boolean) -> Unit = { progress, byUser -> Unit }

  var onStartTracking: (Float) -> Unit = {}

  var onStopTracking: (Float) -> Unit = {}

  var chunkHeight: Int = 0
    get() = if (field == 0) h else Math.abs(field)
    set(value) {
      field = value
      redrawData()
    }

  var chunkWidth: Int = dip(2)
    set(value) {
      field = Math.abs(value)
      redrawData()
    }

  var chunkSpacing: Int = dip(1)
    set(value) {
      field = Math.abs(value)
      redrawData()
    }

  var chunkRadius: Int = 0
    set(value) {
      field = Math.abs(value)
      redrawData()
    }

  var minChunkHeight: Int = dip(2)
    set(value) {
      field = Math.abs(value)
      redrawData()
    }

  var waveColor: Int = Color.BLACK
  var waveFilledColor: Int = Color.YELLOW

    set(value) {
      wavePaint = smoothPaint(ContextCompat.getColor(context, R.color.wave_color))
      waveFilledPaint = filterPaint(value)
      postInvalidate()
    }

  var progress: Float = 0F
    set(value) {
//      require(value in 0..100) { "Progress must be in 0..100" }
      require(value in 0.0..100.0) { "Progress must be in 0..100" }

      field = Math.abs(value)

      onProgressListener?.onProgressChanged(field, isTouched)
      onProgressChanged(field, isTouched)

      postInvalidate()
    }

  var scaledData: ByteArray = byteArrayOf()
    set(value) {
      field = if (value.size <= chunksCount) {
        ByteArray(chunksCount).paste(value)
      } else {
        value
      }

      redrawData()
    }

  var expansionDuration: Long = 400
    set(value) {
      field = Math.max(400, value)
      expansionAnimator.duration = field
    }

  var isExpansionAnimated: Boolean = true

  var isTouched = false

  private val chunksCount: Int
    get() = w / chunkStep

  private val chunkStep: Int
    get() = chunkWidth + chunkSpacing

  private val centerY: Int
    get() = h / 2

  private val progressFactor: Float
    get() = progress / 100F

  private val initialDelay: Long = 100

  private val expansionAnimator = ValueAnimator.ofFloat(0.0F, 1.0F).apply {
    duration = expansionDuration
    interpolator = OvershootInterpolator()
    addUpdateListener {
      redrawData(factor = it.animatedFraction)
    }
  }

  private var wavePaint = smoothPaint(waveColor)
  private var waveFilledPaint = filterPaint(waveFilledColor)
  private var waveBitmap: Bitmap? = null

  private var w: Int = 0
  private var h: Int = 0
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val cv = canvas ?: return

    cv.transform {
      clipRect(0, 0, w, h)
      drawBitmap(waveBitmap!!, 0F, 0F, wavePaint)
    }

    cv.transform {
      clipRect(0F, 0F, w * progressFactor, h.toFloat())
      drawBitmap(waveBitmap!!, 0F, 0F, waveFilledPaint)
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    w = right - left
    h = bottom - top

    if (waveBitmap.fits(w, h)) {
      return
    }

    if (changed) {
      waveBitmap.safeRecycle()
      waveBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

      // absolutely ridiculous hack to draw wave in RecyclerView items
      scaledData = when (scaledData.size) {
        0 -> byteArrayOf()
        else -> scaledData
      }
    }
  }


  // Java convenience
  fun setRawData(raw: ByteArray, callback: OnSamplingListener) {
    setRawData(raw) { callback.onComplete() }
  }

  @JvmOverloads
  fun setRawData(raw: ByteArray, callback: () -> Unit = {}) {
    MAIN_THREAD.postDelayed({
      Sampler.downSampleAsync(raw, chunksCount) {
        scaledData = it
        callback()

        if (isExpansionAnimated) {
          animateExpansion()
        }
      }
    }, initialDelay)
  }

  private fun MotionEvent.toProgress() = this@toProgress.x.clamp(0F, w.toFloat()) / w * 100F

  private fun redrawData(canvas: Canvas? = waveBitmap?.inCanvas(), factor: Float = 1.0F) {
    if (waveBitmap == null || canvas == null) return

    waveBitmap.flush()

    scaledData.forEachIndexed { i, chunk ->
      val chunkHeight = ((chunk.abs.toFloat() / Byte.MAX_VALUE) * chunkHeight).toInt()
      val clampedHeight = Math.max(chunkHeight, minChunkHeight)
      val heightDiff = (clampedHeight - minChunkHeight).toFloat()
      val animatedDiff = (heightDiff * factor).toInt()

      // top Rect
      canvas.drawRoundRect(
              rectFOf(
                      left = chunkSpacing / 2 + i * chunkStep,
                      top = centerY - minChunkHeight - animatedDiff,
                      right = chunkSpacing / 2 + i * chunkStep + chunkWidth,
                      bottom = centerY
              ),
              chunkRadius.toFloat(),
              chunkRadius.toFloat(),
              wavePaint
      )


      // Bottom Rect
      canvas.drawRoundRect(
              rectFOf(
                      left = chunkSpacing / 2 + i * chunkStep,
                      top = centerY ,
                      right = chunkSpacing / 2 + i * chunkStep + chunkWidth,
                      bottom = centerY + minChunkHeight + animatedDiff
              ),
              chunkRadius.toFloat(),
              chunkRadius.toFloat(),
              smoothPaint(wavePaint.color.withAlpha(90))
      )
    }

    postInvalidate()
  }

  private fun animateExpansion() {
    expansionAnimator.start()
  }

  private fun inflateAttrs(attrs: AttributeSet?) {
    val resAttrs = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AudioWaveView,
            0,
            0
    ) ?: return

    with(resAttrs) {
      chunkHeight = getDimensionPixelSize(R.styleable.AudioWaveView_chunkHeight, chunkHeight)
      chunkWidth = getDimensionPixelSize(R.styleable.AudioWaveView_chunkWidth, chunkWidth)
      chunkSpacing = getDimensionPixelSize(R.styleable.AudioWaveView_chunkSpacing,
              chunkSpacing)
      minChunkHeight = getDimensionPixelSize(R.styleable.AudioWaveView_minChunkHeight,
              minChunkHeight)
      chunkRadius = getDimensionPixelSize(R.styleable.AudioWaveView_chunkRadius, chunkRadius)
      waveColor = getColor(R.styleable.AudioWaveView_waveColor, waveColor)
      waveFilledColor  = getColor(R.styleable.AudioWaveView_waveFilledColor, waveFilledColor)
      progress = getFloat(R.styleable.AudioWaveView_progress, progress)
      isExpansionAnimated = getBoolean(R.styleable.AudioWaveView_animateExpansion,
              isExpansionAnimated)
      recycle()
    }
  }


}