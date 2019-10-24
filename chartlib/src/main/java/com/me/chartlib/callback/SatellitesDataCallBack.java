package com.me.chartlib.callback;

import com.me.chartlib.bean.SatellitesModel;
import java.util.List;

/**
 * <pre>
 *     author : me
 *     time   : 2019/01/23
 *     desc   : skyView data call back
 *     version: 1.0
 * </pre>
 */

public interface SatellitesDataCallBack {

    void setSatellitesData(List<SatellitesModel> satellitesModel);
}
