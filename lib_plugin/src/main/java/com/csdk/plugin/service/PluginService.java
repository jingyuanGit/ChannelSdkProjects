package com.csdk.plugin.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;

import com.cwsdk.plugin.IPlugin;
import com.cwsdk.plugin.PluginManager;
import com.cwsdk.plugin.other.PluginPackage;

/**
 * Created by jkyl on 2018/2/27.
 */

public class PluginService extends Service implements IPluginService, IPlugin {

    private static final String TAG = "PluginService";

    private PluginPackage mPluginPackage;

    protected Service that;

    @Override
    public void attach(Service proxyService, PluginPackage pluginPackage) {
        that = proxyService;
        mPluginPackage = pluginPackage;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAG + " onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        return 0;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, TAG + " onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, TAG + " onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, TAG + " onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d(TAG, TAG + " onTrimMemory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, TAG + " onUnbind");
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, TAG + " onRebind");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, TAG + " onTaskRemoved");
    }

    @Override
    public void startActivity(Intent intent) {
        PluginManager.getInstance(that).startActivity(that, intent);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        PluginManager.getInstance(that).startActivity(that, intent, options);
    }

    @Override
    public Object getSystemService(String name) {
        return that.getSystemService(name);
    }

    @Override
    public String getPackageName() {
        return that.getPackageName();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return that.getApplicationInfo();
    }

    @Override
    public Resources getResources() {
        return that.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return that.getAssets();
    }

    @Override
    public ComponentName startService(Intent service) {
        return that.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        return that.stopService(name);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return that.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        that.unbindService(conn);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        that.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(Intent intent, String receiverPermission) {
        that.sendBroadcast(intent, receiverPermission);
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        that.sendBroadcastAsUser(intent, user);
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        that.sendBroadcastAsUser(intent, user, receiverPermission);
    }

}
