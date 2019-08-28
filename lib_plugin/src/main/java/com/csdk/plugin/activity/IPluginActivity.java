package com.csdk.plugin.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

import com.csdk.plugin.IPlugin;
import com.csdk.plugin.other.PluginPackage;


public interface IPluginActivity extends IPlugin {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void attach(Activity proxyActivity, PluginPackage pluginPackage);

    void onRestart();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

    void onNewIntent(Intent intent);

    void onRestoreInstanceState(Bundle savedInstanceState);

    boolean onTouchEvent(MotionEvent event);

    boolean onKeyUp(int keyCode, KeyEvent event);

    boolean onKeyDown(int keyCode, KeyEvent event);

    void onWindowAttributesChanged(LayoutParams params);

    void onWindowFocusChanged(boolean hasFocus);

    void onBackPressed();

    boolean onCreateOptionsMenu(Menu menu);

    boolean onOptionsItemSelected(MenuItem item);

    void onConfigurationChanged(Configuration configuration);

    @TargetApi(23)
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}
