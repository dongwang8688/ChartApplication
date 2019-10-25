# MEChartLib
add ScrollBarView,PointView and usage
usage:
1.main.xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/colorPrimary">
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_margin="5dp"
            android:orientation="vertical">

            <com.me.chartlib.view.CommonBarChart
                android:id="@+id/cbc_test"
                android:layout_width="match_parent"
                android:layout_height="260dp"
                android:layout_marginRight="@dimen/dp_30"
                app:cbcMarkSize="@dimen/sp_10"
                app:comTextColor="@color/yellow"
                app:xyLineColor="@color/yellow" />

            <com.me.chartlib.view.PieChartView
                android:id="@+id/pie_view"
                android:layout_width="fill_parent"
                android:layout_height="200dp"
                android:layout_gravity="center"
                android:layout_margin="@dimen/dp_10"
                app:circleRadius="@dimen/dp_70"
                app:lineSize="@dimen/dp_3"
                app:textSize="@dimen/sp_8" />

            <com.me.chartlib.view.PointView
                android:id="@+id/pv_view"
                android:layout_width="match_parent"
                android:layout_height="@dimen/dp_200" />
        </LinearLayout>
    </ScrollView>
</LinearLayout>
2.mainActivity.java
public class MainActivity extends AppCompatActivity {
    private PieChartView pieView;
    private CommonBarChart cbcTest;
    private PointView pv;
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
        pieView = findViewById(R.id.pie_view);
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
