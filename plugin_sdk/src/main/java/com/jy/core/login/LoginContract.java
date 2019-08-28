package com.jy.core.login;



import android.content.Context;

import com.jy.core.base.BasePresenter;
import com.jy.core.base.BaseView;
import com.jy.core.bean.AccountInfo;

import java.util.List;


public interface LoginContract {

    interface LoginView extends BaseView<LoginPresenter> {

         int INDEX = 0; //首页业务，选择登录方式
         int LOGIN = 1; //登录业务
         int REGISTER = 2; //注册业务
         int GUEST_LOGIN = 3; //游客登录业务


        Context getContext();

        void showLoading(String message);

        void hideLoading(String message);

        void showSuccessMsg(String msg);

        void showFailedMsg(String msg);

        void acceptAccountInfoList(List<AccountInfo> ls);

        void changeBusiness(int business);

        int getCurrentBusiness();

        String getAccount();

        String getPassword();
    }

    interface LoginPresenter extends BasePresenter<LoginView> {
        void login();

        void register();

        void guestLogin();//游客登录

        void cancelLogin();
    }

}
