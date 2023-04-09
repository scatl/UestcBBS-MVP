package com.scatl.uestcbbs.module.credit.adapter

import android.animation.ValueAnimator
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.TaskType
import com.scatl.uestcbbs.entity.TaskBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/7 13:35
 */
class WaterTaskAdapter(layoutResId: Int, onPreload: (() -> Unit)? = null):
    PreloadAdapter<TaskBean, BaseViewHolder>(layoutResId, onPreload) {

    override fun convert(helper: BaseViewHolder, item: TaskBean) {
        super.convert(helper, item)
        helper
            .addOnClickListener(R.id.apply_award)
            .addOnClickListener(R.id.delete)
            .addOnClickListener(R.id.time_left)
            .addOnClickListener(R.id.apply_task)

        val name = helper.getView<TextView>(R.id.name)
        val dsp = helper.getView<TextView>(R.id.dsp)
        val icon = helper.getView<ImageView>(R.id.icon)
        val progressText = helper.getView<TextView>(R.id.progress_text)
        val progressView = helper.getView<CircularProgressIndicator>(R.id.progress_view)
        val joinNum = helper.getView<TextView>(R.id.join_num)
        val applyAward = helper.getView<MaterialButton>(R.id.apply_award)
        val delete = helper.getView<MaterialButton>(R.id.delete)
        val timeLeft = helper.getView<TextView>(R.id.time_left)
        val awardDetail = helper.getView<TextView>(R.id.award_detail)
        val applyTask = helper.getView<MaterialButton>(R.id.apply_task)
        val timeDone = helper.getView<TextView>(R.id.time_done)
        val iconType = helper.getView<ImageView>(R.id.icon_type)
        val timeFailed = helper.getView<TextView>(R.id.time_failed)

        name.text = item.name
        dsp.text = item.dsp
        joinNum.text = item.popularNum.toString().plus("人参与")
        icon.load(item.icon)
        awardDetail.text = "完成任务可获取".plus(item.award.replace(" ", ""))

        if (item.type == TaskType.TYPE_DOING) {
            timeFailed.visibility = View.GONE
            applyTask.visibility = View.GONE
            timeDone.visibility = View.GONE
            iconType.visibility = View.GONE
            delete.visibility = View.VISIBLE
            progressView.visibility = View.VISIBLE
            progressText.visibility = View.VISIBLE
            progressText.text = item.progress.toString().plus("%")
            timeLeft.apply {
                visibility = if (item.progress >= 100 || item.id == 3) View.GONE else View.VISIBLE
                text = "剩余时间：点击查看"
            }
            applyAward.visibility = if (item.progress >= 100) View.VISIBLE else View.GONE
            awardDetail.text = "加油，即将获得".plus(item.award.replace(" ", ""))
            Handler().postDelayed({
                ValueAnimator
                    .ofInt(0, item.progress.toInt() * 100)
                    .setDuration(400)
                    .apply {
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener { progressView.progress = it.animatedValue as Int }
                        start()
                    }
            }, 200)
        } else if (item.type == TaskType.TYPE_NEW) {
            timeFailed.visibility = View.GONE
            timeDone.visibility = View.GONE
            progressView.visibility = View.GONE
            progressText.visibility = View.GONE
            timeLeft.visibility = View.GONE
            applyAward.visibility = View.GONE
            delete.visibility = View.GONE
            applyTask.apply {
                visibility = View.VISIBLE
                text = "申请任务"
            }
            iconType.apply {
                visibility = View.VISIBLE
                setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.ic_new1))
            }
        } else if (item.type == TaskType.TYPE_DONE) {
            timeFailed.visibility = View.GONE
            progressView.visibility = View.GONE
            progressText.visibility = View.GONE
            timeLeft.visibility = View.GONE
            applyAward.visibility = View.GONE
            delete.visibility = View.GONE
            applyTask.visibility = View.GONE
            timeDone.visibility = View.VISIBLE
            iconType.apply {
                visibility = View.VISIBLE
                setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.ic_check))
            }
            timeDone.text = item.doneTime
            awardDetail.text = "您已获得".plus(item.award.replace(" ", ""))
        } else if (item.type == TaskType.TYPE_FAILED){
            timeFailed.visibility = View.VISIBLE
            progressView.visibility = View.GONE
            progressText.visibility = View.GONE
            timeLeft.visibility = View.GONE
            timeDone.visibility = View.GONE
            applyAward.visibility = View.GONE
            delete.visibility = View.GONE
            applyTask.apply {
                visibility = View.VISIBLE
                text = "重新申请"
            }
            iconType.apply {
                visibility = View.VISIBLE
                setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.ic_failed))
            }
            timeFailed.text = item.failedTime.replace("现在可以再次申请","")
            awardDetail.text = "您错过了".plus(item.award.replace(" ", ""))
        }

    }
}