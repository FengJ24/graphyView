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
import android.util.AttributeSet;
import android.view.View;

import com.ablecloud.graphview.DensityUtil;
import com.ablecloud.graphview.R;

/**
 * Created by fengjian on 2017/7/26.
 */

public class CurveView extends View {

    // 控件宽高
    private int viewWidth, viewHeigth;

    // 画笔
    Paint grayPaint, bluePaint, blackPaint, whitePaint;
    private String[] weekDay = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    private int xScaleAccount = 30;  //X轴刻度的数量
    private int yLineAccount;   //Y轴横线的数量
    // 每一份的水平、竖直尺寸
    private float averageWidth = 100;
    private float averageHeigth = 80;
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
        rawWaterValue.setStyle(Paint.Style.FILL);
        rawWaterValue.setStrokeWidth(mDipToPxFloat);
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
        cleanWaterValue.setStyle(Paint.Style.FILL);
        cleanWaterValue.setStrokeWidth(mDipToPxFloat);
        //设置TDS值的画笔
        cleanWaterValueTDS = new Paint();
        cleanWaterValueTDS.setColor(Color.parseColor("#27B1FF"));
        cleanWaterValueTDS.setTextSize(mTextTDSSize);
        cleanWaterValueTDS.setStyle(Paint.Style.FILL);
        cleanWaterValueTDS.setStrokeWidth(mDipToPxFloat);

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
        //画基准线
        drawMiddleLine(canvas);
        //画原水曲线
        drawRawWaterlLine(canvas);
        //画净水曲线
        drawCleanWaterLine(canvas);
        //画X轴刻度
        drawXCoordinateText(canvas);
        //画原水圆圈
//        drawRawCircle(canvas);
        //画净水圆圈
//        drawCleanCircle(canvas);
        //画原水曲线值背景
        drawRawLineValueBg(canvas);
        //画净水曲线值背景
        drawCleanLineValueBg(canvas);
        //画原水圆圈2
        drawRawCircleFollow(canvas);
        //画净水圆圈2
        drawCleanCircleFollow(canvas);


        super.onDraw(canvas);
    }

    private void drawCleanCircleFollow(Canvas canvas) {
        if (mCleanRegion != null) {
            canvas.drawCircle(mCleanRegion.getBounds().left, mCleanRegion.getBounds().top, 20, mCircleFillPaint);
            canvas.drawCircle(mWidthPixels / 2 + mScrollDistence, mCleanRegion.getBounds().top, 20, cleanWaterPaint);
        }
    }

    private void drawRawCircleFollow(Canvas canvas) {
        if (mRawRegion != null) {
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
        if (mBitmap != null && mCleanPoints != null && mCleanRegion != null) {
            //创建显示图片的位置
            Rect srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            //获取数值字体的宽高矩形
            Rect rect = new Rect();
            cleanWaterValue.getTextBounds(mCleanPoints[middlePointLocation].y + "", 0, (mCleanPoints[middlePointLocation].y + "").length(), rect);
            //获取TDS字体的宽高矩形
            Rect tdsRect = new Rect();
            cleanWaterValueTDS.getTextBounds("TDS", 0, "TDS".length(), tdsRect);
            //获取字体的宽高
            int halfWidth = (rect.right - rect.left) / 2;
            int halfHeight = (rect.bottom - rect.top) / 2;
            Rect desRect = new Rect((int) (mWidthPixels / 2 + mScrollDistence - halfWidth * 1.5),
                    (int) (mCleanRegion.getBounds().top - averageHeigth - halfHeight * 2),
                    (int) (mWidthPixels / 2 + mScrollDistence + halfWidth * 1.5),
                    (int) (mCleanRegion.getBounds().top - averageHeigth + halfHeight * 3));
            //画背景
            canvas.drawBitmap(mBitmap,srcRect,desRect,whitePaint);
            Rect targetRect = new Rect(mWidthPixels / 2 + mScrollDistence - halfWidth * 2,
                    (int) (mCleanRegion.getBounds().top - averageHeigth - halfHeight * 2),
                    mWidthPixels / 2 + mScrollDistence + halfWidth * 2,
                    (int) (mCleanRegion.getBounds().top - averageHeigth + halfHeight * 2));
            //画边框
//            drawBorder(canvas,tdsRect,rect);
            Paint.FontMetricsInt fontMetrics = cleanWaterValue.getFontMetricsInt();
            int baseline = (int) (targetRect.top + targetRect.bottom - fontMetrics.bottom - fontMetrics.top) / 2;
            cleanWaterValue.setTextAlign(Paint.Align.CENTER);
            canvas.drawText((int) mCleanPoints[middlePointLocation].y + "", targetRect.centerX() - (tdsRect.right - tdsRect.left) / 2, baseline, cleanWaterValue);
            cleanWaterValueTDS.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("TDS",
                    (int)(targetRect.centerX()+tdsRect.width()+DensityUtil.dipToPx(getContext(),1)),
                    baseline, cleanWaterValueTDS);


        }
    }

    private void drawBorder(Canvas canvas, Rect tdsRect, Rect rect) {
        Rect targetRect1 = new Rect(mWidthPixels / 2 + mScrollDistence - tdsRect.width()/2,
                (int) (mCleanRegion.getBounds().top - averageHeigth - tdsRect.height()/2),
                mWidthPixels / 2 + mScrollDistence + tdsRect.width()/2,
                (int) (mCleanRegion.getBounds().top - averageHeigth + tdsRect.height()/2));

        Rect targetRect2 = new Rect(mWidthPixels / 2 + mScrollDistence - rect.width()/2,
                (int) (mCleanRegion.getBounds().top - averageHeigth - rect.height()/2),
                mWidthPixels / 2 + mScrollDistence + rect.width()/2,
                (int) (mCleanRegion.getBounds().top - averageHeigth + rect.height()/2));
        canvas.drawRect(targetRect2,whitePaint);
        canvas.drawRect(targetRect1,rawWaterValue);
    }

    private void drawRawLineValueBg(Canvas canvas) {
        //确定文字显示的区域
        if (mBitmap != null && mPoints != null && mRawRegion != null) {
            //创建显示图片的位置
            Rect srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            Rect rect = new Rect();
            rawWaterValue.getTextBounds(mPoints[middlePointLocation].y + "", 0, (mPoints[middlePointLocation].y + "").length(), rect);
            int halfWidth = (rect.right - rect.left) / 2;
            int halfHeight = (rect.bottom - rect.top) / 2;
            Rect desRect = new Rect((int) (mWidthPixels / 2 + mScrollDistence - halfWidth * 1.5),
                    (int) (mRawRegion.getBounds().top - averageHeigth - halfHeight * 2),
                    (int) (mWidthPixels / 2 + mScrollDistence + halfWidth * 1.5),
                    (int) (mRawRegion.getBounds().top - averageHeigth + halfHeight * 3));
            //画背景
            canvas.drawBitmap(mBitmap, srcRect, desRect, whitePaint);
            Rect targetRect = new Rect(mWidthPixels / 2 + mScrollDistence - halfWidth * 2,
                    (int) (mRawRegion.getBounds().top - averageHeigth - halfHeight * 2),
                    mWidthPixels / 2 + mScrollDistence + halfWidth * 2,
                    (int) (mRawRegion.getBounds().top - averageHeigth + halfHeight * 2));
            Paint.FontMetricsInt fontMetrics = rawWaterValue.getFontMetricsInt();
            int baseline = (int) (targetRect.top + targetRect.bottom - fontMetrics.bottom - fontMetrics.top) / 2;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            rawWaterValue.setTextAlign(Paint.Align.CENTER);
            canvas.drawText((int) mPoints[middlePointLocation].y + "", targetRect.centerX(), baseline, rawWaterValue);

        }
    }

    private void drawCleanCircle(Canvas canvas) {
        if (mCleanPoints != null) {
            for (int i = 0; i < mCleanPoints.length; i++) {
                if (middlePointLocation == i) {
                    canvas.drawCircle(mCleanPoints[i].x, mCleanPoints[i].y, 20, mCircleFillPaint);
                    canvas.drawCircle(mCleanPoints[i].x, mCleanPoints[i].y, 20, cleanWaterPaint);
                }
            }
        }
    }

    private void drawRawCircle(Canvas canvas) {
        if (mPoints != null) {
            for (int i = 0; i < mPoints.length; i++) {
                if (middlePointLocation == i) {
                    canvas.drawCircle(mPoints[i].x, mPoints[i].y, 20, mCircleFillPaint);
                    canvas.drawCircle(mPoints[i].x, mPoints[i].y, 20, rawWaterPaint);
                }
            }
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
            mPath.moveTo(-mPoints[0].x, mPoints[0].y);

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
            mPath.lineTo(getWidth(), endp.y);
            mPath.lineTo(getWidth(), getHeight() - averageHeigth);
            mPath.lineTo(0, getHeight() - averageHeigth);
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
            mCleanPath.moveTo(-mCleanPoints[0].x, mCleanPoints[0].y);

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
            mCleanPath.lineTo(getWidth(), endp.y);
            mCleanPath.lineTo(getWidth(), getHeight() - averageHeigth);
            mCleanPath.lineTo(0, getHeight() - averageHeigth);
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
            canvas.drawText("7." + i + "", (0.2f + i) * averageWidth, 10.7f * averageHeigth, grayPaint);
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
                mPoints[i] = new PointF(averageWidth * i + averageWidth / 2, (float) (3.6 * averageHeigth));
            }
        }
        mCleanPoints = new PointF[xScaleAccount];
        for (int i = 0; i < mCleanPoints.length; i++) {
            if (i % 2 == 0) {
                //偶数
                mCleanPoints[i] = new PointF(averageWidth * i + averageWidth / 2, (float) (8.1 * averageHeigth));
            } else {
                //奇数
                mCleanPoints[i] = new PointF(averageWidth * i + averageWidth / 2, (float) (8.5 * averageHeigth));
            }
        }
        invalidate();
        mWidthPixels = this.getResources().getDisplayMetrics().widthPixels;
        mHeightPixels = this.getResources().getDisplayMetrics().heightPixels;
        //设置填充颜色的坐标
        setFillColorLocation();
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

    /**
     * 画中准线
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        canvas.drawLine(mWidthPixels / 2 + mScrollDistence, viewHeigth * (10f / 11), mWidthPixels / 2 + mScrollDistence, averageHeigth, grayPaint);
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
            int nextPoint = (int) (averageWidth * (v2 + 1) - mWidthPixels / 2 + averageWidth / 2);
            mOnPointXLocationlistener.pointXLocation(nextPoint);
        } else {
            //滑动到上个点
            middlePointLocation = v2;
            int prePoint = (int) ((averageWidth * v2) - mWidthPixels / 2 + averageWidth / 2);
            mOnPointXLocationlistener.pointXLocation(prePoint);
        }


        invalidate();
    }

    public void setOnPointXLocationlistener(OnPointXLocationlistener onPointXLocationlistener) {
        mOnPointXLocationlistener = onPointXLocationlistener;
    }

    public interface OnPointXLocationlistener {
        void pointXLocation(int xLocation);
    }

}
