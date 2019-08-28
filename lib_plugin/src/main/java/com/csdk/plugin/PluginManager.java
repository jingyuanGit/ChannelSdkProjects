package com.csdk.plugin;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.core.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;


import com.csdk.plugin.activity.PluginActivity;
import com.csdk.plugin.activity.PluginFragmentActivity;
import com.csdk.plugin.activity.ProxyActivity;
import com.csdk.plugin.activity.ProxyFragmentActivity;
import com.csdk.plugin.other.PluginConstants;
import com.csdk.plugin.other.PluginPackage;
import com.csdk.plugin.service.IPluginService;
import com.csdk.plugin.utils.ApkUtils;
import com.csdk.plugin.utils.SoSave;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;

/**
 * Created by jkjoy on 16/8/9.
 */
public class PluginManager {

    public static final String TAG = "PluginManager";

    public static final int START_RESULT_SUCCESS = 0;

    public static final int START_RESULT_NO_PKG = 1;

    private static volatile PluginManager sInstance = null;

    private Context mContext;

    private Map<String, IPluginService> mServices = new ArrayMap<String, IPluginService>();

    private Map<String, ServiceConnection> mServiceConns = new ArrayMap<String, ServiceConnection>();

    private Map<String, PluginPackage> mPlugins = new ConcurrentHashMap<>();

    public static PluginManager getInstance(Context base) {
        if (sInstance == null) {
            synchronized (PluginManager.class) {
                if (sInstance == null)
                    sInstance = new PluginManager(base);
            }
        }
        return sInstance;
    }

    private PluginManager(Context context) {
        Context app = context.getApplicationContext();
        if (app == null) {
            this.mContext = context;
        } else {
            this.mContext = ((Application) app).getBaseContext();
        }
    }

    public void init() {

    }

    /**
     * 用于加载Assets中的插件
     * TODO 需要开启线程去加载
     *
     * @param pluginAssetsPath 插件在assets中的路径
     * @return
     */
    public PluginPackage loadAssetsPlugin(String pluginAssetsPath) {
        File file = new File(pluginAssetsPath);
        String name = file.getName();
        Log.d(TAG, "assets插件文件名:" + name);
        String pluginSaveDir = mContext.getDir(PluginConstants.PLUGIN_SAVE_DIR, Context.MODE_PRIVATE).getAbsolutePath();
        String pluginSavePath = pluginSaveDir + File.separator + name;
        File apk = new File(pluginSavePath);
        if (!apk.exists()) {
            copyPluginFromAssets(mContext, pluginAssetsPath, pluginSavePath);
        } else {
            //
            if (PluginConstants.isDebug) {
                copyPluginFromAssets(mContext, pluginAssetsPath, pluginSavePath);
            }

            //TODO 每次都需拷贝？
            //TODO 检查插件更新
        }
        PackageInfo apkPackageInfo = ApkUtils.getApkPackageInfo(mContext, pluginSavePath);
        if (apkPackageInfo == null) {
            Log.d(TAG, "获取插件:" + name + "信息失败");
            return null;
        } else {
            return loadPlugin(apkPackageInfo, pluginSavePath);
        }
    }

    /**
     * 将插件从assets中复制到指定路径
     *
     * @param context          上下文
     * @param pluginAssetsPath 插件在Assets中路径
     * @param pluginSavePath   指定路径
     */
    public void copyPluginFromAssets(final Context context, final String pluginAssetsPath, final String pluginSavePath) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getAssets().open(pluginAssetsPath);
            os = new FileOutputStream(pluginSavePath);
            int length = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            Log.d(TAG, "copy插件完成");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "请检查" + pluginAssetsPath + "文件是否存在");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PluginPackage loadPlugin(String apkPath) throws Exception {
        if (TextUtils.isEmpty(apkPath)) {
            return null;
        }
        File apk = new File(apkPath);
        return loadPlugin(apk);
    }

    //TODO 加载插件
    public PluginPackage loadPlugin(File apk) {
        if (null == apk) {
            return null;
        }
        if (!apk.exists()) {
            Log.d(TAG, "插件:" + apk.getAbsolutePath() + "不存在");
            return null;
        }
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(apk.getPath(),
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return null;
        }
        return loadPlugin(packageInfo, apk.getPath());
    }

    private PluginPackage loadPlugin(PackageInfo packageInfo, String dexPath) {
        PluginPackage pluginPackage = mPlugins.get(packageInfo.packageName);
        if (pluginPackage != null) {
            Log.d(TAG, "插件:" + dexPath + "已加载");
            return pluginPackage;
        }
        File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
        File nativeLibDir = mContext.getDir("lib", Context.MODE_PRIVATE);
        DexClassLoader dexClassLoader = createDex(dexPath, dexOutputDir.getAbsolutePath(), nativeLibDir.getAbsolutePath());
        AssetManager assetManager = createAsset(dexPath);
        Resources resources = createRes(assetManager);
        pluginPackage = new PluginPackage(dexClassLoader, resources, packageInfo);
        copyNativeLib(dexPath, mContext, pluginPackage.packageInfo, nativeLibDir);
        mPlugins.put(packageInfo.packageName, pluginPackage);
        Log.d(TAG, "插件:" + dexPath + "加载完成");
        return pluginPackage;
    }

    private DexClassLoader createDex(String dexPath, String dexOutputPath, String nativeLibPath) {
        Log.d(TAG, "dexOutputPath: " + dexOutputPath);
        Log.d(TAG, "nativeLibPath: " + nativeLibPath);
        DexClassLoader loader = new DexClassLoader(dexPath, dexOutputPath, nativeLibPath, mContext.getClassLoader());
        return loader;
    }

    private AssetManager createAsset(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Resources createRes(AssetManager assetManager) {
        Resources superRes = mContext.getResources();
        Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        return resources;
    }

    public void copyNativeLib(String apkPath, Context context, PackageInfo packageInfo, File nativeLibDir) {
        File apk = new File(apkPath);
        if (!apk.exists()) {
            return;
        }
        try {
            String cpuArch;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cpuArch = Build.SUPPORTED_ABIS[0];
            } else {
                cpuArch = Build.CPU_ABI;
            }
            boolean findSo = false;

            ZipFile zipfile = new ZipFile(apk.getAbsolutePath());
            ZipEntry entry;
            Enumeration e = zipfile.entries();
            while (e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if (entry.isDirectory())
                    continue;
                if (entry.getName().endsWith(".so") && entry.getName().contains("lib/" + cpuArch)) {
                    findSo = true;
                    break;
                }
            }
            e = zipfile.entries();
            while (e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".so"))
                    continue;
                if ((findSo && entry.getName().contains("lib/" + cpuArch)) || (!findSo && entry.getName().contains("lib/armeabi/"))) {
                    String[] temp = entry.getName().split("/");
                    String libName = temp[temp.length - 1];
                    System.out.println("verify so " + libName);
                    File libFile = new File(nativeLibDir.getAbsolutePath() + File.separator + libName);
                    String key = packageInfo.packageName + "_" + libName;
                    if (libFile.exists()) {
                        int VersionCode = SoSave.getSoVersion(context, key);
                        if (VersionCode == packageInfo.versionCode) {
                            System.out.println("skip existing so : " + entry.getName());
                            continue;
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(libFile);
                    System.out.println("copy so " + entry.getName() + " of " + cpuArch);
                    copySo(zipfile.getInputStream(entry), fos);
                    SoSave.setSoVersion(context, key, packageInfo.versionCode);
                }
            }
            zipfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copySo(InputStream input, OutputStream output) throws IOException {
        BufferedInputStream bufferedInput = new BufferedInputStream(input);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
        int count;
        byte data[] = new byte[8192];
        while ((count = bufferedInput.read(data, 0, 8192)) != -1) {
            bufferedOutput.write(data, 0, count);
        }
        bufferedOutput.flush();
        bufferedOutput.close();
        output.close();
        bufferedInput.close();
        input.close();
    }

    public Context getHostContext() {
        return this.mContext;
    }

    public PluginPackage getPackage(String packageName) {
        return mPlugins.get(packageName);
    }

    /*
     * 必须指定类名和包名
     * 如果使用startActivityForResult 传入的Context必须是Activity
     */
    public int startActivity(Context context, Intent intent) {
        return startActivity(context, intent, null);
    }

    public int startActivity(Context context, Intent intent, Bundle options) {
        return startActivityForResult(context, intent, -1, options);
    }

    public int startActivityForResult(Context context, Intent intent, int requestCode) {
        return startActivityForResult(context, intent, requestCode, null);
    }

    public int startActivityForResult(Context context, Intent intent, int requestCode, Bundle options) {
        String packageName = intent.getStringExtra(PluginConstants.EXTRA_PACKAGE);
        String className = intent.getStringExtra(PluginConstants.EXTRA_CLASS);
        if (TextUtils.isEmpty(packageName)) {
            Log.d(TAG, "请在intent中设置extra_package");
            return START_RESULT_NO_PKG;
        }
        if(TextUtils.isEmpty(className)){
            Log.d(TAG, "请在intent中设置extra_class");
            return START_RESULT_NO_PKG;
        }
        PluginPackage pluginPackage = mPlugins.get(packageName);
        if (pluginPackage == null) {
            Log.d(TAG, "请检查插件是否已经加载");
            return START_RESULT_NO_PKG;
        }
        //TODO 需求区分
        Class<?> clazz = null;
        Class<?> lunchActivityClass = null;
        try {
            clazz = Class.forName(className, true, pluginPackage.classLoader);
            lunchActivityClass = getProxyActivityClass(clazz);

        } catch (Exception e) {
            e.printStackTrace();
            return START_RESULT_NO_PKG;
        }
        intent.setClass(context, lunchActivityClass);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode, options);
        } else {
            context.startActivity(intent);
        }
        return START_RESULT_SUCCESS;
    }


    private Class<? extends Activity> getProxyActivityClass(Class<?> clazz) {
        Class<? extends Activity> activityClass = null;
        if (PluginActivity.class.isAssignableFrom(clazz)) {
            activityClass = ProxyActivity.class;
        } else if (PluginFragmentActivity.class.isAssignableFrom(clazz)) {
            activityClass = ProxyFragmentActivity.class;
        }
        return activityClass;
    }

    public ComponentName startService(Context context, Intent service) {
        service.setClass(context, PluginConstants.PROXY_SERVICE_CLASS);
        service.putExtra(PluginConstants.EXTRA_SERVER_COMMAND, PluginConstants.EXTRA_START_SERVICE);
        return context.startService(service);
    }

    public boolean stopService(Context context, Intent name) {
        name.setClass(context, PluginConstants.PROXY_SERVICE_CLASS);
        name.putExtra(PluginConstants.EXTRA_SERVER_COMMAND, PluginConstants.EXTRA_STOP_SERVICE);
        context.startService(name);
        return true;
    }

    public boolean bindService(Context context, Intent service, ServiceConnection conn, int flags) {
        service.setClass(context, PluginConstants.PROXY_SERVICE_CLASS);
        service.putExtra(PluginConstants.EXTRA_SERVER_COMMAND, PluginConstants.EXTRA_BIND_SERVICE);
        String serviceName = service.getStringExtra(PluginConstants.EXTRA_CLASS);
        mServiceConns.put(serviceName, conn);
        context.startService(service);
        return true;
    }

    public void unbindService(Context context, ServiceConnection conn) {
        Intent intent = new Intent();
        intent.setClass(context, PluginConstants.PROXY_SERVICE_CLASS);
        String serviceName = "";
        Iterator<Map.Entry<String, ServiceConnection>> entries = mServiceConns.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, ServiceConnection> entry = entries.next();
            ServiceConnection value = entry.getValue();
            if (value != null && value.equals(conn)) {
                serviceName = entry.getKey();
                break;
            }
        }
        intent.putExtra(PluginConstants.EXTRA_CLASS, serviceName);
        intent.putExtra(PluginConstants.EXTRA_SERVER_COMMAND, PluginConstants.EXTRA_UNBIND_SERVICE);
        context.startService(intent);
    }

    public Map<String, IPluginService> getServices() {
        return mServices;
    }

    public Map<String, ServiceConnection> getServiceConns() {
        return mServiceConns;
    }
}