package com.me.chartlib.callback;

import com.me.chartlib.bean.PointsModel;

import java.util.ArrayList;

/**
 * <pre>
 *     author : me
 *     time   : 2019/01/28
 *     desc   : pointView set data
 *     version: 1.0
 * </pre>
 */

public interface PointViewDataCallBack {

    void  setPointViewData(ArrayList<PointsModel> pointsModel);
}
