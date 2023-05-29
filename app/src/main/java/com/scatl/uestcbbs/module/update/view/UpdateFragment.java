package com.scatl.uestcbbs.module.update.view;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ToastType;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.widget.MyLinearLayoutManger;
import com.scatl.uestcbbs.entity.UpdateBean;
import com.scatl.uestcbbs.module.update.adapter.UpdateImgAdapter;
import com.scatl.uestcbbs.module.update.presenter.UpdatePresenter;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.util.SystemUtil;

import java.io.File;
import java.text.DecimalFormat;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/18 19:33
 */
public class UpdateFragment extends BaseDialogFragment implements UpdateView{

    private TextView title, content, progressText;
    private CheckBox ignoreUpdate;
    private ProgressBar progressBar;
    private Button webDownloadBtn, onlineDownloadBtn;

    private UpdatePresenter updatePresenter;
    private UpdateBean updateBean;

    private RecyclerView recyclerView;
    private UpdateImgAdapter updateImgAdapter;

    private File apkFile;

    public static UpdateFragment getInstance(Bundle bundle) {
        UpdateFragment updateFragment = new UpdateFragment();
        updateFragment.setArguments(bundle);
        return updateFragment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        if (bundle != null) {
            try {
                updateBean = (UpdateBean) bundle.getSerializable(Constant.IntentKey.DATA_1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_update;
    }

    @Override
    protected void findView() {
        title = view.findViewById(R.id.dialog_update_title);
        content = view.findViewById(R.id.dialog_update_content);
        progressBar = view.findViewById(R.id.dialog_update_progressbar);
        onlineDownloadBtn = view.findViewById(R.id.dialog_update_online_download_btn);
        progressText = view.findViewById(R.id.dialog_update_progress_text);
        webDownloadBtn = view.findViewById(R.id.dialog_update_web_download_btn);
        ignoreUpdate = view.findViewById(R.id.dialog_update_ignore_update);
        recyclerView = view.findViewById(R.id.dialog_update_img_rv);
    }

    @Override
    protected void initView() {
        updatePresenter = (UpdatePresenter) presenter;

        if (updateBean.updateInfo.isForceUpdate) { setCancelable(false); }

        onlineDownloadBtn.setOnClickListener(this);
        webDownloadBtn.setOnClickListener(this);
        ignoreUpdate.setOnClickListener(this);

        updateImgAdapter = new UpdateImgAdapter(R.layout.item_update_img);
        MyLinearLayoutManger myLinearLayoutManger = new MyLinearLayoutManger(mActivity);
        myLinearLayoutManger.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(myLinearLayoutManger);
        recyclerView.setAdapter(updateImgAdapter);
        if (updateBean.updateInfo.apkImages != null && updateBean.updateInfo.apkImages.size() > 0)
            updateImgAdapter.setNewData(updateBean.updateInfo.apkImages);

        onlineDownloadBtn.setTag(DownloadStatus.DOWNLOAD_PREPARE);
        title.setText(updateBean.updateInfo.title);
        content.setText(Html.fromHtml(updateBean.updateInfo.updateContent));
        progressBar.setVisibility(View.GONE);
        ignoreUpdate.setVisibility(updateBean.updateInfo.isForceUpdate ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.dialog_update_online_download_btn) {
            if (onlineDownloadBtn.getTag() == DownloadStatus.DOWNLOAD_PREPARE) {
                showToast("下载中，请稍候...", ToastType.TYPE_NORMAL);
                onlineDownloadBtn.setTag(DownloadStatus.DOWNLOADING);
                onlineDownloadBtn.setText("下载中");
                onlineDownloadBtn.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                updatePresenter.downloadApk(mActivity.getExternalFilesDir(Constant.AppPath.TEMP_PATH),
                        updateBean.updateInfo.apkUrl);
            }
            if (onlineDownloadBtn.getTag() == DownloadStatus.DOWNLOADED) {
                if (apkFile != null) SystemUtil.installApk(mActivity, apkFile);
            }
        }

        if (view.getId() == R.id.dialog_update_ignore_update && !updateBean.updateInfo.isForceUpdate && onlineDownloadBtn.getTag() != DownloadStatus.DOWNLOADING) {
            SharePrefUtil.setIgnoreVersionCode(mActivity, updateBean.updateInfo.apkVersionCode);
            dismiss();
        }

        if (view.getId() == R.id.dialog_update_web_download_btn) {
            CommonUtil.openBrowser(mActivity, updateBean.updateInfo.webDownloadUrl);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        updateImgAdapter.setOnItemClickListener((adapter, view1, position) -> {
            ImageUtil.showImages(mActivity, updateImgAdapter.getData(), position);
        });
    }

    @Override
    protected BasePresenter initPresenter() {
        return new UpdatePresenter();
    }


    @Override
    public void onDownloadProgress(int progress, long total) {
        setCancelable(false);
        mActivity.runOnUiThread(() -> {
            progressBar.setProgress(progress);
            DecimalFormat df = new DecimalFormat("#0.00");
            progressText.setText(String.valueOf(progress + "%  " +
                    df.format((double) progress * total/1024/1024/100) + "MB/" +
                    df.format((double) total/1024/1024) + "MB"));
        });
    }

    @Override
    public void onDownloadSuccess(File file) {
        this.apkFile = file;
        if (!updateBean.updateInfo.isForceUpdate) { setCancelable(true); }
        mActivity.runOnUiThread(() -> {
            onlineDownloadBtn.setText("安装");
            onlineDownloadBtn.setClickable(true);
            onlineDownloadBtn.setTag(DownloadStatus.DOWNLOADED);
        });

        SystemUtil.installApk(mActivity, file);
    }

    @Override
    public void onDownloadFail(String msg) {
        mActivity.runOnUiThread(() -> {
            onlineDownloadBtn.setText("在线更新（速度慢）");
            onlineDownloadBtn.setClickable(true);
            onlineDownloadBtn.setTag(DownloadStatus.DOWNLOAD_PREPARE);
            progressBar.setVisibility(View.GONE);
            showToast(msg, ToastType.TYPE_ERROR);
        });
    }

    public enum DownloadStatus {
        DOWNLOAD_PREPARE,
        DOWNLOADING,
        DOWNLOADED
    }

}
