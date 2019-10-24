package com.me.chartlib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.OverScroller;

import com.me.chartlib.R;
import com.me.chartlib.adapter.BaseViewDataAdapter;

import java.util.Observable;
import java.util.Observer;

/**
 * 滑动控件基类--检测和计算滑动距离
 */
public class BaseScrollerView extends View {
    private Context mContext;
    /**
     * 绘制X轴和Y轴的宽度
     */
    protected float lineWidth = 5f;
    /**
     * x轴的刻度间隔
     * 因为x周是可以滑动的，所以只有刻度的数量这一个属性
     */
    private int xLineMarkCount = 10;
    /**
     * y轴的刻度个数
     */
    private int yLineMarkCount = 10;
    /**
     * 绘制圆点的位置
     */
    protected DataDotGravity dataDotGravity = DataDotGravity.CENTER;
    /**
     * 最大宽度，大于等于width
     */
    private int maxWidth = 0;
    /**
     * 每个刻度的宽度
     */
    protected float markWidth = 0f;
    /**
     * 绘制Y轴的偏移值，这个值用来绘制Y轴的文字
     */
    protected float drawOffsetX = 0f;
    /**
     * 绘制X轴的偏移值，这个值用来绘制X轴下面的文字
     */
    protected float drawOffsetY = 0f;
    /**
     * 是否能滑动
     */
    private boolean canScroll = false;
    /**
     * 滚动器Scroller
     */
    private OverScroller scroller;
    /**
     * 记录手指划过的距离
     */
    private float offsetX = 0f;
    /**
     * 数据适配器
     */
    BaseViewDataAdapter adapter;
    /**
     * 手势处理
     */
    private GestureDetector gestureDetector = new GestureDetector(mContext, new ChartGesture());
    /**
     * 惯性滑动辅助类
     */
    private ViewFling viewFling = new ViewFling();

    public BaseScrollerView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public BaseScrollerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public BaseScrollerView(Context context, AttributeSet attributes, int defStyleAttr) {
        super(context, attributes, defStyleAttr);
        mContext = context;
        init(attributes);
    }

    public void init(AttributeSet attributes) {
        scroller = new OverScroller(mContext);
        TypedArray typedArray = mContext.obtainStyledAttributes(attributes, R.styleable.BaseScrollerView);
        // 绘制X轴和Y轴的宽度
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.BaseScrollerView_lineWidth, 5);
        // 得到x轴的刻度数
        xLineMarkCount = typedArray.getInt(R.styleable.BaseScrollerView_xLineMarkCount, 10);
        // 得到y轴的刻度数
        yLineMarkCount = typedArray.getInt(R.styleable.BaseScrollerView_yLineMarkCount, 10);
        // 得到绘制数据点的位置
        if (typedArray.getInt(R.styleable.BaseScrollerView_dataDotGravity, 0) == 0) {
            dataDotGravity = DataDotGravity.LINE;
        } else {
            dataDotGravity = DataDotGravity.CENTER;
        }
        typedArray.recycle();

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
//                Log.i(TAG,String.format("getViewTreeObserver width = %d,height=%d",getWidth(),getHeight()));
//                增加布局更新重新测量绘制图形算法解决初始化后getWidth()获得view宽度为零bug
                calculateMaxWidth();
            }
        });

    }

    @Nullable
    public final BaseViewDataAdapter getAdapter() {
        return this.adapter;
    }

    public final void setAdapter(@Nullable BaseViewDataAdapter value) {
        this.adapter = value;
        this.invalidate();
        if (value != null) {
            value.addObserver((Observer) (new Observer() {
                public final void update(Observable $noName_0, Object $noName_1) {
                    BaseScrollerView.this.invalidate();
                }
            }));
        }

        this.calculateMaxWidth();
    }

    public DataDotGravity getDataDotGravity() {
        return dataDotGravity;
    }

    public void setDataDotGravity(DataDotGravity dataDotGravity) {
        this.dataDotGravity = dataDotGravity;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getxLineMarkCount() {
        return xLineMarkCount;
    }

    public void setxLineMarkCount(int xLineMarkCount) {
        this.xLineMarkCount = xLineMarkCount;
        this.calculateMaxWidth();
    }

    public int getYLineMarkCount() {
        return yLineMarkCount;
    }

    public void setyLineMarkCount(int yLineMarkCount) {
        this.yLineMarkCount = yLineMarkCount;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public float getMarkWidth() {
        return markWidth;
    }

    public void setMarkWidth(float markWidth) {
        this.markWidth = markWidth;
    }

    public float getDrawOffsetX() {
        return drawOffsetX;
    }

    public void setDrawOffsetX(float drawOffsetX) {
        this.drawOffsetX = drawOffsetX;
    }

    public float getDrawOffsetY() {
        return drawOffsetY;
    }

    public void setDrawOffsetY(float drawOffsetY) {
        this.drawOffsetY = drawOffsetY;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    /**
     * 计算最大宽度
     */
    private void calculateMaxWidth() {
        // 计算每一个刻度的宽度
        markWidth = Math.abs(getWidth() - drawOffsetX - lineWidth) / xLineMarkCount;
        // 得到数据的数量
        int count = adapter != null ? adapter.maxDataCount : 0;
        // 如果数据点在中心位置
        if (dataDotGravity == DataDotGravity.CENTER) {
            if (count < xLineMarkCount) {
                canScroll = false;
                maxWidth = getWidth();
            } else {
                canScroll = true;
                maxWidth = getWidth() / xLineMarkCount * count;
            }
        } else {
            // 如果数据点画在线上，计算是否可以滑动的时候，需要xLineMarkCount - 1
            if (count < xLineMarkCount - 1) {
                canScroll = false;
                maxWidth = getWidth();
            } else {
                canScroll = true;
                maxWidth = getWidth() / xLineMarkCount * count;
            }
        }
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateMaxWidth();
    }

    /**
     * 根据偏移值，计算绘制的数据的开始位置
     */
    protected int getDataStartIndex() {
        // 计算已经偏移了几个刻度
        float index = dataDotGravity == BaseScrollerView.DataDotGravity.CENTER ? (offsetX - drawOffsetX - markWidth / 2) / (markWidth) : (offsetX - drawOffsetX - markWidth) / (markWidth);
        if (index < 0) {
            return 0;
        } else {
            return (int) index;
        }
    }

    /**
     * 根据偏移值，计算绘制的数据的结束位置
     */
    protected int getDataEndIndex(int startIndex) {
        // 如果绘制的是第一个，直接返回偏移值
        return Math.min(startIndex + xLineMarkCount + 2, adapter.maxDataCount);
    }

    /**
     * 计算canvas绘制的偏移值
     * 偏移值 - 刻度值宽度 * 开始位置，相当于对刻度值宽度取模
     */
    protected float getCanvasOffset() {
        // 计算已经偏移了几个刻度
        int index = getDataStartIndex();
        // 计算与第一个刻度的偏移值
        // 请注意这个偏移值值得刻度的虚线的偏移值，不是圆点的偏移值
        float offset = (offsetX - drawOffsetX) % markWidth;
        // 如果是第一个刻度，直接返回偏移值
        if (index == 0) return getRealX(-offsetX);
            // 当绘制数据点的位置刻度的中心
        else if (dataDotGravity == DataDotGravity.CENTER) {
            // 如果正好滑动了当前绘制的第一个点，绘制的第一条虚线变成了之后的第一条虚线
            // 直接返回偏移值就可以了
            if (offset >= markWidth / 2) {
                return getRealX(-offsetX) % markWidth;
            }
            // 刻度到下一个圆点的距离，绘制虚线还是上一个刻度
            // 因为要绘制与上一条的连线，所有要多减去一个刻度的宽度
            else {
                return getRealX(-offsetX) % markWidth - markWidth;
            }
        }
        // 当绘制数据点的位置刻度的线上
        else {
            // 如果正好滑动了虚线的位置，不需要偏移值
            if (offset == 0f) {
                return getRealX(-offsetX) % markWidth;
            }
            // 其他情况都要绘制和上一条的虚线，所有要多减去一个刻度的宽度
            else {
                return getRealX(-offsetX) % markWidth - markWidth;
            }
        }
    }

    /**
     * 把计算的X坐标加上偏移值
     */
    protected float getRealX(float xPos) {
        return xPos + drawOffsetX;
    }

    /**
     * 把计算的Y坐标加上偏移值
     */
    protected float getRealY(float yPos) {
        return yPos - drawOffsetY;
    }

    /**
     * 重写手势
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果不能滑动，不处理手势滑动
        if (!canScroll) {
            return false;
        } else
            return gestureDetector.onTouchEvent(event);
    }

    /**
     * 检查滚动的范围是否已经越界
     *
     * @return 是否已经到了边界，如果已经到了边界，可以停止滚动
     */
    private boolean checkBounds() {
        // 如果小于0，那么等于0
        if (offsetX < 0) {
            offsetX = 0f;
            return true;
        }
        // 如果已经大于了最右边界
        else if (offsetX > maxWidth - getWidth() + drawOffsetX) {
            offsetX = maxWidth - getWidth() + drawOffsetX;
            return true;
        } else
            return false;
    }

    /**
     * View销毁时，停止滑动
     */
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewFling.stop();
    }

    /**
     * 图表手势处理类
     */
    private class ChartGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // 如果scroller正在滑动, 停止滑动
            if (!scroller.isFinished()) {
                viewFling.stop();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // 计算移动的位置
            offsetX += distanceX;
            // 边界检查
            checkBounds();
            invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("lzp", "velocity is : " + velocityX);
            scroller.fling((int) offsetX, 0,
                    -((int) velocityX), (int) velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE,
                    0, 0);
            viewFling.postOnAnimation();
            return true;
        }
    }

    /**
     * ViewFling滑动辅助类
     */
    private class ViewFling implements Runnable {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                offsetX = scroller.getCurrX();
                boolean isBound = checkBounds();
                Log.e("lzp", "offsetX is : " + offsetX);
                invalidate();
                if (isBound) {
                    scroller.abortAnimation();
                } else {
                    postOnAnimation();
                }
            }
        }
        /**
         * 开始滑动
         */
        public void postOnAnimation() {
            ViewCompat.postOnAnimation((View) BaseScrollerView.this, this);
        }
        /**
         * 停止滑动
         */
        public void stop() {
            removeCallbacks(this);
            scroller.abortAnimation();
        }
    }

    /**
     * 线条Style
     */
    public static enum DataDotGravity {
        /**
         * enum
         * 线上
         */
        LINE,

        /**
         * 中心
         */
        CENTER
    }

}
