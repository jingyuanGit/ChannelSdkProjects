package com.csdk.plugin.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.csdk.plugin.IAttach;
import com.csdk.plugin.IPlugin;

public class ProxyFragmentActivity extends FragmentActivity implements IAttach {

    private static final String TAG = "ProxyActivity";
    private IPluginActivity mPlugin;
    private ProxyActivityBridge mBridge = new ProxyActivityBridge(this);

    @Override
    public void attachPlugin(IPlugin iPlugin) {
        mPlugin = (IPluginActivity) iPlugin;
        Log.d(TAG, "attach");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBridge.onCreate(getIntent());
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        if (mPlugin != null)
            mPlugin.onStart();
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        if (mPlugin != null)
            mPlugin.onRestart();
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        if (mPlugin != null)
            mPlugin.onResume();
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        if (mPlugin != null)
            mPlugin.onPause();
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        if (mPlugin != null)
            mPlugin.onStop();
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        if (mPlugin != null)
            mPlugin.onDestroy();
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public AssetManager getAssets() {
        return mBridge.getAssets() == null ? super.getAssets() : mBridge.getAssets();
    }

    @Override
    public Resources getResources() {
        return mBridge.getResources() == null ? super.getResources() : mBridge.getResources();
    }

    @Override
    public Resources.Theme getTheme() {
        return mBridge.getTheme() == null ? super.getTheme() : mBridge.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mBridge.getClassLoader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPlugin != null)
            mPlugin.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mPlugin != null)
            mPlugin.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mPlugin != null)
            mPlugin.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mPlugin != null)
            mPlugin.onNewIntent(intent);
        super.onNewIntent(intent);
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onBackPressed() {
        if (mPlugin != null)
            mPlugin.onBackPressed();
        super.onBackPressed();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPlugin != null)
            mPlugin.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mPlugin != null)
            mPlugin.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPlugin != null)
            mPlugin.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (mPlugin != null)
            mPlugin.onWindowAttributesChanged(params);
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mPlugin != null)
            mPlugin.onWindowFocusChanged(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPlugin != null)
            mPlugin.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mPlugin != null)
            mPlugin.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mPlugin != null)
            mPlugin.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mPlugin != null)
            mPlugin.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return super.getApplicationInfo();
    }

}
