package com.jy.core.login;


import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.jy.core.R;
import com.jy.core.bean.AccountInfo;
import com.jy.core.data.AccountManagerFormSp;
import com.jy.core.data.IAccountManager;
import com.jy.core.utils.EncryptUtils;
import com.jy.core.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Pattern;

public class LoginPresenter implements LoginContract.LoginPresenter {


    private final String accountRe = "^[a-zA-z0-9]{1,1}[a-zA-Z0-9_@.-]{5,23}";

    private List<AccountInfo> accountLists;

    protected WeakReference<LoginContract.LoginView> mViewRef;

    private Context mContext;

    private String noNetworkTips;

    private String loginingTips;

    private String loginSuccessTips;

    private String loginFailedTips;

    private String registeringTips;

    private String registerSuccessTips;

    private String registerFailedTips;

    private String guestLoginingTips;

    private String accountNotEmptyTips;

    private String accountLimitTips;

    private String passwordNotEmptyTips;

    private String passwordLimitTips;
    private IAccountManager mAccountManager;

    public LoginPresenter(Context context) {
        mContext = context;
        initTips();
    }

    private void initTips() {
        noNetworkTips = mContext.getResources().getString(R.string.csdk_no_network_tips);
        loginingTips = mContext.getResources().getString(R.string.csdk_login_loading_text);
        loginSuccessTips = mContext.getResources().getString(R.string.csdk_login_success_tips);
        loginFailedTips = mContext.getResources().getString(R.string.csdk_login_failed_tips);
        registeringTips = mContext.getResources().getString(R.string.csdk_register_loading_text);
        registerSuccessTips = mContext.getResources().getString(R.string.csdk_register_success_tips);
        registerFailedTips = mContext.getResources().getString(R.string.csdk_register_failed_tips);
        guestLoginingTips = mContext.getResources().getString(R.string.csdk_guest_login_loading_tips);
        accountNotEmptyTips = mContext.getResources().getString(R.string.csdk_account_not_empty_tips);
        accountLimitTips = mContext.getResources().getString(R.string.csdk_account_limit_tips);
        passwordNotEmptyTips = mContext.getResources().getString(R.string.csdk_password_not_empty_tips);
        passwordLimitTips = mContext.getResources().getString(R.string.csdk_password_limit_tips);
    }

    @Override
    public void attachView(LoginContract.LoginView view) {
        mViewRef = new WeakReference<LoginContract.LoginView>(view);
        mAccountManager = new AccountManagerFormSp(view.getContext());
        accountLists = mAccountManager.getAccountList();
        getView().acceptAccountInfoList(accountLists);
    }

    public LoginContract.LoginView getView() {
        return mViewRef.get();
    }

    public void updateAccountList(List<AccountInfo> accountLists) {
        this.accountLists = accountLists;
    }

    @Override
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    /**
     * 用户登录
     */
    @Override
    public void login() {
        final String account = getView().getAccount();
        final String password = getView().getPassword();
        if (!checkNetWork()) {
            return;
        }
        getView().showLoading(loginingTips);
        AccountInfo accountInfo = new AccountInfo(account, password);
        // TODO: 2019/8/28 用户登录， 需要自己实现业务逻辑
        /*
        下面模式登录成功
        插件和外部通讯
        自定义通讯协议
        loginSuccess:xiweise
        loginFailed:network error
        */
        final String useId = EncryptUtils.getStringRandom(10);
        //延时发送消息，模拟网络连接
        //延时发送消息，模拟网络连接
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post("loginSuccess:" + useId);
            }
        }, 1000);
        mAccountManager.updateAccount(accountInfo);
        getView().showSuccessMsg(loginSuccessTips);
    }

    /**
     * 用户注册
     */
    @Override
    public void register() {
        String account = getView().getAccount();
        String password = getView().getPassword();
        if (!checkNetWork()) {
            return;
        }
        if (!checkAccountAndPassword(account, password)) {
            return;
        }
        getView().showLoading(registeringTips);
        AccountInfo accountInfo = new AccountInfo(account, password);
        // TODO: 2019/8/28 用户注册， 需要自己实现业务逻辑
        /*
        下面模式登录成功
        插件和外部通讯
        自定义通讯协议
        loginSuccess:xiweise
        loginFailed:network error
        */
        final String useId = EncryptUtils.getStringRandom(10);
        //延时发送消息，模拟网络连接
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post("loginSuccess:" + useId);
            }
        }, 1000);
        mAccountManager.updateAccount(accountInfo);
        getView().showSuccessMsg(loginSuccessTips);
    }

    /**
     * 游客登录
     */
    @Override
    public void guestLogin() {
        // TODO: 2019/8/28 游客登录，需要自己实现业务逻辑
        getView().showLoading(loginingTips);
        getView().changeBusiness(LoginContract.LoginView.GUEST_LOGIN);
         /*
        下面模式登录成功
        插件和外部通讯
        自定义通讯协议
        loginSuccess:xiweise
        loginFailed:network error
        */
        final String useId = EncryptUtils.getStringRandom(10);
        //延时发送消息，模拟网络连接
        //延时发送消息，模拟网络连接
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post("loginSuccess:" + useId);
            }
        }, 1000);
        AccountInfo accountInfo = new AccountInfo(EncryptUtils.getStringRandom(6), EncryptUtils.getStringRandom(6));
        mAccountManager.updateAccount(accountInfo);
        getView().showSuccessMsg(loginSuccessTips);
    }


    @Override
    public void cancelLogin() {
        // TODO: 2019/8/28 取消登录的后续操作，如取消请求
    }

    /**
     * 判断是否有网络
     *
     * @return
     */
    private boolean checkNetWork() {
        if (!NetworkUtils.isConnected(mContext)) {
            getView().showFailedMsg(noNetworkTips);
            return false;
        }
        return true;
    }

    /**
     * 检查账号密码是否正确
     *
     * @param account
     * @param password
     * @return
     */
    private boolean checkAccountAndPassword(String account, String password) {
        if (TextUtils.isEmpty(account)) {
            getView().showFailedMsg(accountNotEmptyTips);
            return false;
        }
        if (!Pattern.matches(accountRe, account)) {
            getView().showFailedMsg(accountLimitTips);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            getView().showFailedMsg(passwordNotEmptyTips);
            return false;
        }
        if (password.length() < 6 || password.length() > 18) {
            getView().showFailedMsg(passwordLimitTips);
            return false;
        }
        return true;
    }

    /**
     * 显示浮标
     */
    private void showFloatIcon() {

    }

}
