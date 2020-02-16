package com.scatl.uestcbbs.helper.rxhelper;

import com.scatl.uestcbbs.helper.ExceptionHelper;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/16 17:55
 */
public abstract class Observer<T> implements io.reactivex.Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {
        //添加订阅关系
        OnDisposable(d);
    }

    @Override
    public void onNext(T t) {
        OnSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        //自定义异常的传递
        onError(ExceptionHelper.handleException(e));
    }

    @Override
    public void onComplete() {
        OnCompleted();
    }

    public abstract void OnSuccess(T t);

    //public abstract void OnFail(String msg);

    public abstract void onError(ExceptionHelper.ResponseThrowable e);

    public abstract void OnCompleted();

    public abstract void OnDisposable(Disposable d);
}
