package com.hdu.easyaccount;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hdu.easyaccount.bean.db.UserInfo;
import com.hdu.easyaccount.utils.ActivityManager;
import com.hdu.easyaccount.utils.InputCheckHelper;
import com.hdu.easyaccount.utils.SharedPrefs;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 登录Activity
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button loginButton;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout pwInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.username_login_activity);
        pwInputLayout = (TextInputLayout) findViewById(R.id.pw_login_activity);

        setToolbar(R.id.toolbar_login, true);
        //设置返回按钮图标
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        //设置软键盘默认隐藏
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                doLogin();
                break;
            default:
        }
    }

    /**
     * 检查用户名和密码有效性,如果有效,就登录.
     */
    private void doLogin() {
        usernameInputLayout.setErrorEnabled(false);
        pwInputLayout.setErrorEnabled(false);

        final String username = usernameInputLayout.getEditText().getText().toString();
        final String password = pwInputLayout.getEditText().getText().toString();
        //检查有效性
        InputCheckHelper.check(username, password, null, new InputCheckHelper.CheckCallback() {
            //输入正确回调
            @Override
            public void onPass() {
                //请求登录的用户信息
                UserInfo requestInfo = new UserInfo(username, password);
                //获得数据库中保存的信息
                UserInfo userInfo = UserInfo.getUserInfo(username);
                if (userInfo == null) {
                    usernameInputLayout.setError("用户名不存在。");
                } else {
                    //如果密码相同,允许登录.
                    if (requestInfo.getPassword().equals(userInfo.getPassword())) {
                        Toast.makeText(LoginActivity.this, "登录成功。", Toast.LENGTH_SHORT).show();
                        //记住当前登录的账号
                        prefs.put("login_name", username);
                        directStartActivity(LoginActivity.this, MainActivity.class);
                        ActivityManager.finishAll();
                    } else {
                        pwInputLayout.setError("密码错误。");
                    }
                }
            }
            //输入有误回调
            @Override
            public void onFailure(int which, String msg) {
                if (which == InputCheckHelper.ERR_USERNAME) {
                    usernameInputLayout.setError(msg);
                } else {
                    pwInputLayout.setError(msg);
                }
            }
        });
    }
}
