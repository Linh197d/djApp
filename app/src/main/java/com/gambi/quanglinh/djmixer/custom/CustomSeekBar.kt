package com.gambi.quanglinh.djmixer.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.gambi.quanglinh.djmixer.R

class CustomSeekBar(
    context: Context,
    attrs: AttributeSet
) : AppCompatSeekBar(context, attrs) {

    private var rect: RectF = RectF()
//    private var rect: Rect = Rect()
    private var paint: Paint = Paint()
    private var seekbarHeight: Int = 16
    private var colorScheme: Int = R.color.primary
   fun setColor(color:Int){
       colorScheme=color
   }
    @Synchronized
    override fun onDraw(canvas: Canvas) {
        // for seek bar line
        rect[30F, (height / 2 - seekbarHeight / 2).toFloat(), width.toFloat()-30] = (height / 2 + seekbarHeight / 2).toFloat()
//        rect[0F, (height / 2- seekbarHeight / 2).toFloat(), width.toFloat()] = (height / 2 + seekbarHeight / 2).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.black)
        canvas.drawRoundRect(rect,10f,10f, paint)


//        for right side
        if (this.progress > 0) {
//            rect[width / 2, height / 2 - seekbarHeight / 2, width / 2 + width / 200 * (progress)] =
//                height / 2 + seekbarHeight / 2
            rect[(width / 2).toFloat(), (height / 2 - seekbarHeight / 2).toFloat(), (width / 2 + width / 200 * (progress)).toFloat()] =
                (height / 2 + seekbarHeight / 2).toFloat()
            paint.color = ContextCompat.getColor(context, colorScheme)
            canvas.drawRoundRect(rect,10f,10f, paint)
        }

        //for left side
        if (this.progress < 0) {
//            rect[width / 2 - width / 200 * (-progress), height / 2 - seekbarHeight / 2, width / 2] =
//                height / 2 + seekbarHeight / 2
            rect[(width / 2 - width / 200 * (-progress)).toFloat(), (height / 2 - seekbarHeight / 2).toFloat(), (width / 2).toFloat()] =
                (height / 2 + seekbarHeight / 2).toFloat()

            paint.color = ContextCompat.getColor(context, colorScheme)
            canvas.drawRoundRect(rect,10f,10f, paint)
        }
//        val rect: Rect = Rect()
//        rectF.set(0F, (height / 2 - seekbarHeight / 2).toFloat(), width.toFloat(),
//            (height / 2 + seekbarHeight / 2).toFloat()
//        )
////        rectF.set(rect)
//        canvas.drawRoundRect(rectF, 10f, 10f, paint)
//        if (this.progress > 0) {
//            rectF.set((width / 2).toFloat(),
//                (height / 2 - seekbarHeight / 2).toFloat(),
//                (width / 2 + width / 100 * (progress)).toFloat(), (height / 2 + seekbarHeight / 2).toFloat()
//            )
////            rectF.set(rect)
//            paint.color = ContextCompat.getColor(context, colorScheme)
//            canvas.drawRoundRect(rectF, 10f, 10f, paint)
//        }
//
//        if (this.progress < 0) {
//            rect.set(width / 2 - width / 100 * (-progress), height / 2 - seekbarHeight / 2, width / 2, height / 2 + seekbarHeight / 2)
//            rectF.set(rect)
//            paint.color = ContextCompat.getColor(context, colorScheme)
//            canvas.drawRoundRect(rectF, 10f, 10f, paint)
//        }
        super.onDraw(canvas)
    }
}