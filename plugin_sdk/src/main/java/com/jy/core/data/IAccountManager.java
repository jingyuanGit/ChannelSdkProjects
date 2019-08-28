package com.jy.core.data;

import com.jy.core.bean.AccountInfo;

import java.util.List;


public interface IAccountManager {

    void updateAccount(AccountInfo accountInfo);


    void deleteAccount(String account);


    List<AccountInfo> getAccountList();
}
