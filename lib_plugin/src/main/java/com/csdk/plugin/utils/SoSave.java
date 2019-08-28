package com.csdk.plugin.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by jkyl on 2018/2/28.
 */

public class SoSave {

    private static final String FILE_NAME = "SoSave";

    public static void setSoVersion(Context cxt, String key, int value) {
        SharedPreferences prefs = cxt.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        prefs.edit().putInt(key, value).apply();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static int getSoVersion(Context cxt, String key) {
        SharedPreferences prefs = cxt.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return prefs.getInt(key, 0);
    }
}
