package com.zzh12138.reboundlayout.drag

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.zzh12138.reboundlayout.R
import com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.*
import java.lang.IllegalArgumentException

/**
 * Created by zhangzhihao on 2019/1/12 16:52.
 */
class DragZoomLayoutKt @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var mTouchSlop = 0
    var mDownX = 0
    var mDownY = 0
    var isIntercept = false
    var innerView: View? = null
    var resistance = 1.0f
    var orientation = LinearLayout.HORIZONTAL
    var mInterpolator = LinearInterpolator()
    var resetDistance = 0
    var onDragDistanceChangeListener: OnDragDistanceChangeListener? = null
    var mMinRadius = 0
    var mMaxRadius = 0
    var mRadius = 0
    var mPath = Path()
    var mTranslationX = 0
    var mTranslationY = 0
    var mLargeX = 0
    var mLargeY = 0
    var mPoint = Point()
    var mDuration = 0

    init {
        if (attrs != null) {
            val arr = context.obtainStyledAttributes(attrs, R.styleable.DragZoomLayoutKt)
            orientation = arr.getInt(R.styleable.DragZoomLayoutKt_interceptOrientationKt, LinearLayout.HORIZONTAL)
            mDuration = arr.getInteger(R.styleable.DragZoomLayoutKt_reSetDurationKt, 300)
            arr.recycle()
            mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
            resetDistance = Int.MAX_VALUE
            resistance = 1f
        }
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
                    val difX = event.x - mDownX
                    val difY = event.y - mDownY
                    if (orientation == LinearLayout.HORIZONTAL) {
                        var needDrag = false
                        if (!it.canScrollHorizontally(-1) && difX > 0) {
                            //右啦到边界
                            needDrag = true
                        }
                        if (!it.canScrollHorizontally(1) && difX < 0) {
                            //左拉到边界
                            needDrag = true
                        }
                        if (needDrag) {
                            mRadius = mMaxRadius * (1 - Math.abs(difX) / mLargeX).toInt()
                            limitRadius()
                            mTranslationX = difX.toInt()
                            mTranslationY = difY.toInt()
                            invalidate()
                            onDragDistanceChangeListener?.onDistanceChange(mTranslationX, mTranslationY,
                                    if (mTranslationX > 0) DIRECTION_RIGHT else DIRECTION_LEFT)
                            return true
                        }else{

                        }
                    } else {
                        if (!it.canScrollVertically(-1) && difY > 0) {
                            //下拉到边界
                            onDragDistanceChangeListener?.onDistanceChange(difX.toInt(), difY.toInt(), DIRECTION_DOWN)
                            return true
                        }else{

                        }
                        if (!it.canScrollVertically(1) && difY > 0) {
                            //上啦到边界
                            it.translationY = difY
                            return true
                        }else{

                        }
                    }
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    if (orientation == LinearLayout.HORIZONTAL) {
                        if (Math.abs(mTranslationX) < resetDistance) {
                            //重置状态
                            finishAnimate(0, 0, mMaxRadius)
                        }
                        onDragDistanceChangeListener?.onFingerUp(mTranslationX, mTranslationY,
                                if (mTranslationX > 0) DIRECTION_RIGHT else DIRECTION_LEFT)
                    } else {
                        if (it.translationY < 0) {
                            it.animate().setDuration(mDuration.toLong()).translationY(0f).setInterpolator(mInterpolator)
                        } else {
                            val x = (event.x - mDownX) / resistance
                            val y = (event.y - mDownY) / resistance
                            onDragDistanceChangeListener?.onFingerUp(x.toInt(), y.toInt(), DIRECTION_DOWN)
                        }
                    }
                }
                else -> {
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun limitRadius() {
        if (mRadius > mMaxRadius) {
            mRadius = mMaxRadius
        } else if (mRadius < mMinRadius) {
            mRadius = mMinRadius
        }
    }

    fun finishAnimate(endX: Int, endY: Int, endRadius: Int) {
        val animateX = PropertyValuesHolder.ofInt("mTranslationX", mTranslationX, endX)
        val animateY = PropertyValuesHolder.ofInt("mTranslationY", mTranslationY, endY)
        val animateRadius = PropertyValuesHolder.ofInt("radius", mRadius, endRadius)
        val animator = ValueAnimator.ofPropertyValuesHolder(animateX, animateY, animateRadius)
        animator.duration = mDuration.toLong()
        animator.interpolator = mInterpolator
        animator.addUpdateListener {
            mTranslationX = it.getAnimatedValue("mTranslationX") as Int
            mTranslationY = it.getAnimatedValue("mTranslationY") as Int
            mRadius = it.getAnimatedValue("radius") as Int
            postInvalidate()
        }
        animator.start()
    }

    fun changeTranslationAndSize(translationX: Int, translationY: Int) {
        mTranslationX = translationX
        mTranslationY = translationY
        mRadius = (mMaxRadius * (1 - Math.abs(translationY).toFloat() / mLargeY)).toInt()
        limitRadius()
        postInvalidate()
    }

    fun enterAnimate(translationX: Int, translationY: Int) {
        mTranslationX = translationX
        mTranslationY = translationY
        mRadius = mMinRadius
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = Math.sqrt(Math.pow(w.toDouble() / 2, 2.0) + Math.pow(h.toDouble() / 2, 2.0)).toInt()
        mMaxRadius = mRadius
        mLargeX = w / 2 - mMinRadius
        mLargeY = h / 2 - mMinRadius
        mPoint.x = w / 2
        mPoint.y = h / 2
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            if (Math.abs(mTranslationX) > mLargeX) {
                mTranslationX = if (mTranslationX > 0) mLargeX else -mLargeX
            }
            if (Math.abs(mTranslationY) > mLargeY) {
                mTranslationY = if (mTranslationY > 0) mLargeY else -mLargeY
            }
            canvas.translate(mTranslationX.toFloat(), mTranslationY.toFloat())
            mPath.reset()
            mPath.addCircle(mPoint.x.toFloat(), mPoint.y.toFloat(), mRadius.toFloat(), Path.Direction.CCW)
            canvas.clipPath(mPath)
        }
        super.onDraw(canvas)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if(childCount>0){
            innerView=getChildAt(0)
        }else{
            throw IllegalArgumentException("it must have innerView")
        }
    }



}