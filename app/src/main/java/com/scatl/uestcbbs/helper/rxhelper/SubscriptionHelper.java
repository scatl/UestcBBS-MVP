package com.scatl.uestcbbs.helper.rxhelper;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2019/11/16 17:47
 */
public interface SubscriptionHelper<T> {
    void add(Disposable subscription);
    void cancel(Disposable t);
    void cancelAll();
}
