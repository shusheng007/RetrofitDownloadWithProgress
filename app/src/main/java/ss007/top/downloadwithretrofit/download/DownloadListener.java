package ss007.top.downloadwithretrofit.download;

import java.io.File;

/**
 * Created by liuyang on 2016/12/20.
 */

public interface DownloadListener {
    void onFinish(File file);

    void onProgress(int progress);

    void onFailed(String errMsg);
}