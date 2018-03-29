package com.ablecloud.graphview.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by fengjian on 2017/7/28.
 */

public class MyHorizontalscrollView extends HorizontalScrollView {
    private OnXScrollPositonChangedListener mOnXScrollPositionChange;
    private float mRawY;
    private float moveRawY;
    private OnXScrollPositonChangedListener mOnXScrollPositionChangeListener;
    private final int JUDGE_IS_STOP = 0x10032;

    public MyHorizontalscrollView(Context context) {
        super(context);
    }

    public MyHorizontalscrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalscrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRawY = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveRawY = ev.getRawX();
                if (mOnXScrollPositionChangeListener != null) {
                    mOnXScrollPositionChangeListener.onXScrollDistence(getScrollX());
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.sendEmptyMessageDelayed(JUDGE_IS_STOP, 100);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        super.setOnScrollChangeListener(l);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnXScrollPositionChangeListener != null) {
            mOnXScrollPositionChangeListener.onXScrollDistence(l);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public interface OnXScrollPositonChangedListener {
        void onXScrollDistence(int distence);

        void scrollStop();
    }

    public void setOnXScrollPositionChangeListener(OnXScrollPositonChangedListener onXScrollPositionChangeListener) {
        mOnXScrollPositionChangeListener = onXScrollPositionChangeListener;
    }

    private int lastScrollX;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int scrollX = getScrollX();
            if (lastScrollX == scrollX) {
                //滑动停止
                if (mOnXScrollPositionChangeListener != null) {
                    mOnXScrollPositionChangeListener.scrollStop();
                    this.removeMessages(JUDGE_IS_STOP);
                }
            } else {
                mHandler.sendEmptyMessageDelayed(JUDGE_IS_STOP, 200);
            }
            lastScrollX = scrollX;
            super.handleMessage(msg);
        }
    };
}
