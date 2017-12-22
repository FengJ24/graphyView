package com.ablecloud.graphview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ablecloud.graphview.DensityUtil;

/**
 * Created by fengjian on 2017/7/26.
 */

public class CurveYView extends View {

    // 控件宽高
    private int viewWidth, viewHeigth;

    // 画笔
    Paint grayPaint;
    private int xScaleAccount = 30;  //X轴刻度的数量
    // 每一份的水平、竖直尺寸
    private float averageWidth = 100;
    private float averageHeigth = 80;
    private int mTextSize;


    public CurveYView(Context context) {
        super(context);
        init();
    }

    public CurveYView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurveYView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initMetrics();
        grayPaint = new Paint();
        grayPaint.setColor(Color.parseColor("#d2d2d2"));
        grayPaint.setStrokeWidth(1.5f);

    }

    private void initMetrics() {
        //初始化轴刻度
        averageWidth = DensityUtil.dipToPx(this.getContext().getApplicationContext(), 60);
        //初始化字体大小
        mTextSize = DensityUtil.sp2px(this.getContext().getApplicationContext(), 12);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) (xScaleAccount * averageWidth), h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画Y轴刻度
        drawYCoordinateNumber(canvas);
        super.onDraw(canvas);
    }
    /**
     * 画y轴坐标值
     *
     * @param canvas
     */
    private void drawYCoordinateNumber(Canvas canvas) {
        grayPaint.setTextSize(mTextSize);
        for (int i = 0; i < 10; i++)
            canvas.drawText("" + (i * 10), viewWidth * (13 / 140), (float) (9.9 - i) * averageHeigth, grayPaint);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            initMeasure();
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void initMeasure() {
        viewWidth = getWidth();
        viewHeigth = getHeight();
        averageHeigth = viewHeigth / 11;
        invalidate();
    }

}
