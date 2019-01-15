package com.zzh12138.reboundlayout.rebound;

/**
 * Created by zhangzhihao on 2019/1/8 18:27.
 */
public interface OnBounceDistanceChangeListener {
    int DIRECTION_LEFT = 1;
    int DIRECTION_RIGHT = 2;
    int DIRECTION_UP = 3;
    int DIRECTION_DOWN = 4;

    void onDistanceChange(int distance, int direction);

    void onFingerUp(int distance, int direction);
}
