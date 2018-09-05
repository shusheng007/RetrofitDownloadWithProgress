package ss007.top.downloadwithretrofit.download;


import android.os.Handler;
import android.os.Looper;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DownloadUtil {
    private static final String TAG = DownloadUtil.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 15;

    private static class SingletonHolder {
        private static final DownloadUtil INSTANCE = new DownloadUtil();
    }

    public static DownloadUtil getInstance() {
        return DownloadUtil.SingletonHolder.INSTANCE;
    }


    private DownloadUtil() {
    }

    public void downloadFile(final String baseUrl, final String rUrl, final String filePath, final DownloadListener listener) {
        final Executor executor=new MainThreadExecutor();
        DownloadInterceptor interceptor = new DownloadInterceptor(executor,listener);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        final DownloadService api = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .build()
                .create(DownloadService.class);
        new Thread(() -> {
            try {
                Response<ResponseBody> result = api.downloadWithDynamicUrl(rUrl).execute();
                File file = writeFile(filePath, result.body().byteStream());
                if (listener != null){
                    executor.execute(()->{
                        listener.onFinish(file);
                    });
                }

            } catch (IOException e) {
                if (listener != null){
                    executor.execute(()->{
                        listener.onFailed(e.getMessage());
                    });
                }
                e.printStackTrace();
            }
        }).start();
    }

    private File writeFile(String filePath, InputStream ins) {
        if (ins == null)
            return null;
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = ins.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException(e.getMessage(), e);
        } finally {
            try {
                ins.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private class DownloadException extends RuntimeException {
        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private class MainThreadExecutor implements Executor
    {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable r)
        {
            handler.post(r);
        }
    }
}
