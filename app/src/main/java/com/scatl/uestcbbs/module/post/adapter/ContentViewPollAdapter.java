package com.scatl.uestcbbs.module.post.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.PostDetailBean;

import java.util.ArrayList;
import java.util.List;

public class ContentViewPollAdapter extends BaseQuickAdapter<PostDetailBean.TopicBean.PollInfoBean.PollItemListBean, BaseViewHolder> {
    private int total;
    private int poll_status;
    private List<Integer> ids = new ArrayList<>();

    public ContentViewPollAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void addPollData(List<PostDetailBean.TopicBean.PollInfoBean.PollItemListBean> data, int total, int poll_status) {
        setNewData(data);
        this.total = total;
        this.poll_status = poll_status;
    }

    public List<Integer> getPollItemIds() {
        return ids;
    }

    @Override
    protected void convert(BaseViewHolder helper, final PostDetailBean.TopicBean.PollInfoBean.PollItemListBean item) {
        CheckBox checkBox = helper.getView(R.id.item_poll_checkbox);
        ProgressBar progressBar = helper.getView(R.id.item_poll_progress);

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (! ids.contains(item.poll_item_id)) {
                    ids.add(item.poll_item_id);
                }
            } else {
                if (ids.contains(item.poll_item_id)) {
                    ids.remove(Integer.valueOf(item.poll_item_id));
                }
            }
        });


        checkBox.setEnabled(poll_status == 2);
        checkBox.setText(mContext.getString(R.string.vote_item_voted_num,
                item.name, item.total_num, item.percent));

        progressBar.setMax(total * 100);
        progressBar.postDelayed(() -> {
            ValueAnimator animator = ValueAnimator.ofInt(0, item.total_num * 100).setDuration(500);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(animation -> progressBar.setProgress((int)animation.getAnimatedValue()));
            animator.start();
        }, 500);

    }
}
