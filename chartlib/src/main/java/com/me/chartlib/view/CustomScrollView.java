package com.me.chartlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * add me
 * 2019.1.28
 * 可设置横向或纵向滑动的view
 */
public class CustomScrollView extends ScrollView {

    private GestureDetector mGestureDetector;
    private View.OnTouchListener mGestureListener;
    private int orientationType =0;

    public void setOrientationType(int type) {
        this.orientationType = type;
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new YScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    // Return false if we're scrolling in the x direction
    public class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(orientationType == 1){
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    return true;
                }else {
                    return false;
                }
            }else {
                if (Math.abs(distanceY) > Math.abs(distanceX)) {
                    return true;
                }
                return false;
            }
        }
    }
}
