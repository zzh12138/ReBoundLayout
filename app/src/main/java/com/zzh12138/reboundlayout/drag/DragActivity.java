package com.zzh12138.reboundlayout.drag;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;

import com.zzh12138.reboundlayout.R;
import com.zzh12138.reboundlayout.Util;
import com.zzh12138.reboundlayout.VPAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.DIRECTION_DOWN;
import static com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.DIRECTION_LEFT;
import static com.zzh12138.reboundlayout.rebound.OnBounceDistanceChangeListener.DIRECTION_RIGHT;

public class DragActivity extends AppCompatActivity implements OnDragDistanceChangeListener {
    private static final String TAG = "DragActivity";

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.dragLayout)
    DragZoomLayout dragLayout;
    private List<Fragment> mList;
    private VPAdapter mAdapter;

    private Point point;
    private int[] location = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        ButterKnife.bind(this);
        mList = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            mList.add(new DragFragment());
        }
        mAdapter = new VPAdapter(getSupportFragmentManager(), mList);
        viewpager.setAdapter(mAdapter);
        viewpager.setPageMargin(Util.dipTopx(this, 20));
        dragLayout.setMinRadius(Util.dipTopx(this, 25));
        dragLayout.setResistance(2f);
        dragLayout.setResetDistance(Util.getScreenWidth(this) / 5);
        dragLayout.setOnDragDistanceChangeListener(this);
        point = getIntent().getParcelableExtra("point");
        dragLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                dragLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                dragLayout.getLocationOnScreen(location);
                dragLayout.enterAnimate((point.x - (location[0] + dragLayout.getWidth() / 2 - dragLayout.getMinRadius())),
                        point.y - (location[1] + dragLayout.getHeight() / 2 - dragLayout.getMinRadius()));
                dragLayout.finishAnimate(0, 0, dragLayout.getMaxRadius());
                //修改背景透明度
                ValueAnimator animator = ValueAnimator.ofInt(0, 128);
                animator.setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        dragLayout.setBackgroundColor(Color.argb((Integer) animation.getAnimatedValue(), 0, 0, 0));
                    }
                });
                animator.start();
                return true;
            }
        });
    }

    @Override
    public void onDistanceChange(int translationX, int translationY, int direction) {
        switch (direction) {
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                float percent = Math.abs(translationX * 1f) / dragLayout.getLargeX();
                if (percent > 1) {
                    percent = 1;
                }
                int a = (int) (128 * (1 - percent));
                dragLayout.setBackgroundColor(Color.argb(a, 0, 0, 0));
                break;
            case DIRECTION_DOWN:
                float p = Math.abs(translationY * 1f) / dragLayout.getLargeY();
                if (p > 1) {
                    p = 1;
                }
                int alpha = (int) (128 * (1 - p));
                dragLayout.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
                dragLayout.changeTranslationAndSize(translationX, translationY);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFingerUp(int translationX, int translationY, int direction) {
        switch (direction) {
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                if (translationX > dragLayout.getResetDistance()) {
                    dragLayout.finishAnimate((point.x - (location[0] + dragLayout.getWidth() / 2 - dragLayout.getMinRadius())),
                            point.y - (location[1] + dragLayout.getHeight() / 2 - dragLayout.getMinRadius()), dragLayout.getMinRadius());
                    dragLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, dragLayout.getDuration());
                }
                break;
            case DIRECTION_DOWN:
                if (translationY > dragLayout.getResetDistance() * 2) {
                    dragLayout.finishAnimate((point.x - (location[0] + dragLayout.getWidth() / 2 - dragLayout.getMinRadius())),
                            point.y - (location[1] + dragLayout.getHeight() / 2 - dragLayout.getMinRadius()), dragLayout.getMinRadius());
                    dragLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, dragLayout.getDuration());
                } else {
                    dragLayout.finishAnimate(0, 0, dragLayout.getMaxRadius());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.animate_alpha_1_0);
    }
}
