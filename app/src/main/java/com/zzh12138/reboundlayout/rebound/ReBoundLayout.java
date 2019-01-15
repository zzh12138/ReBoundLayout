package com.zzh12138.reboundlayout.rebound;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zzh12138.reboundlayout.R;

/**
 * Created by zhangzhihao on 2019/1/8 11:04.
 */
public class ReBoundLayout extends FrameLayout {
    private static final String TAG = "ReBoundLayout";

    private int mTouchSlop;
    private int mDownX;
    private int mDownY;
    private boolean isIntercept;
    private View innerView;
    private float resistance;
    private int orientation;
    private long mDuration;
    private Interpolator mInterpolator;
    private boolean isNeedReset;
    private int resetDistance;
    private OnBounceDistanceChangeListener onBounceDistanceChangeListener;


    public ReBoundLayout(@NonNull Context context) {
        this(context, null);
    }

    public ReBoundLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReBoundLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ReBoundLayout);
        orientation = array.getInt(R.styleable.ReBoundLayout_reBoundOrientation, LinearLayout.HORIZONTAL);
        resistance = array.getFloat(R.styleable.ReBoundLayout_resistance, 3f);
        mDuration = array.getInteger(R.styleable.ReBoundLayout_reBoundDuration, 300);
        if (resistance < 1) {
            resistance = 1f;
        }
        array.recycle();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mInterpolator = new AccelerateDecelerateInterpolator();
        resetDistance = Integer.MAX_VALUE;
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
                int difX = (int) (ev.getX() - mDownX);
                int difY = (int) (ev.getY() - mDownY);
                if (orientation == LinearLayout.HORIZONTAL) {
                    if (Math.abs(difX) > mTouchSlop && Math.abs(difX) > Math.abs(difY)) {
                        ViewParent parent = getParent();
                        while (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                            parent = parent.getParent();
                            isIntercept = true;
                        }
                        if (!innerView.canScrollHorizontally(-1) && difX > 0) {
                            //右拉到边界
                            return true;
                        }
                        if (!innerView.canScrollHorizontally(1) && difX < 0) {
                            //左拉到边界
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
                            //上拉到边界
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
                mDownX = 0;
                mDownY = 0;
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
                if (orientation == LinearLayout.HORIZONTAL) {
                    int difX = (int) ((event.getX() - mDownX) / resistance);
                    boolean isRebound = false;
                    if (!innerView.canScrollHorizontally(-1) && difX > 0) {
                        //右拉到边界
                        isRebound = true;
                    } else if (!innerView.canScrollHorizontally(1) && difX < 0) {
                        //左拉到边界
                        isRebound = true;
                    }
                    if (isRebound) {
                        innerView.setTranslationX(difX);
                        if (onBounceDistanceChangeListener != null) {
                            onBounceDistanceChangeListener.onDistanceChange(Math.abs(difX), difX > 0 ?
                                    OnBounceDistanceChangeListener.DIRECTION_RIGHT : OnBounceDistanceChangeListener.DIRECTION_LEFT);
                        }
                        return true;
                    }
                } else {
                    int difY = (int) ((event.getY() - mDownY) / resistance);
                    boolean isRebound = false;
                    if (!innerView.canScrollVertically(-1) && difY > 0) {
                        //下拉到边界
                        isRebound = true;
                    } else if (!innerView.canScrollVertically(1) && difY < 0) {
                        //上拉到边界
                        isRebound = true;
                    }
                    if (isRebound) {
                        innerView.setTranslationY(difY);
                        if (onBounceDistanceChangeListener != null) {
                            onBounceDistanceChangeListener.onDistanceChange(Math.abs(difY), difY > 0 ?
                                    OnBounceDistanceChangeListener.DIRECTION_DOWN : OnBounceDistanceChangeListener.DIRECTION_UP);
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (orientation == LinearLayout.HORIZONTAL) {
                    int difX = (int) innerView.getTranslationX();
                    if (difX != 0) {
                        if (Math.abs(difX) <= resetDistance || isNeedReset) {
                            innerView.animate().translationX(0).setDuration(mDuration).setInterpolator(mInterpolator);
                        }
                        if (onBounceDistanceChangeListener != null) {
                            onBounceDistanceChangeListener.onFingerUp(Math.abs(difX), difX > 0 ?
                                    OnBounceDistanceChangeListener.DIRECTION_RIGHT : OnBounceDistanceChangeListener.DIRECTION_LEFT);
                        }
                    }
                } else {
                    int difY = (int) innerView.getTranslationY();
                    if (difY != 0) {
                        if (Math.abs(difY) <= resetDistance || isNeedReset) {
                            innerView.animate().translationY(0).setDuration(mDuration).setInterpolator(mInterpolator);
                        }
                        if (onBounceDistanceChangeListener != null) {
                            onBounceDistanceChangeListener.onFingerUp(Math.abs(difY), difY > 0 ?
                                    OnBounceDistanceChangeListener.DIRECTION_DOWN : OnBounceDistanceChangeListener.DIRECTION_UP);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
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

    public void seInnerView(View innerView) {
        this.innerView = innerView;
    }

    public void setResistance(float resistance) {
        this.resistance = resistance;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public void setInterpolator(Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }

    public void setNeedReset(boolean needReset) {
        isNeedReset = needReset;
    }

    public void setResetDistance(int resetDistance) {
        this.resetDistance = resetDistance;
    }

    public void setOnBounceDistanceChangeListener(OnBounceDistanceChangeListener onBounceDistanceChangeListener) {
        this.onBounceDistanceChangeListener = onBounceDistanceChangeListener;
    }
}
