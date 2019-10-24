package com.me.chartlib.adapter;

import android.content.Context;

import com.me.chartlib.bean.SatellitesModel;
import com.me.chartlib.callback.SatellitesDataCallBack;

import java.util.List;

/**
 * <pre>
 *     author : me
 *     time   : 2018/01/28
 *     desc   : provider data for satellites
 *     version: 1.0
 * </pre>
 */

public class SatellitesDataAdapter {
    private Context context;
    private SatellitesDataCallBack satellitesDataCallBack;

    public SatellitesDataAdapter(Context context , SatellitesDataCallBack mSatellitesDataCallBack){

        this.context  = context;
        this.satellitesDataCallBack  = mSatellitesDataCallBack;
    }

    public void GetSatellitesData(List<SatellitesModel> satellitesModel){
        satellitesDataCallBack.setSatellitesData(satellitesModel);
    }
}
