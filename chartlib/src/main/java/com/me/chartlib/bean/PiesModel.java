package com.me.chartlib.bean;

/**
 * <pre>
 *     author : me
 *     time   : 2018/01/28
 *     desc   : for pie data
 *     version: 1.0
 * </pre>
 */

public class PiesModel {

    private String name;//扇形上字儿的名字；
    private int color = 0;//扇形的颜色;
    private float angle = 0;//扇形的起始角；
    private int value;//扇形每一个区域代表的值
    private float percent;//value值占用的比例


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
