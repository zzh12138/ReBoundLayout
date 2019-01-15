package com.zzh12138.reboundlayout.rebound;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzh12138.reboundlayout.Bean;
import com.zzh12138.reboundlayout.Data;
import com.zzh12138.reboundlayout.R;
import com.zzh12138.reboundlayout.RVAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzhihao on 2019/1/8 16:09.
 */
public class ReBoundFragment extends Fragment implements OnBounceDistanceChangeListener {
    private View mView;
    private RecyclerView mRecycler;
    private List<Bean> mList;
    private RVAdapter mAdapter;
    private ReBoundLayout mLayout;
    private OnBounceDistanceChangeListener onBounceDistanceChangeListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReBoundActivity) {
            onBounceDistanceChangeListener = (OnBounceDistanceChangeListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_rebound, container, false);
            mRecycler = mView.findViewById(R.id.recycler);
            mLayout = mView.findViewById(R.id.layout);
            mList = new ArrayList<>(10);
            for (int i = 0; i < 10; i++) {
                mList.add(new Bean(Data.title[i] + i, Data.image[i]));
            }
            mAdapter = new RVAdapter(mList, getContext());
            mRecycler.setAdapter(mAdapter);
            mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mLayout.setOnBounceDistanceChangeListener(this);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
        return mView;
    }

    @Override
    public void onDistanceChange(int distance, int direction) {
        if (onBounceDistanceChangeListener != null) {
            onBounceDistanceChangeListener.onDistanceChange(distance, direction);
        }
    }

    @Override
    public void onFingerUp(int distance, int direction) {
        if (onBounceDistanceChangeListener != null) {
            onBounceDistanceChangeListener.onFingerUp(distance, direction);
        }
    }
}
