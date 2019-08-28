package com.jy.core.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.csdk.plugin.activity.PluginFragmentActivity;
import com.jy.core.R;
import com.jy.core.login.LoginFragment;
import com.jy.core.utils.ActivityUtils;

import java.lang.ref.WeakReference;

public class RouteActivity extends PluginFragmentActivity {

    public int current_business = 0; //默认登录业务

    public static final int BUSINESS_LOGIN = 0; //登录业务

    public static final int BUSINESS_PAY = 1; //支付业务

    private Fragment mFragment;

    private Handler mh = new MH(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.csdk_proxy_activity);
        Intent intent = getIntent();
        current_business = intent.getIntExtra("business", 0);
        //根据业务，切换不同的fragment
        switch (current_business) {
            case BUSINESS_LOGIN:
                that.setFinishOnTouchOutside(false);
                //登录需要用到外部存储读写权限
                mFragment = new LoginFragment();
                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.csk_proxy_activity_flyt_content);
                break;
            case BUSINESS_PAY:
                String orderInfoJson = intent.getStringExtra("orderInfoJson");
                String roleInfoJson = intent.getStringExtra("roleInfoJson");
                String userInfoJson = intent.getStringExtra("userInfoJson");
                mFragment = PayFragment.newInstance(orderInfoJson, roleInfoJson, userInfoJson);
                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.csk_proxy_activity_flyt_content);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (current_business == BUSINESS_LOGIN) {
//            CwSdkLog.d("RouteActivity onBackPressed");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            CwSdkLog.d("RouteActivity onKeyDownBack");
            return current_business == BUSINESS_LOGIN ? true : super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
//        CwSdkLog.d("RouteActivity onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
//        CwSdkLog.d("RouteActivity onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    static class MH extends Handler {

        WeakReference<Activity> weakReference;

        public MH(Activity activity) {
            this.weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = weakReference.get();
            switch (msg.what) {
                case 0:
                    if (activity != null) {
                        activity.finish();
                    }
                    break;
            }
        }
    }
}
