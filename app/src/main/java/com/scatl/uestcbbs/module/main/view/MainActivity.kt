package com.scatl.uestcbbs.module.main.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.jaeger.library.StatusBarUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityMainBinding
import com.scatl.uestcbbs.entity.SettingsBean
import com.scatl.uestcbbs.entity.UpdateBean
import com.scatl.uestcbbs.module.main.adapter.MainViewPagerAdapter
import com.scatl.uestcbbs.module.main.presenter.MainPresenter
import com.scatl.uestcbbs.manager.MessageManager
import com.scatl.uestcbbs.module.post.view.CreatePostActivity
import com.scatl.uestcbbs.module.post.view.CreatePostEntranceActivity
import com.scatl.uestcbbs.module.update.view.UpdateFragment
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.services.DayQuestionService
import com.scatl.uestcbbs.services.HeartMsgService
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.util.ServiceUtil.isServiceRunning
import com.scatl.util.SystemUtil
import org.greenrobot.eventbus.EventBus


/**
 * Created by sca_tl at 2023/4/11 17:29
 */
class MainActivity: BaseVBActivity<MainPresenter, MainView, ActivityMainBinding>(), MainView {

    companion object {
        const val KEY_PAGE_INDEX = "page_index"
    }

    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var mPageIndex = 0
    private var shortCutMessage = false
    private var shortCutHotPost = false

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun initPresenter() = MainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        if ("message" == intent.action) {
            shortCutMessage = true
        }
        if ("hot_post" == intent.action) {
            shortCutHotPost = true
        }
        super.onCreate(savedInstanceState)
    }

    override fun getIntent(intent: Intent?) {
        mPageIndex = intent?.getIntExtra(KEY_PAGE_INDEX, 0)?:0
    }

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        mBinding.createBtn.setOnClickListener(this)
        mBinding.createBtn.setOnLongClickListener(this)

        mainViewPagerAdapter = MainViewPagerAdapter(this, shortCutHotPost)
        mBinding.viewpager.apply {
            adapter = mainViewPagerAdapter
            isUserInputEnabled = false
            offscreenPageLimit = 3
        }

        if (shortCutMessage) {
            mBinding.viewpager.setCurrentItem(2, false)
            mBinding.createBtn.hide()
            mBinding.navigationBar.selectedItemId = R.id.page_notification
        } else {
            when (mPageIndex) {
                0 -> {
                    mBinding.viewpager.setCurrentItem(0, false)
                    mBinding.navigationBar.selectedItemId = R.id.page_home
                    mBinding.createBtn.show()
                }
                1 -> {
                    mBinding.viewpager.setCurrentItem(1, false)
                    mBinding.navigationBar.selectedItemId = R.id.page_board_list
                    mBinding.createBtn.hide()
                }
                3 -> {
                    mBinding.viewpager.setCurrentItem(3, false)
                    mBinding.navigationBar.selectedItemId = R.id.page_mine
                    mBinding.createBtn.hide()
                }
            }
        }

        startService()
        mPresenter?.getSettings()
        mPresenter?.getUpdate(SystemUtil.getVersionCode(this), false)
    }

    override fun onClick(v: View) {
        if (v == mBinding.createBtn) {
            val loc = intArrayOf(0, 0)
            mBinding.createBtn.getLocationOnScreen(loc)
            val intent = Intent(this, CreatePostEntranceActivity::class.java).apply {
                putExtra("x", loc[0] + mBinding.createBtn.width / 2)
                putExtra("y", loc[1] + mBinding.createBtn.height / 2)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mBinding.createBtn, "transition")
            ActivityCompat.startActivity(this, intent, options.toBundle())
        }
    }

    override fun onLongClick(v: View): Boolean {
        if (v == mBinding.createBtn) {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
            return true
        }
        return false
    }

    override fun setOnItemClickListener() {
        mBinding.navigationBar.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.page_home -> {
                    mBinding.createBtn.show()
                    mBinding.viewpager.setCurrentItem(0, false)
                }

                R.id.page_board_list -> {
                    mBinding.createBtn.hide()
                    mBinding.viewpager.setCurrentItem(1, false)
                }

                R.id.page_notification -> {
                    mBinding.createBtn.hide()
                    mBinding.viewpager.setCurrentItem(2, false)
                }

                R.id.page_mine -> {
                    mBinding.createBtn.hide()
                    mBinding.viewpager.setCurrentItem(3, false)
                }
            }
            true
        }
        mBinding.navigationBar.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.page_home) {
                EventBus.getDefault().post(BaseEvent<Any>(BaseEvent.EventCode.HOME_REFRESH))
            }
        }
    }

    override fun getUpdateSuccess(updateBean: UpdateBean) {
        try {
            if (updateBean.updateInfo.isValid
                && updateBean.updateInfo.apkVersionCode > SystemUtil.getVersionCode(this)
                && updateBean.updateInfo.apkVersionCode != SharePrefUtil.getIgnoreVersionCode(this)) {
                val bundle = Bundle()
                bundle.putSerializable(Constant.IntentKey.DATA_1, updateBean)
                UpdateFragment.getInstance(bundle).show(supportFragmentManager, TimeUtil.getStringMs())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getSettingsSuccess(settingsBean: SettingsBean) {
        try {
            if (SharePrefUtil.getGraySaturation(this) != settingsBean.graySaturation) {
                SharePrefUtil.setGraySaturation(this, settingsBean.graySaturation)
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        when(baseEvent.eventCode) {
            BaseEvent.EventCode.NIGHT_MODE -> {
                val night = baseEvent.eventData as Boolean
                AppCompatDelegate.setDefaultNightMode(if (night) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                finish()
                val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                    putExtra(KEY_PAGE_INDEX, 3)
                }
                startActivity(intent)
                overridePendingTransition(
                    R.anim.switch_night_mode_fade_in,
                    R.anim.switch_night_mode_fade_out
                )
            }

            BaseEvent.EventCode.SET_MSG_COUNT -> {
                val badgeDrawable = mBinding.navigationBar.getOrCreateBadge(R.id.page_notification)
                val msgCount = MessageManager.INSTANCE.getUnreadMsgCount()
                if (msgCount == 0) {
                    badgeDrawable.isVisible = false
                    badgeDrawable.clearNumber()
                } else {
                    badgeDrawable.isVisible = true
                    badgeDrawable.number = msgCount
                }
            }

            BaseEvent.EventCode.SWITCH_TO_MESSAGE -> {
                mBinding.viewpager.setCurrentItem(2, false)
            }

            BaseEvent.EventCode.HOME_NAVIGATION_HIDE -> {
                val hide = baseEvent.eventData as Boolean
                if (hide) {
                    if (mBinding.navigationBar.visibility != View.GONE) {
                        mBinding.createBtn.hide()
                        mBinding.navigationBar.visibility = View.GONE
                        mBinding.navigationBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.view_dismiss_y0_y1_no_alpha))
                    }
                } else {
                    if (mBinding.navigationBar.visibility != View.VISIBLE) {
                        if (mBinding.viewpager.currentItem == 0) {
                            mBinding.createBtn.show()
                        }
                        mBinding.navigationBar.visibility = View.VISIBLE
                        mBinding.navigationBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.view_appear_y1_y0_no_alpha))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, HeartMsgService::class.java))
    }

    private fun startService() {
        if (SharePrefUtil.isLogin(this) && !isServiceRunning(this, HeartMsgService.SERVICE_NAME)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, HeartMsgService::class.java))
            } else {
                startService(Intent(this, HeartMsgService::class.java))
            }
        }

        if (SharePrefUtil.isLogin(this) &&
            SharePrefUtil.isSuperLogin(this, SharePrefUtil.getName(this)) &&
            !isServiceRunning(this, DayQuestionService::class.java.name)) {
//            SystemUtil.checkNotificationPermission(this)
            startService(Intent(this, DayQuestionService::class.java))
        }
    }

    override fun setStatusBar() {
        StatusBarUtil.setTransparent(this)
    }

    override fun registerEventBus() = true

    override fun getContext() = this
}