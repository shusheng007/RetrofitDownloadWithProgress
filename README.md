# RetrofitDownloadWithProgress
  show how to use retrofit download file with progress

![download with progress](https://github.com/shusheng007/RetrofitDownloadWithProgress/blob/master/images/download.gif)

## how to use:

1. **clone the library and include it to your project**
2. **set okHttpClick(optional)**

    if you want to set yourself okHttpClick, use following config
    ```
    DownloadUtil.getInstance().initConfig(OkHttpClient.Builder);
    ```
3. **invoke**
    ```
    DownloadUtil.getInstance()
            .downloadFile(new InputParameter.Builder(baseUrl, relativeUrl, downloadedFilePath)
                    .setCallbackOnUiThread(true)
                    .build(), new DownloadListener() {
                    
                @Override
                public void onFinish(final File file) {
                    //you can let this callback run on UI thread by setCallbackOnUiThread(true) in inputParameter
                }
    
                @Override
                public void onProgress(int progress, long downloadedLengthKb, long totalLengthKb) {
                    tvProgress.setText(String.format("文件文件下载进度：%d%s \n\n已下载:%sKB | 总长:%sKB", progress,"%", downloadedLengthKb + "", totalLengthKb + ""));
                }
    
                @Override
                public void onFailed(String errMsg) {
                    //you can let this callback run on UI thread by setCallbackOnUiThread(true) in inputParameter
                }
            });
    ```
    **note**: The `onFinish()` and `onFailed()` methods of **downloadListener** are run on the non-ui thread by default, you can change it by `setCallbackOnUiThread(true)` of inputParameter
    
    
[中文教程](https://blog.csdn.net/ShuSheng0007/article/details/82428733)