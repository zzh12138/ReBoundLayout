package com.zzh12138.reboundlayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by zhangzhihao on 2019/1/8 15:54.
 */
public class VPAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mList;

    public VPAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }
}
