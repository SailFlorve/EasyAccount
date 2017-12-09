package com.hdu.easyaccount.bean.db;

import com.hdu.easyaccount.utils.Utility;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * 收支记录数据库实体类
 */

public class AccountInfo extends DataSupport {
    @Column(nullable = false)
    private String accountId;//账单ID

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int day;

    @Column(nullable = false)
    private String weekday;

    @Column(nullable = false)
    private boolean isExpense;//是否为支出，true：支出，false：收入。

    @Column(nullable = false)
    private String type;//类型

    @Column(nullable = false)
    private double money;//钱数的绝对值

    private String remark;//备注

    public AccountInfo(String accountId, int year, int month, int day, boolean isExpense, String type, double money, String remark) {
        this.accountId = accountId;
        this.year = year;
        this.month = month;
        this.day = day;
        //自动计算出周几
        this.weekday = Utility.getWeekday(year, month, day);
        this.isExpense = isExpense;
        this.type = type;
        this.money = money;
        this.remark = remark;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public void setExpense(boolean expense) {
        isExpense = expense;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
