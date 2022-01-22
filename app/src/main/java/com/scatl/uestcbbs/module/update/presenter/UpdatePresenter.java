package com.scatl.uestcbbs.module.update.presenter;


import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.module.update.model.UpdateModel;
import com.scatl.uestcbbs.module.update.view.UpdateView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/18 19:58
 */
public class UpdatePresenter extends BasePresenter<UpdateView> {

    private UpdateModel updateModel;

    public UpdatePresenter() {
        this.updateModel = new UpdateModel();
    }

    public void downloadApk(File targetDir, String url) {
        updateModel.downloadApk(url, new Observer<ResponseBody>() {
            @Override
            public void OnSuccess(ResponseBody responseBody) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    //文件大小
                    long contentLength = responseBody.contentLength();
                    //读取文件
                    InputStream inputStream = responseBody.byteStream();

                    //创建一个文件夹
                    String fileName = url.substring(url.lastIndexOf("/"));
                    File file = new File(targetDir, fileName);
                    FileOutputStream outputStream = new FileOutputStream(file);

                    byte[] bytes = new byte[1024];
                    int len;
                    //循环读取文件的内容，把他放到新的文件目录里面
                    while ((len = inputStream.read(bytes)) != -1){
                        outputStream.write(bytes,0, len);
                        long length = file.length();
                        //获取下载的大小，并把它传给页面
                        int progress = (int) (length * 100 / contentLength);
                        view.onDownloadProgress(progress, contentLength);
                    }

                    view.onDownloadSuccess(file);
                } catch (Exception e) {
                    view.onDownloadFail("下载失败:" + e.getMessage());
                }

            }

            @Override
            public void onError(ExceptionHelper.ResponseThrowable e) {
                view.onDownloadFail("下载失败:" + e.message);
            }

            @Override
            public void OnCompleted() { }

            @Override
            public void OnDisposable(Disposable d) {
                disposable.add(d);
            }
        });
    }
}
