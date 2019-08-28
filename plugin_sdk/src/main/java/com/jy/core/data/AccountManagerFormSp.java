package com.jy.core.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jy.core.bean.AccountInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountManagerFormSp implements IAccountManager {

    private final String ACCOUNT_LIST_NAME = "CSDK_ACCOUNT_LIST";


    private SharedPreferences sp;

    private Gson mGson;

    public AccountManagerFormSp(Context context) {
        sp = context.getSharedPreferences("CSDK_ACCOUNT_LIST", Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    @Override
    public void updateAccount(AccountInfo accountInfo) {
        //使用sp保存，整个json保存
        List<AccountInfo> accountInfoList = getAccountList();
        if (accountInfoList.contains(accountInfo)) {
            int index = accountInfoList.indexOf(accountInfo);
            accountInfoList.set(index, accountInfo);
        } else {
            accountInfoList.add(accountInfo);
        }
        //将List转为json，存储
        String json = mGson.toJson(accountInfoList);
        sp.edit().putString(ACCOUNT_LIST_NAME, json).apply();
    }

    public void updateAccountList(List<AccountInfo> accountInfoList) {
        //将List转为json，存储
        String json = mGson.toJson(accountInfoList);
        sp.edit().putString(ACCOUNT_LIST_NAME, json).apply();


    }

    @Override
    public void deleteAccount(String account) {

    }

    @Override
    public List<AccountInfo> getAccountList() {
        List<AccountInfo> accountInfoList = new ArrayList<>();
        String accountListJson = sp.getString(ACCOUNT_LIST_NAME, "");

        if (!TextUtils.isEmpty(accountListJson)) {
            List<AccountInfo> temp = mGson.fromJson(accountListJson, new TypeToken<List<AccountInfo>>() {
            }.getType());
            if (temp.size() > 0)
                Collections.sort(temp);
            accountInfoList = temp;
        }
        return accountInfoList;
    }
}
