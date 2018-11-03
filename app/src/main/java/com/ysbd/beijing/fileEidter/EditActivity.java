package com.ysbd.beijing.fileEidter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.ntko.app.office.wps.params.WPSDisallowedActionList;
import com.ntko.app.office.wps.params.WPSMenuBarViewDisabledList;
import com.ntko.app.office.wps.params.WPSMenuBarViewHiddenList;
import com.ntko.app.office.wps.params.WPSWordParameters;
import com.ntko.app.pdf.PDFDrawType;
import com.ntko.app.pdf.PDFSettings;
import com.ntko.app.support.Params;
import com.ntko.app.support.WPSOfficeAgent;
import com.ntko.app.support.callback.WPSWordParametersCallback;
import com.ysbd.beijing.R;
import com.ysbd.beijing.utils.Constants;
import com.ysbd.beijing.utils.SpUtils;


import java.io.File;

/**
 * Created by lcjing on 2018/7/13.
 */

public class EditActivity extends OfficeSDKCompatActivity {

    private boolean firstStart = true;
    private String wpsAppURLPro = "http://mo.wps.cn/pc-app/Android/moffice_9.2.11_1033_ProCn00110_multidex_245481.apk";
    private String wpsAppURLCn = "http://dl.op.wpscdn.cn/dl/wps/mobile/apk/moffice_10.9.1_2052_cn00563_multidex_ff55315e45.apk";

    @Override
    protected void postOnPermissionsGranted() {
        setContentView(R.layout.activity_edit_file);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = getIntent().getStringExtra("path");
        String filename = getIntent().getStringExtra("filename");
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
        open(new File("/storage/emulated/0/bjczj1/document/cb正文.doc"));

//       WPSWordParameters parameters = new WPSWordParameters();
//        parameters.setOpenInView(WPSWordParameters.OPEN_IN_NORMAL);
//        parameters.setRevisionMode(true);
//        parameters.setShowReviewPanel(true);
//        parameters.setUsername(SpUtils.getInstance().getUserName());
////        parameters.setWpsAppDownloadURL(wpsAppURL);
////        initOfficeSDK();
//        officeSDK.withParameters(new WPSWordParametersCallback() {
//            @Override
//            public WPSWordParameters prepare() {
//                WPSWordParameters parameters = new WPSWordParameters();
//                parameters.setOpenInView(WPSWordParameters.OPEN_IN_NORMAL);
//                parameters.setUsePenMode(true);
//                parameters.setInkColor(Color.DKGRAY);
//                parameters.setRevisionMode(true);
//                parameters.setUsername(SpUtils.getInstance().getUserName());
//                // 如果WPS没有安装，下载并安装应用
////                parameters.setWpsAppDownloadURL(getWPSDownloadAddress());
//                return parameters;
//            }
//        }).openLocalWordDocument(filename, path, Params.OfficeVersion.LATEST);
////        officeSDK.openLocalWordDocument(filename, path, uploadurl, createCustomFields(), Params.OfficeVersion.LATEST);

    }

    public void onOpenServerWord(String fileUrl, String uploadUrl) {

        // 文档下载地址

        officeSDK.withParameters(new WPSWordParametersCallback() {
            @Override
            public WPSWordParameters prepare() {
                WPSWordParameters parameters = new WPSWordParameters();
                // 批注模式
                //parameters.setOpenInView(WPSWordParameters.OPEN_IN_HAND_SIGNATURE);
                // WPS自动判断进入何种模式，通常编辑模式
//                parameters.setOpenInView(WPSWordParameters.OPEN_IN_NORMAL);
                // 只读模式
                //parameters.setOpenInView(WPSWordParameters.OPEN_IN_READONLY);
                // 阅读模式
                parameters.setOpenInView(WPSWordParameters.OPEN_IN_READ);
                // 编辑模式
                //parameters.setOpenInView(WPSWordParameters.EDIT_MODE);

                parameters.setRevisionMode(true);
                parameters.setShowReviewPanel(true);
                parameters.setUsername(SpUtils.getInstance().getUserName());
                // 如果WPS没有安装，下载并安装应用
                parameters.setWpsAppDownloadURL(getWPSDownloadAddress());

                WPSDisallowedActionList list = new WPSDisallowedActionList();
                list.AT_COPY = true;
                list.AT_CUT = true;
                list.AT_PASTE = true;
                list.AT_PRINT = true;
                list.AT_SHARE = true;// 不响应分享菜单的点击
                parameters.setDisallowedActionList(list);

                WPSMenuBarViewDisabledList disabledList = new WPSMenuBarViewDisabledList();
                disabledList.VT_FILE_SHARE = true;// 禁用文件分享菜单
                disabledList.VT_INSERT_PICTURE = true;
                disabledList.VT_INSERT_TABLE = true;
                disabledList.VT_INSERT_SHAPE = true;
                parameters.setMenuBarViewDisabledList(disabledList);

                WPSMenuBarViewHiddenList hiddenList = new WPSMenuBarViewHiddenList();
                hiddenList.VT_REVIEW_SPELLCHECK = true;
                hiddenList.VT_REVIEW_MODIFY_USERNAME = true;
                hiddenList.VT_FILE_SHARE = true;// 隐藏分享菜单
                parameters.setMenuBarViewHiddenList(hiddenList);

                return parameters;
            }
        }).openServerWordDocument("", fileUrl, uploadUrl, null, Params.OfficeVersion.LATEST);
    }

    private String getWPSDownloadAddress() {
        String url = null;
        if (officeSDK != null) {
            WPSOfficeAgent.WPSAppEdition edition = officeSDK.getWPSAppEdition();
            url = (edition == WPSOfficeAgent.WPSAppEdition.PROFESSIONAL) ? wpsAppURLPro : wpsAppURLCn;
        }
        return url;
    }

    public void openSeverDocumentExtended(String fileUrl, String uploadURL) {
        officeSDK.withParameters(new WPSWordParametersCallback() {
            @Override
            public WPSWordParameters prepare() {
                WPSWordParameters params = new WPSWordParameters();
                params.setUsername(SpUtils.getInstance().getUserName());
                params.setRevisionMode(true);
                return params;
            }
        })
                .openServerWordDocument("Demo Word", fileUrl, uploadURL, createCustomFields(), Params.OfficeVersion.LATEST);

    }

    public void onOpenLocalPDF(View view) {

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String filePath = new File(directory, "广西壮族自治区发展和改革委员会.pdf").getAbsolutePath();

        Params params = new Params(Params.SourceType.LOCAL);
        params.setRawFileType(Params.RawFileType.PDF);
        params.setDocumentLocalAddress(filePath);
        params.setDocumentTitle("广西壮族自治区发展和改革委员会");
        params.setSessionUser("韩永刚");

        //params.setViewMode(Params.OPEN_IN_READONLY);
//
//        PDFSettings settings = new PDFSettings();
//        settings.setAnnotationColor(Color.BLACK);
//        settings.setAnnotationThickness(12.0f);
//        settings.setSaveAnnotationOnViewTouch(true);// 在电容笔模式下，当手指滑动时保存批注
//        settings.setDrawType(PDFDrawType.SIM);// 不支持手写笔的设备下设定绘制模式为 PDFDrawType.DEFAULT
//        settings.setShowProgressDialogOnUploading(false);// 是否显示上传进度条
//        // 当显示上传进度条时，取消上传的警告信息
//        settings.setWarningOnCancelUploading("文档内容必须和OA中保存的批注一致，请不要取消上传，否则会出现不可预知的问题！");

        // 自定义批注数据
//        CustomFields data = new CustomFields();
//        data.add(new CustomFieldKeyPair("testKey", "the value of testKey", "测试数据1"));
//        data.add(new CustomFieldKeyPair("name1", "the value of name1", "测试数据2"));
//        settings.setCustomAnnotationData(data);

//        // 一般情况下，在支持手写笔的设备上可以在手写笔和手指之间切换输入方式，但是也可以
//        // 强制使用手写笔，禁止在手写模式下使用手指书写
//        PDFUISpecification uiSpec = new PDFUISpecification();
//        uiSpec.setHideFingerSwitcher(true);
//        settings.setUiSpecification(uiSpec);

        // 处于第一页和最后一页时，滑动关闭文档
        // 检测到第一次越界滑动时，会提示用户 “再次（上|下|左|右）滑以关闭文档”
        // 如果用户再次上|下|左|右滑动便会关闭文档
        // 此功能同时适用于页面横屏和竖屏滑动模式
        // 同时滑动间隔超过5秒，重置滑动退出标记
//        settings.setEnableSlideOut(true);
//
//        officeSDK.openLocalPDFDocument(params, settings, createCustomFields());


        // 打开本地文档并设定PDF视图参数：
        PDFSettings settings = new PDFSettings();

        settings.setDrawType(PDFDrawType.DEFAULT);// 不支持手写笔的设备下设定绘制模式为 PDFDrawType.DEFAULT
        officeSDK.openLocalPDFDocument("spring-boot-reference", filePath, settings, createCustomFields(), "");

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
}
