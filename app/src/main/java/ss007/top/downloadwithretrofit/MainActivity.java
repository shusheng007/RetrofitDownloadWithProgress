package ss007.top.downloadwithretrofit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import ss007.top.downloadwithretrofit.download.DownloadListener;
import ss007.top.downloadwithretrofit.download.DownloadUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tvProgress = findViewById(R.id.tv_progess);
        final TextView tvFileLocation = findViewById(R.id.tv_file_location);
        Button download = findViewById(R.id.button);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        final String desFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sstx.apk";
        final String baseUrl = "http://www.apk.anzhi.com/";
        final String url = "data4/apk/201809/06/f2a4dbd1b6cc2dca6567f42ae7a91f11_45629100.apk";

        //如果需要使用自己的OkHttpClient
        //DownloadUtil.getInstance().initConfig(OkHttpClient.Builder);

        download.setOnClickListener(v -> {
            download.setEnabled(false);
            DownloadUtil.getInstance()
                    .downloadFile(baseUrl, url, desFilePath, new DownloadListener() {
                        @Override
                        public void onFinish(final File file) {
                            download.setEnabled(true);
                            tvFileLocation.setText("下载的文件地址为：" + file.getAbsolutePath());
                            installAPK(file, MainActivity.this);
                        }

                        @Override
                        public void onProgress(int progress) {
                            tvProgress.setText(String.format("下载进度为：%s", progress));
                        }

                        @Override
                        public void onFailed(String errMsg) {
                            download.setEnabled(true);
                        }
                    });
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void installAPK(File file, Activity mAct) {
        String authority = "ss007.top.downloadwithretrofit.FileProvider";
        mAct.startActivity(getInstallAppIntent(file, authority, true));
    }

    private Intent getInstallAppIntent(final File file, final String authority, final boolean isNewTask) {
        if (file == null) return null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            data = FileProvider.getUriForFile(MainActivity.this, authority, file);
        }
        intent.setDataAndType(data, type);
        return getIntent(intent, isNewTask);
    }

    private Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }
}
