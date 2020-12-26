package com.scatl.uestcbbs.module.task.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.TaskType;
import com.scatl.uestcbbs.entity.TaskBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;

/**
 * author: sca_tl
 * date: 2020/12/26 12:57
 * description:
 */
public class TaskAdapter extends BaseQuickAdapter<TaskBean, BaseViewHolder> {
    public TaskAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TaskBean item) {
        helper.setText(R.id.item_task_name, item.name)
                .setText(R.id.item_task_dsp, item.dsp)
                .setText(R.id.item_task_award, "奖励：" + item.award)
                .setText(R.id.item_task_popular_num, item.popularNum + "人参与")
                .setText(R.id.item_task_progress, "进度" + item.progress + "%")
                .addOnClickListener(R.id.item_task_apply_btn)
                .addOnClickListener(R.id.item_task_delete_btn);

        helper.getView(R.id.item_task_progress_layout).setVisibility(item.type.equals(TaskType.TYPE_DOING) ? View.VISIBLE : View.GONE);
        Button button = helper.getView(R.id.item_task_apply_btn);
        button.setText(item.type.equals(TaskType.TYPE_DOING) ? "领取奖励" : "立即申请");
        helper.getView(R.id.item_task_delete_btn).setVisibility(item.type.equals(TaskType.TYPE_DOING) ? View.VISIBLE : View.GONE);

        TextView progress = helper.getView(R.id.item_task_progress);
        progress.setTextColor(item.progress == 100 ? Color.parseColor("#fe5300") : Color.parseColor("#5aaf4a"));

        ImageView progressImg = helper.getView(R.id.item_task_progress_img);
        progressImg.setImageTintList(item.progress == 100 ? ColorStateList.valueOf(Color.parseColor("#fe5300")) : ColorStateList.valueOf(Color.parseColor("#5aaf4a")));

        GlideLoader4Common.simpleLoad(mContext, item.icon, helper.getView(R.id.item_task_icon));
    }
}
