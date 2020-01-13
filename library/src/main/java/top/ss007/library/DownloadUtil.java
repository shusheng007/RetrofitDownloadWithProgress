package top.ss007.library;


import android.util.Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    private static final int DEFAULT_TIMEOUT = 15;
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private final MainThreadExecutor uiExecutor = new MainThreadExecutor();
    private OkHttpClient.Builder mBuilder;

    private DownloadUtil() {
    }

    public static DownloadUtil getInstance() {
        return DownloadUtil.SingletonHolder.INSTANCE;
    }

    public void initConfig(OkHttpClient.Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * download file and show the progress
     *
     * @param listener
     */
    public void downloadFile(InputParameter inputParam, final DownloadListener listener) {

        DownloadInterceptor interceptor = new DownloadInterceptor(listener);
        if (mBuilder != null) {
            mBuilder.addInterceptor(interceptor);
        } else {
            mBuilder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        final DownloadService api = new Retrofit.Builder()
                .baseUrl(inputParam.getBaseUrl())
                .client(mBuilder.build())
                .build()
                .create(DownloadService.class);
        mExecutorService.execute(() -> {
            try {
                Response<ResponseBody> result = api.downloadWithDynamicUrl(inputParam.getRelativeUrl()).execute();
                File file = FileUtil.writeFile(inputParam.getLoadedFilePath(), result.body().byteStream());
                if (listener != null) {
                    if (inputParam.isCallbackOnUiThread()) {
                        uiExecutor.execute(() -> listener.onFinish(file));
                    } else {
                        listener.onFinish(file);
                    }
                }
            } catch (Exception e) {
                if (listener != null) {
                    if (inputParam.isCallbackOnUiThread()) {
                        uiExecutor.execute(() -> listener.onFailed(e.getMessage()));
                    } else {
                        listener.onFailed(e.getMessage());
                    }
                }
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    private static class SingletonHolder {
        private static final DownloadUtil INSTANCE = new DownloadUtil();
    }

}
