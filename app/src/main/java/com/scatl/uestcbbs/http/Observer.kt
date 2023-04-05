package com.scatl.uestcbbs.http

import com.scatl.uestcbbs.helper.ExceptionHelper
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * created by sca_tl at 2022/5/20 21:53
 */
class Observer<T: Any>: Observer<T> {

    override fun onSubscribe(d: Disposable) {
        if (::mListener.isInitialized) {
            mListener.mSubscribeAction?.invoke(d)
        }
    }

    override fun onNext(t: T) {
        if (::mListener.isInitialized) {
            mListener.mSuccess?.invoke(t)
        }
    }

    override fun onError(e: Throwable) {
        if (::mListener.isInitialized) {
            mListener.mErrorAction?.invoke(ExceptionHelper.handleException(e))
        }
    }

    override fun onComplete() {
        if (::mListener.isInitialized) {
            mListener.mCompleteAction?.invoke()
        }
    }

    private lateinit var mListener: ListenerBuilder
    fun observer(listenerBuilder: ListenerBuilder.() -> Unit): com.scatl.uestcbbs.http.Observer<T> {
        mListener = ListenerBuilder().also(listenerBuilder)
        return this
    }

    inner class ListenerBuilder {
        internal var mSubscribeAction: ((Disposable) -> Unit)? = null
        internal var mNextAction: ((T) -> Unit)? = null
        internal var mErrorAction: ((ExceptionHelper.ResponseThrowable) -> Unit)? = null
        internal var mCompleteAction: (() -> Unit)? = null
        internal var mSuccess: ((T) -> Unit)? = null
//        internal var mDataError: ((T) -> Unit)? = null

        fun onSubscribe(action: (Disposable) -> Unit) {
            mSubscribeAction = action
        }

        fun onNext(action: (T) -> Unit) {
            mNextAction = action
        }

        fun onError(action: (ExceptionHelper.ResponseThrowable) -> Unit) {
            mErrorAction = action
        }

        fun onComplete(action: () -> Unit) {
            mCompleteAction = action
        }

        fun onSuccess(action: (T) -> Unit) {
            mSuccess = action
        }

    }

}