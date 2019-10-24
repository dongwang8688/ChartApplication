package com.me.chartlib.bean;

/**
 * <pre>
 *     author : me
 *     time   : 2018/01/28
 *     desc   : satellites information;
 *     version: 1.0
 * </pre>
 */

public class SatellitesModel implements Comparable<SatellitesModel> {

    private int mPrn;
    private float mSnr;
    private float mElevation;
    private float mAzimuth;
    private boolean mIsUsed = true;
    private String mSystem;
    private int mConstellationType;

    public int getmConstellationType() {
        return mConstellationType;
    }

    public void setmConstellationType(int mConstellationType) {
        this.mConstellationType = mConstellationType;
    }

    public void setIsUsed(boolean mIsUsed) {
        this.mIsUsed = mIsUsed;
    }

    public void setmSystem(String mSystem) {
        this.mSystem = mSystem;
    }

    public SatellitesModel(int constellationType, int prn, float snr, float elevation, float azimuth) {
        this.mPrn = prn;
        this.mSnr = snr;
        this.mElevation = elevation;
        this.mAzimuth = azimuth;
        this.mConstellationType = constellationType;
    }

    public int getmPrn() {
        return mPrn;
    }

    public void setmPrn(int mPrn) {
        this.mPrn = mPrn;
    }

    public float getmSnr() {
        return mSnr;
    }

    public void setmSnr(float mSnr) {
        this.mSnr = mSnr;
    }

    public float getmElevation() {
        return mElevation;
    }

    public void setmElevation(float mElevation) {
        this.mElevation = mElevation;
    }

    public float getmAzimuth() {
        return mAzimuth;
    }

    public void setmAzimuth(float mAzimuth) {
        this.mAzimuth = mAzimuth;
    }

    public boolean usedInFix() {
        return mIsUsed;
    }

    public String getSystem() {
        return mSystem;
    }

    @Override
    public int compareTo(SatellitesModel o) {
//        int i = this.getmConstellationType() - o.getmConstellationType();//先按照系统排序
//        if(i == 0){
            return this.getmPrn() - o.getmPrn();//如果系统相同，再用卫星号进行排序
//        }
//        return i;
    }
}
