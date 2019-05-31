package ss007.top.downloadwithretrofit.download;


import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DownloadUtil {
    private static final String TAG = DownloadUtil.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 15;

    private OkHttpClient.Builder mBuilder;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private DownloadUtil() {
    }

    private static class SingletonHolder {
        private static final DownloadUtil INSTANCE = new DownloadUtil();
    }

    public static DownloadUtil getInstance() {
        return DownloadUtil.SingletonHolder.INSTANCE;
    }

    public void initConfig(OkHttpClient.Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * download file and show the progress
     * @param baseUrl
     * @param rUrl    related url
     * @param filePath the path of downloaded file
     * @param listener
     */
    public void downloadFile(final String baseUrl, final String rUrl, final String filePath, final DownloadListener listener) {
        final Executor executor = new MainThreadExecutor();
        DownloadInterceptor interceptor = new DownloadInterceptor(executor, listener);
        if (mBuilder != null) {
            mBuilder.addInterceptor(interceptor);
        } else {
            mBuilder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        final DownloadService api = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mBuilder.build())
                .build()
                .create(DownloadService.class);

        mExecutorService.execute(() -> {
            try {
                Response<ResponseBody> result = api.downloadWithDynamicUrl(rUrl).execute();
                File file = FileUtil.writeFile(filePath, result.body().byteStream());
                if (listener != null) {
                    executor.execute(() -> {
                        listener.onFinish(file);
                    });
                }

            } catch (IOException e) {
                if (listener != null) {
                    executor.execute(() -> {
                        listener.onFailed(e.getMessage());
                    });
                }
                e.printStackTrace();
            }
        });
    }

    private class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable r) {
            handler.post(r);
        }
    }
}
