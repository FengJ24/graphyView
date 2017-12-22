package com.ablecloud.graphview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ablecloud.graphview.widget.BarView;
import com.ablecloud.graphview.widget.MyHorizontalscrollView;

/**
 * Created by fengjian on 2017/7/29.
 */

  public class BarActivity extends AppCompatActivity {
    private MyHorizontalscrollView mHorizontalScrollView;
    private BarView mBarView;

    @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_bar);
       mHorizontalScrollView = (MyHorizontalscrollView) findViewById(R.id.graph_scrollView);
        mBarView = (BarView) findViewById(R.id.barview);

//        mHorizontalScrollView.post(new Runnable() {
//            @Override
//            public void run() {
////                mHorizontalScrollView.scrollTo(2500,320);
//            }
//        });
//
        mBarView.setOnPointXLocationlistener(new BarView.OnBarPointXLocationlistener() {
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
               mBarView.tellDistence(distence);
           }

           @Override
           public void scrollStop() {
               mBarView.stopScroll();
           }
       });
   }
}
