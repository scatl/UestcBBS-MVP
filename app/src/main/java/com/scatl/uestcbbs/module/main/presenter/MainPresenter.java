package com.scatl.uestcbbs.module.main.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseAlertDialogBuilder;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.OpenPicBean;
import com.scatl.uestcbbs.entity.SettingsBean;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.glidehelper.GlideLoader4Common;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.main.model.MainModel;
import com.scatl.uestcbbs.module.main.view.MainView;
import com.scatl.uestcbbs.services.DayQuestionService;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.ToastUtil;
import com.scatl.util.common.ServiceUtil;

import io.reactivex.disposables.Disposable;


/**
 * author: sca_tl
 * description:
 * date: 2020/2/14 12:16
 */
public class MainPresenter extends BasePresenter<MainView> {

    private MainModel mainModel = new MainModel();

    public void getUpdate(int oldVersionCode, boolean isTest) {
        mainModel.getUpdate(oldVersionCode, isTest, new Observer<UpdateBean>() {
            @Override
            public void OnSuccess(UpdateBean updateBean) {
                view.getUpdateSuccess(updateBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getUpdateFail(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void getSettings() {
        mainModel.getSettings(new Observer<SettingsBean>() {
            @Override
            public void OnSuccess(SettingsBean settingsBean) {
                view.getSettingsSuccess(settingsBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getSettingsFail(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void getOpenPic() {
        mainModel.getOpenPic(new Observer<OpenPicBean>() {
            @Override
            public void OnSuccess(OpenPicBean openPicBean) {
                view.getOpenPicSuccess(openPicBean);
            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.getOpenPicsFail(e.message);
            }

            @Override
            public void OnCompleted() {

            }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }

    public void showOpenPic(Context context, OpenPicBean openPicBean) {
        if (openPicBean.isValid && SharePrefUtil.getNewShowOpenPicId(context) != openPicBean.id) {

            final View dialog_view = LayoutInflater.from(context).inflate(R.layout.dialog_open_pic, new RelativeLayout(context));
            final LottieAnimationView animationView = dialog_view.findViewById(R.id.dialog_open_pic_animation);
            final ImageView imageView = dialog_view.findViewById(R.id.dialog_open_pic_image);
            final CheckBox neverShow = dialog_view.findViewById(R.id.dialog_pic_open_never_show);

            try {

                neverShow.setTextColor(Color.parseColor(openPicBean.color));
                neverShow.setButtonTintList(ColorStateList.valueOf(Color.parseColor(openPicBean.color)));

                if (openPicBean.isAnimation) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.setAnimationFromUrl(openPicBean.url);
                } else if (openPicBean.isImage) {
                    imageView.setVisibility(View.VISIBLE);
                    GlideLoader4Common.simpleLoad(context, openPicBean.url, imageView);
                }

                final AlertDialog report_dialog = new MaterialAlertDialogBuilder(context, R.style.TransparentDialog)
                        .setView(dialog_view)
                        .create();

                report_dialog.show();

                neverShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        report_dialog.dismiss();
                        SharePrefUtil.setNewShowOpenPicId(context, openPicBean.id);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showDayQuestionTips(Context context) {
        new BaseAlertDialogBuilder(context)
                .setShowOnceId("1")
                .setPositiveButton("立即体验", (dialog, which) -> {
                    ToastUtil.showToast(context, "后台答题中", ToastType.TYPE_NORMAL);
                    SharePrefUtil.setAnswerQuestionBackground(context, true);
                    if (SharePrefUtil.isLogin(context) &&
                            SharePrefUtil.isSuperLogin(context, SharePrefUtil.getName(context)) &&
                            !ServiceUtil.isServiceRunning(context, DayQuestionService.class.getName())) {
                        context.startService(new Intent(context, DayQuestionService.class));
                    }
                })
                .setNegativeButton("下次再说", (dialog, which) -> dialog.dismiss())
                .setTitle("答题功能升级")
                .setCancelable(false)
                .setMessage("答题功能升级啦，现在可以后台自动答题，快来体验吧。\n点击“立即体验”后，后续进入APP会自动后台答题，你可在设置中打开/关闭后台答题功能")
                .show();
    }

}
