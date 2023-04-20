package com.scatl.uestcbbs.module.post.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.AttachmentBean;

/**
 * author: sca_tl
 * date: 2020/6/25 16:14
 * description:
 */
public class AttachmentAdapter extends BaseQuickAdapter<AttachmentBean, BaseViewHolder> {
    public AttachmentAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void delete(int position) {
        getData().remove(position);
        notifyItemRemoved(position);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AttachmentBean item) {
        helper.setText(R.id.item_attachment_file_name, item.fileName)
                .addOnClickListener(R.id.item_attachment_delete_file);
    }
}
