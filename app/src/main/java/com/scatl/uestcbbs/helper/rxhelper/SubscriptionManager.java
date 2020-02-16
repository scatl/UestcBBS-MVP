package com.scatl.uestcbbs.helper.rxhelper;

import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/16 17:47
 */
public class SubscriptionManager implements SubscriptionHelper<Object> {

    private volatile static SubscriptionManager subscriptionManager;
    private CompositeDisposable compositeDisposable;

    public SubscriptionManager() {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
    }

    @Override
    public void add(Disposable disposable) {
        if (disposable == null) return;
        compositeDisposable.add(disposable);
    }

    @Override
    public void cancel(Disposable disposable) {
        if (compositeDisposable != null) {
            compositeDisposable.delete(disposable);
        }
    }

    @Override
    public void cancelAll() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    public static SubscriptionManager getInstance() {
        if (subscriptionManager == null) {
            synchronized (SubscriptionManager.class) {
                if (subscriptionManager == null) {
                    subscriptionManager = new SubscriptionManager();
                }
            }
        }
        return subscriptionManager;
    }
}
