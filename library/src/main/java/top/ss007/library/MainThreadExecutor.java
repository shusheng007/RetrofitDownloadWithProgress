package top.ss007.library;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by Ben.Wang
 *
 * @author Ben.Wang
 * @modifier
 * @createDate 2020/1/13 14:26
 * @description
 */
public class MainThreadExecutor implements Executor {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable r) {
        handler.post(r);
    }
}
