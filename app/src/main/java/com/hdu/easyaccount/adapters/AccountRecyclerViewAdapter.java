package com.hdu.easyaccount.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hdu.easyaccount.R;
import com.hdu.easyaccount.RecordActivity;
import com.hdu.easyaccount.constant.Type;
import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.utils.Utility;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 展示账单的RecyclerView的Adapter
 */
public class AccountRecyclerViewAdapter extends BaseQuickAdapter<AccountInfo, BaseViewHolder> {

    //账单显示的日期类型
    public static final int SHOW_DATE = 0;//只显示月日
    public static final int SHOW_DATE_WEEKDAY = 1;//显示月日和星期
    public static final int SHOW_WEEKDAY = 2;//只显示星期
    //默认显示月日
    private int showWhat = SHOW_DATE;

    public AccountRecyclerViewAdapter(int resId, List<AccountInfo> data) {
        super(resId, data);
    }

    //删除某个账单后回调
    public interface SelectCallback {
        void onDeleteIemClick();
    }

    @Override
    protected void convert(BaseViewHolder helper, AccountInfo item) {
        //为每个类型设置图片
        CircleImageView imageView = (CircleImageView) helper.getView(R.id.type_image_list_item);
        int typeImgResId;
        switch (item.getType()) {
            case Type.MONEY_SPEND_ENTERTAINMENT:
                typeImgResId = R.drawable.ic_type_entertainment;
                break;
            case Type.MONEY_SPEND_SHOPPING:
                typeImgResId = R.drawable.ic_type_shopping;
                break;
            case Type.MONEY_SPEND_SNACK:
                typeImgResId = R.drawable.ic_type_snack;
                break;
            case Type.MONEY_SPEND_MEAL:
                typeImgResId = R.drawable.ic_type_meal;
                break;
            case Type.MONEY_SPEND_TRANSPORTATION:
                typeImgResId = R.drawable.ic_type_transportation;
                break;
            case Type.MONEY_INCOME_BONUS:
                typeImgResId = R.drawable.ic_type_bonus;
                break;
            case Type.MONEY_INCOME_INVESTMENT:
                typeImgResId = R.drawable.ic_type_investment;
                break;
            case Type.MONEY_INCOME_SALARY:
                typeImgResId = R.drawable.ic_type_salary;
                break;
            default:
                typeImgResId = R.drawable.ic_type_others;
                break;
        }
        imageView.setImageResource(typeImgResId);
        //显示何种日期类型
        if (showWhat == SHOW_DATE) {
            helper.setText(R.id.date_text_list_item,
                    item.getMonth() + "月" + item.getDay() + "日");
        } else if (showWhat == SHOW_WEEKDAY) {
            helper.setText(R.id.date_text_list_item,
                    item.getWeekday());
        } else if (showWhat == SHOW_DATE_WEEKDAY) {
            helper.setText(R.id.date_text_list_item,
                    item.getMonth() + "月" + item.getDay() + "日 "
                            + item.getWeekday());
        }
        //设置类型文字
        helper.setText(R.id.type_text_list_item, item.getType());

        //设置备注文字，如果没有备注，显示为“无备注”
        String remark = item.getRemark();
        if (TextUtils.isEmpty(remark)) {
            helper.setText(R.id.remark_text_list_item, "无备注");
        } else if (remark.length() >= 15) {
            remark = remark.substring(0, 14) + "...";
            helper.setText(R.id.remark_text_list_item, remark);
        } else {
            helper.setText(R.id.remark_text_list_item, remark);
        }

        //设置收入支出文字
        TextView moneyText = (TextView) helper.getView(R.id.money_text_list_item);
        if (item.isExpense()) {
            moneyText.setText("-" + Utility.formatNum(item.getMoney(), 2));
            moneyText.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        } else {
            moneyText.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
            moneyText.setText("+" + Utility.formatNum(item.getMoney(), 2));
        }

    }

    //设置日期显示类型
    public void setDateType(int showWhat) {
        this.showWhat = showWhat;
    }

    //点击每个条目后弹出选择对话框,编辑或删除
    public static void onItemSelected(final Context context, final BaseQuickAdapter adapter, final int position, final SelectCallback callback) {
        new AlertDialog.Builder(context)
                .setItems(new String[]{"编辑", "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountRecyclerViewAdapter accountAdapter = (AccountRecyclerViewAdapter) adapter;
                        List<AccountInfo> data = accountAdapter.getData();
                        final AccountInfo info = data.get(position);
                        //选择编辑,打开RecordActivity进入编辑模式
                        if (which == 0) {
                            Intent intent = new Intent(context, RecordActivity.class);
                            intent.putExtra("is_edit_mode", true);
                            intent.putExtra("id", info.getAccountId());
                            intent.putExtra("is_expense", info.isExpense());
                            intent.putExtra("money", info.getMoney());
                            intent.putExtra("classify", info.getType());
                            intent.putExtra("remark", info.getRemark());

                            Calendar c = Calendar.getInstance();
                            c.set(info.getYear(), info.getMonth() - 1, info.getDay());
                            intent.putExtra("time", new SimpleDateFormat("yyyy年MM月dd日",
                                    Locale.getDefault()).format(c.getTime()));
                            context.startActivity(intent);
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle("删除确认")
                                    .setMessage("您确定要删除此记录吗？删除后记录将不可恢复。")
                                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DataSupport.deleteAll(AccountInfo.class, "accountId = ?", info.getAccountId());
                                            //删除回调
                                            callback.onDeleteIemClick();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create().show();
                        }
                    }
                }).create().show();
    }
}
