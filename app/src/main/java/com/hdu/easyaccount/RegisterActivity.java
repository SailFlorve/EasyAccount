package com.hdu.easyaccount;

import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hdu.easyaccount.bean.db.UserInfo;
import com.hdu.easyaccount.utils.ActivityManager;
import com.hdu.easyaccount.utils.Base64Util;
import com.hdu.easyaccount.utils.InputCheckHelper;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 注册
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Button registerButton;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout pwInputLayout;
    private TextInputLayout pwEnsureLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = (Button) findViewById(R.id.register_button);
        usernameInputLayout = (TextInputLayout) findViewById(R.id.username_register_activity);
        pwInputLayout = (TextInputLayout) findViewById(R.id.pw_register_activity);
        pwEnsureLayout = (TextInputLayout) findViewById(R.id.pw_ensure_register_activity);

        setToolbar(R.id.toolbar_register, true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                doRegister();
                break;
        }
    }

    /**
     * 进行输入合法性检查,以及注册
     */
    private void doRegister() {
        usernameInputLayout.setErrorEnabled(false);
        pwInputLayout.setErrorEnabled(false);
        pwEnsureLayout.setErrorEnabled(false);

        final String username = usernameInputLayout.getEditText().getText().toString();
        final String password = pwInputLayout.getEditText().getText().toString();
        String passwordEnsure = pwEnsureLayout.getEditText().getText().toString();

        InputCheckHelper.check(username, password, passwordEnsure, new InputCheckHelper.CheckCallback() {
            @Override
            public void onPass() {
                //根据输入的用户名获取用户信息
                UserInfo userInfoRequest = UserInfo.getUserInfo(username);
                //如果获取到的信息不为null说明用户名存在.
                if (userInfoRequest != null) {
                    usernameInputLayout.setError("用户名已存在。");
                    return;
                }

                Toast.makeText(RegisterActivity.this, "注册成功。", Toast.LENGTH_SHORT).show();
                UserInfo userInfo = new UserInfo(username, password);
                userInfo.save();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //延迟1.5秒后跳转至登陆界面
                            Thread.sleep(1500);
                            directStartActivity(RegisterActivity.this, LoginActivity.class);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

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
