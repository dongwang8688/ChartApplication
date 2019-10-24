package com.me.chartlib.bean;

/**
 * <pre>
 *     author : me
 *     time   : 2018/01/28
 *     desc   : for points data
 *     version: 1.0
 * </pre>
 */
public class PointsModel {
    private double mLatitude;//纬度
    private double mLongitude;//经度
    private double mAltitude;//高度，海拔
    private int service_type; //服务类型
    private boolean isShowFlag = true;//当前点是否显示标志
    private boolean isOpen = false;
    //外部计算坐标差值
    public double[] enu = new double[3];
    //外部计算半径值
    public double caculatR;

    public boolean getIsShowFlag() {
        return isShowFlag;
    }

    public void setIsShowFlag(boolean isShowFlag) {
        this.isShowFlag = isShowFlag;
    }

    public PointsModel(double mLatitude, double mLongitude, double mAltitude, int service_type) {
        this.mAltitude = mAltitude;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.service_type = service_type;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public double getmAltitude() {
        return mAltitude;
    }

    public void setmAltitude(double mAltitude) {
        this.mAltitude = mAltitude;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }
}
