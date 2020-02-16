package com.scatl.uestcbbs.module.board.presenter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.board.model.BoardModel;
import com.scatl.uestcbbs.module.board.view.BoardPostView;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;

public class BoardPostPresenter extends BasePresenter<BoardPostView> {

    private BoardModel boardModel = new BoardModel();

    public void getBoardPostList(int page,
                                       int pageSize,
                                       int topOrder,
                                       int boardId,
                                       int filterId,
                                       String filterType,
                                       String sortby,
                                       Context context) {
        boardModel.getSingleBoardPostList(page, pageSize,
                topOrder, boardId, filterId, filterType, sortby,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context),
                new Observer<SingleBoardBean>() {
                    @Override
                    public void OnSuccess(SingleBoardBean singleBoardBean) {
                        if (singleBoardBean.rs == ApiConstant.Code.SUCCESS_CODE) {
                            view.onGetBoardPostSuccess(singleBoardBean);
                        }
                        if (singleBoardBean.rs == ApiConstant.Code.ERROR_CODE) {
                            view.onGetBoardPostError(singleBoardBean.head.errInfo);
                        }
                    }

                    @Override
                    public void onError(ExceptionHelper.ResponseThrowable e) {
                        view.onGetBoardPostError(e.message);
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

//    public void showFilterDialog(Context context, SingleBoardBean singleBoardBean) {
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_subboard, new LinearLayout(context));
//        TagFlowLayout tagFlowLayout = dialogView.findViewById(R.id.dialog_select_subboard_flow_layout);
//        AlertDialog dialog = new AlertDialog.Builder(context)
//                .setView(dialogView)
//                .setTitle("选择分类")
//                .create();
//        dialog.show();
//
//        SingleBoardBean.ClassificationTypeListBean sc = new SingleBoardBean.ClassificationTypeListBean();
//        sc.classificationType_id = 0;
//        sc.classificationType_name = "全部";
//        singleBoardBean.classificationType_list.add(sc);
//
//        tagFlowLayout.setAdapter(new TagAdapter<SingleBoardBean.ClassificationTypeListBean>(singleBoardBean.classificationType_list) {
//            @Override
//            public View getView(FlowLayout parent, int position, SingleBoardBean.ClassificationTypeListBean o) {
//                TextView textView = new TextView(context);
//                textView.setClickable(true);
//                textView.setFocusable(true);
//                textView.setTextSize(14);
//                textView.setText(o.classificationType_name);
//                textView.setTextColor(context.getColor(R.color.colorPrimary));
//                textView.setBackgroundResource(R.drawable.shape_select_subboard_tag);
//                return textView;
//            }
//
//            @Override
//            public void onSelected(int position, View view) {
//                super.onSelected(position, view);
//                ((TextView)view).setTextColor(Color.WHITE);
//            }
//
//            @Override
//            public void unSelected(int position, View view) {
//                super.unSelected(position, view);
//                ((TextView)view).setTextColor(context.getColor(R.color.colorPrimary));
//            }
//        });
//        tagFlowLayout.setOnTagClickListener((v, position, parent) -> {
//            view.onChangeFilter(singleBoardBean.classificationType_list.get(position).classificationType_id,
//                    singleBoardBean.classificationType_list.get(position).classificationType_name);
////            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.FILTER_ID_CHANGE, singleBoardBean.classificationType_list.get(position).classificationType_id));
//            dialog.dismiss();
//            return true;
//        });
//
//    }


}
