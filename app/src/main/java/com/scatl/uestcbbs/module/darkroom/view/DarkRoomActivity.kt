package com.scatl.uestcbbs.module.darkroom.view

import android.content.Intent
import android.graphics.Color
import android.view.animation.AnimationUtils
import com.gyf.immersionbar.ImmersionBar
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityDarkRoomBinding
import com.scatl.uestcbbs.entity.DarkRoomBean
import com.scatl.uestcbbs.module.darkroom.adapter.DarkRoomAdapter
import com.scatl.uestcbbs.module.darkroom.presenter.DarkRoomPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * Created by sca_tl at 2023/6/6 15:32
 */
class DarkRoomActivity: BaseVBActivity<DarkRoomPresenter, DarkRoomView, ActivityDarkRoomBinding>(), DarkRoomView {

    private lateinit var darkRoomAdapter: DarkRoomAdapter

    override fun getViewBinding() = ActivityDarkRoomBinding.inflate(layoutInflater)

    override fun initPresenter() = DarkRoomPresenter()

    override fun initView(theftProof: Boolean) {
        super.initView(true)

        mBinding.refreshLayout.setEnableLoadMore(false)

        darkRoomAdapter = DarkRoomAdapter(R.layout.item_dark_room)
        mBinding.recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in)
        mBinding.recyclerView.adapter = darkRoomAdapter
        mBinding.statusView.loading()
    }

    override fun setOnItemClickListener() {
        darkRoomAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.user_name) {
                val intent = Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, darkRoomAdapter.data[position].uid)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getDarkRoomList()
    }

    override fun onGetDarkRoomDataSuccess(darkRoomBeanList: List<DarkRoomBean>) {
        mBinding.statusView.success()
        mBinding.refreshLayout.finishRefresh()
        darkRoomAdapter.setNewData(darkRoomBeanList)
        mBinding.recyclerView.scheduleLayoutAnimation()
    }

    override fun onGetDarkRoomDataError(msg: String?) {
        mBinding.statusView.error(msg)
    }

    override fun setStatusBar() {
        ImmersionBar
            .with(this)
            .statusBarColorInt(Color.parseColor("#303030"))
            .init()
    }

    override fun getContext() = this
}