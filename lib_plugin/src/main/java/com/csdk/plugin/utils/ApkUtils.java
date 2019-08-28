package com.csdk.plugin.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by jkyl on 2018/3/1.
 */

public class ApkUtils {

    public static PackageInfo getApkPackageInfo(Context context, String apkPath) {
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        return packageInfo;
    }
}
