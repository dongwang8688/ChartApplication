package com.me.chartlib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.me.chartlib.R;
import com.me.chartlib.adapter.SatellitesDataAdapter;
import com.me.chartlib.bean.SatellitesModel;
import com.me.chartlib.callback.SatellitesDataCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : dell
 *     time   : 2019/01/23
 *     desc   : draw sats in map
 *     动态变化还没做；缺少一个中间观测量
 *     version: 1.0
 * </pre>
 */

public class StarrySkyView extends View implements SatellitesDataCallBack {

    private String TAG = "StarrySkyView";

    //定义圆心和半径
    private int xCenter;
    private int yCenter;
    private int mRadius;
    //缓存SatelliteAdapter数据
    private SatellitesDataAdapter mSatelliteAdapter;
    //设置绘制卫星编号的画笔
    private Paint mTextPaint;
    // 设置绘制卫星颜色的画笔
    private Paint mSatellitePaint;
    private Paint circlePaint;
    private Paint linesPaint;
    private Paint markTextPaint;

    private int screenWith;
    private int screenHeight;
    //大控件外部边框的到屏幕边界的距离；
    private int outspace = (int)getResources().getDimension(R.dimen.view_space_out);
    //自定义view到大控件边框的距离；
    private int innerspace = (int) getResources().getDimension(R.dimen.view_spacing);

    private float markLineSize = getResources().getDimension(R.dimen.dp_1);
    private float mTextSize = getResources().getDimension(R.dimen.sp_8);
    private float markSize = getResources().getDimension(R.dimen.sp_12);
    //定义代表卫星的圆的半径
    private float pointRadius = getResources().getDimension(R.dimen.dp_3);

    private Context mContext;
    private boolean isShapeMore = false;
    private boolean isHalve = false;
    private SatellitesModel satellitesModel;
    private List<SatellitesModel> SatellitesModels = new ArrayList<>();
    private int[] colors;

    //初始化信噪比和卫星号
    int mPrn = 0;
    //方位角
    float mAzimuth = 0.0f;
    //仰角（高度角）
    float mElevation = 0.0f;
    boolean isUsedInFix = false;
    int mSatelliteListSize = 0;

    public StarrySkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenWith = getMeasuredWidth();
        screenHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //进行绘制的操作
        drawBackground(canvas);
        drawStarPoint(SatellitesModels, canvas);
    }

    //初始化数据
    public void init(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StarrySkyView, 0, 0);
        markLineSize = a.getDimension(R.styleable.StarrySkyView_markLineSize, markLineSize);
        pointRadius = a.getDimension(R.styleable.StarrySkyView_pointRadius, pointRadius);
        mTextSize = a.getDimension(R.styleable.StarrySkyView_prnSize, mTextSize);
        markSize = a.getDimension(R.styleable.StarrySkyView_markSize, markSize);
        isHalve = a.getBoolean(R.styleable.StarrySkyView_isHalveCircle, isHalve);

        //设置圆环的画笔
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(markLineSize);

        //设置圆盘中线的画笔
        linesPaint = new Paint();
        linesPaint.setAntiAlias(true);
        linesPaint.setColor(Color.WHITE);
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setStrokeWidth(markLineSize);

        //设置圆盘中刻度盘的画笔
        markTextPaint = new Paint();
        markTextPaint.setAntiAlias(true);
        markTextPaint.setColor(Color.WHITE);
        markTextPaint.setTextAlign(Paint.Align.CENTER);
        markTextPaint.setTextSize(markSize);

        //设置绘制卫星编号的画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.CYAN);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);

        //设置绘制卫星颜色的画笔
        mSatellitePaint = new Paint();
        mSatellitePaint.setAntiAlias(true);
        mSatellitePaint.setColor(Color.WHITE);
        mSatellitePaint.setStyle(Paint.Style.FILL);

    }

    /**
     * 绘制圆盘背景
     *
     * @param canvas
     */
    @SuppressLint("ResourceAsColor")
    public void drawBackground(Canvas canvas) {
        if (null != canvas) {
            //圆心y轴的距离；
            int yCircle = screenHeight / 2;
            yCenter = yCircle;

            //圆心x轴的距离；
            int xCircle = screenWith / 2 + innerspace;
            xCenter = xCircle;

            //圆的半径;
            mRadius = yCircle - outspace - innerspace;
            int radius = mRadius / 3;
            //画外圆
            canvas.drawCircle(xCircle, yCircle, mRadius, circlePaint);

            if(isHalve){
                //中间圆
                canvas.drawCircle(xCircle, yCircle, radius * 2, circlePaint);
                //内圆
                canvas.drawCircle(xCircle, yCircle, radius, circlePaint);
            }else {
                //中间圆
                canvas.drawCircle(xCircle, yCircle, yCircle - outspace * 3, circlePaint);
                //内圆
                canvas.drawCircle(xCircle, yCircle, yCircle - outspace * 5, circlePaint);
            }

            //画横线；
            canvas.drawLine(xCircle - mRadius, yCircle, xCircle + mRadius, yCircle, linesPaint);

            //画竖线；
            canvas.drawLine(xCircle, yCircle - mRadius, xCircle, yCircle + mRadius, linesPaint);

            //画盘上的刻度；
            for (int i = 0; i < 12; i++) {
                canvas.drawText(String.valueOf(i * 30), xCircle, outspace, markTextPaint);
                canvas.rotate(30, xCircle, yCircle);
            }
        }
    }

    /**
     * 绘制所获取的卫星
     *
     * @param
     */
    public void drawStarPoint(List<SatellitesModel> data, Canvas canvas) {
        if (null != canvas) {
            mSatelliteListSize = data.size();
            for (int i = 0; i < mSatelliteListSize; i++) {
                satellitesModel = data.get(i);
                isUsedInFix = satellitesModel.usedInFix();
                mPrn = satellitesModel.getmPrn();
                mAzimuth = satellitesModel.getmAzimuth();
                mElevation = satellitesModel.getmElevation();
                int mConstellationType = satellitesModel.getmConstellationType();

                //获取代表卫星的圆距圆心的距离
                int x = (int) (xCenter + ((mRadius * 0.8 * (90 - mElevation) * Math.sin(Math.PI * mAzimuth / 180) / 90)));
                int y = (int) (yCenter - ((mRadius * 0.8 * (90 - mElevation) * Math.cos(Math.PI * mAzimuth / 180) / 90)));

                // 判断代表卫星圆圈内的填充颜色 1=GPS,3=GLONASS,5=BDS,6=GALILEO
                if (isUsedInFix) {
                    if (colors != null && colors.length > 0) {
                        mSatellitePaint.setColor(Color.BLUE);
                        mTextPaint.setColor(Color.BLUE);
                        if (mConstellationType == 1) {
                            mSatellitePaint.setColor(colors[0]);
                            mTextPaint.setColor(colors[0]);
                        } else if (mConstellationType == 2 && colors.length > 1) {
                            mSatellitePaint.setColor(colors[1]);
                            mTextPaint.setColor(colors[1]);
                        } else if (mConstellationType == 3 && colors.length > 2) {
                            mSatellitePaint.setColor(colors[2]);
                            mTextPaint.setColor(colors[2]);
                        } else if (mConstellationType == 4 && colors.length > 3) {
                            mTextPaint.setColor(colors[3]);
                            mSatellitePaint.setColor(colors[3]);
                        } else if (mConstellationType == 5 && colors.length > 4) {
                            mTextPaint.setColor(colors[4]);
                            mSatellitePaint.setColor(colors[4]);
                        } else if (mConstellationType == 6 && colors.length > 5) {
                            mTextPaint.setColor(colors[5]);
                            mSatellitePaint.setColor(colors[5]);
                        } else {
                            mTextPaint.setColor(Color.BLUE);
                            mSatellitePaint.setColor(Color.BLUE);
                        }
                    } else {
                        if (mConstellationType == 1) {
                            mSatellitePaint.setColor(Color.GREEN);
                            mTextPaint.setColor(Color.GREEN);
                        } else if (mConstellationType == 3) {
                            mSatellitePaint.setColor(Color.parseColor("#F5A623"));
                            mTextPaint.setColor(Color.parseColor("#F5A623"));
                        } else if (mConstellationType == 5) {
                            mSatellitePaint.setColor(Color.RED);
                            mTextPaint.setColor(Color.RED);
                        } else if (mConstellationType == 6) {
                            mSatellitePaint.setColor(Color.YELLOW);
                            mTextPaint.setColor(Color.YELLOW);
                        } else {
                            mSatellitePaint.setColor(Color.GRAY);
                            mTextPaint.setColor(Color.GRAY);
                        }
                    }
                    if(isShapeMore){
                        //需要所有卫星的图，还可以选择显示哪一类卫星；
                        if (!TextUtils.isEmpty(satellitesModel.getSystem()) && satellitesModel.getSystem().equals("gps")) {
                            canvas.drawCircle(x, y, pointRadius, mSatellitePaint);
                        } else {
                            canvas.drawRect(x - pointRadius, y - pointRadius, x + pointRadius, y + pointRadius, mSatellitePaint);
                        }
                    }else {
                        //只有使用的规定系统卫星显示
                        canvas.drawCircle(x, y, pointRadius, mSatellitePaint);
                    }
                    //代表卫星号的文字
                    canvas.drawText(String.valueOf(mPrn), x + pointRadius * 2.5f, y + pointRadius * 0.8f, mTextPaint);
                }
            }
        }
    }

    @Override
    public void setSatellitesData(List<SatellitesModel> satellitesModel) {
        SatellitesModels = satellitesModel;
        invalidate();
    }

    public void setShapeMore(boolean shapeMore) {
        isShapeMore = shapeMore;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }
}


