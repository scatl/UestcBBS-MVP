package com.scatl.uestcbbs.history.presenter;

import android.content.Context;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;

import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.history.view.HistoryView;

import org.litepal.LitePal;

/**
 * author: sca_tl
 * description:
 * date: 2020/4/7 9:47
 */
public class HistoryPresenter extends BasePresenter<HistoryView> {

    public void showClearAllWaringDialog(Context context) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton("取消", null)
                .setNegativeButton("确认", null)
                .setTitle("删除全部浏览记录")
                .setMessage("确认要删除全部浏览记录吗？该操作不可恢复")
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            n.setOnClickListener(v -> {
                int i = LitePal.deleteAll("historybean");
                if (i != 0) {
                    view.onClearAllSuccess();
                } else {
                    view.onClearAllFail();
                }
                dialog.dismiss();
            });
        });
        dialog.show();
    }

}
