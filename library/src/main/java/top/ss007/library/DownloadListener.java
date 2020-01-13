package top.ss007.library;

import java.io.File;

public interface DownloadListener {
    void onFinish(File file);

    void onProgress(int progress,long downloadedLengthKb,long totalLengthKb);

    void onFailed(String errMsg);
}