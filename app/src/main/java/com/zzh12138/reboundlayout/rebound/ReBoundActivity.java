package com.zzh12138.reboundlayout.rebound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.zzh12138.reboundlayout.R;
import com.zzh12138.reboundlayout.Util;
import com.zzh12138.reboundlayout.VPAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReBoundActivity extends AppCompatActivity implements OnBounceDistanceChangeListener {

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.reBoundLayout)
    ReBoundLayout reBoundLayout;
    @BindView(R.id.topTip)
    TextView topTip;
    @BindView(R.id.leftTip)
    TextView leftTip;
    @BindView(R.id.right_tip)
    TextView rightTip;
    private VPAdapter mAdapter;
    private List<Fragment> mList;
    private int mResetDistance;
    private int showTipDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_bound);
        ButterKnife.bind(this);
        mList = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            mList.add(new ReBoundFragment());
        }
        mResetDistance = (int) (Util.getScreenWidth(this) / 3f);
        mAdapter = new VPAdapter(getSupportFragmentManager(), mList);
        viewpager.setAdapter(mAdapter);
        viewpager.setPageMargin(30);
        reBoundLayout.setNeedReset(false);
        reBoundLayout.setResetDistance(mResetDistance);
        reBoundLayout.setOnBounceDistanceChangeListener(this);
        showTipDistance = Util.dipTopx(this, 50);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onDistanceChange(int distance, int direction) {
        switch (direction) {
            case DIRECTION_LEFT:
                if (distance > showTipDistance) {
                    rightTip.setVisibility(View.VISIBLE);
                    rightTip.setTranslationX(showTipDistance - distance);
                    if (distance > mResetDistance) {
                        rightTip.setText("松手关闭");
                    } else {
                        rightTip.setText("继续左滑关闭");
                    }
                } else {
                    rightTip.setVisibility(View.GONE);
                }
                break;
            case DIRECTION_RIGHT:
                if (distance > showTipDistance) {
                    leftTip.setVisibility(View.VISIBLE);
                    leftTip.setTranslationX(distance - showTipDistance);
                    if (distance > mResetDistance) {
                        leftTip.setText("松手关闭");
                    } else {
                        leftTip.setText("继续右滑关闭");
                    }
                } else {
                    leftTip.setVisibility(View.GONE);
                }
                break;
            case DIRECTION_UP:
                break;
            case DIRECTION_DOWN:
                if (distance > showTipDistance) {
                    topTip.setVisibility(View.VISIBLE);
                    topTip.setTranslationY(distance - showTipDistance);
                    if (distance > mResetDistance) {
                        topTip.setText("松手关闭");
                    } else {
                        topTip.setText("继续下拉关闭，松手左右滑切换");
                    }
                } else {
                    topTip.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFingerUp(int distance, int direction) {
        switch (direction) {
            case DIRECTION_LEFT:
                if (distance > mResetDistance) {
                    viewpager.animate().translationXBy(-300).setDuration(50).setInterpolator(new AccelerateInterpolator());
                    viewpager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(0, R.anim.animate_alpha_1_0);
                        }
                    }, 50);
                } else {
                    rightTip.setTranslationX(0);
                    rightTip.setText("继续左滑关闭");
                    rightTip.setVisibility(View.GONE);
                }
                break;
            case DIRECTION_RIGHT:
                if (distance > mResetDistance) {
                    viewpager.animate().translationXBy(300).setDuration(50).setInterpolator(new AccelerateInterpolator());
                    viewpager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(0, R.anim.animate_alpha_1_0);
                        }
                    }, 50);
                } else {
                    leftTip.setTranslationX(0);
                    leftTip.setText("继续右滑关闭");
                    leftTip.setVisibility(View.GONE);
                }
                break;
            case DIRECTION_DOWN:
                if (distance > mResetDistance) {
                    viewpager.animate().translationYBy(300).setDuration(50).setInterpolator(new AccelerateInterpolator());
                    viewpager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(0, R.anim.animate_alpha_1_0);
                        }
                    }, 50);
                } else {
                    topTip.setTranslationX(0);
                    topTip.setText("继续下拉关闭，松手左右滑切换");
                    topTip.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }
}
