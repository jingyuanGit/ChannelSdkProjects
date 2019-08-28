package com.csdk.plugin.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;


import com.csdk.plugin.IAttach;
import com.csdk.plugin.IPlugin;
import com.csdk.plugin.PluginManager;
import com.csdk.plugin.other.PluginConstants;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * ProxyService  只能启动一次
 * <p>
 * 多次startServer   ->    onStart  onStartCommand
 */
public class ProxyService extends Service implements IAttach {

    private static final String TAG = "ProxyService";
    private ProxyServiceBridge mBridge = new ProxyServiceBridge(this);
    private PluginManager mPluginManager;


    @Override
    public void onCreate() {
        super.onCreate();
        mPluginManager = PluginManager.getInstance(this);
    }

    @Override
    public void attachPlugin(IPlugin iPlugin) {

    }

    @Override
    public void onStart(Intent intent, int startId) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //判断是否存在插件Service，如果存在，则不进行Service插件的构造工作
        String serviceName = intent.getStringExtra(PluginConstants.EXTRA_CLASS);
        Log.d(TAG, "服务名：" + serviceName);
        if (TextUtils.isEmpty(serviceName)) {
            return START_STICKY;
        }
        int command = intent.getIntExtra(PluginConstants.EXTRA_SERVER_COMMAND, 0);
        if (command <= 0) {
            return START_STICKY;
        }
        switch (command) {
            case PluginConstants.EXTRA_START_SERVICE: {
                IPluginService service = mPluginManager.getServices().get(serviceName);
                if (service == null) {
                    IPluginService createServer = mBridge.init(intent);
                    if (createServer != null) {
                        service = createServer;
                        service.onCreate();
                        Log.d(TAG, "创建服务：" + serviceName);
                        mPluginManager.getServices().put(serviceName, service);
                    }
                }
                service.onStartCommand(intent, flags, startId);
                break;
            }

            case PluginConstants.EXTRA_STOP_SERVICE: {
                IPluginService service = mPluginManager.getServices().get(serviceName);
                mPluginManager.getServices().remove(serviceName);
                if (null != service) {
                    try {
                        service.onDestroy();
                        Log.d(TAG, "销毁服务：" + serviceName);
                    } catch (Exception e) {
                        Log.e(TAG, "Unable to stop service " + service + ": " + e.toString());
                    }
                } else {
                    Log.i(TAG, " service not found");
                }
                break;
            }
            case PluginConstants.EXTRA_BIND_SERVICE: {
                IPluginService service = mPluginManager.getServices().get(serviceName);
                if (service == null) {
                    IPluginService createServer = mBridge.init(intent);
                    if (createServer != null) {
                        service = createServer;
                        service.onCreate();
                        mPluginManager.getServices().put(serviceName, service);
                    }
                }
                IBinder binder = service.onBind(intent);
                ComponentName component = intent.getComponent();
                ServiceConnection conn = mPluginManager.getServiceConns().get(serviceName);
                //TODO 如何调用ServiceConnection的onConnection方法
                if (conn != null) {
                    conn.onServiceConnected(component, binder);
                }
                break;
            }
            case PluginConstants.EXTRA_UNBIND_SERVICE: {
                IPluginService service = mPluginManager.getServices().get(serviceName);
                mPluginManager.getServices().remove(serviceName);
                ServiceConnection conn = mPluginManager.getServiceConns().remove(serviceName);
                ComponentName component = intent.getComponent();
                if (null != conn) {
                    conn.onServiceDisconnected(component);
                }
                if (null != service) {
                    try {
                        service.onUnbind(intent);
                        service.onDestroy();
                    } catch (Exception e) {
                        Log.e(TAG, "Unable to unbind service " + service + ": " + e.toString());
                    }
                } else {
                    Log.i(TAG, "Unable to unbind service not found");
                }
                break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, TAG + " onConfigurationChanged");
        Map<String, IPluginService> services = mPluginManager.getServices();
        Iterator<Map.Entry<String, IPluginService>> entries = services.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, IPluginService> entry = entries.next();
            String name = entry.getKey();
            IPluginService pluginService = entry.getValue();
            if (pluginService != null) {
                try {
                    pluginService.onConfigurationChanged(newConfig);
                } catch (Exception e) {
                    Log.d(TAG, "service:" + name + " onConfigurationChanged error");
                }
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, TAG + " onLowMemory");
        Map<String, IPluginService> services = mPluginManager.getServices();
        Iterator<Map.Entry<String, IPluginService>> entries = services.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, IPluginService> entry = entries.next();
            String name = entry.getKey();
            IPluginService pluginService = entry.getValue();
            if (pluginService != null) {
                try {
                    pluginService.onLowMemory();
                } catch (Exception e) {
                    Log.d(TAG, "service:" + name + " onLowMemory error");
                }
            }
        }

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, TAG + " onTrimMemory");
        Map<String, IPluginService> services = mPluginManager.getServices();
        Iterator<Map.Entry<String, IPluginService>> entries = services.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, IPluginService> entry = entries.next();
            String name = entry.getKey();
            IPluginService pluginService = entry.getValue();
            if (pluginService != null) {
                try {
                    pluginService.onTrimMemory(level);
                } catch (Exception e) {
                    Log.d(TAG, "service:" + name + " onTrimMemory error");
                }
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, TAG + " onTaskRemoved");
        Map<String, IPluginService> services = mPluginManager.getServices();
        Iterator<Map.Entry<String, IPluginService>> entries = services.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, IPluginService> entry = entries.next();
            String name = entry.getKey();
            IPluginService pluginService = entry.getValue();
            if (pluginService != null) {
                try {
                    pluginService.onTaskRemoved(rootIntent);
                } catch (Exception e) {
                    Log.d(TAG, "service:" + name + " onTaskRemoved error");
                }
            }
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        return PluginManager.getInstance(this).startService(this, service);
    }

    @Override
    public boolean stopService(Intent name) {
        return PluginManager.getInstance(this).stopService(this, name);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return PluginManager.getInstance(this).bindService(this, service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        PluginManager.getInstance(this).unbindService(this, conn);
    }

    @Override
    public AssetManager getAssets() {
        return mBridge.getAssets() == null ? super.getAssets() : mBridge.getAssets();
    }

    @Override
    public Resources getResources() {
        return mBridge.getResources() == null ? super.getResources() : mBridge.getResources();
    }

}
