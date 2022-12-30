package com.scatl.uestcbbs.module.board.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.module.board.view.BoardActivity;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.SharePrefUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/21 17:07
 */
public class ForumListGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<ForumListBean.ListBean.BoardListBean> boardListBeans;

    public ForumListGridViewAdapter(Context context, List<ForumListBean.ListBean.BoardListBean> listBeans) {
        this.context = context;
        this.boardListBeans = listBeans;
    }

    @Override
    public int getCount() {
        return boardListBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return boardListBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.item_forum_list_gridview, new RelativeLayout(context));
            holder = new ViewHolder();

            holder.name = view.findViewById(R.id.forum_list_right_name);
            holder.desc = view.findViewById(R.id.forum_list_right_desc);
            holder.imageView = view.findViewById(R.id.forum_list_right_img);
            holder.rootLayout = view.findViewById(R.id.item_forum_list_gridview_rootlayout);

            holder.rootLayout.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, BoardActivity.class);
                intent.putExtra(Constant.IntentKey.BOARD_ID, boardListBeans.get(i).board_id);
                intent.putExtra(Constant.IntentKey.BOARD_NAME, boardListBeans.get(i).board_name);
                context.startActivity(intent);
            });

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        ForumListBean.ListBean.BoardListBean boardListBean = boardListBeans.get(i);
        holder.name.setText(boardListBean.board_name + "(" + boardListBean.td_posts_num + ")");
//        holder.desc.setText(context.getResources().getString(R.string.today_posts, boardListBean.td_posts_num));

//        GlideLoader4Common.simpleLoad(context, boardListBean.board_img, holder.imageView);//加载已有的板块icon
//        GlideLoader4Common.simpleLoad(context, "file:///android_asset/board_img/" + boardListBean.board_id + ".jpg", holder.imageView);
        GlideLoader4Common.simpleLoad(context, SharePrefUtil.getBoardImg(context, boardListBean.board_id), holder.imageView);

        return view;
    }

    public static class ViewHolder {
        View rootLayout;
        TextView name, desc;
        ImageView imageView;
    }
}
