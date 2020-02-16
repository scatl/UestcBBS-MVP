package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.ForumListBean;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.post.model.PostModel;
import com.scatl.uestcbbs.module.post.view.SelectBoardView;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/9 16:13
 */
public class SelectBoardPresenter extends BasePresenter<SelectBoardView> {

    private PostModel postModel = new PostModel();

    public void getForumList(Context context) {
        postModel.getForumList(
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<ForumListBean>() {
                    @Override
                    public void OnSuccess(ForumListBean forumListBean) {
                        if (forumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetBoardListSuccess(forumListBean);
                        }
                        if (forumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetBoardListError(forumListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetBoardListError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }

    public void getSubBoardList(int fid, String fatherBoardName, Context context) {
        postModel.getSubForumList(fid,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<SubForumListBean>() {
                    @Override
                    public void OnSuccess(SubForumListBean subForumListBean) {
                        if (subForumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            SubForumListBean.ListBean.BoardListBean slb = new SubForumListBean.ListBean.BoardListBean();
                            slb.board_name = fatherBoardName;
                            slb.board_id = fid;
                            if (subForumListBean.list == null || subForumListBean.list.size() == 0) {
                                List<SubForumListBean.ListBean> list = new ArrayList<>();
                                SubForumListBean.ListBean sl = new SubForumListBean.ListBean();
                                sl.board_list = new ArrayList<>();
                                sl.board_list.add(0, slb);
                                list.add(sl);
                                subForumListBean.list = list;
                            } else {
                                subForumListBean.list.get(0).board_list.add(0, slb);
                            }
                            view.onGetSubBoardListSuccess(subForumListBean);
                        }
                        if (subForumListBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetSubBoardListError(subForumListBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetSubBoardListError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }

    public void getSingleBoardPostList(int boardId,
                                       Context context) {
        postModel.getSingleBoardPostList(1, 0,
                1, boardId, 0, "typeid", "new",
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SingleBoardBean>() {
                    @Override
                    public void OnSuccess(SingleBoardBean singleBoardBean) {
                        if (singleBoardBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            SingleBoardBean.ClassificationTypeListBean sc = new SingleBoardBean.ClassificationTypeListBean();
                            sc.classificationType_name = "不分类";
                            sc.classificationType_id = 0;
                            singleBoardBean.classificationType_list.add(0, sc);
                            view.onGetSingleBoardDataSuccess(singleBoardBean);
                        }
                        if (singleBoardBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetSingleBoardDataError(singleBoardBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetSingleBoardDataError(e.message);
                    }

                    @Override
                    public void OnCompleted() {

                    }

                    @Override
                    public void OnDisposable(Disposable d) {
                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }


    public void initTagLayout1(Context context, ForumListBean forumListBean, TagFlowLayout tagFlowLayout1) {
        tagFlowLayout1.setAdapter(new TagAdapter<ForumListBean.ListBean>(forumListBean.list) {
            @Override
            public View getView(FlowLayout parent, int position, ForumListBean.ListBean o) {
                TextView textView = new TextView(context);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(o.board_category_name);
                textView.setTextColor(context.getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_select_subboard_tag);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(context.getColor(R.color.colorPrimary));
            }
        });
        tagFlowLayout1.setOnTagClickListener((v, position, parent) -> {
            view.onTagLayout1Select(position);
            return true;
        });
    }


    public void initTagLayout2(Context context, List<ForumListBean.ListBean.BoardListBean> board_list, TagFlowLayout tagFlowLayout2) {
        tagFlowLayout2.setAdapter(new TagAdapter<ForumListBean.ListBean.BoardListBean>(board_list) {
            @Override
            public View getView(FlowLayout parent, int position, ForumListBean.ListBean.BoardListBean o) {
                TextView textView = new TextView(context);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(o.board_name);
                textView.setTextColor(context.getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_select_subboard_tag);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(context.getColor(R.color.colorPrimary));
            }
        });
        tagFlowLayout2.setOnTagClickListener((v, position, parent) -> {
            view.onTagLayout2Select(board_list.get(position).board_id, board_list.get(position).board_name);
            return true;
        });

    }

    public void initTagLayout3(Context context, SubForumListBean subForumListBean, TagFlowLayout tagFlowLayout3) {
        tagFlowLayout3.setAdapter(new TagAdapter<SubForumListBean.ListBean.BoardListBean>(subForumListBean.list.get(0).board_list) {
            @Override
            public View getView(FlowLayout parent, int position, SubForumListBean.ListBean.BoardListBean o) {
                TextView textView = new TextView(context);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(o.board_name);
                textView.setTextColor(context.getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_select_subboard_tag);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(context.getColor(R.color.colorPrimary));
            }
        });
        tagFlowLayout3.setOnTagClickListener((v, position, parent) -> {
            view.onTagLayout3Select(subForumListBean.list.get(0).board_list.get(position).board_id,
                    subForumListBean.list.get(0).board_list.get(position).board_name);
            return true;
        });

    }

    public void initTagLayout4(Context context, SingleBoardBean singleBoardBean, TagFlowLayout tagFlowLayout4) {
        tagFlowLayout4.setAdapter(new TagAdapter<SingleBoardBean.ClassificationTypeListBean>(singleBoardBean.classificationType_list) {
            @Override
            public View getView(FlowLayout parent, int position, SingleBoardBean.ClassificationTypeListBean o) {
                TextView textView = new TextView(context);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(o.classificationType_name);
                textView.setTextColor(context.getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_select_subboard_tag);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(context.getColor(R.color.colorPrimary));
            }
        });
        tagFlowLayout4.setOnTagClickListener((v, position, parent) -> {
            view.onTagLayout4Select(singleBoardBean.classificationType_list.get(position).classificationType_id,
                    singleBoardBean.classificationType_list.get(position).classificationType_name);
            return true;
        });

    }

}
