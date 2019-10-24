package com.me.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.me.chartlib.adapter.BaseViewDataAdapter;
import com.me.chartlib.bean.PointsModel;
import com.me.chartlib.bean.SatellitesModel;
import com.me.chartlib.view.BarChartView;
import com.me.chartlib.view.CommonBarChart;
import com.me.chartlib.view.PieChartView;
import com.me.chartlib.view.PointView;
import com.me.chartlib.view.StarrySkyView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private StarrySkyView satsview;
    private BarChartView barView;
    private PieChartView pieView;
    private CommonBarChart cbcTest;
    private PointView pv;

    private ArrayList<SatellitesModel> sates = new ArrayList<>();
    private BaseViewDataAdapter adapter;
    private int[] colors = new int[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        colors[0] = getResources().getColor(R.color.red);
        colors[1] = getResources().getColor(R.color.green);
        colors[2] = getResources().getColor(R.color.brown_f5a623);
        colors[3] = getResources().getColor(R.color.yellow);
        colors[4] = getResources().getColor(R.color.white);
        colors[5] = getResources().getColor(R.color.grey_979797);
        colors[6] = getResources().getColor(R.color.acc_count_oval_green);
        adapter = new BaseViewDataAdapter();
        barView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        testData();
    }

    private void initView() {
        cbcTest = findViewById(R.id.cbc_test);
        satsview = findViewById(R.id.phone_satsview);
        barView = findViewById(R.id.phone_chartview);
        pieView = findViewById(R.id.pie_view);
        satsview.setShapeMore(true);
        satsview.setColors(colors);
        barView.setColors(colors);
        pv = findViewById(R.id.pv_view);
        pv.setLineColor(Color.YELLOW);
        pv.setLineWidth(getResources().getDimension(R.dimen.dp_1));
        pv.setMarkTag("cm");
        pv.setTextColor(Color.BLACK);
        pv.setTextSize(getResources().getDimension(R.dimen.sp_8));
        pv.setSetColor(true);
        pv.setPointColor(Color.BLUE);
        pv.setPointWidth((int)getResources().getDimension(R.dimen.dp_10));
    }

    private void testData() {
        initPie();
        initPv();
        sates.clear();
        for (int i = 1; i < 10; i++) {
            SatellitesModel model = new SatellitesModel(1, i, (float) (i * 10.3)
                    , (float) (i * 8.2), (float) (i * 12.1));
            model.setIsUsed(true);
            if (i < 5) {
                model.setmConstellationType(i);
                model.setmSystem("gps");
            }else {
                model.setmSystem("bds");
            }
            sates.add(model);
        }
        Collections.sort(sates);
        satsview.setSatellitesData(sates);
        adapter.addData(sates);
        adapter.notifyDataSetChanged();
        barView.invalidate();
        testCbc();

    }

    private void initPv(){
        ArrayList<PointsModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PointsModel model = new PointsModel(40.22, 118.11, 20.1, 1);
            model.enu[0] = 0.1 + i;
            model.enu[1] = 0.2 + i;
            model.enu[2] = 0.5 * i;
            list.add(model);
        }
        pv.setPointViewData(list);
    }

    private void initPie() {
        List<PieChartView.PieceDataHolder> pieceDataHolders = new ArrayList<>();
        pieceDataHolders.add(new PieChartView.PieceDataHolder(0, Color.YELLOW, "无效解"));
        pieceDataHolders.add(new PieChartView.PieceDataHolder(30, 0xFF77CCAA, "单点解"));
        pieceDataHolders.add(new PieChartView.PieceDataHolder(20, 0xFF11AA33, "浮点解"));
        pieceDataHolders.add(new PieChartView.PieceDataHolder(10, Color.GRAY, "浮点差分"));
        pieceDataHolders.add(new PieChartView.PieceDataHolder(22, Color.RED, "固定解"));
        pieceDataHolders.add(new PieChartView.PieceDataHolder(15, Color.BLUE, "固定差分"));
        pieView.setData(pieceDataHolders);
    }

    private void testCbc(){
        List<Integer> datas = new ArrayList(){{add(60); add(56); add(100); add(50); add(1200);add(67);}};
        List<String> xList = new ArrayList(){{add("2015"); add("2016"); add("2017"); add("2018"); add("19");}};
        //根据数据的最大值生成上下对应的Y轴坐标范围
        List ylist = new ArrayList();

        for (int i = 0;i < datas.size(); i++) {
            ylist.add(datas.get(i));
        }
        cbcTest.updateValueData(datas, xList, ylist);
        cbcTest.setColors(colors);
    }

}
