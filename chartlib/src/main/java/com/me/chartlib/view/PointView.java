package com.me.chartlib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.me.chartlib.R;
import com.me.chartlib.bean.PointsModel;
import com.me.chartlib.callback.PointViewDataCallBack;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * <pre>
 *     author : me
 *     time   : 2018/01/28
 *     desc   : draw point at view
 *     version: 1.0
 * </pre>
 */

public class PointView extends View implements PointViewDataCallBack {

    private Paint mCirclePaint;//画圆的画笔
    private Paint mLinePaint; //画直线的画笔;
    private Paint mTextPaint;//写文字的画笔;
    private Paint mPointPaint;//画点的画笔;
    private int Viewwidth;//获取控件的宽
    private int Viewheight;//获取空间的高
    private int border;//设置边距

    private int outradius;//外圆半径
    private int medradius;//中间圆半径
    private int inradius;//内圆半径

    private Context mContext;
    private int max_r = 5; //设置全局变量；
    private double timesFlag = 1;
    private double multipleFlag = 1;
    private double one = 5;
    private double two = 10;
    private double three = 15;
    private int pointWidth = 10;
    //设置坐标原点为xyz的初始值；
    private int x0;
    private int y0;
    private float textSize = getResources().getDimension(R.dimen.sp_8);
    private float lineWidth = getResources().getDimension(R.dimen.dp_1);
    private int textColor = Color.WHITE;
    private int pointColor = Color.WHITE;
    private int lineColor = Color.WHITE;
    private String markTag = "m";
    private boolean isSetColor = true;

    private ArrayList<PointsModel> pointsModels = new ArrayList<>();

    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        //画圆的画笔
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        //画直线的画笔
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);

        //写文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);

        //画点的画笔(需要区别不同的服务，描出的点不一样)
        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND);

        border = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleAndLines(canvas);
        initPointData(pointsModels, canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Viewwidth = getMeasuredWidth();
        Viewheight = getMeasuredHeight();
        x0 = Viewwidth / 2;
        y0 = Viewheight / 2;
    }

    //画圆坐标系
    public void drawCircleAndLines(Canvas canvas) {
        //自定义view到大控件边框的距离；
        int innerspace = (int) getResources().getDimension(R.dimen.view_spacing);
        //big circle radius;
        inradius = (y0 - innerspace) / 3;
        medradius = inradius * 2;
        outradius = inradius * 3;

        mCirclePaint.setColor(lineColor);
        mCirclePaint.setStrokeWidth(lineWidth);
        mLinePaint.setColor(lineColor);
        mLinePaint.setStrokeWidth(lineWidth);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        mPointPaint.setStrokeWidth(pointWidth);
        mPointPaint.setColor(pointColor);
        //画外圆
        canvas.drawCircle(x0, y0, outradius, mCirclePaint);
        //外圆精度
        canvas.drawText(multipleFlag * three + markTag, x0 + outradius - 2 * innerspace,
                y0 + 2 * innerspace, mTextPaint);

        //画中间圆
        canvas.drawCircle(x0, y0, medradius, mCirclePaint);
        //中间圆精度
        canvas.drawText(multipleFlag * two + markTag, x0 + medradius - 2 * innerspace,
                y0 + 2 * innerspace, mTextPaint);

        //画里面圆
        canvas.drawCircle(x0, y0, inradius, mCirclePaint);
        //里面圆精度
        canvas.drawText(multipleFlag * one + markTag, x0 + inradius - 2 * innerspace,
                y0 + 2 * innerspace, mTextPaint);
        //画x
        canvas.drawLine(x0 - outradius, y0,x0 + outradius, y0, mLinePaint);
        //画y
        canvas.drawLine(x0, y0 - outradius, x0, y0 + outradius, mLinePaint);
    }

    //画点
    public void drawPoints(Canvas canvas, float x, float y) {
        canvas.drawPoint(x, y, mPointPaint);
    }

    @Override
    public void setPointViewData(ArrayList<PointsModel> pointsModel) {
        this.pointsModels = pointsModel;
        invalidate();
    }

    //坐标计算-画点将数据计算放到界面拿到数据的地方，view中只进行绘制操作
    private void initPointData(ArrayList<PointsModel> pointsModel, Canvas canvas) {
        if (pointsModel.size() > 0) {
            for (int i = 0; i < pointsModel.size(); i++) {
                PointsModel point = pointsModel.get(i);
                if(isSetColor){
                    mPointPaint.setColor(pointColor);
                }else {
                    if (1 == point.getService_type()) {
                        mPointPaint.setColor(Color.GREEN);
                    } else if (2 == point.getService_type()) {
                        mPointPaint.setColor(getResources().getColor(R.color.brown_f5a623));
                    } else {
                        mPointPaint.setColor(Color.RED);
                    }
                }
                //保证点可以落在圆内；
                int Viewrads = (int) (0.9 * outradius);//outradius0.95
                //如果半径值大于10000则数据无效，画点时应剔除
                if(point.caculatR > 10000)
                    break;
                //如果计算的半径值大于最大半径，更新值
                if (max_r < point.caculatR) {
                    calculateDial(point.caculatR);
                    max_r = (int) point.caculatR;
                    //在屏幕上的半径
                    int viewx = (int) ((int) (timesFlag * point.enu[0] * Viewrads / max_r + x0));
                    int viewy = (int) ((int) (timesFlag * point.enu[1] * Viewrads / max_r + y0));
                    if (point.getIsShowFlag()) {
                        drawPoints(canvas, viewx, viewy);
                    }
                } else {
                    //在屏幕上的半径
                    int viewx = (int) ((int) (timesFlag * point.enu[0] * Viewrads / max_r + x0));
                    int viewy = (int) ((int) (timesFlag * point.enu[1] * Viewrads / max_r + y0));
                    if (point.getIsShowFlag()) {
                        drawPoints(canvas, viewx, viewy);
                    }
                }
            }
        }
    }

//    计算动态变化的圆环半径
    private void calculateDial(double maxR){
        one = Math.ceil(maxR / 3 / 1000000);
        two = one * 2;
        three = one * 3;
    }

    //放大比例
    public void setZoomOut() {
        //显示少点
        timesFlag = Double.parseDouble(new DecimalFormat("#.00").format(timesFlag * 2));
        multipleFlag = multipleFlag / 2;
        invalidate();
    }

    //缩小比例
    public void setZoomIn() {
        //有更多的点
        multipleFlag = multipleFlag * 2;
        timesFlag = Double.parseDouble(new DecimalFormat("#.00").format(timesFlag / 2));
        invalidate();
    }

    public void setPointWidth(int pointWidth) {
        this.pointWidth = pointWidth;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setMarkTag(String markTag) {
        this.markTag = markTag;
    }

    public void setSetColor(boolean setColor) {
        isSetColor = setColor;
    }
}
