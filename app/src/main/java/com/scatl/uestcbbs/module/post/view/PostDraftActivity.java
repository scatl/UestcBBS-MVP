package com.scatl.uestcbbs.module.post.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.PostDraftBean;
import com.scatl.uestcbbs.module.post.adapter.PostDraftAdapter;
import com.scatl.uestcbbs.module.post.presenter.PostDraftPresenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class PostDraftActivity extends BaseActivity implements PostDraftView{

    private RecyclerView recyclerView;
    private PostDraftAdapter postDraftAdapter;
    private SmartRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private TextView hint;

    private PostDraftPresenter postDraftPresenter;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_post_draft;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.post_draft_toolbar);
        recyclerView = findViewById(R.id.post_draft_rv);
        refreshLayout = findViewById(R.id.post_draft_refresh);
        hint = findViewById(R.id.post_draft_hint);
    }

    @Override
    protected void initView() {
        postDraftPresenter = (PostDraftPresenter) presenter;

        refreshLayout.setEnableLoadMore(false);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postDraftAdapter = new PostDraftAdapter(R.layout.item_post_draft);
        postDraftAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(postDraftAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top));

        refreshLayout.autoRefresh(0, 300, 1, false);

    }

    @Override
    protected BasePresenter initPresenter() {
        return new PostDraftPresenter();
    }

    @Override
    protected void setOnItemClickListener() {
        postDraftAdapter.setOnItemClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.post_draft_root_view) {
                Intent intent = new Intent(PostDraftActivity.this, CreatePostActivity.class);
                intent.putExtra(Constant.IntentKey.DATA, postDraftAdapter.getData().get(position));
                startActivity(intent);
            }
        });

        postDraftAdapter.setOnItemLongClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.post_draft_root_view) {
                postDraftPresenter.deleteDraft(this, position);
            }
            return false;
        });
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                List<PostDraftBean> data = LitePal.findAll(PostDraftBean.class);
                if (data.size() == 0) {
                    hint.setText("啊哦，还没有草稿");
                } else {
                    hint.setText("");
                    postDraftAdapter.setNewData(data);
                }

                refreshLayout.finishRefresh();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onDeleteConfirm(int position) {
        int i = LitePal.delete(PostDraftBean.class, postDraftAdapter.getData().get(position).id);
        if (i != 0) {
            showSnackBar(getWindow().getDecorView(), "删除成功");
            postDraftAdapter.getData().remove(position);
            postDraftAdapter.notifyItemRemoved(position);
            if (postDraftAdapter.getData().size() == 0) hint.setText("啊哦，还没有草稿");
        } else {
            showSnackBar(getWindow().getDecorView(), "删除失败");
        }
    }
}
