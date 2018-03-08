package com.hdu.easyaccount.utils;

import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.constant.Type;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 数据库查找操作的封装类.
 */

public class AccountDAO {
    /**
     * 根据条件查找
     *
     * @param conditions 查找条件
     * @return
     */
    public static List<AccountInfo> where(String... conditions) {
        return DataSupport.order("accountId").where(conditions)
                .find(AccountInfo.class);
    }

    /**
     * 选择某一列查找
     *
     * @param columns 列名
     * @return
     */
    public static List<AccountInfo> select(String... columns) {
        return DataSupport.select(columns).order("accountId").find(AccountInfo.class);
    }

    /**
     * 快速查找常用且特定的数据
     *
     * @param type
     * @return
     */
    public static List<AccountInfo> quickFind(int type) {
        List<AccountInfo> list;
        switch (type) {
            //返回本月账单的List
            case Type.ACCOUNT_THIS_MONTH:
                list = DataSupport.where(
                        "year = ? and month = ?",
                        Utility.getTime(Type.TIME_YEAR),
                        Utility.getTime(Type.TIME_MONTH)
                ).order("accountId").find(AccountInfo.class);
                break;
            //返回本周账单的List
            case Type.ACCOUNT_THIS_WEEK:
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                String firstDay = new SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                        .format(calendar.getTime());
                calendar.add(Calendar.DATE, 6);
                String lastDay = new SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                        .format(calendar.getTime());
                firstDay = firstDay + "000000";
                lastDay = lastDay + "235959";
                list = DataSupport.where(
                        "accountId > ? and accountId<? ",
                        firstDay, lastDay
                ).order("accountId").find(AccountInfo.class);
                break;
            //返回今天账单的List
            case Type.ACCOUNT_TODAY:
                list = DataSupport.where(
                        "year = ? and month = ? and day = ?",
                        Utility.getTime(Type.TIME_YEAR),
                        Utility.getTime(Type.TIME_MONTH),
                        Utility.getTime(Type.TIME_DAY)
                ).order("accountId").find(AccountInfo.class);
                break;
            //返回所有账单
            case Type.ACCOUNT_ALL:
                list = DataSupport.order("accountId").find(AccountInfo.class);
                break;
            default:
                list = new ArrayList<>();
        }
        return list;
    }
}
