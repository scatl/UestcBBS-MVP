package com.scatl.uestcbbs.module.update.view;

import java.io.File;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/18 19:58
 */
public interface UpdateView {
    void onDownloadProgress(int progress, long total);
    void onDownloadSuccess(File file);
    void onDownloadFail(String msg);
}
