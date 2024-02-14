package com.gambi.quanglinh.djmixer.visualizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gambi.quanglinh.djmixer.R;

public class SquareBarVisualizer2 extends BaseVisualizer2 {

    private float density = 10;
    private int gap;
    private int maxheight = 64;
    GradientDrawable gradientDrawable;

    public SquareBarVisualizer2(Context context) {
        super(context);
    }

    public SquareBarVisualizer2(Context context,
                               @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareBarVisualizer2(Context context,
                               @Nullable AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void init() {
        this.density = 10;
        this.gap = 2;
        paint.setStyle(Paint.Style.FILL);
        paint.setDither(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#BE0411"),
                        Color.parseColor("#FE502D"),
                        Color.parseColor("#FE9520"),
                        Color.parseColor("#F2E24E"),
                        Color.parseColor("#56DB64"),
                        Color.parseColor("#01C4B4")});
    }

    private Bitmap convertToBitmap(GradientDrawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    /**
     * Sets the density to the Bar visualizer i.e the number of bars
     * to be displayed. Density can vary from 10 to 256.
     * by default the value is set to 50.
     *
     * @param density density of the bar visualizer
     */
    public void setDensity(float density) {
        this.density = density;
//        if (density > 256) {
//            this.density = 256;
//        } else if (density < 16) {
//            this.density = 16;
//        }
//        this.gap = (int) (getWidth() / (6 * density + 1));
        // Log.d("bacdz", gap + "/" + getWidth());
    }

    /**
     * Set Spacing between the Square in visualizer in pixel.
     *
     * @param gap Spacing between the square
     */
    public void setGap(int gap) {
        this.gap = gap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bytes != null) {
            Bitmap bitmap = convertToBitmap(gradientDrawable, getWidth(), getHeight());
            @SuppressLint("DrawAllocation") BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            paint.setShader(shader);

            //float barWidth = getWidth() / density;
//            this.gap = (int) (getWidth() / (7 * density - 1));
            this.gap = (int) (getWidth() / 3);
            float barWidth = getWidth() / density ;
            float div = bytes.length / density;
            //paint.setStrokeWidth(barWidth - gap);
            for (int i = 0; i < density; i++) {
                if (i >= 2 && i < density - 2) continue;
                int count = 0;
                int bytePosition = (int) Math.ceil(i * div);
                int top = getHeight() + ((byte) (Math.abs(bytes[bytePosition]) + 128)) * getHeight() / maxheight;//64
//                int col = 10;
                int col = Math.abs((getHeight() - top));
                for (int j = 0; j < col + 1; j += barWidth) {

//                    if (j / barWidth < colors.length)
//                        paint.setColor(Color.parseColor(colors[(int) (j / barWidth)]));
//                    else paint.setColor(Color.WHITE);

                    //float barX = (i * barWidth) + (barWidth / 2);
                    float barX1 = i * (barWidth + gap);
                    float barX2 = i * (barWidth + gap) + barWidth;
                    float y1 = getHeight() - ((barWidth  + gap) * count);
                    //float y2 = getHeight() - ((barWidth - gap) + ((barWidth + gap) * count)) / 6f;
                    float y2 = y1 - barWidth;
                    @SuppressLint("DrawAllocation") final Path path = new Path();
//                    Log.d("linhd","barX1:"+barX1+"y1:"+y1+"barX2:"+barX2+"y2:"+y2);
//                    path.addRect(barX1, y1, barX2, y2, Path.Direction.CW);
                    path.addRoundRect(barX1, y1, barX2, y2, 0, 0, Path.Direction.CW);
//                    path.addRoundRect(barX1, y1, barX2, y2, (y1 - y2) / 2f, (y1 - y2), Path.Direction.CW);
//                        canvas.drawRoundRect(barX1, y1, barX2, y2, (y1 - y2) / 2f, (y1 - y2) / 2f, paint);
                    canvas.drawPath(path, paint);
                    count++;
                    if(count==10)break;
                }
            }

            super.onDraw(canvas);
        }
    }
    public void setHeightAll(int heightAll){
        maxheight = heightAll;
    }

}
