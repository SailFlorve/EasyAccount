package com.hdu.easyaccount.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferences的封装
 */

public class SharedPrefs {

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public SharedPrefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }

    /**
     * 存入SharedPreferences
     * @param key 键
     * @param object 值
     */
    public void put(String key, Object object) {
        if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        }

        if (object instanceof String) {
            editor.putString(key, (String) object);
        }

        if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        }
        editor.apply();
    }

    /**
     * 根据键获取值
     * @param key 键
     * @param defaultObj 默认值
     * @return
     */
    public Object get(String key, Object defaultObj) {
        if (defaultObj instanceof Integer) {
            return prefs.getInt(key, (int) defaultObj);
        }

        if (defaultObj instanceof String || defaultObj == null) {
            return prefs.getString(key, (String) defaultObj);
        }

        if (defaultObj instanceof Boolean) {
            return prefs.getBoolean(key, (Boolean) defaultObj);
        }
        return null;
    }
}
