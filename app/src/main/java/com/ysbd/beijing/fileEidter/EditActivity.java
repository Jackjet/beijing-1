package com.ysbd.beijing.fileEidter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ysbd.beijing.R;

import java.io.File;

/**
 * Created by lcjing on 2018/7/13.
 */

public class EditActivity extends OfficeSDKCompatActivity {

    private boolean firstStart = true;
    @Override
    protected void postOnPermissionsGranted() {
        setContentView(R.layout.activity_edit_file);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = getIntent().getStringExtra("path");
        String filename = getIntent().getStringExtra("filexname");
        String uploadurl = getIntent().getStringExtra("uploadurl");
        if (firstStart) {
            openLocalWord(path, filename);
        } else {
//            setResult(101);
//            finish();
        }
        firstStart = false;
    }

    public void openLocalWord(String path, String filename) {
        open(new File(path));


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String retStr = bundle.getString("OpReturn");//Submit:最后执行“提交”   Closed:最后执行“关闭”
            String path = bundle.getString("UriReturn");//最后保存的文件路径
            String docStatus = bundle.getString("DocStatus");//Unmodified:文件没有修改（手写）   Modifyed:文件修改（手写）
            String signStatus = bundle.getString("SignStatus");//None:文件无手写操作    Done:文件有手写操作

            Log.e("return", retStr);
            Log.e("path", path);
            Log.e("docStatus", docStatus);
            Log.e("signStatus", signStatus);
            if ("Submit".equals(retStr)) {//文件已改变
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(101, intent);
            } else {
                setResult(102);
            }
            finish();

        }
    }

    private void open(File file) {
        String path = file.getPath();
        String absolutePath = file.getAbsolutePath();
        Log.e("absolutePath", absolutePath);
        Log.e("path", path);
        //创建一个Office的实例
        Intent aIntent = new Intent("android.intent.action.StartEIOffice_1");
        //是将该实例设置为在屏幕最前端显示
        aIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //File_Name为需要打开文件的绝对路径名
        aIntent.putExtra("File_Name", absolutePath);
//        File_Path为需要打开文件的绝对路径名
        aIntent.putExtra("File_Path", absolutePath);
        //是否是OA调用(true: OA调用; false: 非OA调用)
        aIntent.putExtra("IS_OA", true);
        //是否是新建文档(true: 新建文档为true; false: 打开文档为)
        aIntent.putExtra("isNew", false);
        //用于控制签批或是编辑模式(handwrite:签批模式;  edit:编辑模式；read:只读模式)
        aIntent.putExtra("Start_Type", "edit");
        //编辑此文档的用户名
//        aIntent.putExtra("User_Name", "用户名");
        //进入修订时的状态（ 0: 修订状态带标记的最终状态;  1:修订状态不带标记的最终状态 ;  2:修订状态带标记的原始状态;  3:修订状态不带标记的原始状态）
        aIntent.putExtra("Revise_Status", "1");
        //保存按钮显示成"提交"按钮。
        aIntent.putExtra("ID_SUBMIT", "提交");
        //是否显示切换按钮，不传入此参数，不显示切换模式按钮
//        aIntent.putExtra("ID_SWITCH_VIEW", "编辑");
        //开档后根据传入的内容进行搜索
//        aIntent.putExtra("Find_Text", "搜索内容");
        //开档后根据传入的内容定位到第几个搜索到的结果
//        aIntent.putExtra("Find_Index", "搜索索引");
        startActivityForResult(aIntent, 101);
    }

    private void openToDo(File file) {
        String path = file.getPath();
        String absolutePath = file.getAbsolutePath();
        Log.e("absolutePath", absolutePath);
        Log.e("path", path);
        //创建一个Office的实例
        Intent aIntent = new Intent("android.intent.action.StartEIOffice_1");
        //是将该实例设置为在屏幕最前端显示
        aIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //File_Name为需要打开文件的绝对路径名
        aIntent.putExtra("File_Name", absolutePath);
//        File_Path为需要打开文件的绝对路径名
        aIntent.putExtra("File_Path", absolutePath);
        //是否是OA调用(true: OA调用; false: 非OA调用)
        aIntent.putExtra("IS_OA", true);
        //是否是新建文档(true: 新建文档为true; false: 打开文档为)
        aIntent.putExtra("isNew", false);
        //用于控制签批或是编辑模式(handwrite:签批模式;  edit:编辑模式；read:只读模式)
        aIntent.putExtra("Start_Type", "edit");
        //编辑此文档的用户名
//        aIntent.putExtra("User_Name", "用户名");
        //进入修订时的状态（ 0: 修订状态带标记的最终状态;  1:修订状态不带标记的最终状态 ;  2:修订状态带标记的原始状态;  3:修订状态不带标记的原始状态）
        aIntent.putExtra("Revise_Status", "1");
        //保存按钮显示成"提交"按钮。
        aIntent.putExtra("ID_SUBMIT", "提交");
        //是否显示切换按钮，不传入此参数，不显示切换模式按钮
//        aIntent.putExtra("ID_SWITCH_VIEW", "编辑");
        //开档后根据传入的内容进行搜索
//        aIntent.putExtra("Find_Text", "搜索内容");
        //开档后根据传入的内容定位到第几个搜索到的结果
//        aIntent.putExtra("Find_Index", "搜索索引");
        startActivityForResult(aIntent, 101);
    }
}
