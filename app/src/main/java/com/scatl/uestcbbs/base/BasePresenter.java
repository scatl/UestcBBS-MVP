package com.scatl.uestcbbs.base;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V> {
    public V view;
    public CompositeDisposable disposable;

    //加载View,建立连接
    public void attachView(V v) {
        this.view = v;
        disposable = new CompositeDisposable();
    }

    //断开连接
    public void detachView() {
        if (view != null) {
            view = null;
            disposable.clear();
            disposable = null;
        }
    }
}
