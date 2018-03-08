package com.hdu.easyaccount;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hdu.easyaccount.adapters.AccountRecyclerViewAdapter;
import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.constant.Type;
import com.hdu.easyaccount.utils.AccountDAO;
import com.hdu.easyaccount.utils.Utility;
import com.hdu.easyaccount.view.GradeProgressView;
import com.john.waveview.WaveView;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private FloatingActionButton fab;
    private ImageView menuButton;
    private ImageView menuBgImg;
    private LinearLayout navHeaderUserLayout;
    private LinearLayout monthTotalLayout;
    private TextView showTodayText;
    private CircleImageView profileImg;
    private TextView monthTotalIncomeText;
    private TextView monthTotalExpenseText;
    private TextView budgetSurplusText;
    private TextView weekBalanceText;
    private TextView weekIncomeText;
    private TextView accountRecordText;
    private TextView weekExpenseText;
    private TextView monthBalanceText;
    private TextView monthIncomeText;
    private TextView monthExpenseText;
    private RecyclerView todayRecyclerView;
    private CardView weekAccountCard;
    private CardView monthAccountCard;
    private TextView navHeaderText;
    private CollapsingToolbarLayout toolbarLayout;
    private GradeProgressView progressView;
    private WaveView waveView;
    private CardView recordNowCard;

    //登陆的用户名
    private String loginName;
    //月预算
    private int budget;
    //recyclerView的adapter绑定的List
    private List<AccountInfo> todayAccountInfo = new ArrayList<>();
    private AccountRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(R.id.toolbar_main, false);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(0).setChecked(true);
        fab = findViewById(R.id.fab);
        navHeaderUserLayout = navView.getHeaderView(0)
                .findViewById(R.id.nav_header_username_layout);
        monthTotalLayout = findViewById(R.id.month_total_layout);
        showTodayText = findViewById(R.id.show_today_text);
        menuBgImg = navView.getHeaderView(0)
                .findViewById(R.id.menu_bg_img);
        menuButton = findViewById(R.id.menu_button);
        profileImg = navView.getHeaderView(0)
                .findViewById(R.id.profile_img);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        navHeaderText = navView.getHeaderView(0).findViewById(R.id.nav_header_text);
        progressView = findViewById(R.id.progress_view);
        waveView = findViewById(R.id.wave_view);
        recordNowCard = findViewById(R.id.record_now_card);
        monthTotalIncomeText = findViewById(R.id.month_total_income);
        monthTotalExpenseText = findViewById(R.id.month_total_expense);
        budgetSurplusText = findViewById(R.id.budget_surplus_text);
        weekBalanceText = findViewById(R.id.week_balance_text);
        weekIncomeText = findViewById(R.id.week_income_text);
        weekExpenseText = findViewById(R.id.week_expense_text);
        accountRecordText = findViewById(R.id.account_record_text);
        monthBalanceText = findViewById(R.id.month_balance_text);
        monthIncomeText = findViewById(R.id.month_income_text);
        monthExpenseText = findViewById(R.id.month_expense_text);
        todayRecyclerView = findViewById(R.id.today_account_list);
        weekAccountCard = findViewById(R.id.week_account_card);
        monthAccountCard = findViewById(R.id.month_account_card);

        fab.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        recordNowCard.setOnClickListener(this);
        weekAccountCard.setOnClickListener(this);
        monthAccountCard.setOnClickListener(this);
        navHeaderUserLayout.setOnClickListener(this);
        monthTotalLayout.setOnClickListener(this);
        showTodayText.setOnClickListener(this);

        initSettings();
        initView();

        //菜单栏点击事件
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.settings:
                        directStartActivity(MainActivity.this, SettingsActivity.class);
                        break;
                    case R.id.quit:
                        finish();
                        break;
                    case R.id.account_history:
                        directStartActivity(MainActivity.this, HistoryActivity.class);
                    default:
                        break;
                }
                return true;
            }
        });

        //点击RecyclerView事件
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
                AccountRecyclerViewAdapter.onItemSelected(MainActivity.this, adapter, position,
                        new AccountRecyclerViewAdapter.SelectCallback() {
                            @Override
                            public void onDeleteIemClick() {
                                setAllData();
                            }
                        });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //记账按钮
            case R.id.record_now_card:
            case R.id.fab:
                directStartActivity(this, RecordActivity.class);
                break;
            //月账单卡片,进入月数据Activity
            case R.id.month_account_card:
                Intent intent = new Intent(this, MonthAccountActivity.class);
                intent.putExtra("year", Utility.getTime(Calendar.YEAR));
                intent.putExtra("month", Utility.getTime(Type.TIME_MONTH));
                startActivity(intent);
                break;
            //周账单卡片,设置List为本周
            case R.id.week_account_card:
                showTodayText.setVisibility(View.VISIBLE);
                List<AccountInfo> infoList =
                        AccountDAO.quickFind(Type.ACCOUNT_THIS_WEEK);
                adapter.setDateType(AccountRecyclerViewAdapter.SHOW_DATE_WEEKDAY);
                weekAccountCard.setEnabled(false);
                setList(infoList);
                accountRecordText.setText("-- 本周账单记录 --");
                break;
            //菜单键打开菜单
            case R.id.menu_button:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            //点击用户名,弹出选择
            case R.id.nav_header_username_layout:
                String[] items = {"修改头像", "更改背景图", "退出登录"};
                new AlertDialog.Builder(this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        choosePhoto(Type.CHANGE_IMG_PROFILE);
                                        break;
                                    case 1:
                                        choosePhoto(Type.CHANGE_IMG_BG);
                                        break;
                                    case 2:
                                        exitLogin();
                                        break;
                                    default:
                                }
                            }
                        }).create().show();

                break;

            case R.id.month_total_layout:
                monthAccountCard.callOnClick();
                break;
            case R.id.show_today_text:
                accountRecordText.setText("-- 今日账单记录 --");
                adapter.setDateType(AccountRecyclerViewAdapter.SHOW_DATE);
                showTodayText.setVisibility(View.INVISIBLE);
                weekAccountCard.setEnabled(true);
                setContentData();
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSettings();
        setAllData();
    }

    /**
     * 初始化设置项
     */
    private void initSettings() {
        //设置登陆的用户名
        loginName = (String) prefs.get("login_name", "");
        //如果不存在用户名, 退出登录回到主界面
        if (TextUtils.isEmpty(loginName)) exitLogin();
        //设置预算
        budget = Integer.parseInt((String) prefs.get("month_budget", "2000"));
        //设置数据库名,默认为用户名
        LitePalDB litePalDB = LitePalDB.fromDefault(loginName);
        LitePal.use(litePalDB);
        //设置头像
        String fileName = (String) prefs.get("profile_path", null);
        if (!TextUtils.isEmpty(fileName)) {
            Uri uri = Uri.fromFile(new File(fileName));
            profileImg.setImageURI(uri);
        }
        //设置菜单栏背景图
        String menuBgUri = (String) prefs.get("menu_bg_uri", null);
        if (!TextUtils.isEmpty(menuBgUri)) {
            Glide.with(this).load(Uri.parse(menuBgUri)).into(menuBgImg);
        }
    }

    /**
     * 初始化控件显示值和RecyclerView
     */
    private void initView() {
        if (loginName == null) {
            exitLogin();
            return;
        }
        navHeaderText.setText(loginName);
        toolbarLayout.setTitle(Utility.getTime(Type.TIME_MONTH_AND_DAY));
        monthTotalIncomeText.setText("￥0.00");
        monthTotalExpenseText.setText("￥0.00");
        weekBalanceText.setText("结余" + "￥0.00");
        weekExpenseText.setText("-0.00");
        weekIncomeText.setText("+0.00");
        monthBalanceText.setText("结余" + "￥0.00");
        monthExpenseText.setText("-0.00");
        monthIncomeText.setText("+0.00");
        budgetSurplusText.setText("100%");
        showTodayText.setVisibility(View.INVISIBLE);
        waveView.setProgress(100);
        progressView.setProgressWidthAnimation(100);

        adapter = new AccountRecyclerViewAdapter(
                R.layout.account_list_item,
                todayAccountInfo);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        todayRecyclerView.setAdapter(adapter);
        todayRecyclerView.setLayoutManager(manager);
    }


    /**
     * 设置所有的数据
     */
    private void setAllData() {
        setHeaderData();
        setContentData();
    }

    /**
     * 设置标题栏数据
     */
    private void setHeaderData() {
        List<AccountInfo> infoList = AccountDAO.quickFind(Type.ACCOUNT_THIS_MONTH);
        monthTotalIncomeText.setText("￥"
                + Utility.getAccountMoneyNum(infoList, Type.ACCOUNT_TOTAL_INCOME));
        monthTotalExpenseText.setText("￥"
                + Utility.getAccountMoneyNum(infoList, Type.ACCOUNT_TOTAL_EXPENSE));
        double expenseMoney = Double.parseDouble(
                Utility.getAccountMoneyNum(infoList, Type.ACCOUNT_TOTAL_EXPENSE));

        int percent = (int) ((budget - expenseMoney) / budget * 100);
        if (percent < 0) percent = 0;
        waveView.setProgress(percent);
        progressView.setProgressWidthAnimation(percent);
        budgetSurplusText.setText(percent + "%");
        if (percent <= 20) {
            budgetSurplusText.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        } else {
            budgetSurplusText.setTextColor(ContextCompat.getColor(this, R.color.colorBrown));
        }
    }

    /**
     * 设置RecyclerView.以及周和月卡片的数据
     */
    private void setContentData() {

        List<AccountInfo> infoList;
        //设置TodayRecyclerView
        if (showTodayText.getVisibility() == View.VISIBLE) {
            infoList = AccountDAO.quickFind(Type.ACCOUNT_THIS_WEEK);
        } else {
            infoList = AccountDAO.quickFind(Type.ACCOUNT_TODAY);
        }
        setList(infoList);
        //设置本周账单卡片
        infoList = AccountDAO.quickFind(Type.ACCOUNT_THIS_WEEK);
        weekBalanceText.setText("结余 ￥" + Utility.getAccountMoneyNum(
                infoList, Type.ACCOUNT_BALANCE));
        weekIncomeText.setText("+" + Utility.getAccountMoneyNum(
                infoList, Type.ACCOUNT_TOTAL_INCOME));
        weekExpenseText.setText("-" + Utility.getAccountMoneyNum(
                infoList, Type.ACCOUNT_TOTAL_EXPENSE));
        //设置月账单卡片
        infoList = AccountDAO.quickFind(Type.ACCOUNT_THIS_MONTH);

        monthBalanceText.setText("结余 ￥" + Utility.getAccountMoneyNum(
                infoList, Type.ACCOUNT_BALANCE));
        monthIncomeText.setText("+" + Utility.getAccountMoneyNum(
                infoList, Type.ACCOUNT_TOTAL_INCOME));
        monthExpenseText.setText("-" + Utility.getAccountMoneyNum(
                infoList, Type.ACCOUNT_TOTAL_EXPENSE));
    }

    /**
     * 设置RecyclerView显示为List
     * @param list
     */
    private void setList(List<AccountInfo> list) {
        if (todayAccountInfo == null) {
            todayAccountInfo = new ArrayList<>();
        } else {
            todayAccountInfo.clear();
        }
        Collections.reverse(list);
        todayAccountInfo.addAll(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 开启图片选择Activity
     * @param type
     */
    private void choosePhoto(int type) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, type);
    }

    /**
     * 开启裁剪Activity
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        //宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片宽高
        intent.putExtra("outputX", 196);
        intent.putExtra("outputY", 196);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, Type.SAVE_PROFILE);
    }

    /**
     * 图片选择Activity回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            //设置头像,开启裁剪Activity
            case Type.CHANGE_IMG_PROFILE:
                Uri profileUri = data.getData();
                startPhotoZoom(profileUri);
                break;
            //设置背景图
            case Type.CHANGE_IMG_BG:
                Uri bgUri = data.getData();
                //保存背景图的Uri
                prefs.put("menu_bg_uri", bgUri.toString());
                Glide.with(MainActivity.this).load(bgUri).into(menuBgImg);
                break;
            //裁剪回调,设置头像并保存
            case Type.SAVE_PROFILE:
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    profileImg.setImageBitmap(bitmap);
                    saveProfile(bitmap);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 保存头像到SD卡
     * @param bitmap
     */
    private void saveProfile(Bitmap bitmap) {
        //保存到SD卡
        FileOutputStream fos = null;
        String fileName = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + "profile.jpg";
        try {
            fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            prefs.put("profile_path", fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}