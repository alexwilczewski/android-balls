package com.example.AnimationTutorial;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.Canvas;
import android.content.Context;
import android.view.MotionEvent;
import android.util.Log;
import java.util.Random;
import java.util.ArrayList;

public class CustomDrawableView extends View {
    private boolean EXECUTE_ON_DRAW = false;

    private static final String TAG = "2PRO";

    private int MAX_X = 0;
    private int MAX_Y = 0;

    private long then;
    private boolean touchHeld;

    protected ArrayList<Ball> balls;

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    protected float mDownX;
    protected float mDownY;
    protected final float SCROLL_TRESHOLD = 10;
    protected boolean isOnClick;

    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            CustomDrawableView.this.update();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    class Ball extends ShapeDrawable {
        protected static final int BOUNDS_IN         = 0;
        protected static final int BOUNDS_OUT_LEFT   = 1;
        protected static final int BOUNDS_OUT_TOP    = 2;
        protected static final int BOUNDS_OUT_RIGHT  = 3;
        protected static final int BOUNDS_OUT_BOTTOM = 4;

        public float x;
        public float y;
        public int r;

        public float dx;
        public float dy;

        public Ball() {
            super(new OvalShape());

            getPaint().setColor(0x88ff0000);
        }

        public void reBounds() {
            setBounds((int)x-r, (int)y-r, (int)x+r, (int)y+r);
        }

        public void step(long deltaTimeMs) {
//            Log.i(TAG, "Time:" + deltaTimeMs);

            x += dx*deltaTimeMs/1000;
            y += dy*deltaTimeMs/1000;

//            Log.i(TAG, "x: "+x);
        }

        public int inBounds(int minx, int miny, int maxx, int maxy) {
            if(dx > 0 && x+r > maxx) {
                return BOUNDS_OUT_RIGHT;
            } else if(dx < 0 && x-r < minx) {
                return BOUNDS_OUT_LEFT;
            } else if(dy > 0 && y+r > maxy) {
                return BOUNDS_OUT_BOTTOM;
            } else if(dy < 0 && y-r < miny) {
                return BOUNDS_OUT_TOP;
            }
            return BOUNDS_IN;
        }
    };

    public CustomDrawableView(Context context) {
        super(context);

        Log.i(TAG, "Constructor Start");

        balls = new ArrayList<Ball>();

        update();

        Log.i(TAG, "Constructor End");
    }

    protected void onFirstDraw(Canvas canvas) {
        addRandomBalls(10);
    }

    protected void addRandomBalls(int number) {
        Ball mBall;

        Random r = new Random();

        for(int i=0; i<number; i++) {
            mBall = new Ball();
            mBall.x = r.nextInt(MAX_X);
            mBall.y = r.nextInt(MAX_Y);
            mBall.r = 40;
            mBall.dx = r.nextInt(50)-25;
            mBall.dy = r.nextInt(50)-25;

            balls.add(mBall);
        }
    }

    protected void addRandomBalls(int number, int x, int y, int diff) {
        Ball mBall;

        Random r = new Random();

        for(int i=0; i<number; i++) {
            mBall = new Ball();
            mBall.x = x+r.nextInt(diff)-diff/2;
            mBall.y = y+r.nextInt(diff)-diff/2;
            mBall.r = 40;
            mBall.dx = (1+r.nextInt(5))*20-50;
            mBall.dy = (1+r.nextInt(5))*20-50;

            balls.add(mBall);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        MAX_X = MeasureSpec.getSize(widthMeasureSpec);
        MAX_Y = MeasureSpec.getSize(heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
//        Log.i(TAG, "onDraw Start");

        if(EXECUTE_ON_DRAW == false) {
            EXECUTE_ON_DRAW = true;

            onFirstDraw(canvas);
        }

        for(Ball b : balls) {
//            Log.i(TAG, "ball draw");
            b.reBounds();
            b.draw(canvas);
        }
    }

    public void update() {
        long now = System.currentTimeMillis();

        long difference = now - then;
        int inBound;
        for(Ball b : balls) {
            b.step(difference);
            inBound = b.inBounds(0, 0, MAX_X, MAX_Y);

            if(inBound == Ball.BOUNDS_OUT_BOTTOM || inBound == Ball.BOUNDS_OUT_TOP) {
                b.dy *= -1;
            } else if(inBound == Ball.BOUNDS_OUT_LEFT || inBound == Ball.BOUNDS_OUT_RIGHT) {
                b.dx *= -1;
            }
        }

        then = now;

        invalidate();

        mRedrawHandler.sleep(60);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                isOnClick = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isOnClick) {
                    Log.i(TAG, "onClick ");
                    //TODO onClick code
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_TRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_TRESHOLD)) {
                    Log.i(TAG, "movement detected");
                    addRandomBalls(1, (int)ev.getX(), (int)ev.getY(), 50);
//                    isOnClick = false;
                }
                break;
            default:
                break;
        }

        return true;
    }
}
