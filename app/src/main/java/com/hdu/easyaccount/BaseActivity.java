package com.hdu.easyaccount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hdu.easyaccount.bean.db.AccountInfo;
import com.hdu.easyaccount.utils.ActivityManager;
import com.hdu.easyaccount.utils.SharedPrefs;

/**
 * 所有Activity的基类,封装了一些通用操作.
 */

public class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    protected SharedPrefs prefs;

    /**
     * 把Activity添加到ActivityManager,初始化SharedPrefs
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        prefs = new SharedPrefs(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    /**
     * 设置返回按钮的默认行为
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Activity直接跳转的intent的封装
     * @param context
     * @param cls
     */
    protected void directStartActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }

    /**
     * 为该Activity设置Toolbar
     * @param toolbarId toolbar的Id
     * @param haveHomeButton 是否设置默认返回按钮
     */
    protected void setToolbar(int toolbarId, boolean haveHomeButton) {
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        if (haveHomeButton) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    /**
     * 退出登录
     */
    protected void exitLogin() {
        prefs.put("login_name", "");
        directStartActivity(this, WelcomeActivity.class);
        ActivityManager.finishAll();
    }

}
