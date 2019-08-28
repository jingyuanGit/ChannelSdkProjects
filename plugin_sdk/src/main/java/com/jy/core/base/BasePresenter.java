package com.jy.core.base;

/**
 * Created by yuan on 2017/5/17.
 */

public interface BasePresenter<T extends BaseView> {

    void attachView(T view);

    void detachView();
}
