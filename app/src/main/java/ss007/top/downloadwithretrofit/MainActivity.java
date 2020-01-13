package ss007.top.downloadwithretrofit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import top.ss007.library.DownloadListener;
import top.ss007.library.DownloadUtil;
import top.ss007.library.InputParameter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_CODE = 1;

    private final static String BASE_URL = "http://www.apk.anzhi.com/";
    private final static String FILE_URL = "data4/apk/201809/06/f2a4dbd1b6cc2dca6567f42ae7a91f11_45629100.apk";
    private String desFilePath;
    private Button download;
    private TextView tvProgress;
    private TextView tvFileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvProgress = findViewById(R.id.tv_progess);
        tvFileLocation = findViewById(R.id.tv_file_location);
        download = findViewById(R.id.button);

        desFilePath = getExternalFilesDir(null).getAbsolutePath() + "/sstx.apk";
        //如果需要使用自己的OkHttpClient
        //DownloadUtil.getInstance().initConfig(OkHttpClient.Builder);
        download.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                startDownload(desFilePath);
            }
        });
    }

    private void startDownload(String desFilePath) {
        download.setEnabled(false);
        DownloadUtil.getInstance()
                .downloadFile(new InputParameter.Builder(BASE_URL, FILE_URL, desFilePath)
                        .setCallbackOnUiThread(true)
                        .build(), new DownloadListener() {
                    @Override
                    public void onFinish(final File file) {
                        download.setEnabled(true);
                        tvFileLocation.setText("下载的文件地址为:\n" + file.getAbsolutePath());
                        installAPK(file, MainActivity.this);
                    }

                    @Override
                    public void onProgress(int progress, long downloadedLengthKb, long totalLengthKb) {
                        tvProgress.setText(String.format("文件文件下载进度：%d%s \n\n已下载:%sKB | 总长:%sKB", progress,"%", downloadedLengthKb + "", totalLengthKb + ""));

                    }

                    @Override
                    public void onFailed(String errMsg) {
                        download.setEnabled(true);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE != requestCode) {
            return;
        }
        if ((permissions != null && permissions.length > 0) &&
                (grantResults != null && grantResults.length > 0) &&
                Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0]) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDownload(desFilePath);
        } else {
            Toast.makeText(this, "请授予存储权限", Toast.LENGTH_LONG).show();
        }
    }

    public void installAPK(File file, Activity mAct) {
        if (file == null) return;
        String authority = "ss007.top.downloadwithretrofit.FileProvider";
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mAct.startActivity(intent);
    }
}
