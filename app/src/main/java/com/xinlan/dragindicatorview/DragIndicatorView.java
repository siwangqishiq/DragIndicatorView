package com.xinlan.dragindicatorview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 可拖拽的红点提示
 * <p/>
 * Created by panyi on 2016/3/31.
 */
public class DragIndicatorView extends TextView {
    private static int DRAW_COLOR = Color.RED;
    private static int DEFAULT_DISTANCE = 200;
    private Paint mPaint;
    private int mRadius = 0;

    private float mOriginX = 0;
    private float mOriginY = 0;
    private int mCenterX = 0;
    private int mCenterY = 0;

    private float mDx = 0;
    private float mDy = 0;

    private int mDismissDetectDistance = DEFAULT_DISTANCE;//超过此距离 判定为让提示View消失

    private ViewGroup mRootView;//根布局视图 作为画板使用
    private DragIndicatorView mCloneView;

    public DragIndicatorView(Context context) {
        super(context);
        initView(context);
    }

    public DragIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DragIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);//内容居中

        mPaint = new Paint();
        mPaint.setColor(DRAW_COLOR);

        mRootView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        //System.out.println("init View");

    }

    @Override
    public void setBackgroundResource(int resid) {
        showCannotSetBgErrorLog();
    }

    @Override
    public void setBackground(Drawable background) {
        showCannotSetBgErrorLog();
    }

    @Override
    public void setBackgroundColor(int color) {
        showCannotSetBgErrorLog();
    }

    private void showCannotSetBgErrorLog() {
        Log.e("error", "This drag indicator view can not set custom background");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        super.onDraw(canvas);
    }

    /**
     * draw circle background
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        mRadius = Math.min(getMeasuredWidth(), getMeasuredHeight()) >> 1;
        canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, mRadius, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //System.out.println("down");
                mDx = event.getX();
                mDy = event.getY();
                mOriginX = event.getRawX() - mDx + (getWidth() >> 1);
                mOriginY = event.getRawY() - mDy + (getHeight() >> 1);
                break;
            case MotionEvent.ACTION_MOVE:
                //System.out.println("move");
                if (getVisibility() == View.VISIBLE) {
                    setVisibility(View.INVISIBLE);
                    mCloneView = cloneSelfView();
                    mRootView.addView(mCloneView, getLayoutParams());
                }//end if

                if (mCloneView != null) {
                    mCloneView.setX(event.getRawX() - mDx);
                    mCloneView.setY(event.getRawY() - mDy);
                    mCloneView.invalidate();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //System.out.println("up");
                if (mCloneView != null) {
                    mRootView.removeView(mCloneView);
                    mCloneView = null;
                }

                //判断是否dismiss View
                float deltaX = event.getRawX() - mOriginX;
                float deltaY = event.getRawY() - mOriginY;
                if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) >= mDismissDetectDistance) {//超过拉力的极限距离
                    killView(event.getRawX(), event.getRawY());
                } else {//未超过极限
                    // TODO: 2016/3/31 显示回弹效果动画  恢复View可见
                    setVisibility(View.VISIBLE);
                }//end if
                break;
        }//end switch
        return true;
    }

    /**
     * 手动控制  提示按钮按钮不可见
     */
    public void dismissView() {
        int[] screens = new int[2];
        getLocationOnScreen(screens);
        killView(screens[0] + (getWidth() >> 1), screens[1] + (getHeight() >> 1));
    }

    protected void killView(final float x, final float y) {
        final ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.clean_anim);
        mRootView.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setX(x - (imageView.getMeasuredWidth() >> 1));
                imageView.setY(y - (imageView.getMeasuredHeight() >> 1));
            }
        });
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();

        setVisibility(View.GONE);
    }

    /**
     * '
     * 产生一个自己的备份
     *
     * @return
     */
    protected DragIndicatorView cloneSelfView() {
        DragIndicatorView textView = new DragIndicatorView(getContext());
        textView.setText(getText());
        textView.setTextColor(getTextColors());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
        textView.setGravity(getGravity());
        textView.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        textView.setEnabled(false);
        return textView;
    }
}//end class
