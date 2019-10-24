package com.me.chartlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.me.chartlib.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by me
 * 2019/1/28
 * description:柱状统计图
 */

public class CommonBarChart extends View {
    private Context mContext;

    private Paint mPaintBar;
    private Paint mPaintLine;
    private Paint mPaintText;
    private Paint mPaintMark;
    //柱状条对应的颜色数组
    private int[] colors;
    private int lineColor = Color.GRAY;
    private int markColor = Color.BLACK;
    private int textColor = Color.BLACK;
    private int itemColor = Color.BLUE;
    private int markSize = 25; //y标值大小
    private int mTextSize = 25; //文字大小
    private int keduTextSpace = 20;//刻度与文字之间的间距
    private int keduWidth = 20; //坐标轴上横向标识线宽度
    private int keduSpace = 100; //每个刻度之间的间距 px
    private int itemSpace = 60;//柱状条之间的间距
    private int itemWidth = 50;//柱状条的宽度
    private int yNum = 5; //y轴刻度分段个数
    //刻度递增的值
    private int valueSpace = 20;
    private int lineWidth = 2;
    //是否要展示柱状条对应的值
    private boolean isShowValue = true;
    private boolean showMark = true;
    private boolean isColors = true;
    private boolean isShowXLines = true;
    //绘制柱形图的坐标起点
    private int startX;
    private int startY;
    private int mMaxTextWidth;
    private int mMaxTextHeight;
    private int maxYAxis = 100;
    private Rect mXMaxTextRect;
    private Rect mYMaxTextRect;
    //数据值
    private List<Integer> mData = new ArrayList<>();
    private List<Integer> yAxisList = new ArrayList<>();
    private List<String> xAxisList = new ArrayList<>();

    public CommonBarChart(Context context) {
        this(context, null);
    }

    public CommonBarChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CommonBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        colors = new int[]{ContextCompat.getColor(context, R.color.blue), ContextCompat.getColor(context, R.color.green), ContextCompat.getColor(context, R.color.brown_f5a623)
                , ContextCompat.getColor(context, R.color.red), ContextCompat.getColor(context, R.color.yellow)};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonBarChart);
        lineColor = typedArray.getColor(R.styleable.CommonBarChart_xyLineColor, lineColor);
        keduTextSpace = typedArray.getDimensionPixelSize(R.styleable.CommonBarChart_lineTextSpace, keduTextSpace);
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.CommonBarChart_xyLineWidth, lineWidth);
        maxYAxis = typedArray.getInt(R.styleable.CommonBarChart_yMarkMax, maxYAxis);
//        yNum = typedArray.getInt(R.styleable.CommonBarChart_yNum, yNum);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CommonBarChart_comTextSize, mTextSize);
        textColor = typedArray.getColor(R.styleable.CommonBarChart_comTextColor, textColor);
        isColors = typedArray.getBoolean(R.styleable.CommonBarChart_showColors, isColors);
        showMark = typedArray.getBoolean(R.styleable.CommonBarChart_showMark, showMark);
        isShowValue = typedArray.getBoolean(R.styleable.CommonBarChart_showValue, isShowValue);
        markSize = typedArray.getDimensionPixelSize(R.styleable.CommonBarChart_cbcMarkSize, markSize);
        markColor = typedArray.getColor(R.styleable.CommonBarChart_markColor, markColor);
        itemWidth = typedArray.getDimensionPixelSize(R.styleable.CommonBarChart_itemWidth, itemWidth);
        itemSpace = typedArray.getDimensionPixelSize(R.styleable.CommonBarChart_itemSpace, itemSpace);
        itemColor = typedArray.getColor(R.styleable.CommonBarChart_itemColor, itemColor);
        isShowXLines = typedArray.getBoolean(R.styleable.CommonBarChart_showXLines, isShowXLines);
        typedArray.recycle();
        init(context, false);
    }

    private void init(Context context, boolean isUpdate) {
        //设置边缘特殊效果
        BlurMaskFilter PaintBGBlur = new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER);
        //绘制柱状图的画笔
        mPaintBar = new Paint();
        mPaintBar.setStyle(Paint.Style.FILL);
        mPaintBar.setStrokeWidth(itemWidth);
        mPaintBar.setMaskFilter(PaintBGBlur);
        //绘制直线的画笔
        mPaintLine = new Paint();
        mPaintLine.setColor(lineColor);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(lineWidth);

        //绘制文字的画笔
        mPaintText = new Paint();
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(textColor);
        mPaintText.setAntiAlias(true);
        //绘制mark文字的画笔
        mPaintMark = new Paint();
        mPaintMark.setTextSize(markSize);
        mPaintMark.setColor(markColor);
        mPaintMark.setTextAlign(Paint.Align.CENTER);

        mYMaxTextRect = new Rect();
        mXMaxTextRect = new Rect();
        if (yAxisList.size() > 0) {
            mPaintText.getTextBounds(Integer.toString(yAxisList.get(yAxisList.size() - 1)), 0, Integer.toString(yAxisList.get(yAxisList.size() - 1)).length(), mYMaxTextRect);
            maxYAxis = Collections.max(yAxisList);
            if (maxYAxis % 2 == 0) {
                maxYAxis = maxYAxis + 2;
            } else {
                maxYAxis = maxYAxis + 1;
            }
            valueSpace = (maxYAxis / yNum);
        } else {
            mPaintText.getTextBounds(Integer.toString(yAxisList.size()), 0, Integer.toString(yAxisList.size()).length(), mYMaxTextRect);
        }
        if (xAxisList.size() > 0) {
            mPaintText.getTextBounds(xAxisList.get(xAxisList.size() - 1), 0, xAxisList.get(xAxisList.size() - 1).length(), mXMaxTextRect);
        } else {
            mPaintText.getTextBounds(Integer.toString(xAxisList.size()), 0, Integer.toString(xAxisList.size()).length(), mXMaxTextRect);
        }
        //绘制的刻度文字的最大值所占的宽高
        mMaxTextWidth = mYMaxTextRect.width() > mXMaxTextRect.width() ? mYMaxTextRect.width() : mXMaxTextRect.width();
        mMaxTextHeight = mYMaxTextRect.height() > mXMaxTextRect.height() ? mYMaxTextRect.height() : mXMaxTextRect.height();

        //文字+刻度宽度+文字与刻度之间间距
        startX = mMaxTextWidth + keduWidth + keduTextSpace;
        //坐标原点 y轴起点
        startY = keduSpace * (yAxisList.size() - 1) + mMaxTextHeight + (isShowValue ? keduTextSpace : 0);
//        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //增加布局更新重新测量绘制图形算法解决初始化后getWidth()获得view宽度为零bug
//                startY = getMeasuredHeight();
//            }
//        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            if (keduWidth > mMaxTextHeight + keduTextSpace) {
                heightSize = (yAxisList.size() - 1) * keduSpace + keduWidth + mMaxTextHeight;
            } else {
                heightSize = (yAxisList.size() - 1) * keduSpace + (mMaxTextHeight + keduTextSpace) + mMaxTextHeight;
            }
            heightSize = heightSize + keduTextSpace + (isShowValue ? keduTextSpace : 0);//x轴刻度对应的文字距离底部的padding:keduTextSpace
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = startX + mData.size() * itemWidth + (mData.size() + 1) * itemSpace;
        }
        //保存测量结果
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //从下往上绘制Y 轴
        canvas.drawLine(startX, startY, startX, startY - yNum * keduSpace, mPaintLine);
//        canvas.drawLine(startX, startY, startX + mData.size() * itemWidth + itemSpace * (mData.size() + 1), startY, mPaintLine);
        Rect textRect = new Rect();
        for (int i = 0; i <= yNum; i++) {
            //绘制Y轴的文字
            if(showMark) {
                mPaintMark.getTextBounds(Integer.toString(maxYAxis), 0, Integer.toString(maxYAxis).length(), textRect);
                canvas.drawText(Integer.toString(valueSpace * i), startX - textRect.width() / 2 - keduTextSpace / 4, startY - i * keduSpace + textRect.height() / 2, mPaintMark);
            }
            //画X轴及上方横向的刻度线
//            canvas.drawLine(startX, startY - keduSpace * i, startX + mData.size() * itemWidth + itemSpace * (mData.size() + 1), startY - keduSpace * i, mPaintLine);
            if(i == 0)
                canvas.drawLine(startX, startY - keduSpace * i, getMeasuredWidth(), startY - keduSpace * i, mPaintLine);
            else {
                if(isShowXLines){
                    canvas.drawLine(startX, startY - keduSpace * i, getMeasuredWidth(), startY - keduSpace * i, mPaintLine);
                }
            }
        }
        for (int j = 0; j < xAxisList.size(); j++) {
            mPaintMark.setTextAlign(Paint.Align.LEFT);
            //绘制X轴的文字
            Rect rect = new Rect();
            mPaintMark.getTextBounds(xAxisList.get(j), 0, xAxisList.get(j).length(), rect);
            canvas.drawText(xAxisList.get(j), startX + itemSpace * (j + 1) + itemWidth * j + itemWidth / 2 - rect.width() / 2, startY + rect.height() + keduTextSpace / 4, mPaintMark);
        }
        int colorSize = colors.length;
        for (int j = 0; j < mData.size(); j++) {
            //绘制bar
            if(isColors)
                mPaintBar.setColor(colors[j % colorSize]);
            else
                mPaintBar.setColor(itemColor);
            int initx = startX + itemSpace * (j + 1) + j * itemWidth;
            canvas.drawRect(initx, (float) (startY - (mData.get(j) * (keduSpace * 1.0 / valueSpace))), initx + itemWidth, startY, mPaintBar);
            //绘制柱状条上的值
            if (isShowValue) {
                Rect rectText = new Rect();
                mPaintText.getTextBounds(mData.get(j) + "", 0, (mData.get(j) + "").length(), rectText);
                canvas.drawText(mData.get(j) + "", startX + itemSpace * (j + 1) + itemWidth * j + itemWidth / 2 - rectText.width() / 2, (float) (startY - keduTextSpace / 2 - (mData.get(j) * (keduSpace * 1.0 / valueSpace))), mPaintText);
            }
        }
    }

    /**
     * 根据真实的数据刷新界面
     *
     * @param datas
     * @param xList
     * @param yList
     */
    public void updateValueData(@NonNull List<Integer> datas, @NonNull List<String> xList, @NonNull List<Integer> yList) {
        this.mData = datas;
        this.xAxisList = xList;
        this.yAxisList = yList;
        init(mContext, true);
        invalidate();
    }

    public void setyNum(int yNum) {
        this.yNum = yNum;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
    }

    public void setMarkSize(int markSize) {
        this.markSize = markSize;
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setItemSpace(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setShowValue(boolean showValue) {
        isShowValue = showValue;
    }

    public void setShowMark(boolean showMark) {
        this.showMark = showMark;
    }

    public void setColors(boolean colors) {
        isColors = colors;
    }

    public void setShowXLines(boolean showXLines) {
        isShowXLines = showXLines;
    }

    public void setMaxYAxis(int maxYAxis) {
        this.maxYAxis = maxYAxis;
    }
}

