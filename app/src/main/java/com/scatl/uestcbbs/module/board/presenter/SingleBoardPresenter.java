package com.scatl.uestcbbs.module.board.presenter;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.SingleBoardBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.board.model.BoardModel;
import com.scatl.uestcbbs.module.board.view.SingleBoardView;
import com.scatl.uestcbbs.util.SharePrefUtil;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/30 14:59
 */
public class SingleBoardPresenter extends BasePresenter<SingleBoardView> {

    private BoardModel boardModel = new BoardModel();

    public void getSingleBoardPostList(int page,
                                       int pageSize,
                                       int topOrder,
                                       int boardId,
                                       int filterId,
                                       String filterType,
                                       String sortby,
                                       Context context) {
        boardModel.getSingleBoardPostList(page, pageSize,
                topOrder, boardId, filterId, filterType, sortby,
                new Observer<SingleBoardBean>() {
                    @Override
                    public void OnSuccess(SingleBoardBean singleBoardBean) {
                        if (singleBoardBean.rs == ApiConstant.Code.SUCCESS_CODE) {
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
                        disposable.add(d);
//                        SubscriptionManager.getInstance().add(d);
                    }
                });
    }

    /**
     * author: sca_tl
     * description: 显示选择分类的dialog
     */
    public void showClassificationDialog(Context context, List<SingleBoardBean.ClassificationTypeListBean> data,
                                         int filterId) {

        String[] items = new String[data.size() + 1];
        items[0] = "全部";

        int selected = 0;

        for (int i = 0; i < data.size(); i ++) {
            items[i + 1] = data.get(i).classificationType_name;
            if (data.get(i).classificationType_id == filterId) { selected = i + 1; }
        }

        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("选择分类")
                .setSingleChoiceItems(items, selected, (dialog1, which) -> {
                    view.onClassificationSelected(which == 0 ? 0 : data.get(which - 1).classificationType_id);
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

}
