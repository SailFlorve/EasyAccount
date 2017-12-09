package com.hdu.easyaccount.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.hdu.easyaccount.constant.Type;
import com.hdu.easyaccount.bean.db.AccountInfo;

import org.litepal.crud.DataSupport;

/**
 * 工具类
 */

public class Utility {
    private static final String TAG = "Utility";
    private static String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    /**
     * 保留double类型的数字为几位小数。
     *
     * @param d   数字
     * @param num 位数
     * @return 保留小数后，返回的字符串
     */
    public static String formatNum(double d, int num) {
        if (num == 2) {
            return String.format("%.2f", d);
        } else {
            return String.format("%.1f", d);
        }

    }

    /**
     * 获取各类时间的字符串形式
     *
     * @param type
     * @return
     */
    public static String getTime(int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        switch (type) {
            //返回今天的日期
            case Type.TIME_DAY:
                return String.valueOf(calendar.get(Calendar.DATE));

            //返回本月日期
            case Type.TIME_MONTH:
                return String.valueOf(calendar.get(Calendar.MONTH) + 1);

            //返回M月d日格式的日期
            case Type.TIME_MONTH_AND_DAY:
                return new SimpleDateFormat("M月d日", Locale.CHINA)
                        .format(calendar.getTime());

            //返回今日yyyy年MM月dd日格式的日期
            case Type.TIME_YEAR_MONTH_DAY:
                return new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
                        .format(calendar.getTime());

            //返回昨天日期
            case Type.TIME_YESTERDAY:
                calendar.add(Calendar.DATE, -1);
                return new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
                        .format(calendar.getTime());

            //返回周几
            case Type.TIME_WEEK_DAY:
                return getWeekday(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DATE));

            //返回年份
            case Type.TIME_YEAR:
                return String.valueOf(calendar.get(Calendar.YEAR));

            //返回时间字符串，例如120000，代表12时00分00秒。
            case Type.TIME_STRING_STREAM:
                return new SimpleDateFormat("HHmmss", Locale.CHINA)
                        .format(calendar.getTime());

            default:
                return String.valueOf(calendar.get(type));
        }
    }

    /**
     * 获取参数中的年月日当天为星期几
     * @param year
     * @param month
     * @param day
     * @return 星期
     */
    public static String getWeekday(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        return weekDays[c.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 获取参数中的年月日当天为星期几
     * @param year
     * @param month
     * @param day
     * @return 星期
     */
    public static String getWeekday(String year, String month, String day) {
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        return weekDays[c.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 获得参数中年月的天数
     *
     * @param year
     * @param month
     * @return 天数
     */
    public static int getDayNum(int year, int month) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year);
        time.set(Calendar.MONTH, month - 1);
        return time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
    }

    /**
     * 得到该infoList中的总收入、总支出或结余。
     *
     * @param infoList 要计算的List
     * @param type 返回类型
     * @return
     */
    public static String getAccountMoneyNum(List<AccountInfo> infoList, int type) {
        if (infoList.isEmpty()) return "0.00";
        double totalIncome = 0f;
        double totalExpense = 0f;
        double balance = 0f;
        for (AccountInfo info : infoList) {
            if (info.isExpense()) {
                totalExpense += info.getMoney();
            } else {
                totalIncome += info.getMoney();
            }
        }
        balance = totalIncome - totalExpense;
        switch (type) {
            //返回list中结余
            case Type.ACCOUNT_BALANCE:
                return formatNum(balance, 2);
            //返回list中总支出
            case Type.ACCOUNT_TOTAL_EXPENSE:
                return formatNum(totalExpense, 2);
            //返回list中总收入
            case Type.ACCOUNT_TOTAL_INCOME:
                return formatNum(totalIncome, 2);
            default:
                return "0.00";
        }
    }
}
