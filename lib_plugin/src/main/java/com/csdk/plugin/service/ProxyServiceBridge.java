package com.csdk.plugin.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;


import com.csdk.plugin.IAttach;
import com.csdk.plugin.PluginManager;
import com.csdk.plugin.other.PluginConstants;
import com.csdk.plugin.other.PluginPackage;

import java.lang.reflect.Constructor;

/**
 * Created by jkyl on 2018/2/27.
 */

public class ProxyServiceBridge {

    private static final String TAG = "ProxyServiceBridge";
    private Service mProxyService;
    private AssetManager mAssetManager;
    private Resources mResources;
    private IPluginService mPluginService;
    private PluginPackage mPluginPackage;
    private PluginManager mPluginManager;

    public ProxyServiceBridge(ProxyService proxyService) {
        mProxyService = proxyService;
    }

    public IPluginService init(Intent intent) {
        intent.setExtrasClassLoader(PluginConstants.class.getClassLoader());
        String packageName = intent.getStringExtra(PluginConstants.EXTRA_PACKAGE);
        String clazz = intent.getStringExtra(PluginConstants.EXTRA_CLASS);
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(clazz)) {
            Log.d(TAG, "extra_package 或者 extra_class 为空，请检查启动intent!");
            return null;
        }
        mPluginManager = PluginManager.getInstance(mProxyService);
        mPluginPackage = mPluginManager.getPackage(packageName);
        if (mPluginPackage == null) {
            Log.d(TAG, "无法启动:" + clazz + " 插件是否已经加载？");
            return null;
        }
        mAssetManager = mPluginPackage.assetManager;
        mResources = mPluginPackage.resources;
        return createPluginService(clazz);
    }

    private IPluginService createPluginService(String clazz) {
        try {
            Class<?> localClass = mPluginPackage.classLoader.loadClass(clazz);
            Constructor<?> localConstructor = localClass.getConstructor(new Class[]{});
            Object instance = localConstructor.newInstance(new Object[]{});
            mPluginService = (IPluginService) instance;
            ((IAttach) mProxyService).attachPlugin(mPluginService);
            mPluginService.attach(mProxyService, mPluginPackage);
            return mPluginService;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "createPluginService: exception");
            return null;
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

    public PluginPackage getPluginPackage() {
        return mPluginPackage;
    }

    public PluginManager getPluginManager() {
        return mPluginManager;
    }

}
