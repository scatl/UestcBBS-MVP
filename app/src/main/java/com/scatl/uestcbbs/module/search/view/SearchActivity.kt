package com.scatl.uestcbbs.module.search.view

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import com.jaeger.library.StatusBarUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivitySearchBinding
import com.scatl.uestcbbs.entity.SearchPostBean
import com.scatl.uestcbbs.entity.SearchUserBean
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.search.presenter.SearchPresenter
import com.scatl.uestcbbs.module.search.adapter.SearchPostAdapter
import com.scatl.uestcbbs.module.search.adapter.SearchUserAdapter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ColorUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by sca_tl at 2023/2/8 10:21
 */
class SearchActivity : BaseVBActivity<SearchPresenter, SearchView, ActivitySearchBinding>(),
    SearchView, View.OnKeyListener, TextWatcher {

    private lateinit var searchUserAdapter: SearchUserAdapter
    private lateinit var searchPostAdapter: SearchPostAdapter
    private var page = 1

    override fun getViewBinding() = ActivitySearchBinding.inflate(layoutInflater)

    override fun initPresenter() = SearchPresenter()

    override fun getContext() = this

    override fun initView(theftProof: Boolean) {
        super.initView(false)
        bindClickEvent(mBinding.edittext, mBinding.searchBtn)
        mBinding.edittext.addTextChangedListener(this)
        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            afterTextChanged(mBinding.edittext.editableText)
        }

        CommonUtil.showSoftKeyboard(this, mBinding.edittext, 1)

        searchPostAdapter = SearchPostAdapter(R.layout.item_simple_post)
        searchUserAdapter = SearchUserAdapter(R.layout.item_search_user)
        mBinding.rv.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        if (v == mBinding.searchBtn) {
            CommonUtil.hideSoftKeyboard(this@SearchActivity, mBinding.edittext)
            mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        }
    }

    override fun setOnItemClickListener() {
        searchPostAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(this@SearchActivity, NewPostDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.TOPIC_ID, searchPostAdapter.data[position].topic_id)
            }
            startActivity(intent)
        }

        searchPostAdapter.setOnItemChildClickListener { adapter, view, position ->
            val intent = Intent(this@SearchActivity, UserDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.USER_ID, searchPostAdapter.data[position].user_id)
            }
            startActivity(intent)
        }

        searchUserAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(this@SearchActivity, UserDetailActivity::class.java).apply {
                putExtra(Constant.IntentKey.USER_ID, searchUserAdapter.data[position].uid)
            }
            startActivity(intent)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        startSearch()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        startSearch()
    }

    private fun startSearch() {
        val inputText = mBinding.edittext.text.toString()
        if (mBinding.btnByPost.isChecked) {
            mPresenter?.searchPost(page, SharePrefUtil.getPageSize(this@SearchActivity), inputText)
        } else {
            mPresenter?.searchUser(page, SharePrefUtil.getPageSize(this@SearchActivity),
                inputText.replace(" ", "").replace("\n", ""))
        }
    }

    override fun onSearchUserSuccess(searchUserBean: SearchUserBean) {
        page += 1
        mBinding.hint.text = ""
        mBinding.refreshLayout.finishRefresh()
        if (searchUserBean.has_next == 1) {
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishRefreshWithNoMoreData()
        }

        if (searchUserBean.page == 1) {
            mBinding.rv.adapter = searchUserAdapter
            mBinding.rv.scheduleLayoutAnimation()
            searchUserAdapter.setNewData(searchUserBean.body.list)
        } else {
            searchUserAdapter.addData(searchUserBean.body.list)
        }
        if (searchUserAdapter.data.size == 0) {
            mBinding.hint.text = "啊哦，没有数据"
        }
    }

    override fun onSearchUserError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (page == 1) {
            if (searchPostAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.hint.text = msg
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onSearchPostSuccess(searchPostBean: SearchPostBean) {
        page += 1
        mBinding.hint.text = ""
        mBinding.refreshLayout.finishRefresh()
        if (searchPostBean.has_next == 1) {
            mBinding.refreshLayout.finishLoadMore(true)
        } else {
            mBinding.refreshLayout.finishRefreshWithNoMoreData()
        }

        if (searchPostBean.page == 1) {
            mBinding.rv.adapter = searchPostAdapter
            mBinding.rv.scheduleLayoutAnimation()
            searchPostAdapter.addSearchPostData(searchPostBean.list, true)
        } else {
            searchPostAdapter.addSearchPostData(searchPostBean.list, false)
        }

        if (searchPostAdapter.data.size == 0) {
            mBinding.hint.text = "啊哦，没有数据"
        }
    }

    override fun onSearchPostError(msg: String?) {
        mBinding.refreshLayout.finishRefresh()
        if (page == 1) {
            if (searchPostAdapter.data.size != 0) {
                showToast(msg, ToastType.TYPE_ERROR)
            } else {
                mBinding.hint.text = msg
            }
            mBinding.refreshLayout.finishLoadMore()
        } else {
            mBinding.refreshLayout.finishLoadMore(false)
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
            CommonUtil.hideSoftKeyboard(this@SearchActivity, v)
            mBinding.refreshLayout.autoRefresh(0, 300, 1f, false)
        }
        return false
    }

    override fun setStatusBar() {
        StatusBarUtil.setColor(this, ColorUtil.getAttrColor(this, R.attr.colorSurface), 0)
    }

    override fun afterTextChanged(s: Editable?) {
        val searchId = try { s.toString().toInt() } catch (e: Exception) { -1 }
        if (searchId != -1) {
            mBinding.suggestionText.apply {
                visibility = View.VISIBLE
                text = if (mBinding.btnByPost.isChecked) {
                    "检测到你输入了一串可能代表帖子id的数字，点击即可跳转到该帖子详情页"
                } else {
                    "检测到你输入了一串可能代表用户id的数字，点击即可跳转到该用户详情页"
                }
                setOnClickListener {
                    val intent = if (mBinding.btnByPost.isChecked) {
                        Intent(this@SearchActivity, NewPostDetailActivity::class.java).apply {
                            putExtra(Constant.IntentKey.TOPIC_ID, searchId)
                        }
                    } else {
                        Intent(this@SearchActivity, UserDetailActivity::class.java).apply {
                            putExtra(Constant.IntentKey.USER_ID, searchId)
                        }
                    }
                    startActivity(intent)
                }
            }
        } else {
            mBinding.suggestionText.visibility = View.GONE
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

}