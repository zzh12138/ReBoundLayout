package com.zzh12138.reboundlayout.drag;

/**
 * Created by zhangzhihao on 2019/1/10 15:10.
 */
public interface OnDragDistanceChangeListener {
    void onDistanceChange(int translationX, int translationY, int direction);

    void onFingerUp(int translationX, int translationY, int direction);
}
