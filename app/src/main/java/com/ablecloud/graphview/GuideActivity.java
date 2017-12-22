package com.ablecloud.graphview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by fengjian on 2017/7/29.
 */

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mCureview;
    private Button mBarview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        mCureview = (Button) findViewById(R.id.cureview);
        mBarview = (Button) findViewById(R.id.barview);

        mCureview.setOnClickListener(this);
        mBarview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cureview:
                startActivity(new Intent(this, CurveActivity.class));
                break;
            case R.id.barview:
                startActivity(new Intent(this, BarActivity.class));
                break;
        }
    }
}
