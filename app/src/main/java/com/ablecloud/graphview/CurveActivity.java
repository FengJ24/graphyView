package com.ablecloud.graphview;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ablecloud.graphview.widget.CurveView;
import com.ablecloud.graphview.widget.MyHorizontalscrollView;

import java.util.ArrayList;

public class CurveActivity extends AppCompatActivity {
    private ArrayList<PointF> mPointArrayList = new ArrayList<>();
    private MyHorizontalscrollView mHorizontalScrollView;
    private CurveView mCurvechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve);
        mHorizontalScrollView = (MyHorizontalscrollView) findViewById(R.id.graph_scrollView);
        mCurvechat = (CurveView) findViewById(R.id.curveChart);

//        mHorizontalScrollView.post(new Runnable() {
//            @Override
//            public void run() {
////                mHorizontalScrollView.scrollTo(2500,320);
//            }
//        });
//
        mCurvechat.setOnPointXLocationlistener(new CurveView.OnPointXLocationlistener() {
            @Override
            public void pointXLocation(final int xLocation) {
                mHorizontalScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mHorizontalScrollView.scrollTo(xLocation, 0);
                    }
                });
            }
        });
        mHorizontalScrollView.setOnXScrollPositionChangeListener(new MyHorizontalscrollView.OnXScrollPositonChangedListener() {
            @Override
            public void onXScrollDistence(int distence) {
                mCurvechat.tellDistence(distence);
            }

            @Override
            public void scrollStop() {
                mCurvechat.stopScroll();
            }
        });
    }
}
