package com.scatl.uestcbbs.module.home.adapter.latestpost

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager.ImageTextLoaderInterface
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.databinding.HomeItemGonggeViewBinding
import com.scatl.uestcbbs.helper.BaseOneItemAdapter
import com.scatl.uestcbbs.helper.ViewBindingHolder
import com.scatl.uestcbbs.module.board.view.BoardActivity
import com.scatl.uestcbbs.module.credit.view.CreditHistoryActivity
import com.scatl.uestcbbs.module.credit.view.CreditTransferFragment
import com.scatl.uestcbbs.module.darkroom.view.DarkRoomActivity
import com.scatl.uestcbbs.module.dayquestion.view.DayQuestionActivity
import com.scatl.uestcbbs.module.home.view.OnLineUserFragment
import com.scatl.uestcbbs.module.houqin.view.HouQinReportListActivity
import com.scatl.uestcbbs.module.magic.view.MagicShopActivity
import com.scatl.uestcbbs.module.medal.view.MedalCenterActivity
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.webview.view.WebViewActivity
import com.scatl.uestcbbs.services.DayQuestionService
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.module.credit.view.WaterTaskFragment
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ServiceUtil

/**
 * Created by sca_tl at 2023/6/12 16:11
 */
class FunctionAdapter(val childFragmentManager: FragmentManager): BaseOneItemAdapter<Any, HomeItemGonggeViewBinding>() {

    override fun getViewBinding(parent: ViewGroup): HomeItemGonggeViewBinding {
        return HomeItemGonggeViewBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<HomeItemGonggeViewBinding>, item: Any?) {
        super.onBindViewHolder(holder, item)

        val titles = arrayOf(
            "每日答题", "失物招领", "交通指南", "部门直通车",
            "水滴小任务", "勋章中心", "道具商店", "后勤投诉",
            "在线用户", "水滴转账", "积分记录", "新手导航",
            "小黑屋"
        )
        val iconS = intArrayOf(
            R.drawable.ic_hot, R.drawable.ic_lost_and_found, R.drawable.ic_timetable, R.drawable.ic_department,
            R.drawable.ic_task, R.drawable.ic_xunzhang, R.drawable.ic_magic, R.drawable.ic_report1,
            R.drawable.ic_huiyuan, R.drawable.ic_transfer, R.drawable.ic_integral, R.drawable.ic_daohang,
            R.drawable.ic_black_list1
        )
        val colorStateLists = arrayOf(
            ColorStateList.valueOf(Color.parseColor("#ff9090")),
            ColorStateList.valueOf(Color.parseColor("#90caf9")),
            ColorStateList.valueOf(Color.parseColor("#80deea")),
            ColorStateList.valueOf(Color.parseColor("#E3B0E2")),
            ColorStateList.valueOf(Color.parseColor("#59B2D1")),
            ColorStateList.valueOf(Color.parseColor("#C9A6D1")),
            ColorStateList.valueOf(Color.parseColor("#FF9C87")),
            ColorStateList.valueOf(Color.parseColor("#FF7D7F")),
            ColorStateList.valueOf(Color.parseColor("#B8A6FF")),
            ColorStateList.valueOf(Color.parseColor("#0BBCB3")),
            ColorStateList.valueOf(Color.parseColor("#4BB3FF")),
            ColorStateList.valueOf(Color.parseColor("#BA76C6")),
            ColorStateList.valueOf(Color.parseColor("#CC884C"))
        )

        holder.binding.homeGonggeGridviewpager
            .setDataAllCount(titles.size)
            .setImageTextLoaderInterface { imageView, textView, position ->
                imageView.setImageResource(iconS[position])
                imageView.imageTintList = colorStateLists[position]
                textView.text = titles[position]
                textView.textSize = 12f
                textView.setTextColor(colorStateLists[position])
            }
            .setGridItemClickListener { position: Int ->
                when (position) {
                    0 -> {
                        if (!ServiceUtil.isServiceRunning(context, DayQuestionService::class.java.name)) {
                            context.startActivity(Intent(context, DayQuestionActivity::class.java))
                        } else {
                            context.showToast("后台答题中", ToastType.TYPE_ERROR)
                        }
                    }

                    1 -> {
                        val intent = Intent(context, BoardActivity::class.java).apply {
                            putExtra(Constant.IntentKey.BOARD_ID, Constant.LOST_FOUND_BOARD_ID)
                            putExtra(Constant.IntentKey.BOARD_NAME, Constant.LOST_FOUND_BOARD_NAME)
                        }
                        context.startActivity(intent)
                    }

                    2 -> {
                        val intent = Intent(context, WebViewActivity::class.java).apply {
                            putExtra(Constant.IntentKey.URL, Constant.BUS_TIME)
                        }
                        context.startActivity(intent)
                    }

                    3 -> {
                        val intent = Intent(context, BoardActivity::class.java).apply {
                            putExtra(Constant.IntentKey.BOARD_ID, Constant.DEPARTMENT_BOARD_ID)
                            putExtra(Constant.IntentKey.BOARD_NAME, Constant.DEPARTMENT_BOARD_NAME)
                        }
                        context.startActivity(intent)
                    }

                    4 -> {
                        WaterTaskFragment.getInstance(null).show(childFragmentManager, TimeUtil.getStringMs())
                    }

                    5 -> {
                        context.startActivity(Intent(context, MedalCenterActivity::class.java))
                    }

                    6 -> {
                        context.startActivity(Intent(context, MagicShopActivity::class.java))
                    }

                    7 -> {
                        context.startActivity(Intent(context, HouQinReportListActivity::class.java))
                    }

                    8 -> {
                        OnLineUserFragment.getInstance(null).show(childFragmentManager, TimeUtil.getStringMs())
                    }

                    9 -> {
                        CreditTransferFragment.getInstance(null).show(childFragmentManager, TimeUtil.getStringMs())
                    }

                    10 -> {
                        context.startActivity(Intent(context, CreditHistoryActivity::class.java))
                    }

                    11 -> {
                        val intent = Intent(context, NewPostDetailActivity::class.java).apply {
                            putExtra(Constant.IntentKey.TOPIC_ID, 1821753)
                        }
                        context.startActivity(intent)
                    }

                    12 -> {
                        context.startActivity(Intent(context, DarkRoomActivity::class.java))
                    }
                }
            }
            .show()
    }
}