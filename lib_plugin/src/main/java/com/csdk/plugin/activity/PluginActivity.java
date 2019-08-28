package com.csdk.plugin.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.csdk.plugin.PluginManager;
import com.csdk.plugin.other.PluginPackage;



public class PluginActivity extends Activity implements IPluginActivity {

    protected Activity that;

    protected PluginManager mPluginManager;

    protected PluginPackage mPluginPackage;

    @Override
    public void setContentView(View view) {
        that.setContentView(view);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        that.setContentView(view, params);
    }

    @Override
    public void setContentView(int layoutResID) {
        that.setContentView(layoutResID);
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        that.addContentView(view, params);
    }

    @Override
    public View findViewById(int id) {
        return that.findViewById(id);
    }

    public <T extends View> T findView(int id) {
        return (T) that.findViewById(id);
    }

    @Override
    public Intent getIntent() {
        return that.getIntent();
    }

    @Override
    public ClassLoader getClassLoader() {
        return that.getClassLoader();
    }

    @Override
    public Resources getResources() {
        return that.getResources();
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return that.getLayoutInflater();
    }

    @Override
    public MenuInflater getMenuInflater() {
        return that.getMenuInflater();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return that.getSharedPreferences(name, mode);
    }

    @Override
    public Context getApplicationContext() {
        return that.getApplicationContext();
    }

    @Override
    public WindowManager getWindowManager() {
        return that.getWindowManager();
    }

    @Override
    public Window getWindow() {
        return that.getWindow();
    }

    @Override
    public Object getSystemService(String name) {
        return that.getSystemService(name);
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        that.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mPluginManager.startActivityForResult(that, intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        mPluginManager.startActivityForResult(that, intent, requestCode, options);
    }

    @Override
    public void startActivity(Intent intent) {
        mPluginManager.startActivity(that, intent);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        mPluginManager.startActivity(that, intent, options);
    }

    @Override
    public ComponentName startService(Intent service) {
        return mPluginManager.startService(that, service);
    }

    @Override
    public boolean stopService(Intent name) {
        return mPluginManager.stopService(that, name);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return mPluginManager.bindService(that, service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        mPluginManager.unbindService(that, conn);
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

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        that.sendOrderedBroadcast(intent, receiverPermission);
    }


    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return that.registerReceiver(receiver, filter);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return that.registerReceiver(receiver, filter, flags);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return that.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        return that.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        that.unregisterReceiver(receiver);
    }

    @Override
    public void finish() {
        that.finish();
    }

    @Override
    public AssetManager getAssets() {
        return that.getAssets();
    }

    @Override
    public boolean isFinishing() {
        return that.isFinishing();
    }


    @Override
    public void attach(Activity proxyActivity, PluginPackage pluginPackage) {
        that = proxyActivity;
        mPluginPackage = pluginPackage;
        mPluginManager = PluginManager.getInstance(that);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }
}
