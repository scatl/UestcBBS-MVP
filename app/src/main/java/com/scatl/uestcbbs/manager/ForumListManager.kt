package com.scatl.uestcbbs.manager

import com.scatl.uestcbbs.App
import com.scatl.uestcbbs.http.Observer
import com.scatl.uestcbbs.util.BBSLinkUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.FileUtil
import com.scatl.uestcbbs.util.RetrofitUtil
import com.scatl.uestcbbs.util.subscribeEx
import io.reactivex.disposables.CompositeDisposable
import org.jsoup.Jsoup
import java.io.File

/**
 * Created by sca_tl at 2023/4/26 13:33
 */
class ForumListManager private constructor() {

    var forumList = mutableListOf<ForumList>()
        private set

    private val mCompositeDisposable = CompositeDisposable()

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ForumListManager()
        }
    }

    fun init() {
        val cacheData = FileUtil.readTextFile(File(App.getContext().getExternalFilesDir(Constant.AppPath.TEMP_PATH), Constant.FileName.FORUM_LIST_HTML))
        if (cacheData != null) {
            parseData(cacheData)
        }

        RetrofitUtil
            .getInstance()
            .apiService
            .allForumList()
            .subscribeEx(Observer<String>().observer {
                onSuccess {
                    FileUtil.saveStringToFile(it, File(App.getContext().getExternalFilesDir(Constant.AppPath.TEMP_PATH), Constant.FileName.FORUM_LIST_HTML))
                    parseData(it)
                }

                onSubscribe {
                    mCompositeDisposable.add(it)
                }
            })
    }

    private fun parseData(s: String) {
        try {
            val document = Jsoup.parse(s.replace("<![CDATA[<div id=\"flsrchdiv\">", ""))
            val data = document.select("li")[0]

            var currentF = ForumList("", 0, null)
            var currentS = SubForum("", 0, null)
            data.select("p").forEachIndexed { index, p ->
                if (p.hasClass("xw1")) {
                    val f = ForumList(p.text(), BBSLinkUtil.getLinkInfo(p.select("a").attr("href")).id, null)
                    currentF = f
                    forumList.add(f)
                } else if (p.hasClass("sub")) {
                    val sub = SubForum(p.text(), BBSLinkUtil.getLinkInfo(p.select("a").attr("href")).id, null)
                    if (currentF.sub == null) {
                        currentF.sub = mutableListOf()
                    }
                    currentS = sub

                    if (currentS.child == null) {
                        val self = ChildForum(sub.name, sub.id)
                        currentS.child = mutableListOf(self)
                    }

                    currentF.sub!!.add(sub)
                } else if (p.hasClass("child")) {
                    val c = ChildForum(p.text(), BBSLinkUtil.getLinkInfo(p.select("a").attr("href")).id)
                    if (currentS.child == null) {
                        currentS.child = mutableListOf()
                    }
                    currentS.child!!.add(c)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getParentForum(fid: Int): SubForum {
        forumList.forEach { group ->
            group.sub?.forEach { sub ->
                sub.child?.forEach {
                    if (it.id == fid) {
                        return sub
                    }
                }
            }
        }
        return SubForum("", 0, null)
    }

    fun getForumInfo(fid: Int): ChildForum {
        forumList.forEach { group ->
            group.sub?.forEach { sub ->
                sub.child?.forEach {
                    if (it.id == fid) {
                        return it
                    }
                }
            }
        }
        return ChildForum("", 0)
    }

    data class ForumList(
        var groupName: String?,
        var id: Int?,
        var sub: MutableList<SubForum>?
    )

    data class SubForum (
        var name: String?,
        var id: Int,
        var child: MutableList<ChildForum>?
    )

    data class ChildForum(
        var name: String?,
        var id: Int
    )
}