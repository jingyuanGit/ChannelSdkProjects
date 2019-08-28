package com.jy.core.bean;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuan on 2017/8/19.
 */

public class AccountInfo  implements Comparable<AccountInfo>{

    private String account;

    private String password;

    private String lastLoginTime;

    private int isGuest;

    public AccountInfo() {
    }

    public AccountInfo(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getIsGuest() {
        return isGuest;
    }

    public void setIsGuest(int isGuest) {
        this.isGuest = isGuest;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof AccountInfo)) {
            return false;
        }
        AccountInfo temp = (AccountInfo) obj;
        return temp.getAccount().equals(this.getAccount());
    }

    @Override
    public int compareTo(@NonNull AccountInfo accountInfo) {
        String lastLoginTime1 = this.getLastLoginTime();
        String lastLoginTime2 = accountInfo.getLastLoginTime();
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
        try {
            Date date1 = dateFormat.parse(lastLoginTime1);
            Date date2 = dateFormat.parse(lastLoginTime2);
            int i = date2.compareTo(date1);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }
}
