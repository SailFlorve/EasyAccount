package com.hdu.easyaccount;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hdu.easyaccount.bean.db.UserInfo;
import com.hdu.easyaccount.utils.ActivityManager;
import com.hdu.easyaccount.utils.Base64Util;
import com.takwolf.android.lock9.Lock9View;

import org.litepal.crud.DataSupport;

/**
 * 手势密码Activity
 */
public class VerifyActivity extends BaseActivity {

    //是否为进入app的验证,默认true
    //为证时输入手势密码进入MainActivity,否则视为设置手势密码.
    private boolean enterValidate;
    private String username;
    //设置手势密码时保存第一次的输入
    private String passwordFirst = null;
    //提示文字
    private TextView verifyTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        setToolbar(R.id.toolbar_verify, true);
        Lock9View lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        verifyTip = (TextView) findViewById(R.id.verify_tip);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
            enterValidate = bundle.getBoolean("enter", true);
        }

        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.length() < 4) {
                    verifyTip.setText("密码至少四位！");
                    return;
                }
                //如果是在进入验证
                if (enterValidate) {
                    validate(password);
                } else {
                    setGesture(password);
                }
            }

        });
    }

    /**
     * 进行登陆时验证密码正确性
     * @param password 输入的密码
     */
    private void validate(String password) {
        UserInfo userInfo = UserInfo.getUserInfo(username);
        if (userInfo == null) return;
        Log.d(TAG, "validate: " + userInfo.getGestureDecoded());
        //如果手势密码与数据库中一致,进入MainActivity
        if (userInfo.getGestureDecoded().equals(password)) {
            directStartActivity(VerifyActivity.this, MainActivity.class);
            ActivityManager.finishAll();
        } else {
            verifyTip.setText("密码错误。");
        }
    }

    /**
     * 手势密码的设置
     * @param password
     */
    private void setGesture(String password) {
        //如果保存的第一次输入为null,设置第一次输入的值,并要求确认输入
        if (passwordFirst == null) {
            verifyTip.setText("请确认一次。");
            passwordFirst = password;
        } else {
            //如果第二次输入与第一次输入相同
            if (password.equals(passwordFirst)) {
                Toast.makeText(VerifyActivity.this, "设置成功。", Toast.LENGTH_SHORT).show();
                ContentValues values = new ContentValues();
                values.put("gesture", Base64Util.encode(password));
                //把手势密码保存至数据库
                DataSupport.updateAll(UserInfo.class, values, "username = ?",
                        Base64Util.encode(username));
                finish();
            } else {
                verifyTip.setText("两次密码不一致，请重试。");
                passwordFirst = null;
            }
        }
    }
}
