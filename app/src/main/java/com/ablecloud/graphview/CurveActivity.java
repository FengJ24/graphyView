package com.ablecloud.graphview;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ablecloud.graphview.widget.CurveView;
import com.ablecloud.graphview.widget.MyHorizontalscrollView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        Gson gson = new Gson();
        TdsInfo tdsInfo = null;
        try {
            String tdsData = getTdsData();
            tdsInfo = gson.fromJson(tdsData, TdsInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurvechat.setCureData(tdsInfo.getTdsBean());


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
        mHorizontalScrollView.setOnXScrollPositionChangeListener(new MyHorizontalscrollView.OnXScrollPositonChangedListener()

        {
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


    public String getTdsData() throws IOException {
        InputStreamReader streamReader = new InputStreamReader(CurveActivity.this.getAssets().open("tds.json"), "UTF-8");
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        streamReader.close();
        return stringBuilder.toString();
    }


}
