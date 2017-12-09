package com.hdu.easyaccount.bean;

import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.constant.Type;
import com.hdu.easyaccount.utils.Utility;

import java.util.List;

/**
 * 显示月数据的列表Adapter实体类
 */

public class MonthData {
    private String year;
    private String month;
    private List<AccountInfo> monthList;
    private String income;
    private String expense;
    private String balance;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<AccountInfo> getMonthList() {
        return monthList;
    }

    public void setMonthList(List<AccountInfo> monthList) {
        this.monthList = monthList;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public MonthData(String year, String month, List<AccountInfo> list) {
        this.year = year;
        this.month = month;
        this.monthList = list;
        income = Utility.getAccountMoneyNum(monthList, Type.ACCOUNT_TOTAL_INCOME);
        expense = Utility.getAccountMoneyNum(monthList, Type.ACCOUNT_TOTAL_EXPENSE);
        balance = (Utility.getAccountMoneyNum(monthList, Type.ACCOUNT_BALANCE));

    }
}
