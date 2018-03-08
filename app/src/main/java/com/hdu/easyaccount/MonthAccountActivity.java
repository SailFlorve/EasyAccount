package com.hdu.easyaccount;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hdu.easyaccount.adapters.AccountRecyclerViewAdapter;
import com.hdu.easyaccount.constant.Type;
import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.utils.AccountDAO;
import com.hdu.easyaccount.utils.Utility;

import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * 展示某月账单的Activity
 */
public class MonthAccountActivity extends BaseActivity {

    //要展示的年月
    private String mYear;
    private String mMonth;

    private PieChartView pieChartView;
    private RecyclerView monthAccountListView;
    private AccountRecyclerViewAdapter adapter;
    private TextView balanceText;
    private CardView noAccountCard;
    private TextView titleText;
    private TextView incomeText;
    private TextView expenseText;
    private ImageView backButton;
    private Button showAllDataButton;
    private LineChartView lineChartView;
    private List<AccountInfo> infoList = new ArrayList<>();
    //当前饼图点击,默认为-1(没有点击饼图)
    private int currentSelect = -1;
    //展示类型,默认为全部类型(点击饼图后会展示点击的类型)
    private String dataType = Type.TYPE_ALL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_account);
        //获取intent中传来的年月信息
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mYear = extras.getString("year");
            mMonth = extras.getString("month");
        } else {
            Toast.makeText(this, "月份异常。", Toast.LENGTH_SHORT).show();
            finish();
        }
        setToolbar(R.id.toolbar_month, false);
        pieChartView = (PieChartView) findViewById(R.id.month_pie_chart_view);
        monthAccountListView = (RecyclerView) findViewById(R.id.month_account_recycler_view);
        balanceText = (TextView) findViewById(R.id.balance_text_activity_month);
        incomeText = (TextView) findViewById(R.id.income_text_activity_month);
        expenseText = (TextView) findViewById(R.id.expense_text_activity_month);
        noAccountCard = (CardView) findViewById(R.id.month_no_account_card);
        titleText = (TextView) findViewById(R.id.activity_month_title);
        showAllDataButton = (Button) findViewById(R.id.show_month_data);
        lineChartView = (LineChartView) findViewById(R.id.activity_month_line_chart_view);
        backButton = (ImageView) findViewById(R.id.back_img_month);

        showAllDataButton.setVisibility(View.GONE);
        titleText.setText(mYear + "年" + mMonth + "月" + "账单");

        initRecyclerView();
        //初始化饼图
        setPieChart();

        //饼图点击事件,选择某个类型后,设置数据显示为该类型
        pieChartView.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                if (currentSelect == arcIndex) return;
                showAllDataButton.setVisibility(View.VISIBLE);
                currentSelect = arcIndex;
                setAllData(String.valueOf(value.getLabelAsChars()));
                titleText.setText(dataType + "账单");
            }

            @Override
            public void onValueDeselected() {
            }
        });

        //折线图点击事件,选择某个日期后,设置数据显示为该日期
        lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                showAllDataButton.setVisibility(View.VISIBLE);
                List<AccountInfo> list;
                if (dataType.equals(Type.TYPE_ALL)) {
                    list = AccountDAO.where("year = ? and month = ? and day = ?",
                            mYear, mMonth, String.valueOf(pointIndex + 1));

                } else {
                    list = AccountDAO.where("year = ? and month = ? " +
                                    "and day = ? and type = ?",
                            mYear, mMonth, String.valueOf(pointIndex + 1), dataType);
                }
                setList(list);
            }

            @Override
            public void onValueDeselected() {
            }
        });

        //显示所有数据(清除日期和类型的选择)
        showAllDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllDataButton.setVisibility(View.GONE);
                currentSelect = -1;
                titleText.setText(mYear + "年" + mMonth + "月" + "账单");
                setAllData(Type.TYPE_ALL);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AccountRecyclerViewAdapter.onItemSelected(MonthAccountActivity.this, adapter, position,
                        new AccountRecyclerViewAdapter.SelectCallback() {
                            @Override
                            public void onDeleteIemClick() {
                                setAllData(dataType);
                            }
                        });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAllData(dataType);
    }

    /**
     * 设置标题栏,折线图,RecyclerView等数据
     * @param type
     */
    private void setAllData(String type) {
        dataType = type;
        setHeaderData();
        setLineChart();
        setRecyclerView();
    }

    /**
     * 设置饼图数据
     */
    private void setPieChart() {
        List<SliceValue> values = new ArrayList<>();
        //获取支出类型数组
        String[] expenseTypeArray = getResources()
                .getStringArray(R.array.default_expense_type);
        values.clear();
        //遍历所有支出类型,添加数据
        for (String anExpenseTypeArray : expenseTypeArray) {
            float money = DataSupport.where("year = ? and month = ? and type = ?",
                    mYear, mMonth, anExpenseTypeArray)
                    .sum(AccountInfo.class, "money", Float.class);
            if (money != 0f) {
                values.add(new SliceValue(money,
                        ChartUtils.pickColor())
                        .setLabel(anExpenseTypeArray));
            }
        }

        PieChartData pieChartData = new PieChartData(values);
        pieChartData.setHasLabels(true);
        pieChartData.setHasLabelsOnlyForSelected(false);
        pieChartData.setHasLabelsOutside(false);
        pieChartData.setHasCenterCircle(true);
        pieChartData.setValueLabelTextSize(10);
        pieChartData.setSlicesSpacing(1);//设置分离距离
        //饼图属性设置
        pieChartView.setPieChartData(pieChartData);
        pieChartView.setCircleFillRatio(1);//设置放大缩小范围
        pieChartView.setChartRotationEnabled(false);
    }

    /**
     * 设置折线图数据
     */
    private void setLineChart() {
        List<PointValue> mPointValues = new ArrayList<>();
        List<AxisValue> mAxisValues = new ArrayList<>();

        //获得本月的天数
        int dayNum = Utility.getDayNum(Integer.parseInt(mYear), Integer.parseInt(mMonth));
        //遍历天数,计算出每天的支出总数,添加到折线图
        for (int i = 0; i < dayNum; i++) {
            mAxisValues.add(new AxisValue(i).setLabel(String.valueOf(i + 1) + "日"));
            ClusterQuery query;
            //如果类型为全部类型
            if (dataType.equals(Type.TYPE_ALL)) {
                query = DataSupport.where(
                        "year = ? and month = ? and day = ? and isExpense = ?",
                        mYear, mMonth, String.valueOf(i + 1), "1");
            } else {
                query = DataSupport.where("year = ? and month = ? and day = ? and isExpense = ? and type = ?",
                        mYear, mMonth, String.valueOf(i + 1), "1", dataType);
            }
            mPointValues.add(new PointValue(i, query.sum(
                    AccountInfo.class, "money", Float.class)));
        }

        Line line = new Line(mPointValues).setColor(
                ContextCompat.getColor(this, R.color.colorBrown))
                .setCubic(false).setHasLabels(true);//折线的颜色
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部

        //设置行为属性，支持缩放、滑动以及平移

        lineChartView.setInteractive(true);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setZoomEnabled(false);
        lineChartView.setLineChartData(data);
        Viewport v1 = new Viewport(lineChartView.getMaximumViewport());
        v1.left = 0;
        v1.right = 6;
        v1.bottom = 0;
        lineChartView.setCurrentViewport(v1);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        adapter = new AccountRecyclerViewAdapter(R.layout.account_list_item, infoList);
        adapter.setDateType(AccountRecyclerViewAdapter.SHOW_DATE_WEEKDAY);
        monthAccountListView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        monthAccountListView.setLayoutManager(manager);
    }

    /**
     * 设置标题栏处数据
     */
    private void setHeaderData() {
        List<AccountInfo> list = getListByType(dataType);
        balanceText.setText(Utility.getAccountMoneyNum(list, Type.ACCOUNT_BALANCE));
        incomeText.setText(Utility.getAccountMoneyNum(list, Type.ACCOUNT_TOTAL_INCOME));
        expenseText.setText(Utility.getAccountMoneyNum(list, Type.ACCOUNT_TOTAL_EXPENSE));
    }

    /**
     * 设置RecyclerView的数据
     */
    private void setRecyclerView() {
        List<AccountInfo> list = getListByType(dataType);
        setList(list);
    }

    /**
     * 设置RecyclerView的list
     * @param list
     */
    private void setList(List<AccountInfo> list) {
        if (infoList == null) {
            infoList = new ArrayList<>();
        } else {
            infoList.clear();
        }
        Collections.reverse(list);
        if (!list.isEmpty()) noAccountCard.setVisibility(View.GONE);
        else noAccountCard.setVisibility(View.VISIBLE);
        infoList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 通过类型获得该类型的List
     * @param type 类型
     * @return
     */
    private List<AccountInfo> getListByType(String type) {
        List<AccountInfo> list;
        //如果为全部类型,where条件中不添加类型限定
        if (type.equals(Type.TYPE_ALL)) {
            list = AccountDAO.where("year = ? and month = ?",
                    mYear, mMonth);
        } else {
            list = AccountDAO.where("year = ? and month = ? and type = ?",
                    mYear, mMonth, dataType);
        }
        return list;
    }
}
