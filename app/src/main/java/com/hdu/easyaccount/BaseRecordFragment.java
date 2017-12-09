package com.hdu.easyaccount;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.constant.Type;
import com.hdu.easyaccount.utils.SharedPrefs;
import com.hdu.easyaccount.utils.Utility;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 收入记录和支出记录Fragment的基类
 */

abstract public class BaseRecordFragment extends Fragment {
    private final String TAG = "RecordFragment";
    protected EditText moneyText;
    protected LinearLayout classifyLayout;
    protected TextView classifyText;
    protected TextView timeText;
    protected EditText remarkText;
    protected TextInputLayout moneyInputLayout;
    protected LinearLayout timeLayout;

    //是否为编辑状态
    protected boolean isEditMode = false;
    //是否为支出
    protected boolean isExpense;
    //时间
    protected String year;
    protected String month;
    protected String day;
    //为编辑状态时,要编辑的账单信息
    protected double moneySaved;
    protected String timeSaved;
    protected String classifySaved;
    protected String remarkSaved;
    protected boolean expenseSaved;
    //类型数组
    protected String[] typeArray;
    protected SharedPrefs prefs;

    public BaseRecordFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 从Activity获取当前的模式是否为编辑模式
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        RecordActivity recordActivity = (RecordActivity) context;
        isEditMode = recordActivity.isEditMode();
        moneySaved = recordActivity.getMoney();
        classifySaved = recordActivity.getType();
        remarkSaved = recordActivity.getRemark();
        expenseSaved = recordActivity.isExpense();
        timeSaved = recordActivity.getTime();
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = new SharedPrefs(getContext());
        //初始化
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        moneyText = (EditText) view.findViewById(R.id.money_input_record);
        classifyLayout = (LinearLayout) view.findViewById(R.id.money_classify_record);
        classifyText = (TextView) view.findViewById(R.id.money_classify_text_record);
        timeText = (TextView) view.findViewById(R.id.money_time_record);
        remarkText = (EditText) view.findViewById(R.id.money_remark_record);
        moneyInputLayout = (TextInputLayout) view.findViewById(R.id.money_input_layout_record);
        timeLayout = (LinearLayout) view.findViewById(R.id.record_time_layout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //点击时间Layout,弹出时间选择对话框
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        //点击钱数Text后,清空TextView(方便输入)
        moneyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyInputLayout.setErrorEnabled(false);
                moneyText.setText("");
            }
        });
        //点击分类Layout
        classifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });
        //给时间的Text增加文本变化监听,当文本变化时,根据文本更新变量year month 和 day的值.
        timeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Date date = new Date();
                try {
                    date = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
                            .parse(s.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(date);
                month = new SimpleDateFormat("MM", Locale.getDefault()).format(date);
                day = new SimpleDateFormat("dd", Locale.getDefault()).format(date);
            }
        });
        //设置默认值为今日的时间
        timeText.setText(Utility.getTime(Type.TIME_YEAR_MONTH_DAY));
        //文本变化监听,用于限制小数点后只能输入两位
        moneyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                String temp = s.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }
        });
        //子类实现的方法
        initSettings();
    }

    /**
     * 保存前,检查输入有效性
     *
     * @param money
     * @return
     */
    protected boolean checkInput(String money) {
        if (money.indexOf(".") <= 0) moneyText.append(".00");
        //如果没有输入金额或金额为0
        if (TextUtils.isEmpty(money)) {
            moneyInputLayout.setError("请输入金额。");
            return false;
        } else if (Double.parseDouble(money) == 0f) {
            moneyInputLayout.setError("金额为0！");
            return false;
        } else if (Double.parseDouble(money) >= 1000000.0f) {
            //金额超过1百万
            Toast.makeText(getContext(), "金额太大了~", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (classifyText.getText().toString().equals(
                getResources().getString(R.string.none_type))) {
            Toast.makeText(getContext(), "请选择分类！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //弹出类型选择对话框
    protected void showTypeDialog() {
        String title = isExpense ? "选择支出类型" : "选择收入类型";
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setItems(typeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        classifyText.setText(typeArray[which]);
                    }
                }).create().show();
    }

    //弹出时间选择对话框
    protected void showDatePickerDialog() {
        final int yearNum = Integer.parseInt(year);
        final int monthNum = Integer.parseInt(month);
        final int dayNum = Integer.parseInt(day);

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSet, int monthSet, int daySet) {
                //如果选择的未来的日期,给出提示
                if (yearSet >= yearNum && monthSet >= monthNum && daySet > dayNum) {
                    Toast.makeText(getContext(), "你选择的日期太超前了。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar c = Calendar.getInstance();
                c.set(yearSet, monthSet, daySet);
                timeText.setText(new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
                        .format(c.getTime()));
            }//对话框默认选择的日期
        }, yearNum, monthNum - 1, dayNum).show();
    }

    /**
     * 处于编辑状态时,Activity调用此方法,用于更新数据库信息
     *
     * @param updateId  需要更新的Id
     * @param isExpense 是否为支出
     * @return
     */
    public boolean updateData(String updateId, boolean isExpense) {
        String money = moneyText.getText().toString();
        if (!checkInput(money)) return false;
        ContentValues values = new ContentValues();
        values.put("money", Double.parseDouble(money));
        values.put("type", classifyText.getText().toString());
        values.put("remark", remarkText.getText().toString());
        values.put("isExpense", isExpense);
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("weekday", Utility.getWeekday(year, month, day));
        values.put("accountId", year + month + day
                + Utility.getTime(Type.TIME_STRING_STREAM));

        DataSupport.updateAll(AccountInfo.class, values, "accountId = ?", updateId);
        return true;
    }

    /**
     * 设置金额,类型,时间,备注,用于编辑状态下显示编辑前的信息
     *
     * @param money
     * @param type
     * @param remark
     */
    public void setItemData(Double money, String type, String time, String remark) {
        moneyText.setText(Utility.formatNum(money, 2));
        if (expenseSaved == isExpense) {
            classifyText.setText(type);
        }
        remarkText.setText(remark);
        timeText.setText(time);
    }

    /**
     * 由Activity调用,保存至数据库
     *
     * @return
     */
    public boolean saveToDatabase() {
        String money = moneyText.getText().toString();
        if (!checkInput(money)) return false;

        AccountInfo info = new AccountInfo(
                year + month + day
                        + Utility.getTime(Type.TIME_STRING_STREAM),
                Integer.parseInt(year),
                Integer.parseInt(month),
                Integer.parseInt(day),
                isExpense,
                classifyText.getText().toString(),
                Double.parseDouble(money),
                remarkText.getText().toString()
        );
        return info.save();
    }

    /**
     * 子类需要重写此方法,设置金额文本颜色,设置是否为支出,设置类型数组,默认选择的类型,
     * 以及处于编辑模式时,设置编辑前的信息
     */
    abstract void initSettings();
}
