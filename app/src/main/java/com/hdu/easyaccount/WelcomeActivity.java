package com.hdu.easyaccount;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hdu.easyaccount.bean.db.UserInfo;
import com.hdu.easyaccount.utils.ActivityManager;
import com.hdu.easyaccount.utils.Base64Util;
import com.hdu.easyaccount.utils.SharedPrefs;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.useDefault();
        //如果开启时Activity不在栈底,直接结束.
        if (!isTaskRoot()) {
            finish();
            return;
        }

        String username = (String) prefs.get("login_name", null);
        Log.d(TAG, "onCreate: " + username);
        //如果保存了登陆的用户名
        if (!TextUtils.isEmpty(username)) {
            UserInfo userInfo = UserInfo.getUserInfo(username);

            if (userInfo != null) {
                //如果手势密码为空,进入MainActivity
                if (TextUtils.isEmpty(userInfo.getGesture())) {
                    directStartActivity(this, MainActivity.class);
                } else {
                    //手势密码不为空,进入VerifyActivity
                    Intent intent = new Intent(this, VerifyActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("enter", true);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "APP已崩溃。", Toast.LENGTH_SHORT).show();
            }
            ActivityManager.finishAll();
        }

        setContentView(R.layout.activity_welcome);
        Button loginButton = (Button) findViewById(R.id.to_login_button);
        Button registerButton = (Button) findViewById(R.id.to_register_button);
        //使用存储了用户名和密码的数据库
        LitePal.useDefault();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directStartActivity(WelcomeActivity.this, LoginActivity.class);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directStartActivity(WelcomeActivity.this, RegisterActivity.class);
            }
        });
    }
}
