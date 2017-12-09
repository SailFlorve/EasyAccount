package com.hdu.easyaccount.bean.db;

import android.util.Base64;

import com.hdu.easyaccount.utils.Base64Util;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * 存储注册的用户名和密码数据库的实体类
 */

public class UserInfo extends DataSupport {
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String gesture;

    public UserInfo(String name, String pw) {
        //参数为未加密,构造函数自动进行加密
        username = Base64Util.encode(name);
        password = Base64Util.encode(pw);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGesture() {
        return gesture;
    }

    //获得手势密码的解密形式
    public String getGestureDecoded() {
        return Base64Util.decode(gesture);
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    //根据用户名获得UserInfo对象
    public static UserInfo getUserInfo(String username) {
        List<UserInfo> userList = DataSupport.where(
                "username = ?", Base64Util.encode(username))
                .find(UserInfo.class);
        if (userList.isEmpty()) return null;
        return userList.get(0);
    }
}
