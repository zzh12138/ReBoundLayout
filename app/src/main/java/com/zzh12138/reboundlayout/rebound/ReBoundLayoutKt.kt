package com.zzh12138.reboundlayout.rebound

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.zzh12138.reboundlayout.R
import com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.*
import java.lang.IllegalArgumentException

/**
 * Created by zhangzhihao on 2019/1/12 16:53.
 */
class ReBoundLayoutKt @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var mTouchSlop = 0
    var mDownX = 0
    var mDownY = 0
    var isIntercept = false
    var innerView: View? = null
    var resistance = 1f
    var orientation = LinearLayout.HORIZONTAL
    var mDuration = 0L
    var mInterpolator = AccelerateDecelerateInterpolator()
    var isNeedReset = false
    var resetDistance = Int.MAX_VALUE
    var onBounceDistanceChangeListener: OnBounceDistanceChangeListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ReBoundLayoutKt)
        orientation = array.getInt(R.styleable.ReBoundLayoutKt_reBoundOrientationKt, LinearLayout.HORIZONTAL)
        resistance = array.getFloat(R.styleable.ReBoundLayoutKt_resistanceKt, 1f)
        mDuration = array.getInt(R.styleable.ReBoundLayoutKt_reBoundOrientationKt, 300).toLong()
        array.recycle()
        if (resistance < 1f) {
            resistance = 1f
        }
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        innerView?.let {
            when (ev?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    it.clearAnimation()
                    mDownX = ev.x.toInt()
                    mDownY = ev.x.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val difX = ev.x - mDownX
                    val difY = ev.y - mDownY
                    if (orientation == LinearLayout.HORIZONTAL) {
                        if (Math.abs(difX) > mTouchSlop && Math.abs(difX) > Math.abs(difY)) {
                            var parent = parent
                            while (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true)
                                parent = parent.parent
                                isIntercept = true
                            }
                            if (!it.canScrollHorizontally(-1) && difX > 0) {
                                //右啦到边界
                                return true
                            }
                            if (!it.canScrollHorizontally(1) && difX < 0) {
                                //左拉到边界
                                return true
                            }
                        }
                    } else {
                        if (Math.abs(difY) > mTouchSlop && Math.abs(difY) > Math.abs(difX)) {
                            var parent = parent
                            while (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true)
                                parent = parent.parent
                                isIntercept = true
                            }
                            if (!it.canScrollVertically(-1) && difY > 0) {
                                //下拉到边界
                                return true
                            }
                            if (!it.canScrollVertically(1) && difY > 0) {
                                //上啦到边界
                                return true
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    if (isIntercept) {
                        var parent = parent
                        while (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false)
                            parent = parent.parent
                        }
                    }
                    isIntercept = false
                    mDownY = 0
                    mDownX = 0
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        innerView?.let {
            when (event?.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    if (orientation == LinearLayout.HORIZONTAL) {
                        val difX = event.x - mDownX / resistance
                        var isRebound = false
                        if (!it.canScrollHorizontally(-1) && difX > 0) {
                            //右啦到边界
                            isRebound = true
                        } else if (!it.canScrollHorizontally(1) && difX < 0) {
                            //左拉到边界
                            isRebound = true
                        }
                        if (isRebound) {
                            it.translationX = difX
                            onBounceDistanceChangeListener?.onDistanceChange(Math.abs(difX).toInt(),
                                    if (difX > 0) DIRECTION_RIGHT else DIRECTION_LEFT)
                            return true
                        }
                    } else {
                        val difY = event.x - mDownY
                        var isRebound = false
                        if (!it.canScrollVertically(-1) && difY > 0) {
                            //下拉到边界
                            isRebound = true
                        } else if (!it.canScrollVertically(1) && difY < 0) {
                            isRebound = true
                        }
                        if (isRebound) {
                            it.translationY = difY
                            onBounceDistanceChangeListener?.onDistanceChange(Math.abs(difY).toInt(),
                                    if (difY > 0) DIRECTION_DOWN else DIRECTION_UP)
                            return true
                        }
                    }
                }
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    if (orientation == LinearLayout.HORIZONTAL) {
                        val difX = it.translationX
                        if (difX != 0f) {
                            if (Math.abs(difX) < resetDistance || isNeedReset) {
                                it.animate().translationX(0f).setDuration(mDuration).interpolator = mInterpolator
                            }
                            onBounceDistanceChangeListener?.onFingerUp(Math.abs(difX).toInt(),
                                    if (difX > 0) DIRECTION_RIGHT else DIRECTION_LEFT)
                        }
                    } else {
                        val difY = it.translationY
                        if(difY!=0f){
                            if(Math.abs(difY)<resetDistance||isNeedReset){
                                it.animate().translationY(0f).setDuration(mDuration).interpolator=mInterpolator
                            }
                            onBounceDistanceChangeListener?.onFingerUp(Math.abs(difY).toInt(),
                                    if(difY>0) DIRECTION_DOWN else DIRECTION_UP)
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if(childCount>0){
            innerView=getChildAt(0)
        }else{
            throw  IllegalArgumentException("it must have innerView")
        }
    }
}