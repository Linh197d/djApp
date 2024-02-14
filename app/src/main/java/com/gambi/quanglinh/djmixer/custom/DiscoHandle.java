package com.gambi.quanglinh.djmixer.custom;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.gambi.quanglinh.djmixer.listener.IDiscoHandleBack;
import com.gambi.quanglinh.djmixer.listener.IDiscoRotate;

public class DiscoHandle extends FrameLayout {
    private MediaPlayer mediaPlayer;
    private double mCurrAngle = 0;
    private double mPrevAngle = 0;
    private ObjectAnimator anim = null;
    private IDiscoHandleBack iDiscoHandleBack;
    private IDiscoRotate iDiscoRotate;
    private boolean isPlaying;

    public DiscoHandle(Context context) {
        super(context);
    }

    private boolean touch = false;

    public DiscoHandle(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        create();

    }

    public void setListener(IDiscoHandleBack iDiscoHandleBack) {
        this.iDiscoHandleBack = iDiscoHandleBack;
    }
    public void setListenerRotateDisco(IDiscoRotate iDiscoRotate) {
        this.iDiscoRotate = iDiscoRotate;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        create();
    }
    public void isPlaying(boolean b){
        this.isPlaying = b;
    }
    public void clearAnimated(){
       clearAnimation();
    }
    private void create() {

    }

    public void setPlayPause(boolean b) {
        if (b) {
            mediaPlayer.start();
            if (anim == null)
                animateForever(0, 360);
            else {
                anim.resume();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("linhd", "complete");
                    clearAnimation();
                    anim.cancel();
                }
            });
        } else {
            mediaPlayer.pause();
            anim.pause();
        }

    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float xc = getWidth() / 2;
        final float yc = getHeight() / 2;

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (mediaPlayer != null) {
                    //mCurrAngle = Math.toDegrees(Math.atan2(x - xc, y - yc));
                    mCurrAngle = Math.toDegrees(Math.atan2(xc - x, y - yc));
//                    Log.d("linhd", "current:" + mCurrAngle);
                    touch = true;
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        if(anim!=null)
                        anim.pause();
                    }
//                mCircle.clearAnimation();
//                mCircle.setRotation((float) mCurrAngle);
//                    Log.d("linhd", ":" + mediaPlayer.getCurrentPosition());
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mediaPlayer != null) {
                    mPrevAngle = mCurrAngle;
                    mCurrAngle = Math.toDegrees(Math.atan2(xc - x, y - yc));
//                mCurrAngle = Math.toDegrees(Math.atan2(x - xc, yc - y));
//                Log.d("linhd", "pre:" + mPrevAngle + "cur:" + mCurrAngle);
                    double deltaAngle = mCurrAngle - mPrevAngle;
                    if (deltaAngle > 180) {
                        deltaAngle -= 360;
                    } else if (deltaAngle < -180) {
                        deltaAngle += 360;
                    }
//                    double MAX_DELTA_ANGLE = 10.0; // Đây là giá trị tối đa mà góc xoay có thể thay đổi, bạn có thể điều chỉnh nó
//                    if (deltaAngle > MAX_DELTA_ANGLE) {
//                        deltaAngle = MAX_DELTA_ANGLE;
//                    } else if (deltaAngle < -MAX_DELTA_ANGLE) {
//                        deltaAngle = -MAX_DELTA_ANGLE;
//                    }

                    int seekForwardTime = (int) ((int) Math.abs(deltaAngle) * 100/3.6); // 100 milliseconds/góc
                    animate(mPrevAngle, mCurrAngle, 0);
                    if (deltaAngle > 0) {
//                    animate(mPrevAngle, mCurrAngle, (long) (deltaAngle*3600*1000));
                    } else {
//                    animate(mPrevAngle, mCurrAngle, (long) (-deltaAngle*3600*1000));
                        seekForwardTime = -seekForwardTime;

                    }
                    if(iDiscoRotate!=null)
                    iDiscoRotate.onDiscoRotate();
                    int currentPosition = mediaPlayer.getCurrentPosition(); // lấy vị trí hiện tại
//                    Log.d("linhd", "pre:" + mPrevAngle + "cur:" + mCurrAngle + "delta:" + deltaAngle);
                    if (currentPosition + seekForwardTime <= mediaPlayer.getDuration() && currentPosition + seekForwardTime > 0) { // kiểm tra xem có quá thời lượng không
                        mediaPlayer.seekTo(currentPosition + seekForwardTime); // tua tới
                    } else {

//                    Log.d("linhd", "outRangeeeeeeeê");
                    }
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mediaPlayer != null) {
//                startAutoSpin();
//                isAutoSpin = true; // Người dùng bỏ chạm, bắt đầu quay tự động
                    mPrevAngle = mCurrAngle = 0;
//                    Log.d("linhd", ":" + mediaPlayer.getCurrentPosition());
                    if (anim != null) {
                        if(isPlaying) {
                            mediaPlayer.start();
                            anim.resume();
//                            Log.d("linhd", "resume anim");

                        }
                        if (iDiscoRotate != null) {
                            iDiscoRotate.onDiscoRotate();
                        }
                    }
                } else {
//                    getContext().
                    if (iDiscoHandleBack != null) {
                        iDiscoHandleBack.onDiscoHandleBack();
                    }
                }

                break;

            }
        }

        return true;
    }

    private void animate(double fromDegrees, double toDegrees, long durationMillis) {
        final RotateAnimation rotate = new RotateAnimation((float) fromDegrees, (float) toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        startAnimation(rotate);

    }

    private static final int DURATION = 36000;

    private void animateForever(double fromDegrees, double toDegrees) {
        anim = ObjectAnimator.ofFloat(this, View.ROTATION, (float) fromDegrees, (float) toDegrees);
        anim.setDuration(DURATION);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
    }

}
