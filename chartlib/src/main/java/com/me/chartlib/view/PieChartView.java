package com.me.chartlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.me.chartlib.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 饼状统计图，带有标注线，都可以自行设定其多种参数选项
 * <p/>
 */
public class PieChartView extends View {

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float textBottom;
    /**
     * 记录文字大小
     */
    private float mTextSize = getResources().getDimension(R.dimen.sp_10);

    /**
     * 饼图半径
     */
    private float pieChartRadius = getResources().getDimension(R.dimen.dp_80);
    /**
     * 标记线长度
     */
    private float markerLineLength = getResources().getDimension(R.dimen.dp_15);
    /**
     * 饼图画笔
     */
    private Paint piePaint;

    /**
     * 饼图所占矩形区域（不包括文字）
     */
    private RectF pieChartRectF = new RectF();

    /**
     * 饼状图信息列表
     */
    private List<PieceDataHolder> pieceDataHolders = new ArrayList<>();


    public PieChartView(Context context) {
        super(context);
        init(null, 0);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PieChartView, defStyle, 0);

        pieChartRadius = a.getDimension(R.styleable.PieChartView_circleRadius, pieChartRadius);
        markerLineLength = a.getDimension(R.styleable.PieChartView_lineSize, markerLineLength);
        mTextSize = a.getDimension(R.styleable.PieChartView_textSize, mTextSize);
        piePaint = new Paint();
        piePaint.setColor(Color.RED);
        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getContext().getResources().getDisplayMetrics()));
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;
        textBottom = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPieChartRectF();
        drawAllSectors(canvas);
        invalidate();
    }

//    恢复原始比例算法，添加比分比显示
    private void drawAllSectors(Canvas canvas) {
        float sum = 0f;
        float sum2 = 0f;
        for (PieceDataHolder pieceDataHolder : pieceDataHolders) {
            sum += pieceDataHolder.value;
        }

        for (PieceDataHolder pieceDataHolder : pieceDataHolders) {
            float startAngel = sum2 / sum * 360;
            sum2 += pieceDataHolder.value;
            float sweepAngel = pieceDataHolder.value / sum * 360;
            drawSector(canvas, pieceDataHolder.color, startAngel, sweepAngel);
            drawMarkerLineAndText(canvas, pieceDataHolder.color, startAngel + sweepAngel / 2,
                    pieceDataHolder.marker + new DecimalFormat("#.00").format(pieceDataHolder.value / sum * 100) + "%");
        }
    }

    /**
     * 初始化饼图绘制区域
     */
    private void initPieChartRectF() {
        pieChartRectF.left = getWidth() / 2 - pieChartRadius;
        pieChartRectF.top = getHeight() / 2 - pieChartRadius;
        pieChartRectF.right = pieChartRectF.left + pieChartRadius * 2;
        pieChartRectF.bottom = pieChartRectF.top + pieChartRadius * 2;
    }

    /**
     * 绘制扇形
     *
     * @param canvas     画布
     * @param color      要绘制扇形的颜色
     * @param startAngle 起始角度
     * @param sweepAngle 结束角度
     */
    protected void drawSector(Canvas canvas, int color, float startAngle, float sweepAngle) {
        piePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        piePaint.setStyle(Paint.Style.FILL);
        piePaint.setColor(color);

        canvas.drawArc(pieChartRectF, startAngle, sweepAngle, true, piePaint);
    }

    /**
     * 绘制标注线和标记文字
     *
     * @param canvas      画布
     * @param color       标记的颜色
     * @param rotateAngel 标记线和水平相差旋转的角度
     */
    protected void drawMarkerLineAndText(Canvas canvas, int color, float rotateAngel, String text) {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(5);

        Path path = new Path();
        path.close();
        path.moveTo(getWidth() / 2, getHeight() / 2);
        final float x = (float) (getWidth() / 2 + (markerLineLength + pieChartRadius) * Math.cos(Math.toRadians(rotateAngel)));
        final float y = (float) (getHeight() / 2 + (markerLineLength + pieChartRadius) * Math.sin(Math.toRadians(rotateAngel)));
        path.lineTo(x, y);
        float landLineX;
        if (270f > rotateAngel && rotateAngel > 90f) {
            landLineX = x - 20;
        } else {
            landLineX = x + 20;
        }
        path.lineTo(landLineX, y);
        canvas.drawPath(path, paint);
        Paint paintOne = new Paint();
        paintOne.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintOne.setStyle(Paint.Style.FILL);
        paintOne.setColor(color);
        canvas.drawCircle(landLineX, y, 10, paintOne);

        mTextPaint.setColor(color);
        if (270f > rotateAngel && rotateAngel > 90f) {
            float textWidth = mTextPaint.measureText(text);
            canvas.drawText(text, landLineX - 20 - textWidth, y + mTextHeight / 2 - textBottom, mTextPaint);
        } else {
            canvas.drawText(text, landLineX + 20, y + mTextHeight / 2 - textBottom, mTextPaint);
        }

    }

    /**
     * 饼状图每块的信息持有者
     */
    public static final class PieceDataHolder {
        /**
         * 每块扇形的值的大小
         */
        private float value;

        /**
         * 扇形的颜色
         */
        private int color;

        /**
         * 每块的标记
         */
        private String marker;

        public PieceDataHolder(float value, int color, String marker) {
            this.value = value;
            this.color = color;
            this.marker = marker;
        }
    }


    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.(sp)
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the view's text dimension attribute value. In the PieChartView view, this dimension
     * is the font size.
     *
     * @param textSize The text dimension attribute value to use.(sp)
     */
    public void setTextSize(float textSize) {
        mTextSize = textSize;
        invalidateTextPaintAndMeasurements();
    }


    /**
     * 设置饼状图的半径
     *
     * @param pieChartRadius 饼状图的半径（px）
     */
    public void setPieChartRadius(int pieChartRadius) {
        this.pieChartRadius = pieChartRadius;
        invalidate();
    }

    /**
     * 设置标记线的长度
     *
     * @param markerLineLength 标记线的长度（px）
     */
    public void setMarkerLineLength(int markerLineLength) {
        this.markerLineLength = markerLineLength;
    }

    /**
     * 设置饼状图要显示的数据
     *
     * @param data 列表数据
     */
    public void setData(List<PieceDataHolder> data) {
        if (data != null) {
            pieceDataHolders.clear();
            pieceDataHolders.addAll(data);
        }
        invalidate();
    }

}
