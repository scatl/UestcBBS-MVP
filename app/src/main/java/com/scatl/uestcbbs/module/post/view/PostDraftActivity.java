package com.scatl.uestcbbs.module.post.view;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Rect;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.callback.OnRefresh;
import com.scatl.uestcbbs.entity.PostDraftBean;
import com.scatl.uestcbbs.module.post.adapter.PostDraftAdapter;
import com.scatl.uestcbbs.module.post.presenter.PostDraftPresenter;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.RefreshUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class PostDraftActivity extends BaseActivity<PostDraftPresenter> implements PostDraftView{

    private RecyclerView recyclerView;
    private PostDraftAdapter postDraftAdapter;
    private SmartRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private TextView hint;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_post_draft;
    }

    @Override
    protected void findView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.post_draft_rv);
        refreshLayout = findViewById(R.id.post_draft_refresh);
        hint = findViewById(R.id.post_draft_hint);
    }

    @Override
    protected void initView() {
        super.initView();
        refreshLayout.setEnableLoadMore(false);

        postDraftAdapter = new PostDraftAdapter(R.layout.item_post_draft);
        postDraftAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(postDraftAdapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_scale_in));

        refreshLayout.autoRefresh(10, 300, 1, false);
    }

    @Override
    protected PostDraftPresenter initPresenter() {
        return new PostDraftPresenter();
    }

    @Override
    protected void onOptionsSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            presenter.showClearAllWaringDialog(this);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        postDraftAdapter.setOnItemClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.post_draft_root_view) {
                Intent intent = new Intent(PostDraftActivity.this, CreatePostActivity.class);
                intent.putExtra(Constant.IntentKey.DATA_2, postDraftAdapter.getData().get(position));
                intent.putExtra(Constant.IntentKey.DATA_1, createRect(view));
                startActivity(intent);
            }
        });

        postDraftAdapter.setOnItemLongClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.post_draft_root_view) {
                presenter.deleteDraft(this, position);
            }
            return false;
        });
    }

    private Rect createRect(View view) {
        Rect rect = new Rect();
        view.getDrawingRect(rect);
        ((ViewGroup) view.getParent()).offsetDescendantRectToMyCoords(view, rect);
        return rect;
    }

    @Override
    protected void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                setData();
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
            showToast("删除成功", ToastType.TYPE_SUCCESS);
            postDraftAdapter.getData().remove(position);
            postDraftAdapter.notifyItemRemoved(position);
            if (postDraftAdapter.getData().size() == 0) hint.setText("啊哦，还没有草稿");
        } else {
            showToast("删除失败", ToastType.TYPE_ERROR);
        }
    }

    @Override
    public void onDeleteAllSuccess(String msg) {
        setData();
        showToast(msg, ToastType.TYPE_SUCCESS);
    }

    @Override
    public void onDeleteAllError(String msg) {
        showToast(msg, ToastType.TYPE_ERROR);
    }

    private void setData() {
        List<PostDraftBean> data = LitePal.findAll(PostDraftBean.class);
        if (data.size() == 0) {
            hint.setText("啊哦，还没有草稿");
            postDraftAdapter.setNewData(new ArrayList<>());
        } else {
            hint.setText("");
            postDraftAdapter.setNewData(data);
        }
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    protected void receiveEventBusMsg(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.EXIT_CREATE_POST) {
            setData();
        }
    }
}
