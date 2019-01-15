package com.zzh12138.reboundlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zzh12138.reboundlayout.drag.CircleImageActivity;
import com.zzh12138.reboundlayout.rebound.ReBoundActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.reBound)
    Button reBound;
    @BindView(R.id.drag)
    Button drag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        reBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReBoundActivity.class));
                overridePendingTransition(R.anim.animate_alpha_0_1, 0);
            }
        });

        drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CircleImageActivity.class));
            }
        });
    }
}
