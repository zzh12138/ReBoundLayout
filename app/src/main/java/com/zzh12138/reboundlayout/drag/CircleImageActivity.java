package com.zzh12138.reboundlayout.drag;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zzh12138.reboundlayout.Data;
import com.zzh12138.reboundlayout.R;
import com.zzh12138.reboundlayout.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleImageActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;

    private int[] location = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_image_acitivy);
        ButterKnife.bind(this);
        Glide.with(this).load(Data.image[0]).apply(new RequestOptions().circleCrop()).into(image);
        image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                image.getLocationOnScreen(location);
                return true;
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CircleImageActivity.this, DragActivity.class);
                Point point = new Point(location[0]
                        , location[1]);
                intent.putExtra("point", point);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
