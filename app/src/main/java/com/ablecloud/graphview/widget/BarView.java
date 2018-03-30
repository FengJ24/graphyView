package com.ablecloud.graphview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.ablecloud.graphview.DensityUtil;
import com.ablecloud.graphview.R;

/**
 * Created by fengjian on 2017/7/26.
 */

public class BarView extends View {

    // 控件宽高
    private int viewWidth, viewHeigth;

    // 画笔
    Paint grayPaint, bluePaint, blackPaint, whitePaint;
    private String[] weekDay = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    private int xScaleAccount = 30;  //X轴刻度的数量
    private int yLineAccount;   //Y轴横线的数量
    // 每一份的水平、竖直尺寸
    private float averageWidth;
    private float averageHeigth;
    /**
     * 曲线上总点数
     */
    private PointF[] mPoints;
    private Paint rawWaterPaint;
    private Paint cleanWaterPaint;
    private Paint rawWaterBgPaint;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScrollDistence;
    private int totalOffset;
    private int mWidthPixels;
    private OnBarPointXLocationlistener mOnPointXLocationlistener;
    private Bitmap mBitmap;
    private int middlePointLocation = 0;
    private int mHeightPixels;
    private Paint blueChoosePaint;
    private int mTextSize;
    private int mTextValueSize;
    private Paint blueChooseValuePaint;
    private float mDipToPxFloat;

    public BarView(Context context) {
        super(context);
        init();
    }

    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initMetrics();
        grayPaint = new Paint();
        grayPaint.setColor(Color.parseColor("#d2d2d2"));
        grayPaint.setStrokeWidth(1.5f);

        bluePaint = new Paint();
        bluePaint.setColor(Color.parseColor("#A9E0FF"));
        bluePaint.setAntiAlias(true);
        bluePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        blueChoosePaint = new Paint();
        blueChoosePaint.setColor(Color.parseColor("#27B1FF"));
        blueChoosePaint.setAntiAlias(true);
        blueChoosePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        blueChooseValuePaint = new Paint();
        blueChooseValuePaint.setColor(Color.parseColor("#27B1FF"));
        blueChooseValuePaint.setTextSize(mTextValueSize);
        blueChooseValuePaint.setStrokeWidth(mDipToPxFloat);
        blueChooseValuePaint.setStyle(Paint.Style.FILL);

        blackPaint = new Paint();
        blackPaint.setColor(Color.parseColor("#000000"));
        blackPaint.setStrokeWidth(1.0f);

        whitePaint = new Paint();
        whitePaint.setColor(Color.parseColor("#ffffff"));
        whitePaint.setStrokeWidth(2.5f);

        mBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_round);

    }

    private void initMetrics() {
        //屏幕宽高
        mWidthPixels = this.getResources().getDisplayMetrics().widthPixels;
        mHeightPixels = this.getResources().getDisplayMetrics().heightPixels;
        //初始化轴刻度
        averageWidth = DensityUtil.dipToPx(this.getContext().getApplicationContext(), 60);
        //初始化字体
        mTextSize = DensityUtil.sp2px(this.getContext().getApplicationContext(), 12);
        //初始化值的字体
        mTextValueSize = DensityUtil.sp2px(this.getContext().getApplicationContext(), 22);
        //初始化画笔的宽度
        mDipToPxFloat = DensityUtil.dipToPxFloat(this.getContext().getApplicationContext(), 1f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) (xScaleAccount * averageWidth), h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画背景
        drawGrayBackground(canvas);
        //画水平横线
        drawYCoordinateLine(canvas);
        //画x轴坐标
        drawXCoordinateText(canvas);
        //画柱状图
        draBarGraph(canvas);
        //画柱状图数值
        drawBarValue(canvas);
        super.onDraw(canvas);
    }

    /**
     * 话柱状图
     *
     * @param canvas
     */
    private void draBarGraph(Canvas canvas) {
        if (mPoints != null) {
            for (int i = 0; i < mPoints.length; i++) {
                if (i == middlePointLocation) {
                    canvas.drawRect((0.45f + i) * averageWidth, mPoints[i].y, (0.85f + i) * averageWidth, 10 * averageHeigth, blueChoosePaint);
                } else {
                    canvas.drawRect((0.45f + i) * averageWidth, mPoints[i].y, (0.85f + i) * averageWidth, 10 * averageHeigth, bluePaint);
                }
            }
        }
    }

    private void drawBarValue(Canvas canvas) {
        if (mBitmap != null && mPoints != null) {
            canvas.drawBitmap(mBitmap, mWidthPixels / 2 + mScrollDistence - mBitmap.getWidth() / 2, mPoints[middlePointLocation].y - averageHeigth - mBitmap.getHeight() / 2, whitePaint);
            canvas.drawText(mPoints[middlePointLocation].y + "L", mWidthPixels / 2 + mScrollDistence - mBitmap.getWidth() / 2, mPoints[middlePointLocation].y - averageHeigth, blueChooseValuePaint);
        }
    }


    /**
     * 画灰、白色背景
     */
    private void drawGrayBackground(Canvas canvas) {
        grayPaint.setStyle(Paint.Style.FILL);
        whitePaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, viewHeigth * (10f / 11), viewWidth, viewHeigth, whitePaint); // 白色背景

    }


    /**
     * 画Y坐标上横线
     *
     * @param canvas
     */
    private void drawYCoordinateLine(Canvas canvas) {
        for (int i = 1; i < 10; i++)
            canvas.drawLine(0, averageHeigth * i, viewWidth, averageHeigth * i, grayPaint);
    }

    /**
     * 画x坐标文字
     *
     * @param canvas
     */
    private void drawXCoordinateText(Canvas canvas) {
        grayPaint.setTextSize(mTextSize);
        for (int i = 0; i < xScaleAccount; i++)
            canvas.drawText("7." + i + "", (0.5f + i) * averageWidth, 10.7f * averageHeigth, grayPaint);
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
        //确定点的位置
        mPoints = new PointF[xScaleAccount];
        for (int i = 0; i < mPoints.length; i++) {
            if (i % 2 == 0) {
                //偶数
                mPoints[i] = new PointF(averageWidth * i + averageWidth / 2, (float) (3.1 * averageHeigth));
            } else {
                //奇数
                mPoints[i] = new PointF(averageWidth * i + averageWidth / 2, (float) (3.9 * averageHeigth));
            }
        }
        invalidate();
    }


    //将水平滑动的数据传回来
    public void tellDistence(int distence) {
        mScrollDistence = distence;
        invalidate();
    }


    /**
     * 手指停止滑动 记录位置
     */
    public void stopScroll() {
        //中轴线的位置 X坐标
        int i = mWidthPixels / 2 + mScrollDistence;
        //得余数
        float v1 = ((i - averageWidth / 2) % averageWidth);
        //得结果
        int v2 = (int) ((i - averageWidth / 2) / averageWidth);


        if (v1 > averageWidth / 2) {
            //滑动到下一个点
            middlePointLocation = v2 + 1;
            int nextPoint = (int) (averageWidth * (v2 + 1) - mWidthPixels / 2 + averageWidth / 2 + (0.15 * averageWidth));
            mOnPointXLocationlistener.pointXLocation(nextPoint);
        } else {
            //滑动到上个点
            middlePointLocation = v2;
            int prePoint = (int) ((averageWidth * v2) - mWidthPixels / 2 + averageWidth / 2 + (0.15 * averageWidth));
            mOnPointXLocationlistener.pointXLocation(prePoint);
        }


        invalidate();
    }

    public void setOnPointXLocationlistener(OnBarPointXLocationlistener onBarPointXLocationlistener) {
        mOnPointXLocationlistener = onBarPointXLocationlistener;
    }

    public interface OnBarPointXLocationlistener {
        void pointXLocation(int xLocation);
    }

}
