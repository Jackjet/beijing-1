package com.ysbd.beijing.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ysbd.beijing.App;
import com.ysbd.beijing.BaseActivity;
import com.ysbd.beijing.R;
import com.ysbd.beijing.utils.FileUtils;
import com.ysbd.beijing.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebActivity extends BaseActivity {

    private String url;

    private WebView webView;

    private String fileName,filePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.web_webView);
        findViewById(R.id.webBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        url = getIntent().getStringExtra("url");
        String substring = url.substring(0, url.lastIndexOf(".")) + "_app_czj.html";
        //自己使用屏幕大小
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() );
        webView.loadUrl(substring);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                String name=s.substring(s.lastIndexOf('/')+1);
                down(s,name);
                ToastUtil.show("正在加载",WebActivity.this);
            }
        });

    }
    public void down(String url, String name) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this!= null && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20102);
                ToastUtil.show("请允许本应用读取设备内存", this);
            }
        }
        try {
            fileName = name;
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            OkHttpClient mOkHttpClient = new OkHttpClient();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    InputStream inputStream = response.body().byteStream();
                    filePath = FileUtils.getInstance().makeDir().getPath() + File.separator + fileName;
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        FileUtils.getInstance().saveToFile(filePath, inputStream);
                        Message message=new Message();
                        message.what=2;
                        message.obj=filePath;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            });
        } catch (Exception e) {
            App.catchE(e);
        }

    }
    private String getFileName(String s) {
        String fileName = s.substring(s.lastIndexOf("/") + 1);
        return fileName;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ToastUtil.show("下载失败", WebActivity.this);
                    break;
                case 2:
                    if (msg.obj != null)
                        startActivity(FileUtils.getInstance().openFile(msg.obj.toString(), WebActivity.this));
                    break;


            }
        }
    };

}
