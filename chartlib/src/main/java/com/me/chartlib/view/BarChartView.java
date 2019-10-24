package com.me.chartlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;

import com.me.chartlib.R;
import com.me.chartlib.adapter.BaseViewDataAdapter;
import com.me.chartlib.bean.SatellitesModel;
import com.me.chartlib.utils.PathCacheManager;

import java.util.List;

/**
 * <pre>
 *     author : me
 *     time   : 2018/01/28
 *     desc   : draw bar at view
 *     version: 1.0
 * </pre>
 */
public class BarChartView extends BaseScrollerView {
    private final Paint paint = new Paint();

    /**
     * 绘制X轴和Y轴的颜色
     * 默认是系统自带的蓝色
     */
    private int lineColor = Color.GRAY;

    /**
     * y轴的最大刻度
     */
    private int yLineMax;

    /**
     * 绘制文字的大小
     */
    private int textSize;

    /**
     * 绘制文字的颜色
     */
    private int textColor = Color.BLACK;

    /**
     * 是否只显示第一象限
     */
    private boolean onlyFirstArea = false;

    /**
     * Path缓存管理器
     */
    private PathCacheManager pathCacheManager = new PathCacheManager();

    /**
     * 文字宽度的缓存，这里可以考虑直接使用Lrucache
     */
    private LruCache textWidthLruCache = new LruCache<String, Float>(6);

    /**
     * 是否显示刻度值
     */
    private boolean showMarkText = true;

    /**
     * 刻度文字的大小
     */
    private int markTextSize;

    /**
     * 刻度文字的颜色
     */
    private int markTextColor = Color.BLACK;

    /**
     * x轴的刻度值宽度
     */
    private float xMarkTextMaxWidth;

    /**
     * Y轴的留白
     */
    private float yLineSpace = 0f;

    private int border;//设置柱状图宽度;
    private int barColor;//设置柱状图颜色;
    private Paint barPaint;//画柱状图的画笔;
    private Paint mTextPaint;//写文字的画笔;

    private float top = 0f;
    private float bottom = 0f;
    private int mSnr = 0;
    private int mPrn = 0;
    private int mConstellationType;
    private SatellitesModel satellitesModel;
    private int[] colors;

    private final Paint getPaint() {
        return paint;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (showMarkText) {
            getPaint().setTextSize(markTextSize);
            xMarkTextMaxWidth = getTextWidth(String.valueOf(yLineMax));
            setDrawOffsetX(xMarkTextMaxWidth);
            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            setDrawOffsetY(fontMetrics.bottom - fontMetrics.top);
        }
        pathCacheManager.resetCache();
        drawXYLine(canvas);
        if (getAdapter() != null) {
            canvas.translate(getCanvasOffset() + getLineWidth(), -getLineWidth());
            canvas.clipRect(getRealX(getLineWidth() - getCanvasOffset()), 0.0F, (float) getWidth() - getCanvasOffset(), (float) getHeight());
            drawData(canvas);
            canvas.restore();
        }
    }

    private final void drawXYLine(Canvas canvas) {
        getPaint().setColor(lineColor);
        getPaint().setStrokeWidth(getLineWidth());
        getPaint().setStyle(Paint.Style.STROKE);
        drawXLine(canvas);
        drawYLine(canvas);
//        drawYMarkText(canvas, 0, 0);
    }

    private final void drawXLine(Canvas canvas) {
        float width = (float) getWidth();
        if (onlyFirstArea) {
            canvas.drawLine(getRealX(0.0F), getRealY((float) getHeight() - getLineWidth() / (float) 2), width, getRealY((float) getHeight() - getLineWidth() / (float) 2), getPaint());
        } else {
            float yCenter = ((float) getHeight() - getLineWidth()) / (float) 2;
            canvas.drawLine(getRealX(0.0F), getRealY(yCenter), width, getRealY(yCenter), getPaint());
        }
    }

    private final void drawYLine(Canvas canvas) {
        float offsetX = getRealX(getLineWidth() / (float) 2);
        canvas.drawLine(offsetX, getRealY(0.0F), offsetX, getRealY((float) getHeight()), getPaint());
    }

    private final void drawYMarkText(Canvas canvas, int index, float yLineSpace) {
        if (showMarkText) {
            getPaint().setColor(markTextColor);
            getPaint().setTextSize(markTextSize);
            getPaint().setPathEffect((PathEffect) null);
            getPaint().setStyle(Paint.Style.FILL);
            Rect textRect = new Rect();
            for (int i = 0; i <= 5; i++) {
                //绘制Y轴的文字
                String text = String.valueOf(yLineMax - i * yLineMax / getYLineMarkCount());
                getPaint().getTextBounds(text, 0, text.length(), textRect);
                canvas.drawText(text, getRealX(0.0F), getMeasuredHeight() + i * yLineSpace, getPaint());
                Log.i("barchart","test = " + text +" x = " +(getRealX(0.0F) - textRect.width()) + " y = " + (getMeasuredHeight() + i * yLineSpace));
//                String text = "test";
//                canvas.drawText(text, getRealX(0.0F), getRealY((float) getHeight()) / (float) getYLineMarkCount() * (float) index + yLineSpace, getPaint());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private final void drawData(Canvas canvas) {
        getPaint().setPathEffect((PathEffect) null);
        BaseViewDataAdapter dataAdapter = getAdapter();
        if (dataAdapter != null) {
            List dataList = dataAdapter.getData();
            if (dataList != null) {
                drawCno(canvas, dataList);
                return;
            }
        }
    }

    //绘制信噪比柱状图
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawCno(Canvas canvas, List<SatellitesModel> dataList) {
        int startIndex = getDataStartIndex();
        int endIndex = getDataEndIndex(startIndex);
        for (int index = startIndex; index < endIndex && index < dataList.size(); ++index) {
            satellitesModel = dataList.get(index);
            mSnr = (int) satellitesModel.getmSnr();
            mPrn = satellitesModel.getmPrn();
            mConstellationType = satellitesModel.getmConstellationType();
            // 判断柱状图颜色 1=GPS,3=GLONASS,5=BDS,6=GALILEO
            if (satellitesModel.usedInFix()) {
                if (colors != null && colors.length > 0) {
                    if (mConstellationType == 1) {
                        barPaint.setColor(colors[0]);
                        mTextPaint.setColor(colors[0]);
                    } else if (mConstellationType == 2 && colors.length > 1) {
                        barPaint.setColor(colors[1]);
                        mTextPaint.setColor(colors[1]);
                    } else if (mConstellationType == 3 && colors.length > 2) {
                        barPaint.setColor(colors[2]);
                        mTextPaint.setColor(colors[2]);
                    } else if (mConstellationType == 4 && colors.length > 3) {
                        barPaint.setColor(colors[3]);
                        mTextPaint.setColor(colors[3]);
                    } else if (mConstellationType == 5 && colors.length > 4) {
                        barPaint.setColor(colors[4]);
                        mTextPaint.setColor(colors[4]);
                    } else if (mConstellationType == 6 && colors.length > 5) {
                        barPaint.setColor(colors[5]);
                        mTextPaint.setColor(colors[5]);
                    } else {
                        barPaint.setColor(Color.BLUE);
                        mTextPaint.setColor(Color.BLUE);
                    }
                } else {
                    if (mConstellationType == 1) {
                        barPaint.setColor(Color.GREEN);
                        mTextPaint.setColor(Color.GREEN);
                    } else if (mConstellationType == 3) {
                        barPaint.setColor(Color.parseColor("#F5A623"));
                        mTextPaint.setColor(Color.parseColor("#F5A623"));
                    } else if (mConstellationType == 5) {
                        barPaint.setColor(Color.RED);
                        mTextPaint.setColor(Color.RED);
                    } else if (mConstellationType == 6) {
                        barPaint.setColor(Color.YELLOW);
                        mTextPaint.setColor(Color.YELLOW);
                    } else {
                        barPaint.setColor(Color.BLUE);
                        mTextPaint.setColor(Color.BLUE);
                    }
                }
                //只有使用的规定系统卫星显示--服务结算出来的系统只用默认0和1
//                if (mConstellationType == 0 || mConstellationType == 1 || mConstellationType == 3
//                        || mConstellationType == 5 || mConstellationType == 6) {
                    //滑动后计算x坐标
                    float xPos = calculateXPosition(startIndex, index);
                    bottom = Math.abs(getRealY(getHeight()));
                    top = bottom - Math.abs(mSnr * border / 6);

                    canvas.drawRoundRect(xPos - border / 2, top, xPos + border / 2, bottom, 9, 6, barPaint);
                    if (mSnr < 10)
                        canvas.drawText(String.valueOf(mSnr), xPos - border / 4, top - border / 3, mTextPaint);
                    else
                        canvas.drawText(String.valueOf(mSnr), xPos - border / 2, top - border / 3, mTextPaint);
                    if (mPrn < 10)
                        canvas.drawText(String.valueOf(mPrn), xPos - border / 4, bottom + border, mTextPaint);
                    else
                        canvas.drawText(String.valueOf(mPrn), xPos - border / 2, bottom + border, mTextPaint);
//                Log.i("barchart","prn = " + mPrn+" bottom = " + bottom +border);
//                }
            }
        }
    }

    //    计算x坐标
    private final float calculateXPosition(int startIndex, int index) {
        float xpos = getDataDotGravity() == DataDotGravity.CENTER ? getMarkWidth() / 2 + (float) (index - startIndex) * getMarkWidth() : getMarkWidth() + (float) (index - startIndex) * getMarkWidth();
//        Log.i("barchart","getMarkWidth = " + getMarkWidth()+" xpos = " + xpos);
        return xpos;
    }

//    计算y坐标
//    private final float calculateYPosition(ChartBean value) {
//        float scale = value.getNumber() / (float) this.yLineMax;
//        float yCenter = this.onlyFirstArea ? (float) this.getHeight() - this.getLineWidth() : ((float) this.getHeight() - this.getLineWidth()) / (float) 2;
//        return yCenter - yCenter * scale;
//    }

    private final float getTextWidth(String key) {
        Float width = (Float) this.textWidthLruCache.get(key);
        if (width == null) {
            width = this.getPaint().measureText(key);
            textWidthLruCache.put(key, width);
        }
        return width;
    }

    public BarChartView(Context context, @Nullable AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
        yLineMax = (int)getResources().getDimension(R.dimen.dp_180);
        xMarkTextMaxWidth = getResources().getDimension(R.dimen.dp_0);
        border = (int) getResources().getDimension(R.dimen.dp_10);
        textSize = (int) getResources().getDimension(R.dimen.sp_8);
        markTextSize = (int) getResources().getDimension(R.dimen.sp_10);
        TypedArray typedArray = context.obtainStyledAttributes(attributes, R.styleable.BarChartView);
        lineColor = typedArray.getColor(R.styleable.BarChartView_lineColor, lineColor);
        yLineMax = typedArray.getInt(R.styleable.BarChartView_yLineMax, yLineMax);
        textSize = typedArray.getDimensionPixelSize(R.styleable.BarChartView_numTextSize, textSize);
        textColor = typedArray.getColor(R.styleable.BarChartView_textColor, textColor);
        onlyFirstArea = typedArray.getBoolean(R.styleable.BarChartView_onlyFirstArea, false);
        showMarkText = typedArray.getBoolean(R.styleable.BarChartView_showMarkText, false);
        markTextSize = typedArray.getDimensionPixelSize(R.styleable.BarChartView_markTextSize, markTextSize);
        markTextColor = typedArray.getColor(R.styleable.BarChartView_markTextColor, markTextColor);
        border = typedArray.getDimensionPixelSize(R.styleable.BarChartView_barBorder, border);
        barColor = typedArray.getColor(R.styleable.BarChartView_barColor, barColor);
        typedArray.recycle();
        //画柱状图的画笔；
        barPaint = new Paint();
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setColor(barColor);
        //写文字的画笔;
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
    }

    public BarChartView(Context context, @Nullable AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public BarChartView(Context context) {
        this(context, (AttributeSet) null);
    }


    public final float getTextSize() {
        return this.textSize;
    }

    public final void setTextSize(int texts) {
        this.textSize = texts;
    }

    public final int getTextColor() {
        return this.textColor;
    }

    public final void setTextColor(int textc) {
        this.textColor = textc;
    }

    public void setColors(int[] pColors) {
        this.colors = pColors;
    }
}
