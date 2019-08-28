package com.csdk.plugin.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by jkyl on 2018/2/27.
 */

public class LogUtils {

    public static String TAG = "PluginLog";

    public static boolean sDebug = true;

    public LogUtils() {

    }

    public static void logInit(boolean logSwitch, String tag) {
        sDebug = logSwitch;
        if (!TextUtils.isEmpty(tag)) {
            TAG = tag;
        }
    }

    public static void log(String var0, String var1) {
        log(var0, var1, (Throwable) null);
    }

    public static void log(String var0, String var1, Throwable throwable) {
        if (sDebug) {
            Log.d(TAG, var0 + ":" + var1, throwable);
        }
    }

    public static void error(String var0, String var1) {
        error(var0, var1, (Throwable) null);
    }

    public static void error(String var0, String var1, Throwable var2) {
        if (sDebug) {
            Log.e(TAG, var0 + ":" + var1, var2);
        }
    }
}
