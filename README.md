### 自定义回弹布局
详情戳[这里](https://www.jianshu.com/p/600380d78779)
##### 效果一：
##### ReBoundLayout
**Step 1**
```
<com.zzh12138.reboundlayout.rebound.ReBoundLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/reBoundLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:resistance="1"
    app:reBoundOrientation="horizontal"
    android:background="#80000000"
    tools:context=".rebound.ReBoundActivity">

    <android.support.v4.view.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</com.zzh12138.reboundlayout.rebound.ReBoundLayout>
```
自定义属性相关：
```
    <declare-styleable name="ReBoundLayout">
        <attr name="reBoundOrientation" format="enum">
            <enum name="horizontal" value="0" />
            <enum name="vertical" value="1" />
        </attr>
        <attr name="resistance" format="float" />
        <attr name="reBoundDuration" format="integer" />
    </declare-styleable>
```
* innerView 移动的View
* resistance 阻力系数
* orientation 回弹方向
* mDuration 回弹时长
* resetDistance 回弹阈值
* isNeedReset 手指抬起时距离大于阈值是否需要回弹

也可调用set方法进行修改
**Step 2**
```
reBoundLayout.setOnBounceDistanceChangeListener(new OnBounceDistanceChangeListener() {
            @Override
            public void onDistanceChange(int distance, int direction) {
                //移动回调
            }

            @Override
            public void onFingerUp(int distance, int direction) {
               //手指松开回调
            }
        });
```
>PS:**Kotlin**请使用 **ReBoundLayoutKt** 类
##### 效果二：
**Step 1**
```
<com.zzh12138.reboundlayout.drag.DragZoomLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#00000000"
    app:interceptOrientation="vertical">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recycler"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</com.zzh12138.reboundlayout.drag.DragZoomLayout>
```
自定义属性相关：
```
    <declare-styleable name="DragZoomLayout">
        <attr name="interceptOrientation" format="enum">
            <enum name="horizontal" value="0" />
            <enum name="vertical" value="1" />
        </attr>
        <attr name="reSetDuration" format="integer" />
    </declare-styleable>
```
* innerView 移动的View
* resistance 阻力系数
* orientation 回弹方向
* mDuration 回弹时长
* resetDistance 回弹阈值
* isNeedReset 手指抬起时距离大于阈值是否需要回弹
* mMinRadius 圆的最小半径
* mMaxRadius 圆的最大半径

也可调用set方法进行修改
**Step 2**
```
        dragLayout.setOnDragDistanceChangeListener(new OnDragDistanceChangeListener() {
            @Override
            public void onDistanceChange(int translationX, int translationY, int direction) {
                //距离改变
            }

            @Override
            public void onFingerUp(int translationX, int translationY, int direction) {
              //手指抬起
            }
        });
```
>PS:**Kotlin**请使用 **DragZoomLayoutKt** 类

# License
MIT
