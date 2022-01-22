package com.scatl.uestcbbs.module.post.presenter;

import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.module.post.view.PostDraftView;

import org.litepal.LitePal;

/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 20:38
 */
public class PostDraftPresenter extends BasePresenter<PostDraftView> {

    public void deleteDraft(Context context, int position) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setNegativeButton("确认", null)
                .setPositiveButton("取消", null )
                .setTitle("删除草稿")
                .setMessage("确认删除该草稿嘛？删除后不可恢复")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            n.setOnClickListener(v -> {
                dialog.dismiss();
                view.onDeleteConfirm(position);
            });
        });
        dialog.show();
    }

    public void showClearAllWaringDialog(Context context) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setPositiveButton("取消", null)
                .setNegativeButton("确认", null)
                .setTitle("删除全部草稿")
                .setMessage("确认要删除全部草稿吗？该操作不可恢复")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            n.setOnClickListener(v -> {
                int i = LitePal.deleteAll("postdraftbean");
                if (i != 0) {
                    view.onDeleteAllSuccess("删除成功");
                } else {
                    view.onDeleteAllError("删除失败");
                }
                dialog.dismiss();
            });
        });
        dialog.show();
    }

}
