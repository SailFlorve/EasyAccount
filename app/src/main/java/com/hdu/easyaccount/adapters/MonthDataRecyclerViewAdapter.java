package com.hdu.easyaccount.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hdu.easyaccount.R;
import com.hdu.easyaccount.bean.MonthData;

import java.util.List;

/**
 * 年账单流水中RecyclerView的Adapter
 */
public class MonthDataRecyclerViewAdapter extends BaseQuickAdapter<MonthData, BaseViewHolder> {

    public MonthDataRecyclerViewAdapter(int resId, List<MonthData> data) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MonthData item) {
        helper.setText(R.id.month_data_year_text, item.getYear() + "年");
        helper.setText(R.id.month_data_month_text, item.getMonth());
        helper.setText(R.id.month_data_month_income, "收: ￥" + item.getIncome());
        helper.setText(R.id.month_data_month_expense, "支: ￥" + item.getExpense());
        helper.setText(R.id.month_data_month_balance, "￥" + item.getBalance());
    }
}
