package com.scatl.uestcbbs.module.credit.adapter

import android.animation.ValueAnimator
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.content.res.AppCompatResources
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.TaskType
import com.scatl.uestcbbs.databinding.ItemWaterTaskDoingBinding
import com.scatl.uestcbbs.entity.TaskBean
import com.scatl.uestcbbs.helper.PreloadAdapter
import com.scatl.uestcbbs.util.load

/**
 * Created by sca_tl at 2023/4/7 13:35
 */
class WaterTaskAdapter: PreloadAdapter<TaskBean, ItemWaterTaskDoingBinding>() {

    override fun getViewBinding(parent: ViewGroup): ItemWaterTaskDoingBinding {
        return ItemWaterTaskDoingBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: TaskBean?) {
        super.onBindViewHolder(holder, position, item)
        if (item == null) {
            return
        }
        val binding = holder.binding as ItemWaterTaskDoingBinding

        binding.name.text = item.name
        binding.dsp.text = item.dsp
        binding.joinNum.text = item.popularNum.toString().plus("人参与")
        binding.icon.load(item.icon)
        binding.awardDetail.text = "完成任务可获取".plus(item.award.replace(" ", ""))

        when (item.type) {
            TaskType.TYPE_DOING -> {
                binding.timeFailed.visibility = View.GONE
                binding.applyTask.visibility = View.GONE
                binding.timeDone.visibility = View.GONE
                binding.iconType.visibility = View.GONE
                binding.delete.visibility = View.VISIBLE
                binding.progressView.visibility = View.VISIBLE
                binding.progressText.visibility = View.VISIBLE
                binding.progressText.text = item.progress.toString().plus("%")
                binding.timeLeft.apply {
                    visibility = if (item.progress >= 100 || item.id == 3) View.GONE else View.VISIBLE
                    text = "剩余时间：点击查看"
                }
                binding.applyAward.visibility = if (item.progress >= 100) View.VISIBLE else View.GONE
                binding.awardDetail.text = "加油，即将获得".plus(item.award.replace(" ", ""))
                Handler().postDelayed({
                    ValueAnimator
                        .ofInt(0, item.progress.toInt() * 100)
                        .setDuration(400)
                        .apply {
                            interpolator = AccelerateDecelerateInterpolator()
                            addUpdateListener { binding.progressView.progress = it.animatedValue as Int }
                            start()
                        }
                }, 200)
            }
            TaskType.TYPE_NEW -> {
                binding.timeFailed.visibility = View.GONE
                binding.timeDone.visibility = View.GONE
                binding.progressView.visibility = View.GONE
                binding.progressText.visibility = View.GONE
                binding.timeLeft.visibility = View.GONE
                binding.applyAward.visibility = View.GONE
                binding.delete.visibility = View.GONE
                binding.applyTask.apply {
                    visibility = View.VISIBLE
                    text = "申请任务"
                }
                binding.iconType.apply {
                    visibility = View.VISIBLE
                    setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_new1))
                }
            }
            TaskType.TYPE_DONE -> {
                binding.timeFailed.visibility = View.GONE
                binding.progressView.visibility = View.GONE
                binding.progressText.visibility = View.GONE
                binding.timeLeft.visibility = View.GONE
                binding.applyAward.visibility = View.GONE
                binding.delete.visibility = View.GONE
                binding.applyTask.visibility = View.GONE
                binding.timeDone.visibility = View.VISIBLE
                binding.iconType.apply {
                    visibility = View.VISIBLE
                    setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_check))
                }
                binding.timeDone.text = item.doneTime
                binding.awardDetail.text = "您已获得".plus(item.award.replace(" ", ""))
            }
            TaskType.TYPE_FAILED -> {
                binding.timeFailed.visibility = View.VISIBLE
                binding.progressView.visibility = View.GONE
                binding.progressText.visibility = View.GONE
                binding.timeLeft.visibility = View.GONE
                binding.timeDone.visibility = View.GONE
                binding.applyAward.visibility = View.GONE
                binding.delete.visibility = View.GONE
                binding.applyTask.apply {
                    visibility = View.VISIBLE
                    text = "重新申请"
                }
                binding.iconType.apply {
                    visibility = View.VISIBLE
                    setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_failed))
                }
                binding.timeFailed.text = item.failedTime.replace("现在可以再次申请","")
                binding.awardDetail.text = "您错过了".plus(item.award.replace(" ", ""))
            }
        }
    }
}