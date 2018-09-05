package ss007.top.downloadwithretrofit.download;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {

    private DownloadListener listener;
    private Executor executor;

    public DownloadInterceptor(Executor executor, DownloadListener listener) {
        this.listener = listener;
        this.executor = executor;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse.body(), executor, listener))
                .build();
    }
}
