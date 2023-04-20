package com.scatl.uestcbbs.module.board.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.util.ColorUtil;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 16:16
 */
public class ForumListLeftAdapter extends BaseQuickAdapter<ForumListBean.ListBean, BaseViewHolder> {

    private int selected = 0;

    public ForumListLeftAdapter(int layoutResId) {
        super(layoutResId);
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, ForumListBean.ListBean item) {
        TextView name = helper.getView(R.id.forum_list_left_text);
        if (helper.getLayoutPosition() == selected) {
            name.setTextSize(18f);
            name.setTextColor(ColorUtil.getAttrColor(mContext, R.attr.colorPrimary));
        } else {
            name.setTextSize(15f);
            name.setTextColor(ColorUtil.getAttrColor(mContext, R.attr.colorOnSurfaceVariant));
        }
        helper.setText(R.id.forum_list_left_text, item.board_category_name);
    }
}
