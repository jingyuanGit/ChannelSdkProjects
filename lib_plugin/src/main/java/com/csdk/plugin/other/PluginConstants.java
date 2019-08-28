package com.csdk.plugin.other;


import com.csdk.plugin.activity.ProxyActivity;
import com.csdk.plugin.service.ProxyService;

/**
 * 根据自己需要进行修改
 */
public class PluginConstants {

    //插件名
    public static final String PLUGIN_APK_NAME = "sdkplugin-release.apk";
    //插件存放文件夹，放在/data/data/package/plugins/
    public static final String PLUGIN_SAVE_DIR = "plugins";
    //插件存放在Asset的路径
    public static final String PLUGIN_ASSETS_PATH = PLUGIN_APK_NAME;
    //代理Activity的class
    public static final Class<?> PROXY_ACTIVITY_CLASS = ProxyActivity.class;
    //代理Service的class
    public static final Class<?> PROXY_SERVICE_CLASS = ProxyService.class;
    public static final String EXTRA_CLASS = "extra_class";
    public static final String EXTRA_PACKAGE = "extra_package";
    public static final String EXTRA_SERVER_COMMAND = "extra_server_command";
    public static final int EXTRA_START_SERVICE = 1;
    public static final int EXTRA_STOP_SERVICE = 2;
    public static final int EXTRA_BIND_SERVICE = 3;
    public static final int EXTRA_UNBIND_SERVICE = 4;
    //是否在Debug模式
    public static boolean isDebug = true;
}
