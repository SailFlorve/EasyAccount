package com.hdu.easyaccount.utils;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity管理器，便于同时结束多个Activity。
 */

public class ActivityManager {
    private static List<Activity> activities = new ArrayList<>();
    private static String TAG = "ActivityManager";

    /**
     * 添加Activity到List,在onCreate方法调用
     * @param activity
     */
    public static void addActivity(Activity activity) {
        Log.d(TAG, "addActivity: " + activity.getLocalClassName());
        activities.add(activity);
    }

    /**
     * 从List中移除Activity,在onDestroy方法调用
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        if (activity != null) {
            Log.d(TAG, "removeActivity: " + activity.getLocalClassName());
            activities.remove(activity);
        }
    }

    /**
     * 结束List中所有Activity
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (activity != null) {
                Log.d(TAG, "finish: " + activity.getLocalClassName());
                activity.finish();
            }
        }
    }
}
