package com.hdu.easyaccount;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.hdu.easyaccount.bean.db.UserInfo;
import com.hdu.easyaccount.utils.Base64Util;
import com.hdu.easyaccount.utils.SharedPrefs;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 设置Activity
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LitePal.useDefault();
        setToolbar(R.id.toolbar_settings, true);
        if (savedInstanceState == null) {
            //添加设置Fragment
            SettingFragment settingFragment = new SettingFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_content, settingFragment)
                    .commit();
        }
    }

    /**
     * 设置Fragment
     */
    public static class SettingFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener {

        private SharedPrefs prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 加载xml资源文件
            addPreferencesFromResource(R.xml.preferences);
            prefs = new SharedPrefs(getActivity());
            Preference exitPrefs = findPreference("quit_login");
            exitPrefs.setOnPreferenceClickListener(this);
            Preference setGesture = findPreference("set_gesture_pw");
            setGesture.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            //退出登录
            if (preference.getKey().equals("quit_login")) {
                SettingsActivity parent = (SettingsActivity) getActivity();
                parent.exitLogin();
            } else if (preference.getKey().equals("set_gesture_pw")) {
                //手势密码
                final String username = (String) prefs.get("login_name", null);
                UserInfo userInfo = UserInfo.getUserInfo(username);
                assert userInfo != null;
                //如果此用户没有设置手势密码,直接跳转到VerifyActivity
                if (TextUtils.isEmpty(userInfo.getGesture())) {
                    Intent intent = new Intent(getActivity(), VerifyActivity.class);
                    intent.putExtra("username", username);
                    //enter值为false,代表新设置
                    intent.putExtra("enter", false);
                    startActivity(intent);
                } else {
                    //如果设置过手势密码,弹出对话框
                    new AlertDialog.Builder(getActivity())
                            .setTitle("手势密码")
                            .setItems(new String[]{"修改密码", "删除密码"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        Intent intent = new Intent(getActivity(), VerifyActivity.class);
                                        intent.putExtra("username", username);
                                        intent.putExtra("enter", false);
                                        startActivity(intent);
                                    } else {
                                        //删除手势密码:更新数据库
                                        ContentValues values = new ContentValues();
                                        values.put("gesture", "");
                                        DataSupport.updateAll(UserInfo.class, values, "username = ?",
                                                Base64Util.encode(username));
                                        Toast.makeText(getActivity(), "删除成功。", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).create().show();
                }

            }
            return true;
        }
    }
}
