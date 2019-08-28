package com.csdk.plugin.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import com.csdk.plugin.IAttach;
import com.csdk.plugin.PluginManager;
import com.csdk.plugin.other.PluginConstants;
import com.csdk.plugin.other.PluginPackage;

import java.lang.reflect.Constructor;

public class ProxyActivityBridge {

    private static final String TAG = "ProxyActivityBridge";
    private String mClass;
    private String mPackageName;
    private PluginPackage mPluginPackage;
    private PluginManager mPluginManager;
    private AssetManager mAssetManager;
    private Resources mResources;
    private Theme mTheme;
    private ActivityInfo mActivityInfo;
    private Activity mProxyActivity;
    protected IPluginActivity mPluginActivity;
    public ClassLoader mPluginClassLoader;

    public ProxyActivityBridge(Activity activity) {
        mProxyActivity = activity;
    }

    public void onCreate(Intent intent) {
        intent.setExtrasClassLoader(PluginConstants.class.getClassLoader());
        mPackageName = intent.getStringExtra(PluginConstants.EXTRA_PACKAGE);
        mClass = intent.getStringExtra(PluginConstants.EXTRA_CLASS);
        if (TextUtils.isEmpty(mPackageName) || TextUtils.isEmpty(mPackageName)) {
            Log.d(TAG, "extra_package 或者 extra_class 为空，请检查启动intent!");
            return;
        }
        mPluginManager = PluginManager.getInstance(mProxyActivity);
        mPluginPackage = mPluginManager.getPackage(mPackageName);
        if (mPluginPackage == null) {
            Log.d(TAG, "无法启动:" + mClass + " 插件是否已经加载？");
            return;
        }
        mAssetManager = mPluginPackage.assetManager;
        mResources = mPluginPackage.resources;
        mPluginClassLoader = mPluginPackage.classLoader;
        setUpDefaultTheme();
        setupPluginTheme();
        createPluginActivity();
    }

    @TargetApi(14)
    private void setUpDefaultTheme() {
        PackageInfo packageInfo = mPluginPackage.packageInfo;
        if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {
            if (mClass == null) {
                mClass = packageInfo.activities[0].name;
            }
            //Finals 修复主题BUG
            int defaultTheme = packageInfo.applicationInfo.theme;
            for (ActivityInfo a : packageInfo.activities) {
                if (a.name.equals(mClass)) {
                    mActivityInfo = a;
                    // Finals ADD 修复主题没有配置的时候插件异常
                    if (mActivityInfo.theme == 0) {
                        if (defaultTheme != 0) {
                            mActivityInfo.theme = defaultTheme;
                        } else {
                            if (Build.VERSION.SDK_INT >= 14) {
                                mActivityInfo.theme = android.R.style.Theme_DeviceDefault;
                            } else {
                                mActivityInfo.theme = android.R.style.Theme;
                            }
                        }
                    }
                }
            }

        }
    }

    private void setupPluginTheme() {
        Log.d(TAG, "setupPluginTheme theme=" + mActivityInfo.theme);
        if (mActivityInfo.theme > 0) {
            mProxyActivity.setTheme(mActivityInfo.theme);
        }
        Theme superTheme = mProxyActivity.getTheme();
        mTheme = mResources.newTheme();
        mTheme.setTo(superTheme);
        // Finals适配三星以及部分加载XML出现异常BUG
        try {
            mTheme.applyStyle(mActivityInfo.theme, true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "createTheme-error when applyStyle()");
        }
    }

    private void createPluginActivity() {
        try {
            Class<?> localClass = getClassLoader().loadClass(mClass);
            Constructor<?> localConstructor = localClass.getConstructor(new Class[]{});
            Object instance = localConstructor.newInstance(new Object[]{});
            //创建插件Activity，
            mPluginActivity = (IPluginActivity) instance;
            ((IAttach) mProxyActivity).attachPlugin(mPluginActivity);
            //调用插件Activity attach方法
            mPluginActivity.attach(mProxyActivity, mPluginPackage);
            //调用插件onCreate 方法
            mPluginActivity.onCreate(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getClassLoader() {
        return mPluginPackage.classLoader;
    }

    public AssetManager getAssets() {
        return mAssetManager;
    }

    public Resources getResources() {
        return mResources;
    }

    public Theme getTheme() {
        return mTheme;
    }

    public IPluginActivity getRemoteActivity() {
        return mPluginActivity;
    }
}
