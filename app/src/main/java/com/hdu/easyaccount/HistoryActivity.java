package com.hdu.easyaccount;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hdu.easyaccount.adapters.MonthDataRecyclerViewAdapter;
import com.hdu.easyaccount.bean.MonthData;
import com.hdu.easyaccount.constant.Type;
import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.utils.AccountDAO;
import com.hdu.easyaccount.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 年账单流水Activity
 */
public class HistoryActivity extends BaseActivity {

    private TextView yearBalanceText;
    private TextView yearIncomeText;
    private TextView yearExpenseText;
    private TextView monthAverExpenseText;
    private TextView monthAverIncomeText;
    private RecyclerView monthDataRecyclerView;
    private ImageView backImg;
    private TextView chooseYear;
    private TextView titleText;
    private LineChartView lineChartView;
    private MonthDataRecyclerViewAdapter adapter;
    //显示数据的年份
    private String mYear;
    private List<AccountInfo> yearList = new ArrayList<>();
    private List<MonthData> monthDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setToolbar(R.id.toolbar_year, false);

        yearBalanceText = (TextView) findViewById(R.id.year_balance);
        yearIncomeText = (TextView) findViewById(R.id.year_income);
        yearExpenseText = (TextView) findViewById(R.id.year_expense);
        monthAverExpenseText = (TextView) findViewById(R.id.month_aver_expense);
        monthAverIncomeText = (TextView) findViewById(R.id.month_aver_income);
        chooseYear = (TextView) findViewById(R.id.choose_year);
        titleText = (TextView) findViewById(R.id.activity_year_title);
        lineChartView = (LineChartView) findViewById(R.id.year_line_chart);
        backImg = (ImageView) findViewById(R.id.back_img_year);
        monthDataRecyclerView = (RecyclerView) findViewById(R.id.month_data_list_view);
        //默认年份为当前
        mYear = Utility.getTime(Calendar.YEAR);
        initRecyclerView();

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        chooseYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAndSetYearData();
            }
        });
        //recyclerView点击事件,传年月给MonthAccountActivity并启动
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MonthData data = (MonthData) adapter.getItem(position);
                Intent intent = new Intent(HistoryActivity.this, MonthAccountActivity.class);
                intent.putExtra("year", data.getYear());
                intent.putExtra("month", data.getMonth());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAllData();
    }

    private void initRecyclerView() {
        adapter = new MonthDataRecyclerViewAdapter(
                R.layout.month_data_list_item, monthDataList);
        monthDataRecyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        monthDataRecyclerView.setLayoutManager(manager);
    }

    /**
     * 设置顶部标题栏,折线图以及RecyclerView的数据
     */
    private void setAllData() {
        yearList = AccountDAO.where("year = ?", mYear);
        setHeaderData();
        setLineChartData();
        setRecyclerView();
    }

    private void setHeaderData() {
        titleText.setText(mYear + "年流水");
        String yearExpense = Utility.getAccountMoneyNum(
                yearList, Type.ACCOUNT_TOTAL_EXPENSE);
        String yearIncome = Utility.getAccountMoneyNum(
                yearList, Type.ACCOUNT_TOTAL_INCOME);
        yearBalanceText.setText(Utility.getAccountMoneyNum(
                yearList, Type.ACCOUNT_BALANCE));
        yearExpenseText.setText("￥" + yearExpense);
        yearIncomeText.setText("￥" + yearIncome);
        monthAverExpenseText.setText("￥" + Utility.formatNum(
                Double.valueOf(yearExpense) / 12.0, 2));
        monthAverIncomeText.setText("￥" + Utility.formatNum(
                Double.valueOf(yearIncome) / 12.0, 2));
    }

    private void setLineChartData() {
        List<PointValue> pointValues = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();

        //遍历每个月,获取数值.
        for (int i = 0; i < 12; i++) {
            axisValues.add(new AxisValue(i).setLabel(String.valueOf(i + 1) + "月"));
            List<AccountInfo> list = AccountDAO.where(
                    "year = ? and month = ?",
                    mYear, String.valueOf(i + 1));
            if (list.isEmpty()) {
                pointValues.add(new PointValue(i, 0));
            } else {
                pointValues.add(new PointValue(i, Float.parseFloat(
                        Utility.getAccountMoneyNum(list, Type.ACCOUNT_TOTAL_EXPENSE))));
            }
        }

        Line line = new Line(pointValues).setColor(
                ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setCubic(false).setHasLabels(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        axisX.setValues(axisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部

        //设置行为属性
        lineChartView.setClickable(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setZoomEnabled(false);
        lineChartView.setLineChartData(data);
        //显示多少坐标
        Viewport v1 = new Viewport(lineChartView.getMaximumViewport());
        v1.left = 0;
        v1.right = 6;
        v1.bottom = 0;
        lineChartView.setCurrentViewport(v1);
    }

    private void setRecyclerView() {
        if (monthDataList == null) {
            monthDataList = new ArrayList<>();
        } else {
            monthDataList.clear();
        }

        int totalMonth;
        //如果时间为今年,最高月份为当前月份
        if (mYear.equals(Utility.getTime(Calendar.YEAR))) {
            totalMonth = Integer.parseInt(Utility.getTime(Type.TIME_MONTH));
        } else {
            //时间不是今年,最高月份为12.
            totalMonth = 12;
        }
        //遍历每个月,获取数据
        for (int i = totalMonth; i >= 1; i--) {
            String month = String.valueOf(i);
            List<AccountInfo> monthList = AccountDAO.where(
                    "year = ? and month = ?", mYear, month);
            MonthData data = new MonthData(mYear, month, monthList);
            monthDataList.add(data);
        }
        adapter.notifyDataSetChanged();
    }

    private void chooseAndSetYearData() {
        List<AccountInfo> list = AccountDAO.select("year");
        List<String> yearList = new ArrayList<>();
        //得到数据库中所有年份的List
        for (AccountInfo info : list) {
            String infoYear = String.valueOf(info.getYear());
            if (!yearList.contains(infoYear)) {
                yearList.add(infoYear);
            }
        }
        //如果为空,默认添加今年
        if (yearList.isEmpty()) {
            yearList.add(Utility.getTime(Type.TIME_YEAR));
        }
        Collections.reverse(yearList);
        final String[] yearArray = yearList.toArray(new String[0]);
        //列出所有年份,并选择展示
        new AlertDialog.Builder(this)
                .setTitle("选择年份")
                .setItems(yearArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mYear = yearArray[which];
                        setAllData();
                    }
                }).create().show();
    }
}
