package com.ysbd.beijing;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.ysbd.beijing.ui.activity.LoginActivity;
import com.ysbd.beijing.utils.ActivityManager;
import com.ysbd.beijing.utils.Constants;
import com.ysbd.beijing.utils.SpUtils;
import com.ysbd.beijing.utils.WebServiceUtils;

import java.io.File;

/**
 * Created by lcjing on 2018/8/13.
 */

public class BaseActivity extends AppCompatActivity {
    public SharedPreferences sp;
    private Handler mHandler_ = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(Constants.SP, MODE_PRIVATE);
        WebServiceUtils.getInstance().initId(this);
        ActivityManager.getInstance().addActivity(this);
        Log.d("BaseActivity",getClass().getSimpleName());
}

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                checkLogin();
//                mHandler_.removeCallbacks(runnable);
                break;
//            case MotionEvent.ACTION_UP:
//                startAD();
//                Log.e("=======", "ACTION_UP");
//                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setTitle("温馨提示")
                    .setCancelable(false)
                    .setMessage("当前登录已失效，请重新登录")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setClass(BaseActivity.this, LoginActivity.class);
                            startActivity(intent);
                            ActivityManager.getInstance().finishAllActivity();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };

    private long maxTime = 1000L * 60 * 10;//10分钟

    public void checkLogin() {
        long time = SpUtils.getInstance().getTime();
        long l = System.currentTimeMillis();
        if (l - time >= maxTime) {
            //超过规定时长
            sp.edit().putBoolean(Constants.IS_LOGIN, false).apply();
            mHandler_.post(runnable);
        } else {
            SpUtils.getInstance().setTime(System.currentTimeMillis());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().finishActivity(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//获取SD卡路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                    + this.getPackageName() + "/bjczj1/";

            File file = new File(path);

            if (file.exists()) {//如果路径存在

                if (file.isDirectory()) {//如果是文件夹
                    File[] childFiles = file.listFiles();//获取文件夹下所有文件
                    if (childFiles == null || childFiles.length == 0) {//如果为空文件夹
                        file.delete();//删除文件夹
                        return;
                    }

                    for (int i = 0; i < childFiles.length; i++) {//删除文件夹下所有文件
                        childFiles[i].delete();
                    }
                    file.delete();//删除文件夹
                }
            }
        }

    }
}
