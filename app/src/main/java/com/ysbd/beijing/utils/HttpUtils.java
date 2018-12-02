package com.ysbd.beijing.utils;

import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtils {

    public static void sendHttpRequest(String url,Callback callback ){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendHttpRequest(String url, HashMap<String,String> map, Callback callback ){
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
