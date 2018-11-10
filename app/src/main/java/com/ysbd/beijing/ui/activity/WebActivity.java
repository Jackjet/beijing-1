package com.ysbd.beijing.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ysbd.beijing.App;
import com.ysbd.beijing.BaseActivity;
import com.ysbd.beijing.R;
import com.ysbd.beijing.utils.FileUtils;
import com.ysbd.beijing.utils.ToastUtil;

public class WebActivity extends BaseActivity {

    private String url;

    private WebView webView;



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
        webView.loadUrl(substring);
        //自己使用屏幕大小
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                view.loadUrl(url);
                return true;
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String s, String s1, String s2, String s3, long l) {
                ClipboardManager clipboardManager = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText("webview::::"+s);
//                ToastUtil.show("正在下载", WebActivity.this);
//                Request request = new Request.Builder()
//                        .url(s)
//                        .build();
//                OkHttpClient mOkHttpClient = new OkHttpClient();
//                Call call = mOkHttpClient.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Message message = new Message();
//                        message.what = 1;
//                        handler.sendMessage(message);
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        InputStream inputStream = response.body().byteStream();
//                        getFileName(s);
//                        String file = FileUtils.getInstance().makeDir().getPath() + File.separator + getFileName(s);
//                        FileUtils.getInstance().saveToFile(file, inputStream);
//                        Message message = new Message();
//                        message.what = 2;
//                        message.obj = file;
//                        handler.sendMessage(message);
//                    }
//                });
            }
        });
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
//                    ClipboardManager clipboardManager = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                    clipboardManager.setText(msg.obj.toString());

                    if (msg.obj != null)
                        startActivity(FileUtils.getInstance().openFile(msg.obj.toString(), WebActivity.this));
                    break;


            }
        }
    };

}
