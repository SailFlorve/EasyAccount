package com.hdu.easyaccount.constant;

import com.hdu.easyaccount.R;

/**
 * 常量
 */

public class Type {
    //获取时间的类型
    public static final int TIME_YEAR = 1001;
    public static final int TIME_MONTH = 1002;
    public static final int TIME_DAY = 1003;
    public static final int TIME_WEEK_DAY = 1004;
    public static final int TIME_MONTH_AND_DAY = 1005;
    public static final int TIME_YESTERDAY = 1007;
    public static final int TIME_YEAR_MONTH_DAY = 1008;
    public static final int TIME_STRING_STREAM = 1013;
    //数据库查询的类型
    public static final int ACCOUNT_TOTAL_INCOME = 2001;
    public static final int ACCOUNT_TOTAL_EXPENSE = 2002;
    public static final int ACCOUNT_BALANCE = 2003;
    public static final int ACCOUNT_THIS_MONTH = 2004;
    public static final int ACCOUNT_TODAY = 2005;
    public static final int ACCOUNT_THIS_WEEK = 2006;
    public static final int ACCOUNT_ALL = 2007;
    //修改菜单栏图片的类型.
    public static final int CHANGE_IMG_PROFILE = 3001;
    public static final int CHANGE_IMG_BG = 3002;
    public static final int SAVE_PROFILE = 3003;
    //记账时类型
    public static final String TYPE_ALL = "全部";
    public static final String MONEY_SPEND_MEAL = "早中晚餐";
    public static final String MONEY_SPEND_SNACK = "副食烟酒";
    public static final String MONEY_SPEND_SHOPPING = "逛街购物";
    public static final String MONEY_SPEND_TRANSPORTATION = "交通出行";
    public static final String MONEY_SPEND_ENTERTAINMENT = "休闲娱乐";
    public static final String MONEY_SPEND_OTHERS = "其他支出";

    public static final String MONEY_INCOME_SALARY = "工资收入";
    public static final String MONEY_INCOME_BONUS = "奖金礼金";
    public static final String MONEY_INCOME_INVESTMENT = "经营收入";
    public static final String MONEY_INCOME_OTHERS = "其他收入";
}
