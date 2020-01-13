package top.ss007.library;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 *
 */

public interface DownloadService {
    @Streaming
    @GET
    Call<ResponseBody> downloadWithDynamicUrl(@Url String fileUrl);
}
