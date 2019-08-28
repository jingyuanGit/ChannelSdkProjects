package com.jy.core.base;

/**
 * Created by yuan on 2017/5/17.
 */

public interface BaseView<T extends BasePresenter> {
    T createPresenter();
}
