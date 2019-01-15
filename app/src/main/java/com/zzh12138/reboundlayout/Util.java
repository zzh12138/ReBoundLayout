package com.zzh12138.reboundlayout;

import android.content.Context;

/**
 * Created by zhangzhihao on 2019/1/9 15:06.
 */
public class Util {
    public static int dipTopx(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
