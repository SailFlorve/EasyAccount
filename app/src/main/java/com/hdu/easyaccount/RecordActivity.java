package com.hdu.easyaccount;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdu.easyaccount.adapters.RecordFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账Activity
 */
public class RecordActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExpenseRecordFragment expenseFragment;
    private IncomeRecordFragment incomeFragment;
    private Button saveButton;
    private Button cancelButton;
    private ImageView backImg;
    private TextView saveText;

    private boolean isEditMode = false;//是否为编辑模式，默认false。
    //为编辑模式准备 用于显示编辑前的信息
    private String id;
    private boolean isExpense;
    private double money;
    private String type;
    private String remark;
    private String time;

    List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setToolbar(R.id.toolbar_month, false);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_record);
        viewPager = (ViewPager) findViewById(R.id.view_pager_record);
        saveButton = (Button) findViewById(R.id.save_record);
        cancelButton = (Button) findViewById(R.id.cancel_record);
        backImg = (ImageView) findViewById(R.id.back_img_month);
        saveText = (TextView) findViewById(R.id.save_text_record);
        initTabLayout();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isEditMode = extras.getBoolean("is_edit_mode");
            id = extras.getString("id");
            money = extras.getDouble("money");
            type = extras.getString("classify");
            remark = extras.getString("remark");
            isExpense = extras.getBoolean("is_expense");
            time = extras.getString("time");
            //编辑模式下如果为支出,设置viewPager显示
            if (isExpense) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        }

        saveButton.setOnClickListener(this);
        saveText.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        backImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_record:
            case R.id.save_text_record:
                saveRecord();
                break;
            case R.id.cancel_record:
            case R.id.back_img_month:
                finish();
                break;
            default:
        }
    }

    /**
     * 初始化TabLayout
     */
    private void initTabLayout() {
        List<String> titles = new ArrayList<>();
        fragments = new ArrayList<>();
        titles.add("支出记录");
        titles.add("收入记录");
        fragments.add(new ExpenseRecordFragment());
        fragments.add(new IncomeRecordFragment());
        RecordFragmentPagerAdapter adapter = new RecordFragmentPagerAdapter(
                getSupportFragmentManager(),
                titles, fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        expenseFragment = (ExpenseRecordFragment) adapter.getItem(0);
        incomeFragment = (IncomeRecordFragment) adapter.getItem(1);
    }

    /**
     * 保存记录
     */
    private void saveRecord() {
        int position = tabLayout.getSelectedTabPosition();
        boolean isExpense;
        //如果tabLayout位置为0说明是支出
        isExpense = tabLayout.getSelectedTabPosition() == 0;
        if (position == 0) {
            //如果处于编辑模式下,调用fragment的updateData方法更新数据
            if (isEditMode) {
                if (expenseFragment.updateData(id, isExpense)) {
                    onBackPressed();
                }
            } else {
                //如果不处于编辑模式,调用fragment的save方法保存新的数据
                if (expenseFragment.saveToDatabase()) {
                    onBackPressed();
                }
            }
        } else {
            if (isEditMode) {
                if (incomeFragment.updateData(id, isExpense)) {
                    onBackPressed();
                }
            } else {
                if (incomeFragment.saveToDatabase()) {
                    onBackPressed();
                }
            }
        }
    }

    //对Fragment开放的方法,设置Fragment中的数据
    public double getMoney() {
        return money;
    }

    public String getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public String getTime() {
        return time;
    }
}
