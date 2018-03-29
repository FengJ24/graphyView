package com.ablecloud.graphview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.ablecloud.graphview.DensityUtil;
import com.ablecloud.graphview.R;
import com.ablecloud.graphview.TDSBean;

import java.util.List;



/**
 * Created by fengjian on 2017/7/26.
 */

public class CurveView extends View {

    // 控件宽高
    private int viewWidth, viewHeigth;

    // 画笔
    Paint grayPaint, bluePaint, blackPaint, whitePaint;
    private int xScaleAccount = 30;  //X轴刻度的数量
    private int yLineAccount;   //Y轴横线的数量
    // 每一份的水平、竖直尺寸
    private float averageWidth = 100;
    private float averageHeigth;
    /**
     * 曲线上总点数
     */
    private PointF[] mPoints;
    private Paint rawWaterPaint;
    private Paint cleanWaterPaint;
    private Paint rawWaterBgPaint;
    private Point mP3;
    private Point mP4;
    private int[] mIntPoint;
    private Path mPath;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScrollDistence;
    private float mLastScrollDistence;
    private PointF[] mCleanPoints;
    private Path mCleanPath;
    private Paint cleanWaterBgPaint;
    private int rawX;
    private int rawY;
    private int lastX;
    private int lastY;
    private int totalOffset;
    private int mWidthPixels;
    private OnPointXLocationlistener mOnPointXLocationlistener;
    private Bitmap mBitmap;
    private int middlePointLocation = 0;
    private Paint mCircleFillPaint;
    private int mHeightPixels;
    private LinearGradient mCleanWaterShader;
    private LinearGradient mCircleWhiteFill;
    private int mTextSize;
    private Paint cleanWaterValue;
    private int mTextValueSize;
    private Paint rawWaterValue;
    private float mDipToPxFloat;
    private Region mRawRegion;
    private Path mBgPath;
    private Paint bgPaint;
    private Region mCleanRegion;
    private int mTextTDSSize;
    private Paint cleanWaterValueTDS;
    private Paint rawWaterValueTDS;
    private int mVerticalScal = 8;
    private int mMaxScale = 1000;
    private List<TDSBean> mTdsBeanList;
    private boolean isReady;


    public CurveView(Context context) {
        super(context);
        init();
    }

    public CurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initMetrics();
        grayPaint = new Paint();
        grayPaint.setColor(Color.parseColor("#d2d2d2"));
        grayPaint.setStrokeWidth(1.5f);

        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#d2d2d2"));
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(1.5f);

        bluePaint = new Paint();
        bluePaint.setColor(Color.parseColor("#2E88CE"));
        bluePaint.setAntiAlias(true);
        bluePaint.setStyle(Paint.Style.STROKE);

        blackPaint = new Paint();
        blackPaint.setColor(Color.parseColor("#000000"));
        blackPaint.setStrokeWidth(1.0f);

        whitePaint = new Paint();
        whitePaint.setColor(Color.parseColor("#ffffff"));
        whitePaint.setStrokeWidth(2.5f);

        //设置圆圈填充的颜色
        mCircleWhiteFill = new LinearGradient(
                0, 0,
                2000, 2000,
                new int[]{Color.WHITE, Color.WHITE},
                new float[]{1f, 1f},
                Shader.TileMode.CLAMP);
        //设置圆圈填充的画笔
        mCircleFillPaint = new Paint();
        mCircleFillPaint.setColor(Color.parseColor("#27B1FF"));
        mCircleFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCircleFillPaint.setStrokeWidth(5.5f);
        mCircleFillPaint.setShader(mCircleWhiteFill);
        initRawwaterPaint();
        initCleanWaterPaint();
        mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.nuber_bg);
    }

    private void initRawwaterPaint() {
        LinearGradient shader = new LinearGradient(
                0, 0,
                0, 2000,
                new int[]{0x667CA6C0, 0xFFffffff},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP);

        //设置原水曲线画笔
        rawWaterPaint = new Paint();
        rawWaterPaint.setColor(Color.parseColor("#7CA6C0"));
        rawWaterPaint.setStyle(Paint.Style.STROKE);
        rawWaterPaint.setStrokeWidth(5.5f);
        rawWaterPaint.setAntiAlias(true);
        //设置原水填充画笔
        rawWaterBgPaint = new Paint();
        rawWaterBgPaint.setStyle(Paint.Style.FILL);
        rawWaterBgPaint.setShader(shader);
        //画原水值的画笔
        rawWaterValue = new Paint();
        rawWaterValue.setColor(Color.parseColor("#7CA6C0"));
        rawWaterValue.setTextSize(mTextValueSize);
        rawWaterValue.setStyle(Paint.Style.FILL_AND_STROKE);
        rawWaterValue.setStrokeWidth(2);
    }

    private void initCleanWaterPaint() {
        //设置净水填充的渐变色
        mCleanWaterShader = new LinearGradient(
                0, 100,
                0, 800,
                new int[]{0x1127B1FF, 0x11FFFFFF},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP);
        //设置净水的画笔
        cleanWaterPaint = new Paint();
        cleanWaterPaint.setColor(Color.parseColor("#27B1FF"));
        cleanWaterPaint.setStyle(Paint.Style.STROKE);
        cleanWaterPaint.setStrokeWidth(5.5f);
        //设置净水填充的画笔
        cleanWaterBgPaint = new Paint();
        cleanWaterBgPaint.setStyle(Paint.Style.FILL);
        cleanWaterBgPaint.setShader(mCleanWaterShader);
        //设置净水值的画笔
        cleanWaterValue = new Paint();
        cleanWaterValue.setColor(Color.parseColor("#27B1FF"));
        cleanWaterValue.setTextSize(mTextValueSize);
        cleanWaterValue.setStyle(Paint.Style.FILL_AND_STROKE);
        cleanWaterValue.setStrokeWidth(2);
        //设置TDS值的画笔
        cleanWaterValueTDS = new Paint();
        cleanWaterValueTDS.setColor(Color.parseColor("#27B1FF"));
        cleanWaterValueTDS.setTextSize(mTextTDSSize);
        cleanWaterValueTDS.setStyle(Paint.Style.FILL);
        cleanWaterValueTDS.setStrokeWidth(mDipToPxFloat);
        //原水TDS值的画笔
        rawWaterValueTDS = new Paint();
        rawWaterValueTDS.setColor(Color.parseColor("#7CA6C0"));
        rawWaterValueTDS.setTextSize(mTextTDSSize);
        rawWaterValueTDS.setStyle(Paint.Style.FILL);
        rawWaterValueTDS.setStrokeWidth(mDipToPxFloat);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) (xScaleAccount * averageWidth) + mWidthPixels, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //初始化完毕,开始执行绘画
        if (isReady) {
            //画背景
            drawGrayBackground(canvas);
            //画水平横线
            drawYCoordinateLine(canvas);
            //画基准线
            drawMiddleLine(canvas);
            //画原水曲线
            drawRawWaterlLine(canvas);
            //画原水圆圈2
            drawRawCircleFollow(canvas);
            //画原水曲线值背景
            drawRawLineValueBg(canvas);
            //画净水曲线
            drawCleanWaterLine(canvas);
            //画X轴刻度
            drawXCoordinateText(canvas);
            //画净水曲线值背景
            drawCleanLineValueBg(canvas);
            //画净水圆圈2
            drawCleanCircleFollow(canvas);
            //画Y轴坐标轴
            drawYCoordinateNumber(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawCleanCircleFollow(Canvas canvas) {
        if (mCleanRegion != null && mCleanRegion.getBounds().top != 0) {
            canvas.drawCircle(mCleanRegion.getBounds().left, mCleanRegion.getBounds().top, 20, mCircleFillPaint);
            canvas.drawCircle(mWidthPixels / 2 + mScrollDistence, mCleanRegion.getBounds().top, 20, cleanWaterPaint);
        }
    }

    private void drawRawCircleFollow(Canvas canvas) {
        if (mRawRegion != null && mRawRegion.getBounds().top != 0) {
            canvas.drawCircle(mRawRegion.getBounds().left, mRawRegion.getBounds().top, 20, mCircleFillPaint);
            canvas.drawCircle(mWidthPixels / 2 + mScrollDistence, mRawRegion.getBounds().top, 20, rawWaterPaint);
        }
    }

    private void initMetrics() {
        //屏幕宽高
        mWidthPixels = this.getResources().getDisplayMetrics().widthPixels;
        mHeightPixels = this.getResources().getDisplayMetrics().heightPixels;
        //初始化轴刻度
        averageWidth = DensityUtil.dipToPx(this.getContext().getApplicationContext(), 60);
        //初始化坐标字体大小
        mTextSize = DensityUtil.sp2px(this.getContext().getApplicationContext(), 12);
        //数值字体的大小
        mTextValueSize = DensityUtil.sp2px(this.getContext().getApplicationContext(), 20);
        //初始化画笔的宽度)
        mDipToPxFloat = DensityUtil.dipToPxFloat(this.getContext().getApplicationContext(), 1f);
        //TDS字体的大小
        mTextTDSSize = DensityUtil.sp2px(this.getContext().getApplicationContext(), 12);

    }

    private void drawCleanLineValueBg(Canvas canvas) {
        if (mBitmap != null && mCleanPoints != null && mCleanRegion != null && null != mTdsBeanList) {
            //创建显示图片的位置
            Rect srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            //获取数值字体的宽高矩形
            Rect rect = new Rect();
            cleanWaterValue.getTextBounds("999", 0, ("999").length(), rect);
            //获取TDS字体的宽高矩形
            Rect tdsRect = new Rect();
            cleanWaterValueTDS.getTextBounds("TDS", 0, "TDS".length(), tdsRect);
            //获取字体的宽高
            int halfWidth = (rect.right - rect.left) / 2;
            int halfHeight = (rect.bottom - rect.top) / 2;
            Rect desRect = new Rect((int) (mWidthPixels / 2 + mScrollDistence - halfWidth * 1.5),
                    (int) (mCleanRegion.getBounds().top - averageHeigth * 2.0f / 3 - halfHeight * 2),
                    (int) (mWidthPixels / 2 + mScrollDistence + halfWidth * 1.5),
                    (int) (mCleanRegion.getBounds().top - averageHeigth * 2.0f / 3 + halfHeight * 3));
            //画背景
            canvas.drawBitmap(mBitmap, srcRect, desRect, whitePaint);
            Rect targetRect = new Rect(mWidthPixels / 2 + mScrollDistence - halfWidth * 2,
                    (int) (mCleanRegion.getBounds().top - averageHeigth * 2.0f / 3 - halfHeight * 2),
                    mWidthPixels / 2 + mScrollDistence + halfWidth * 2,
                    (int) (mCleanRegion.getBounds().top - averageHeigth * 2.0f / 3 + halfHeight * 2));
            Paint.FontMetricsInt fontMetrics = cleanWaterValue.getFontMetricsInt();
            int baseline = (int) (targetRect.top + targetRect.bottom - fontMetrics.bottom - fontMetrics.top) / 2;
            cleanWaterValue.setTextAlign(Paint.Align.CENTER);
            canvas.drawText((int) mTdsBeanList.get(middlePointLocation).getCleanTDSInfo() + "", targetRect.centerX(), baseline, cleanWaterValue);
            cleanWaterValueTDS.setTextAlign(Paint.Align.CENTER);


        }
    }

    private void drawRawLineValueBg(Canvas canvas) {
        //确定文字显示的区域
        if (mBitmap != null && mPoints != null && mRawRegion != null && null != mTdsBeanList) {
            //创建显示图片的位置
            if (middlePointLocation == mTdsBeanList.size() - 1) {

            }
            Rect srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            Rect rect = new Rect();
            rawWaterValue.getTextBounds("999", 0, ("999").length(), rect);
            //获取TDS字体的宽高矩形
            Rect tdsRect = new Rect();
            rawWaterValueTDS.getTextBounds("TDS", 0, "TDS".length(), tdsRect);
            int halfWidth = (rect.right - rect.left) / 2;
            int halfHeight = (rect.bottom - rect.top) / 2;
            Rect desRect = new Rect((int) (mWidthPixels / 2 + mScrollDistence - halfWidth * 1.5),
                    (int) (mRawRegion.getBounds().top - averageHeigth * 2.0f / 3 - halfHeight * 2),
                    (int) (mWidthPixels / 2 + mScrollDistence + halfWidth * 1.5),
                    (int) (mRawRegion.getBounds().top - averageHeigth * 2.0f / 3 + halfHeight * 3));
            //画背景
            canvas.drawBitmap(mBitmap, srcRect, desRect, whitePaint);
            Rect targetRect = new Rect(mWidthPixels / 2 + mScrollDistence - halfWidth * 2,
                    (int) (mRawRegion.getBounds().top - averageHeigth * 2.0f / 3 - halfHeight * 2),
                    mWidthPixels / 2 + mScrollDistence + halfWidth * 2,
                    (int) (mRawRegion.getBounds().top - averageHeigth * 2.0f / 3 + halfHeight * 2));
            Paint.FontMetricsInt fontMetrics = rawWaterValue.getFontMetricsInt();
            int baseline = (int) (targetRect.top + targetRect.bottom - fontMetrics.bottom - fontMetrics.top) / 2;
            rawWaterValue.setTextAlign(Paint.Align.CENTER);
            if (middlePointLocation == mTdsBeanList.size() - 1) {
                System.out.println();
            }
            canvas.drawText((int) mTdsBeanList.get(middlePointLocation).getRawTDSInfo() + "", targetRect.centerX(), baseline, rawWaterValue);
            rawWaterValueTDS.setTextAlign(Paint.Align.CENTER);

        }
    }


    /**
     * 画原水
     *
     * @param canvas
     */
    private void drawRawWaterlLine(Canvas canvas) {
        if (mPoints != null) {
            PointF startp = new PointF();
            PointF endp = new PointF();
            mPath = new Path();
            mPath.moveTo(mPoints[0].x, mPoints[0].y);

            for (int i = 0; i < mPoints.length - 1; i++) {
                startp = mPoints[i];
                endp = mPoints[i + 1];
                float wt = (startp.x + endp.x) / 4;
                PointF p3 = new PointF();
                PointF p4 = new PointF();
                p3.y = startp.y;
                p3.x = 2 * wt;
                p4.y = endp.y;
                p4.x = 2 * wt;

                mPath.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            }
            canvas.drawPath(mPath, rawWaterPaint);
            mPath.lineTo(mPoints[mPoints.length - 1].x + 1, endp.y);
            mPath.lineTo(mPoints[mPoints.length - 1].x, getHeight() - averageHeigth);
            mPath.lineTo(mPoints[0].x, getHeight() - averageHeigth);
            mPath.close();
            canvas.drawPath(mPath, rawWaterBgPaint);
        }
    }

    /**
     * 画净水
     *
     * @param canvas
     */
    private void drawCleanWaterLine(Canvas canvas) {
        if (mCleanPoints != null) {
            PointF startp = new PointF();
            PointF endp = new PointF();
            mCleanPath = new Path();
            mCleanPath.moveTo(mCleanPoints[0].x, mCleanPoints[0].y);
            for (int i = 0; i < mCleanPoints.length - 1; i++) {
                startp = mCleanPoints[i];
                endp = mCleanPoints[i + 1];
                float wt = (startp.x + endp.x) / 4;
                PointF p3 = new PointF();
                PointF p4 = new PointF();
                p3.y = startp.y;
                p3.x = 2 * wt;
                p4.y = endp.y;
                p4.x = 2 * wt;
                mCleanPath.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            }
            canvas.drawPath(mCleanPath, cleanWaterPaint);
            mCleanPath.lineTo(mCleanPoints[mCleanPoints.length - 1].x + 1, endp.y);
            mCleanPath.lineTo(mCleanPoints[mCleanPoints.length - 1].x, getHeight() - averageHeigth);
            mCleanPath.lineTo(mCleanPoints[0].x, getHeight() - averageHeigth);
            mCleanPath.close();
            canvas.drawPath(mCleanPath, cleanWaterBgPaint);
        }
    }

    /**
     * 画灰、白色背景
     */
    private void drawGrayBackground(Canvas canvas) {
        grayPaint.setStyle(Paint.Style.FILL);
        whitePaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, viewHeigth * ((mVerticalScal + 1) * 1.0f / (mVerticalScal + 2)),
                viewWidth, viewHeigth, whitePaint); // 白色背景

    }

    /**
     * 画y轴坐标值
     *
     * @param canvas
     */
    private void drawYCoordinateNumber(Canvas canvas) {
        grayPaint.setTextSize(mTextSize);
        int i1 = mMaxScale / mVerticalScal;
        for (int i = 0; i < mVerticalScal + 1; i++) {
            canvas.drawText("" + (i * i1), mScrollDistence + viewWidth * (13 / 140)+DensityUtil.dipToPx(getContext(),4), (float) ((mVerticalScal * 1.0f) + 0.9 - i) * averageHeigth, grayPaint);
            if (i == mVerticalScal) {
                canvas.drawText("" + mMaxScale, mScrollDistence + viewWidth * (13 / 140)+DensityUtil.dipToPx(getContext(),4), (float) ((mVerticalScal * 1.0f) + 0.9 - i) * averageHeigth, grayPaint);
            }
        }
    }

    /**
     * 画Y坐标上横线
     *
     * @param canvas
     */
    private void drawYCoordinateLine(Canvas canvas) {
        for (int i = 1; i < mVerticalScal + 2; i++)
            canvas.drawLine(0, averageHeigth * i, viewWidth, averageHeigth * i, grayPaint);
    }

    /**
     * 画x坐标文字
     *
     * @param canvas
     */
    private void drawXCoordinateText(Canvas canvas) {
        if (null != mTdsBeanList) {
            grayPaint.setTextSize(mTextSize);
            for (int i = 0; i < xScaleAccount; i++)
                canvas.drawText(mTdsBeanList.get(i).getDate(), (0.2f + i) * averageWidth + mWidthPixels / 2, (float) (mVerticalScal * 1.0f + 1.7) * averageHeigth, grayPaint);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            initMeasure();

        }
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void initMeasure() {
        isReady = true;
        viewWidth = getWidth();
        viewHeigth = getHeight();
        averageHeigth = viewHeigth / (mVerticalScal + 2);
        mWidthPixels = this.getResources().getDisplayMetrics().widthPixels;
        mHeightPixels = this.getResources().getDisplayMetrics().heightPixels;
        setNeedData();
        //设置填充颜色的坐标
        setFillColorLocation();
    }

    private void setNeedData() {
        if (null != mTdsBeanList) {
            //确定点的位置
            mPoints = new PointF[xScaleAccount];
            mCleanPoints = new PointF[xScaleAccount];
            xScaleAccount = mTdsBeanList.size();
            for (int i = 0; i < mTdsBeanList.size(); i++) {
                mPoints[i] = new PointF(averageWidth * i + averageWidth / 2 + mWidthPixels / 2, (float) (changeFrame(mTdsBeanList.get(i).getRawTDSInfo())));
                mCleanPoints[i] = new PointF(averageWidth * i + averageWidth / 2 + mWidthPixels / 2, (float) (changeFrame(mTdsBeanList.get(i).getCleanTDSInfo())));
            }
            invalidate();
            if (null != mOnPointXLocationlistener) {
                mScrollDistence = ((int) ((averageWidth / 2 + (mTdsBeanList.size() - 1) * averageWidth)) + 1);
                tellDistence(mScrollDistence);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopScroll();
                    }
                },200);
            }
        }
    }


    private void setFillColorLocation() {
        int colors2[] = new int[2];
        float positions2[] = new float[2];
        // 第1个点
        colors2[0] = 0x6627B1FF;
        positions2[0] = 0;
        // 第2个点
        colors2[1] = 0xffffffff;
        positions2[1] = 1f;
        // 第3个点
//        colors2[2] = 0xE5E5E5;
//        positions2[2] = 1;
        mCleanWaterShader = new LinearGradient(
                0, mHeightPixels / 2,
                0, mHeightPixels,
                colors2,
                positions2,
                Shader.TileMode.CLAMP);
        cleanWaterBgPaint.setShader(mCleanWaterShader);
    }

    //将水平滑动的数据传回来
    public void tellDistence(int distence) {
        if (null != mCleanPath && null != mPath) {
            //设置原水剪裁矩形
            mRawRegion = new Region();
            Region clip = new Region(distence + mWidthPixels / 2, 0, distence + mWidthPixels / 2 + 1, viewHeigth);
            mRawRegion.setPath(mPath, clip);
            //设置净水剪裁矩形
            mCleanRegion = new Region();
            Region cleanClip = new Region(distence + mWidthPixels / 2, 0, distence + mWidthPixels / 2 + 1, viewHeigth);
            mCleanRegion.setPath(mCleanPath, cleanClip);
            mScrollDistence = distence;
            invalidate();
        }
    }

    /**
     * 画中准线
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        canvas.drawLine(mWidthPixels / 2 + mScrollDistence, averageHeigth * (mVerticalScal + 1), mWidthPixels / 2 + mScrollDistence, averageHeigth, grayPaint);
    }

    /**
     * 手指停止滑动 记录位置
     */
    public void stopScroll() {
        if (null != mTdsBeanList && null != mOnPointXLocationlistener) {
            //当滑动距离大于 屏幕一半+水平分割一半+所有有刻度
            if (mScrollDistence > averageWidth / 2 + (mTdsBeanList.size() - 1) * averageWidth) {
                middlePointLocation = mTdsBeanList.size() - 1;
                mOnPointXLocationlistener.pointXLocation((int) ((averageWidth / 2 + (mTdsBeanList.size() - 1) * averageWidth)));
            } else if (mScrollDistence > averageWidth / 2) {
                //得余数
                float v1 = ((mScrollDistence - averageWidth / 2) % averageWidth);
                //得结果
                int v2 = (int) ((mScrollDistence - averageWidth / 2) / averageWidth);
                if (v1 > averageWidth / 2) {
                    //滑动到下一个点
                    middlePointLocation = v2 + 1;
                    if (middlePointLocation > mTdsBeanList.size() - 1) {
                        middlePointLocation = mTdsBeanList.size() - 1;
                    }
                    int nextPoint = (int) ((averageWidth * middlePointLocation) + averageWidth / 2);
                    mOnPointXLocationlistener.pointXLocation(nextPoint);
                } else {
                    //滑动到上个点
                    middlePointLocation = v2;
                    int prePoint = (int) ((averageWidth * v2) + averageWidth / 2);
                    mOnPointXLocationlistener.pointXLocation(prePoint);
                }
            } else {
                //小距离滑动
                middlePointLocation = 0;
                int nextPoint = (int) (averageWidth / 2);
                mOnPointXLocationlistener.pointXLocation(nextPoint);

            }
            invalidate();
        }
    }

    /**
     * 设置垂直刻度
     *
     * @param verticalScal
     */
    public void setVerticalScal(int verticalScal) {
        mVerticalScal = verticalScal;
    }

    /**
     * 设置最大刻度
     *
     * @param maxScale
     */
    public void setMaxScale(int maxScale) {
        mMaxScale = maxScale;
    }

    /**
     * 将传进来的值进行坐标转换
     *
     * @param value
     * @return
     */
    private float changeFrame(int value) {
        float v1 = 1 - (value * 1.00f) / mMaxScale;
        float v = v1 * ((mVerticalScal) * averageHeigth) + averageHeigth;
        return v;
    }

    /**
     * 设置数据 日期/原水TDS/净水TDS
     *
     * @param tdsBeanList
     */
    public void setCureData(List<TDSBean> tdsBeanList) {
        mTdsBeanList = tdsBeanList;
        initMeasure();
    }

    public void setOnPointXLocationlistener(OnPointXLocationlistener onPointXLocationlistener) {
        mOnPointXLocationlistener = onPointXLocationlistener;
    }

    public interface OnPointXLocationlistener {
        void pointXLocation(int xLocation);
    }

}
