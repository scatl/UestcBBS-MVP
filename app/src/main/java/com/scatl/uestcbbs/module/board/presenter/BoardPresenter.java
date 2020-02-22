package com.scatl.uestcbbs.module.board.presenter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.callback.OnPermission;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.entity.SubForumListBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.board.model.BoardModel;
import com.scatl.uestcbbs.module.board.view.BoardView;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import io.reactivex.disposables.Disposable;

public class BoardPresenter extends BasePresenter<BoardView> {

    private BoardModel boardModel = new BoardModel();

    public void getSubBoardList(int fid, Context context) {
        boardModel.getSubForumList(fid,
                SharePrefUtil.getToken(context),
                SharePrefUtil.getSecret(context), new Observer<SubForumListBean>() {
                    @Override
                    public void OnSuccess(SubForumListBean subForumListBean) {
                        if (subForumListBean.rs == ApiConstant.Code.SUCCESS_CODE) {
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

    public void showSubBoardDialog(Context context, SubForumListBean subForumListBean) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_subboard, new LinearLayout(context));
        TagFlowLayout tagFlowLayout = dialogView.findViewById(R.id.dialog_select_subboard_flow_layout);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("选择板块")
                .create();
        dialog.show();

        tagFlowLayout.setAdapter(new TagAdapter<SubForumListBean.ListBean.BoardListBean>(subForumListBean.list.get(0).board_list) {
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
        tagFlowLayout.setOnTagClickListener((v, position, parent) -> {
            view.onSubBoardSelect(position);
//            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BOARD_ID_CHANGE, position));
            dialog.dismiss();
            return true;
        });

    }

    public void showFilterDialog(Context context, SingleBoardBean singleBoardBean) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_subboard, new LinearLayout(context));
        TagFlowLayout tagFlowLayout = dialogView.findViewById(R.id.dialog_select_subboard_flow_layout);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("选择分类")
                .create();
        dialog.show();

        if (singleBoardBean.classificationType_list.get(0).classificationType_id != 0){
            SingleBoardBean.ClassificationTypeListBean sc = new SingleBoardBean.ClassificationTypeListBean();
            sc.classificationType_id = 0;
            sc.classificationType_name = "全部";
            singleBoardBean.classificationType_list.add(0, sc);
        }

        tagFlowLayout.setAdapter(new TagAdapter<SingleBoardBean.ClassificationTypeListBean>(singleBoardBean.classificationType_list) {
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
        tagFlowLayout.setOnTagClickListener((v, position, parent) -> {
            view.onFilterSelect(singleBoardBean.classificationType_list.get(position).classificationType_id,
                    singleBoardBean.classificationType_list.get(position).classificationType_name,
                    position);
            dialog.dismiss();
            return true;
        });

    }

    /**
     * author: sca_tl
     * description: 请求权限
     */
    public void requestPermission(FragmentActivity activity, final int action, String... permissions) {
        CommonUtil.requestPermission(activity, new OnPermission() {
            @Override
            public void onGranted() {
                view.onPermissionGranted(action);
            }

            @Override
            public void onRefusedWithNoMoreRequest() {
                view.onPermissionRefusedWithNoMoreRequest();
            }

            @Override
            public void onRefused() {
                view.onPermissionRefused();
            }
        }, permissions);
    }

}
