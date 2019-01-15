package com.zzh12138.reboundlayout.drag;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zzh12138.reboundlayout.R;

import static com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.DIRECTION_DOWN;
import static com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.DIRECTION_LEFT;
import static com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.DIRECTION_RIGHT;
import static com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.DIRECTION_UP;

/**
 * Created by zhangzhihao on 2019/1/9 18:00.
 */
public class DragZoomLayout extends FrameLayout {
    private static final String TAG = "DragZoomLayout";

    private int mTouchSlop;
    private int mDownX;
    private int mDownY;
    private boolean isIntercept;
    private View innerView;
    private float resistance;
    private int orientation;
    private long mDuration;
    private Interpolator mInterpolator;
    private int resetDistance;
    private OnDragDistanceChangeListener onDragDistanceChangeListener;
    private int mMinRadius;
    private int mMaxRadius;
    private int mRadius;
    private Path mPath;
    private int mTranslationX;
    private int mTranslationY;
    private int mLargeX;
    private int mLargeY;
    private Point mPoint;

    public DragZoomLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DragZoomLayout);
        orientation = array.getInt(R.styleable.DragZoomLayout_interceptOrientation, LinearLayout.HORIZONTAL);
        mDuration = array.getInteger(R.styleable.DragZoomLayout_reSetDuration, 300);
        array.recycle();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mInterpolator = new LinearInterpolator();
        resetDistance = Integer.MAX_VALUE;
        resistance = 1f;
        mPath = new Path();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (innerView != null) {
                    innerView.clearAnimation();
                }
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int difX = (int) ev.getX() - mDownX;
                int difY = (int) ev.getY() - mDownY;
                if (orientation == LinearLayout.HORIZONTAL) {
                    if (Math.abs(difX) > mTouchSlop && Math.abs(difX) > Math.abs(difY)) {
                        ViewParent parent = getParent();
                        while (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                            parent = parent.getParent();
                            isIntercept = true;
                        }
                        if (!innerView.canScrollHorizontally(-1) && difX > 0) {
                            //右啦到边界
                            return true;
                        }
                        if (!innerView.canScrollHorizontally(1) && difX < 0) {
                            //左啦到边界
                            return true;
                        }
                    }
                } else {
                    if (Math.abs(difY) > mTouchSlop && Math.abs(difY) > Math.abs(difX)) {
                        ViewParent parent = getParent();
                        while (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                            parent = parent.getParent();
                            isIntercept = true;
                        }
                        if (!innerView.canScrollVertically(-1) && difY > 0) {
                            //下拉到边界
                            return true;
                        }
                        if (!innerView.canScrollVertically(1) && difY < 0) {
                            //上啦到边界
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isIntercept) {
                    ViewParent parent = getParent();
                    while (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                        parent = parent.getParent();
                    }
                }
                isIntercept = false;
                mDownY = 0;
                mDownX = 0;
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                int difX = (int) ((event.getX() - mDownX) / resistance);
                int difY = (int) ((event.getY() - mDownY) / resistance);
                if (orientation == LinearLayout.HORIZONTAL) {
                    boolean needDrag = false;
                    if (!innerView.canScrollHorizontally(-1) && difX > 0) {
                        //右啦到边界
                        needDrag = true;
                    } else if (!innerView.canScrollHorizontally(1) && difX < 0) {
                        //左拉到边界
                        needDrag = true;
                    }
                    if (needDrag) {
                        mRadius = (int) (mMaxRadius * (1 - Math.abs(difX) * 1f / mLargeX));
                        if (mRadius < mMinRadius) {
                            mRadius = mMinRadius;
                        } else if (mRadius > mMaxRadius) {
                            mRadius = mMaxRadius;
                        }
                        mTranslationX = difX;
                        mTranslationY = difY;
                        invalidate();
                        if (onDragDistanceChangeListener != null) {
                            onDragDistanceChangeListener.onDistanceChange(mTranslationX, mTranslationY, mTranslationX > 0 ? DIRECTION_RIGHT : DIRECTION_LEFT);
                        }
                        return true;
                    }
                } else {
                    if (!innerView.canScrollVertically(-1) && difY > 0) {
                        //下拉到边界
                        if (onDragDistanceChangeListener != null) {
                            onDragDistanceChangeListener.onDistanceChange(difX, difY, DIRECTION_DOWN);
                        }
                        return true;
                    } else if (!innerView.canScrollVertically(1) && difY < 0) {
                        //上啦到边界
                        innerView.setTranslationY(difY);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (orientation == LinearLayout.HORIZONTAL) {
                    //水平
                    if (Math.abs(mTranslationX) <= resetDistance) {
                        //重置状态
                        finishAnimate(0, 0, mMaxRadius);
                    }
                    if (onDragDistanceChangeListener != null) {
                        onDragDistanceChangeListener.onFingerUp(mTranslationX, mTranslationY, mTranslationX > 0 ? DIRECTION_RIGHT : DIRECTION_LEFT);
                    }
                } else {
                    //竖直
                    if (innerView.getTranslationY() < 0) {
                        innerView.animate().setDuration(mDuration).translationY(0).setInterpolator(mInterpolator);
                    } else {
                        int x = (int) ((event.getX() - mDownX) / resistance);
                        int y = (int) ((event.getY() - mDownY) / resistance);
                        if (onDragDistanceChangeListener != null) {
                            onDragDistanceChangeListener.onFingerUp(x, y, y > 0 ? DIRECTION_DOWN : DIRECTION_UP);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    public void finishAnimate(int endX, int endY, int endRadius) {
        PropertyValuesHolder animateX = PropertyValuesHolder.ofInt("mTranslationX", mTranslationX, endX);
        PropertyValuesHolder animateY = PropertyValuesHolder.ofInt("mTranslationY", mTranslationY, endY);
        PropertyValuesHolder animateRadius = PropertyValuesHolder.ofInt("radius", mRadius, endRadius);
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(animateX, animateY, animateRadius);
        animator.setDuration(mDuration);
        animator.setInterpolator(mInterpolator);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTranslationX = (int) animation.getAnimatedValue("mTranslationX");
                mTranslationY = (int) animation.getAnimatedValue("mTranslationY");
                mRadius = (int) animation.getAnimatedValue("radius");
                postInvalidate();
            }
        });
        animator.start();
    }

    public void changeTranslationAndSize(int translationX, int translationY) {
        mTranslationX = translationX;
        mTranslationY = translationY;
        mRadius = (int) (mMaxRadius * (1 - Math.abs(translationY) * 1f / mLargeY));
        if (mRadius < mMinRadius) {
            mRadius = mMinRadius;
        } else if (mRadius > mMaxRadius) {
            mRadius = mMaxRadius;
        }
        postInvalidate();
    }

    public void enterAnimate(int translationX, int translationY) {
        mTranslationX = translationX;
        mTranslationY = translationY;
        mRadius = mMinRadius;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = mMaxRadius = (int) Math.sqrt(Math.pow(w / 2f, 2) + Math.pow(h / 2f, 2));
        mLargeX = (int) (w / 2f - mMinRadius);
        mLargeY = (int) (h / 2f - mMinRadius);
        mPoint = new Point((int) (w / 2f), (int) (h / 2f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Math.abs(mTranslationX) > mLargeX) {
            mTranslationX = mTranslationX > 0 ? mLargeX : -mLargeX;
        }
        if (Math.abs(mTranslationY) > mLargeY) {
            mTranslationY = mTranslationY > 0 ? mLargeY : -mLargeY;
        }
        canvas.translate(mTranslationX, mTranslationY);
        mPath.reset();
        mPath.addCircle(mPoint.x, mPoint.y, mRadius, Path.Direction.CCW);
        canvas.clipPath(mPath);
        super.onDraw(canvas);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            innerView = getChildAt(0);
        } else {
            throw new IllegalArgumentException("it must have innerView");
        }
    }

    public void setInnerView(View innerView) {
        this.innerView = innerView;
    }

    public void setOnDragDistanceChangeListener(OnDragDistanceChangeListener
                                                        onDragDistanceChangeListener) {
        this.onDragDistanceChangeListener = onDragDistanceChangeListener;
    }

    public void setMinRadius(int mMinRadius) {
        this.mMinRadius = mMinRadius;
    }

    public void setResistance(float resistance) {
        if (resistance < 1f) {
            resistance = 1f;
        }
        this.resistance = resistance;
    }

    public void setResetDistance(int resetDistance) {
        this.resetDistance = resetDistance;
    }

    public int getMinRadius() {
        return mMinRadius;
    }

    public long getDuration() {
        return mDuration;
    }

    public int getLargeX() {
        return mLargeX;
    }

    public int getLargeY() {
        return mLargeY;
    }

    public int getResetDistance() {
        return resetDistance;
    }

    public int getMaxRadius() {
        return mMaxRadius;
    }
}
