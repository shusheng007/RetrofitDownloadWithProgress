# RetrofitDownloadWithProgress
show how to use retrofit download file with progress

![download with progress](https://github.com/shusheng007/RetrofitDownloadWithProgress/blob/master/images/download.gif)

## how to use:

```
DownloadUtil.getInstance()
        .downloadFile(baseUrl, url, desFilePath, new DownloadListener() {
            @Override
            public void onFinish(final File file) {
            }

            @Override
            public void onProgress(int progress) {
                //tvProgress.setText(String.format("下载进度为：%s", progress));
            }

            @Override
            public void onFailed(String errMsg) {
            }
        });
```

if you need set yourself okHttpClick, use following config

```
DownloadUtil.getInstance().initConfig(OkHttpClient.Builder);
```

[中文教程](https://blog.csdn.net/ShuSheng0007/article/details/82428733)