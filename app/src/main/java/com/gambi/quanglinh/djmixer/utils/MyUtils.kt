package com.gambi.quanglinh.djmixer.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.gambi.quanglinh.djmixer.ui.activity.ListFileAudioActivity
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.custom.ThumbWaveView
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.util.ArrayList
import java.util.Locale

class MyUtils {
    companion object {
        fun bitmapResize(bmp: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
            val width = bmp.width
            val height = bmp.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            // CREATE A MATRIX FOR THE MANIPULATION
            val matrix = Matrix()
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight)

            // "RECREATE" THE NEW BITMAP
            val newBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false)
            val bitmap = Bitmap.createBitmap(
                newWidth,
                newHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.TRANSPARENT)
            canvas.drawBitmap(newBitmap, 0f, 0f, null)
            return bitmap
        }

        fun convertDuration(dr: Int): String {
            val seconds = dr / 1000 % 60
            val minutes = dr / (1000 * 60) % 60
            val hours = dr / (1000 * 60 * 60) % 24
            if (hours > 0)
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            else
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
        fun deleteFile(filepath: String) {
            val file = File(filepath)
            Log.d("linhd", filepath)
            try {
                file.delete()
                if (file.exists()) {
                    Log.d("linhd", "file exists 1")
                    file.canonicalFile.delete()
                    if (file.exists()) {
                        Log.d("linhd", "file exists 2")
                        deleteFile(file.name)
                        if (file.exists()) {
                            Log.d("linhd", "file exists 3")
                            file.absoluteFile.delete()
                        }
                    }
                }
                Log.d("linhd", "File deleted")
            } catch (e: Exception) {
                Log.d("linhd", "Exception")
            }
        }
        fun setUpStatusBar(activity: Activity, color: Int) {
            val window: Window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                false
            window.statusBarColor = ContextCompat.getColor(activity, color)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            hideBottomBar(activity)
        }

        @JvmStatic
        fun setStatusBar3(activity: Activity) {
            val window = activity.window
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            hideBottomBar(activity)
        }

        @JvmStatic
        private fun hideBottomBar(activity: Activity) {
            hideSystemUI(activity)
            @Suppress("DEPRECATION")
            activity.window.decorView
                .setOnSystemUiVisibilityChangeListener { visibility ->
                    if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                        hideSystemUI(activity)
                    }
                }
        }

        private fun hideSystemUI(activity: Activity) {
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }


        fun dpToPixel(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            )
        }

        fun showPermissionRequestDialog(
            context: Activity,
            message: String,
            isBack: Boolean = false
        ) {
//            val dialog = Dialog(context)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setContentView(R.layout.dialog_permission)
//
//            val window = dialog.window ?: return
//            window.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
////        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            val windowAttribute = window.attributes
//            windowAttribute.gravity = Gravity.CENTER
//            window.attributes = windowAttribute
//            dialog.setCancelable(true)
//            dialog.findViewById<TextView>(R.id.mess_permission).text = message
//            dialog.findViewById<LinearLayoutCompat>(R.id.cancel).setOnClickListener {
//                dialog.dismiss()
//                if (isBack) context.finish()
//            }
//            dialog.findViewById<LinearLayoutCompat>(R.id.goToSetting).setOnClickListener {
//                openAppSettings(context)
//                dialog.dismiss()
//            }
//            dialog.show()
        }
        fun playBottomSheetMenu(
            context: Activity
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.activity_menu)

            val window = dialog.window ?: return
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val windowAttribute = window.attributes
            windowAttribute.gravity = Gravity.BOTTOM
            window.attributes = windowAttribute
            dialog.setCancelable(true)
            dialog.show()
            dialog.findViewById<ImageView>(R.id.cancel).setOnClickListener{
                dialog.dismiss()
            }
            dialog.findViewById<LinearLayout>(R.id.ringtone_cutter).setOnClickListener {
                context.startActivity(Intent(context, ListFileAudioActivity::class.java))
            }
            dialog.findViewById<LinearLayout>(R.id.audio_mixer).setOnClickListener {

            }
            dialog.findViewById<LinearLayout>(R.id.auto_mix).setOnClickListener {

            }
            dialog.findViewById<LinearLayout>(R.id.merge_audio).setOnClickListener {

            }
            dialog.findViewById<LinearLayout>(R.id.store).setOnClickListener {

            }
            dialog.findViewById<TextView>(R.id.paynow).setOnClickListener {

            }



        }
        private var screenWidth:Int = 0

        fun getScreenWidth(c: Context): Int {
            if (screenWidth == 0) {
                val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                screenWidth = size.x
            }
            return screenWidth
        }


        fun pxtodp(context: Context, value: Int): Int {
            val r = context.resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value.toFloat(),
                r.displayMetrics
            ).toInt()
        }
        fun ReadWaveFormFile(WaveFormFile: File, wave: ThumbWaveView, samplingRate: Float) {
            var HeightCounter = 0f
            val bytes = ArrayList<Byte>()
            val `in`: InputStream?
            try {
                `in` = BufferedInputStream(FileInputStream(WaveFormFile))
                val sb = StringBuilder()
                var line: String? = null
                var linecount = 0
                try {
                    val reader = BufferedReader(InputStreamReader(`in`, "UTF-8"))
                    while ({ line = reader.readLine(); line }() != null) {
                        sb.append(line)
                        linecount++
                        HeightCounter += java.lang.Float.valueOf(line)
                        if (linecount % samplingRate == 0f) {
                            val avgheight = HeightCounter / samplingRate
                            val height = (java.lang.Float.valueOf(avgheight)!! * 127).toInt()
                            bytes.add(height.toByte())
                            HeightCounter = 0f
                        }
                    }
                    val data = ByteArray(bytes.size)
                    for (i in bytes.indices) {
                        val x = bytes[i]
                        data[i] = x
                    }
                    wave.scaledData = data

                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
        fun getAlbumImage(path: String): Bitmap? {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(path)
            val data = mmr.embeddedPicture
            return if (data != null) BitmapFactory.decodeByteArray(data, 0, data.size) else null
        }

    }
}