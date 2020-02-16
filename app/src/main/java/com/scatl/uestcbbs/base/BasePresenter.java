package com.scatl.uestcbbs.base;

public class BasePresenter<V> {
    public V view;

    //加载View,建立连接
    public void addView(V v) {
        this.view = v;
    }

    //断开连接
    public void detachView() {
        if (view != null) {
            view = null;
        }
    }
}
