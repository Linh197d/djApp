package com.gambi.quanglinh.djmixer.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gambi.quanglinh.djmixer.R;
import com.gambi.quanglinh.djmixer.utils.MyUtils;


public class AnalogController extends View {

    float midx, midy;

    Paint textPaint, circlePaint, circlePaint2, progressPaint;
    String angle;
    float currdeg, deg = 3, downdeg;

    int progressColor, lineColor;

    onProgressChangedListener mListener;

    String label;

    private Bitmap knob = null;
    private Bitmap led = null;

    private RectF rectF = null;

    private SweepGradient shader = null;
    private LinearGradient textShader = null;
    private int[] colorList = {Color.parseColor("#00A3FF"), Color.parseColor("#6DEDF7")};

    public interface onProgressChangedListener {
        void onProgressChanged(int progress);
    }
    public void setColorListForProgress(){
        colorList = new int[]{Color.parseColor("#AE00C0"), Color.parseColor("#AE00C0")};
    }

    public void setOnProgressChangedListener(onProgressChangedListener listener) {
        mListener = listener;
    }

    public AnalogController(Context context) {
        super(context);
        init();
    }

    public AnalogController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnalogController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.parseColor("#6DEDF7"));

        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#222222"));
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint2 = new Paint();
//        circlePaint2.setColor(EqualizerFragment.themeColor); //test circle Paint2 check
        circlePaint2.setColor(Color.parseColor("#FFA036"));
        circlePaint2.setStyle(Paint.Style.FILL);

        angle = "0.0";
        label = "";//"Label"
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        midx = getWidth() / 2f;
        midy = getHeight() / 2f;

//        int ang = 0;
//        float x = 0, y = 0;
        int radius = (int) (Math.min(midx, midy) * ((float) 14.5 / 16));
//        float deg2 = Math.max(3, deg);
//        float deg3 = Math.min(deg, 21);
//        for (int i = (int) (deg2); i < 22; i++) {
//            float tmp = (float) i / 24;
//            x = midx + (float) (radius * Math.sin(2 * Math.PI * (1.0 - tmp)));
//            y = midy + (float) (radius * Math.cos(2 * Math.PI * (1.0 - tmp)));
//            circlePaint.setColor(Color.parseColor("#111111"));
//            canvas.drawCircle(x, y, ((float) radius / 15), circlePaint);
//        }
//        for (int i = 3; i <= deg3; i++) {
//            float tmp = (float) i / 24;
//            x = midx + (float) (radius * Math.sin(2 * Math.PI * (1.0 - tmp)));
//            y = midy + (float) (radius * Math.cos(2 * Math.PI * (1.0 - tmp)));
//            canvas.drawCircle(x, y, ((float) radius / 15), circlePaint2);
//        }

        float tmp2 = deg / 24;
//        float x1 = midx + (float) (radius * ((float) 3 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
//        float y1 = midy + (float) (radius * ((float) 3 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
        float x2 = midx + (float) (radius * ((float) 6.7 / 10) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
        float y2 = midy + (float) (radius * ((float) 6.7 / 10) * Math.cos(2 * Math.PI * (1.0 - tmp2)));

//        circlePaint.setColor(Color.parseColor("#222222"));
//        canvas.drawCircle(midx, midy, radius * ((float) 13 / 15), circlePaint);
        if (knob == null)
            knob = MyUtils.Companion.bitmapResize(BitmapFactory.decodeResource(getResources(), R.drawable.knob), (int) (radius * 2f * 12f / 15), (int) (radius * 2f * 12f / 15));
        if (led == null)
            led = MyUtils.Companion.bitmapResize(BitmapFactory.decodeResource(getResources(), R.drawable.led), (int) (getWidth() / 23f + 2), (int) (getWidth() / 23f + 2));

        canvas.drawBitmap(rotateBitmap(knob, (int) (getProgress() / 24f * 360)), midx - radius * ((float) 12 / 15), midy - radius * ((float) 12 / 15), null);
//        circlePaint.setColor(Color.parseColor("#000000"));
//        canvas.drawCircle(midx, midy, radius * ((float) 11 / 15), circlePaint);
        textPaint.setTextSize(getHeight() / 10f);
        canvas.drawText(label, midx, midy + (float) (radius * 1.1), textPaint);

        //canvas.drawLine(x1, y1, x2, y2, linePaint);
        canvas.drawBitmap(led, x2 - getWidth() / 23f / 2 + 1, y2 - getWidth() / 23f / 2 + 1, null);

        if (rectF == null)
            rectF = new RectF(midx - radius, midy - radius, midx + radius, midy + radius);

        //draw shadow progress
        progressPaint.setStrokeWidth(getWidth() / 24f + 8);
        progressPaint.setShadowLayer(6, 0, 0, Color.GRAY);
        setLayerType(LAYER_TYPE_SOFTWARE, progressPaint);
        canvas.drawArc(rectF, 135f, 270f, false, progressPaint);
        progressPaint.clearShadowLayer();

        //draw progress background black border
        progressPaint.setStrokeWidth(getWidth() / 24f + 6);
        progressPaint.setColor(Color.BLACK);
        canvas.drawArc(rectF, 135f, 270f, false, progressPaint);

        //draw progress background border
        progressPaint.setStrokeWidth(getWidth() / 24f + 3);
        progressPaint.setColor(Color.parseColor("#3D3D43"));
        progressPaint.setColor(Color.GRAY);
        canvas.drawArc(rectF, 135f, 270f, false, progressPaint);

        //draw progress background
        progressPaint.setColor(Color.parseColor("#1A1A1D"));
        progressPaint.setStrokeWidth(getWidth() / 24f);
        canvas.drawArc(rectF, 135f, 270f, false, progressPaint);

        //draw progress
        if (shader == null) {
            shader = new SweepGradient(midx, midy, colorList, null);
            Matrix matrix = new Matrix();
            matrix.setRotate(125f, midx, midy);
            shader.setLocalMatrix(matrix);
        }
        progressPaint.setShader(shader);
        progressPaint.setStrokeWidth(getWidth() / 24f + 2);
        canvas.drawArc(rectF, 135f, (getProgress() - 1) / 24f * 360, false, progressPaint);
        progressPaint.setShader(null);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int rotationAngleDegree) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int newW = w, newH = h;
        if (rotationAngleDegree == 90 || rotationAngleDegree == 270) {
            newW = h;
            newH = w;
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(newW, newH, bitmap.getConfig());
        Canvas canvas = new Canvas(rotatedBitmap);

        Rect rect = new Rect(0, 0, newW, newH);
        Matrix matrix = new Matrix();
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        matrix.postTranslate(-bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f);
        matrix.postRotate(rotationAngleDegree);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
        matrix.reset();

        return rotatedBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        mListener.onProgressChanged((int) (deg - 2));

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            downdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            downdeg -= 90;
            if (downdeg < 0) {
                downdeg += 360;
            }
            downdeg = (float) Math.floor(downdeg / 15);
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            currdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            currdeg -= 90;
            if (currdeg < 0) {
                currdeg += 360;
            }
            currdeg = (float) Math.floor(currdeg / 15);

            if (currdeg == 0 && downdeg == 23) {
                deg++;
                if (deg > 21) {
                    deg = 21;
                }
                downdeg = currdeg;
            } else if (currdeg == 23 && downdeg == 0) {
                deg--;
                if (deg < 3) {
                    deg = 3;
                }
                downdeg = currdeg;
            } else {
                deg += (currdeg - downdeg);
                if (deg > 21) {
                    deg = 21;
                }
                if (deg < 3) {
                    deg = 3;
                }
                downdeg = currdeg;
            }

            angle = String.valueOf(deg);
            invalidate();
            return true;
        }
        return e.getAction() == MotionEvent.ACTION_UP || super.onTouchEvent(e);
    }

    public int getProgress() {
        return (int) (deg - 2);
    }

    public void setProgress(int param) {
        deg = param + 2;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String txt) {
        label = txt;
        textPaint.setTextSize(getHeight() / 8f);
        Rect bounds = new Rect();
        textPaint.getTextBounds(label, 0, label.length(), bounds);
        textShader = new LinearGradient(0, textPaint.getTextSize() + bounds.top, 0, textPaint.getTextSize(), Color.parseColor("#6DEDF7"), Color.parseColor("#00A3FF"), Shader.TileMode.CLAMP);
        textPaint.setShader(textShader);
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }
}
