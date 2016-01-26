package com.app.warmalarm.colorfragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016-01-26.
 * QJD
 */
public class ColorSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private Timer timer;
    private TimerTask timerTask;
    private Paint paint;

    public ColorSurfaceView(Context context) {
        super(context, null);
        getHolder().addCallback(this);
    }

    public ColorSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        getHolder().addCallback(this);
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                draw();
            }
        };
        timer.schedule(timerTask, 100, 100);
    }

    private void stopTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void draw() {
        Canvas canvas = getHolder().lockCanvas();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, 100, 100, paint);
        getHolder().unlockCanvasAndPost(canvas);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //startTimer();
        new ColorSurfaceViewThread().start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //stopTimer();
    }

    class ColorSurfaceViewThread extends Thread {

        public ColorSurfaceViewThread() {
            super();
        }
        @Override
        public void run() {
            draw();
        }
    }
}
