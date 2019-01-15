package com.zzh12138.reboundlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by zhangzhihao on 2019/1/8 16:38.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyHolder> {
    private List<Bean> mList;
    private Context mContext;

    public RVAdapter(List<Bean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_rv, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) myHolder.layout.getLayoutParams();
        if (i == 0) {
            myHolder.layout.setBackgroundResource(R.drawable.bg_corner);
            p.topMargin = 200;
        } else {
            myHolder.layout.setBackgroundResource(R.drawable.bg_normal);
            p.topMargin = 0;
        }
        Glide.with(mContext).load(mList.get(i).getUrl()).into(myHolder.image);
        Glide.with(mContext).load(R.mipmap.ic_launcher).into(myHolder.avatar);
        myHolder.title.setText(mList.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ImageView avatar;
        private TextView title;
        private LinearLayout layout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
