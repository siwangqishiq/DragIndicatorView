package com.xinlan.dragindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinlan.dragindeocatorview.R;

/**
 * 可拖拽的红点提示
 * <p/>
 * Created by panyi on 2016/3/31.
 */
public class DragIndicatorView extends TextView {
    private static int DRAW_COLOR = Color.RED;
    //private static int DEFAULT_DISTANCE = 200;
    private static float DEFAULT_VISCOUS_VALUE = 0.15f;//粘滞系数

    private Paint mPaint;
    private int mRadius = 0;
    private float mViscous = DEFAULT_VISCOUS_VALUE;
    private float mOriginX = 0;
    private float mOriginY = 0;
    private int mCenterX = 0;
    private int mCenterY = 0;

    private float mDx = 0;
    private float mDy = 0;

    //private int mDismissDetectDistance = DEFAULT_DISTANCE;//超过此距离 判定为让提示View消失

    private ViewGroup mRootView;//根布局视图 作为画板使用
    private DragIndicatorView mCloneView;
    private ViewParent mParentView;
    private SpringView mSpringView;
    private OnIndicatorDismiss mOnDismissAction;

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
        mPaint.setAntiAlias(true);

        if (context instanceof Activity) {
            mRootView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        }
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
                if (mParentView == null) {
                    mParentView = getScrollableParent();
                }
                if (mParentView != null) {//屏蔽父控件的事件响应
                    mParentView.requestDisallowInterceptTouchEvent(true);
                }

                mDx = event.getX();
                mDy = event.getY();
                mOriginX = event.getRawX() - mDx + (getWidth() >> 1);
                mOriginY = event.getRawY() - mDy + (getHeight() >> 1);
                break;
            case MotionEvent.ACTION_MOVE:
                //System.out.println("move");
                if (getVisibility() == View.VISIBLE) {
                    setVisibility(View.INVISIBLE);

                    mSpringView = new SpringView(this.getContext());
                    mSpringView.initSpring(mOriginX, mOriginY, mRadius, getWidth(), getHeight());
                    mRootView.addView(mSpringView);

                    mCloneView = cloneSelfView();
                    mRootView.addView(mCloneView, getLayoutParams());
                }//end if


                if (mCloneView != null) {
                    mCloneView.setX(event.getRawX() - mDx);
                    mCloneView.setY(event.getRawY() - mDy);
                    mCloneView.invalidate();
                }
                //拉伸水滴效果
                if (mSpringView != null) {
                    //更新弹性控件
                    mSpringView.update(event.getRawX() - mDx, event.getRawY() - mDy);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //System.out.println("up");
                //判断是否dismiss View
                if (mSpringView != null && mSpringView.radius <= 0) {
                    killView(event.getRawX(), event.getRawY());
                    mRootView.removeView(mSpringView);
                    mSpringView = null;

                    if (mCloneView != null) {
                        mRootView.removeView(mCloneView);
                        mCloneView = null;
                    }
                } else {//不取消
                    // TODO: 2016/3/31 显示回弹效果动画  恢复View可见
                    //setVisibility(View.VISIBLE);
                    if (mSpringView != null && mSpringView.spring_len > 1f) {//存在弹性势能  显示弹性动画效果
                        mSpringView.startSpringAction();
                    } else {
                        resetView();
                    }
                }

                if (mParentView != null) {//恢复父控件对事件的处理
                    mParentView.requestDisallowInterceptTouchEvent(false);
                }
                break;
        }//end switch
        return true;
    }

    private void resetView() {
        if (mCloneView != null) {
            mRootView.removeView(mCloneView);
        }
        if (mSpringView != null) {
            mRootView.removeView(mSpringView);
        }
        setVisibility(View.VISIBLE);
    }

    /**
     * 获得父控件
     *
     * @return
     */
    private ViewGroup getScrollableParent() {
        View target = this;
        while (true) {
            View parent;
            try {
                parent = (View) target.getParent();
            } catch (Exception e) {
                return null;
            }
            if (parent == null)
                return null;
            if (parent instanceof ViewGroup) {
                return (ViewGroup) parent;
            }
            target = parent;
        }//end while
    }

    /**
     * 手动控制  提示按钮按钮不可见
     */
    public void dismissView() {
        if (getVisibility() == View.VISIBLE) {
            int[] screens = new int[2];
            getLocationOnScreen(screens);
            killView(screens[0] + (getWidth() >> 1), screens[1] + (getHeight() >> 1));
        }//end if
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
        int totalDuring = 0;
        for (int i = 0, len = animationDrawable.getNumberOfFrames(); i < len; i++) {
            totalDuring += animationDrawable.getDuration(i);
        }
        animationDrawable.start();

        //动画播放结束后 移除ImageView
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootView.removeView(imageView);
            }
        }, totalDuring + 20);

        if (mOnDismissAction != null) {
            mOnDismissAction.OnDismiss(this);
        }

        setVisibility(View.GONE);
    }

    /**
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

    /**
     * 获取粘滞系数
     *
     * @return
     */
    public float getViscous() {
        return mViscous;
    }

    /**
     * 设置粘滞系数
     *
     * @param mViscous
     */
    public void setViscous(float mViscous) {
        this.mViscous = mViscous;
    }

    public interface OnIndicatorDismiss {
        void OnDismiss(DragIndicatorView view);
    }

    public void setOnDismissAction(OnIndicatorDismiss mOnDismissAction) {
        this.mOnDismissAction = mOnDismissAction;
    }

    /**
     *
     */
    private final class SpringView extends View {
        public float from_x;
        public float from_y;
        public float radius;
        public float to_x;
        public float to_y;

        public float toWidth;
        public float toHeight;

        private Path mPath = new Path();
        boolean isSpringAction = false;
        float cur_x;
        float cur_y;
        float spring_len = 0;
        float origin_len = 0;

        ValueAnimator mSpringAnimation;

        public SpringView(Context context) {
            super(context);
            isSpringAction = false;
        }

        public void initSpring(float init_x, float init_y, float r, float w, float h) {
            this.from_x = init_x;
            this.from_y = init_y;
            this.to_x = init_x;
            this.to_y = init_y;
            this.radius = r;

            this.toWidth = w;
            this.toHeight = h;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (radius > 0) {
                canvas.drawPath(mPath, mPaint);//draw path
                canvas.drawCircle(from_x, from_y, radius, mPaint);
            }//end if
        }


        public void update(float x, float y) {
            this.to_x = x;
            this.to_y = y;

            //目的圆 球心坐标
            float dest_x = to_x + toWidth / 2;
            float dest_y = to_y + toHeight / 2;
            updatePosition(dest_x, dest_y);
        }

        private void updatePosition(final float dest_x, final float dest_y) {
            this.cur_x = dest_x;
            this.cur_y = dest_y;

            float deltaX = 0;
            float deltaY = 0;
            if (dest_y >= from_y) {
                deltaX = dest_x - from_x;
                deltaY = dest_y - from_y;
            } else {
                deltaX = from_x - dest_x;
                deltaY = from_y - dest_y;
            }//end if

            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            //radius = (float)mRadius/(distance + 1);
            //  r = R - R * (1 -1/d));
            radius = mRadius - mViscous * distance;
            if (radius < 0.2f * mRadius) {
                radius = 0;
            }

            if (radius > 0) {
                // (1 , 0)  (x,y)
                double cos_delta = deltaX / distance;
                double angle = Math.acos(cos_delta);
                double circle_from_thela1 = angle + Math.PI / 2;
                double circle_from_thela2 = circle_from_thela1 + Math.PI;

                float circle_from_circle_x1 = (float) (from_x + radius * Math.cos(circle_from_thela1));
                float circle_from_circle_y1 = (float) (from_y + radius * Math.sin(circle_from_thela1));

                float circle_from_circle_x2 = (float) (from_x + radius * Math.cos(circle_from_thela2));
                float circle_from_circle_y2 = (float) (from_y + radius * Math.sin(circle_from_thela2));

                float circle_to_circle_x1 = (float) (dest_x + mRadius * Math.cos(circle_from_thela1));
                float circle_to_circle_y1 = (float) (dest_y + mRadius * Math.sin(circle_from_thela1));

                float circle_to_circle_x2 = (float) (dest_x + mRadius * Math.cos(circle_from_thela2));
                float circle_to_circle_y2 = (float) (dest_y + mRadius * Math.sin(circle_from_thela2));

                mPath.reset();
                mPath.moveTo(circle_from_circle_x1, circle_from_circle_y1);
                mPath.lineTo(circle_from_circle_x2, circle_from_circle_y2);
                mPath.quadTo((from_x + dest_x) / 2, (from_y + dest_y) / 2,
                        circle_to_circle_x2, circle_to_circle_y2);
                //mPath.lineTo(dest_x,dest_y);
                //mPath.lineTo(circle_to_circle_x2, circle_to_circle_y2);
                mPath.lineTo(circle_to_circle_x1, circle_to_circle_y1);
                mPath.quadTo((from_x + dest_x) / 2, (from_y + dest_y) / 2,
                        circle_from_circle_x1, circle_from_circle_y1);
                mPath.close();

                if (mCloneView != null) {
                    mCloneView.setX(cur_x - toWidth / 2);
                    mCloneView.setY(cur_y - toHeight / 2);
                }

                spring_len = distance;
            } else {
                spring_len = 0;
            }

            invalidate();
        }

        /**
         * 做回弹操作
         */
        public void startSpringAction() {
            isSpringAction = true;
            origin_len = spring_len;

            if (mSpringAnimation != null) {
                mSpringAnimation.cancel();
            }
            mSpringAnimation = ValueAnimator.ofObject(new PointEvaluator(),
                    new Point(cur_x, cur_y), new Point(from_x, from_y));
            mSpringAnimation.setDuration(120);
            mSpringAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Point p = (Point) animation.getAnimatedValue();
                    updatePosition(p.getX(), p.getY());
                    //invalidate();
                }
            });

            mSpringAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetView();
                }
            });

            mSpringAnimation.setInterpolator(new OvershootInterpolator(5));
            mSpringAnimation.start();
            postInvalidate();
        }
    }//end inner class

}//end class
