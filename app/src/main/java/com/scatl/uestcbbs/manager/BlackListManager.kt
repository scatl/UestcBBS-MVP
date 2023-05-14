package com.scatl.uestcbbs.manager

import android.text.TextUtils
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.entity.BlackListBean
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.RetrofitUtil
import com.scatl.uestcbbs.util.subscribeEx
import com.scatl.util.NumberUtil
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import org.litepal.LitePal
import java.util.regex.Pattern

/**
 * Created by sca_tl at 2023/4/11 16:54
 */
class BlackListManager private constructor() {

    private val mCompositeDisposable = CompositeDisposable()

    var updateTime = 0L
        private set

    var blackList = mutableListOf<BlackListBean>()
        private set

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BlackListManager()
        }
    }

    fun init() {
        RetrofitUtil
            .getInstance()
            .apiService
            .getAccountBlackList(1)
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    if (it.contains("先登录后才能继续")) {

                    } else {
                        try {
                            val document = Jsoup.parse(it)
                            val pageText = document.select("div[class=pgs cl mtm]").select("div[class=pg]").select("label").select("span").attr("title")
                            var totalPage = 1
                            if (!TextUtils.isEmpty(pageText)) {
                                val matcher = Pattern.compile("(.*?)(\\d+)(.*?)").matcher(pageText)
                                if (matcher.find()) {
                                    totalPage = NumberUtil.parseInt(matcher.group(2), 1)
                                }
                            }

                            if (totalPage == 1) {
                                val data = parseHtml(it)
                                initDataBase(data)
                            } else {
                                getAllData(totalPage)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                onSubscribe {
                    mCompositeDisposable.add(it)
                }
            })
    }

    //这里是串行执行的，并行（加上subscribeOn(Schedulers.io())）的话会503
    private fun getAllData(totalPage: Int) {
        val observables = ArrayList<ObservableSource<String>>()
        for (page in 1 .. totalPage) {
            observables.add(RetrofitUtil.getInstance().apiService.getAccountBlackList(page))
        }

        val function = Function<Array<Any>, ArrayList<BlackListBean>> { t ->
            val list: ArrayList<BlackListBean> = ArrayList()
            for (item in t) {
                val data = parseHtml(item as String)
                list.addAll(data)
            }
            list
        }

        Observable
            .zipArray(function, false, Observable.bufferSize(), *(observables.toTypedArray()))
            .subscribeEx(Observer<ArrayList<BlackListBean>>().observer {
                onSuccess {
                    initDataBase(it)
                }

                onSubscribe {
                    mCompositeDisposable.add(it)
                }
            })
    }

    private fun parseHtml(html: String): MutableList<BlackListBean> {
        val list: MutableList<BlackListBean> = ArrayList()

        try {
            val document = Jsoup.parse(html)
            val elements = document.select("ul[class=buddy cl]").select("li")
            for (i in elements.indices) {
                val blackListBean = BlackListBean()
                blackListBean.userName = elements[i].select("h4").select("a")[1].text()
                blackListBean.uid = BBSLinkUtil.getLinkInfo(elements[i].select("h4").select("a")[1].attr("href")).id
                blackListBean.avatar = "https://bbs.uestc.edu.cn/uc_server/avatar.php?uid=" + blackListBean.uid + "&size=middle"
                list.add(blackListBean)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    private fun initDataBase(data: MutableList<BlackListBean>) {
        LitePal.deleteAll(BlackListBean::class.java)
        LitePal.saveAll(data)
        updateTime = System.currentTimeMillis()
        blackList = data
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.BLACK_LIST_DATA_CHANGED))
    }

    fun delete(uid: Int) {
        LitePal.deleteAll(BlackListBean::class.java, "uid = $uid")

        val iterator = blackList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().uid == uid) {
                iterator.remove()
                break
            }
        }
        
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.BLACK_LIST_DATA_CHANGED))
    }

    fun add(uid: Int, name: String) {
        val blackListBean = BlackListBean().apply {
            this.uid = uid
            this.userName = name
            this.avatar = Constant.USER_AVATAR_URL.plus(uid)
        }
        blackListBean.save()
        blackList.add(0, blackListBean)
        EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.BLACK_LIST_DATA_CHANGED))
    }

    fun isBlacked(uid: Int) = blackList.contains(BlackListBean().apply { this.uid = uid })

}