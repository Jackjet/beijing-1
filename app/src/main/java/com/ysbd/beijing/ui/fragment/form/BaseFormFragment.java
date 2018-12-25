package com.ysbd.beijing.ui.fragment.form;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.ysbd.beijing.App;
import com.ysbd.beijing.BaseFragment;
import com.ysbd.beijing.bean.DocumentBean;
import com.ysbd.beijing.fileEidter.EditActivity;
import com.ysbd.beijing.fileEidter.FileReaderActivity;
import com.ysbd.beijing.fileProgress.ProgressLoadFile;
import com.ysbd.beijing.ui.activity.CommentActivity;
import com.ysbd.beijing.ui.activity.FormActivity;
import com.ysbd.beijing.ui.activity.IntentWeb;
import com.ysbd.beijing.ui.adapter.AttachmentAdapter;
import com.ysbd.beijing.ui.adapter.CommentAdapter;
import com.ysbd.beijing.ui.bean.AttachmentBean;
import com.ysbd.beijing.ui.bean.CommentBean;
import com.ysbd.beijing.ui.bean.CurrentCommentBean;
import com.ysbd.beijing.ui.bean.OpinionModel;
import com.ysbd.beijing.utils.CommentFormUtils;
import com.ysbd.beijing.utils.Constants;
import com.ysbd.beijing.utils.DateFormatUtil;
import com.ysbd.beijing.utils.FileUtils;
import com.ysbd.beijing.utils.SpUtils;
import com.ysbd.beijing.utils.ToastUtil;
import com.ysbd.beijing.utils.WebServiceUtils;
import com.ysbd.beijing.view.CommentLinearLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by lcjing on 2018/8/28.
 */

public class BaseFormFragment extends BaseFragment implements CommentAdapter.CommentClick {

    Map<String, List<OpinionModel>> opinionMap;
    Map<String, CommentAdapter> adapterMap;
    Map<String, CommentLinearLayout> layoutMap;
    List<String> frameMap;
    private String guid, formName;

    public void setFrameAddLayout(Map<String, CommentLinearLayout> layoutMap, List<String> frameMap) {
        this.frameMap = frameMap;
        this.layoutMap = layoutMap;
    }

    public void initData(Map<String, CommentLinearLayout> layoutMap, List<String> frameMap, String guid, String formName) {
        this.frameMap = frameMap;
        this.layoutMap = layoutMap;
        this.guid = guid;
        this.formName = formName;
        userName = getContext().getSharedPreferences(Constants.SP, Context.MODE_PRIVATE).getString(Constants.USER_NAME, "");
        userId = getContext().getSharedPreferences(Constants.SP, Context.MODE_PRIVATE).getString(Constants.USER_ID, "");
        initComment();
    }
    public String quanXian="";
    public void initData(Map<String, CommentLinearLayout> layoutMap, List<String> frameMap, String guid, String formName,String quanXian) {
        this.frameMap = frameMap;
        this.layoutMap = layoutMap;
        this.guid = guid;
        this.formName = formName;
        this.quanXian=quanXian;
        userName = getContext().getSharedPreferences(Constants.SP, Context.MODE_PRIVATE).getString(Constants.USER_NAME, "");
        userId = getContext().getSharedPreferences(Constants.SP, Context.MODE_PRIVATE).getString(Constants.USER_ID, "");
        initComment();
    }

    private void initComment() {
        boolean editable = ("todo".equals(actor)&&quanXian.equals("") )|| ("待办".equals(actor)&&quanXian.equals(""));
        opinionMap = new HashMap<>();
        adapterMap = new HashMap<>();
        for (int i = 0; i < frameMap.size(); i++) {
            String frame = frameMap.get(i);
            opinionMap.put(frame, new ArrayList<OpinionModel>());
            adapterMap.put(frame, new CommentAdapter(getContext(), opinionMap.get(frame), frame));
            adapterMap.get(frame).setCommentClick(this);
            adapterMap.get(frame).setEditable(editable);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            layoutMap.get(frame).setLayoutParams(lp);//设置布局参数
//            layoutMap.get(frame).setOrientation(LinearLayout.VERTICAL);// 设置子View的Linearlayout
            layoutMap.get(frame).setAdapter(adapterMap.get(frame));
        }
    }

    private String userName = "";
    private String userId = "";

    //是否可以发送
    public boolean canSend() {
        if (opinionMap.get("处长批示") != null && opinionMap.get("处长批示").size() > 0
                && opinionMap.get("处长批示").get(0).isParent() && opinionMap.get("处长批示").get(0).isAddable())//没有填写处长批示
        {
            return false;
        } else if (opinionMap.get("处长意见") != null && opinionMap.get("处长意见").size() > 0
                && opinionMap.get("处长意见").get(0).isParent() && opinionMap.get("处长意见").get(0).isAddable())//没有填写处长批示
        {
            return false;
        }
        return true;
    }

    public void initCommentDate(List<CurrentCommentBean> currentComment, List<CommentBean> comment) {
        String frame = null;
        List<OpinionModel> opinionModels1 = new ArrayList<>();
        if (comment.size() == 0) {
            for (int i = 0; i < currentComment.size(); i++) {
                String frame1 = CommentFormUtils.getCommentFrame(formName, currentComment.get(i).getComment_guid());
                List<OpinionModel> opinionModels2 = opinionMap.get(frame1);
                OpinionModel opinionModel = new OpinionModel();
                opinionModel.setParent(true);
                opinionModel.setOpinionFrameMark(currentComment.get(i).getComment_guid());
                opinionModel.setAddable(true);
                opinionModel.setId(currentComment.get(i).getComment_guid());
                opinionModel.setOpinionFrameName(frame1);
                opinionModels2.add(opinionModel);
            }
        } else if (comment != null) {
            for (int i = 0; i < comment.size(); i++) {
                OpinionModel opinionModel = new OpinionModel();
                opinionModel.setParent(false);
                opinionModel.setOpinionFrameMark(comment.get(i).getComment_guid());
                opinionModel.setAddable(false);
                opinionModel.setId(comment.get(i).getRow_guid());
                opinionModel.setContent(comment.get(i).getComment_content());
                opinionModel.setUserName(comment.get(i).getComment_person());
                opinionModel.setUserId(comment.get(i).getPerson_guid());
//                opinionModel.setUserParentId(DBUtils.getPIdByName(comment.get(i).getComment_person()));
                opinionModel.setCreateDate(DateFormatUtil.subDate(comment.get(i).getComment_date()));
                opinionModel.setStep(comment.get(i).getStep());
                opinionModels1.add(opinionModel);
                frame = CommentFormUtils.getCommentFrame(formName, comment.get(i).getComment_guid());
                if (opinionMap.get(frame) != null) {
                    opinionMap.get(frame).add(opinionModel);
                }

            }
        }

        for (List<OpinionModel> opinionModels : opinionMap.values()) {
            Collections.sort(opinionModels, new Comparator<OpinionModel>() {

                @Override
                public int compare(OpinionModel o1, OpinionModel o2) {
                    /*按员工编号正序排序*/
                    return (int) (DateFormatUtil.getLongDate(o1.getCreateDate()) - DateFormatUtil.getLongDate(o2.getCreateDate()));
                    /*按员工编号逆序排序*/
                    //return o2.getEmpno()-o1.getEmpno();
                    /*按员工姓名正序排序*/
                    //return o1.getEname().compareTo(o2.getEname());
                    /*按员工姓名逆序排序*/
                    //return o2.getEname().compareTo(o1.getEname());
                }
            });
            for (int i = 0; i < opinionModels.size(); i++) {
                for (int i1 = i; i1 < opinionModels.size(); i1++) {
                    if (i == i1) {
                        continue;
                    }
                    if (DateFormatUtil.getLongDate(opinionModels.get(i).getCreateDate()) > DateFormatUtil.getLongDate(opinionModels.get(i1).getCreateDate())) {
                        Collections.swap(opinionModels, i, i1);
                    }
                }
            }
        }

        if (currentComment != null) {
            for (int i = 0; i < currentComment.size(); i++) {
                boolean in = false;
                String frame1 = CommentFormUtils.getCommentFrame(formName, currentComment.get(i).getComment_guid());
                List<OpinionModel> opinionModels2 = opinionMap.get(frame1);
                OpinionModel opinionModel = new OpinionModel();
                opinionModel.setParent(true);
                opinionModel.setOpinionFrameMark(currentComment.get(i).getComment_guid());
                opinionModel.setAddable(true);
                opinionModel.setId(currentComment.get(i).getComment_guid());
                opinionModel.setOpinionFrameName(frame1);
                for (int j = 0; j < opinionModels1.size(); j++) {
                    String step = opinionModels1.get(j).getStep();
                    String step1 = currentComment.get(i).getStep();
                    if (opinionModels1.get(opinionModels1.size() - 1).getUserId().equals(userId)) {

                        if (step == null) {
                            in = true;
                        } else if (step1 == null) {
                            in = true;
                        } else if (step.equals(step1)) {
                            opinionModels1.get(opinionModels1.size() - 1).setEditable(true);
                            in = false;
                        } else {
                            opinionModels1.get(opinionModels1.size() - 1).setEditable(false);
                            in = true;
                        }
                    } else if (j == opinionModels1.size() - 1) {
                        opinionModels2.add(opinionModel);
                    }
                }
                if (in) {
                    opinionModels2.add(opinionModel);
                }
            }
        }


        handler.sendEmptyMessage(1);
    }


    public void setQianfa(String qianfa, String huiqian) {
        if ("一般发文".equals(formName)) {
            opinionMap.get("局内会签").add(getOpinionModel(huiqian));
            opinionMap.get("局领导批示").add(getOpinionModel(qianfa));
        }
        handler.sendEmptyMessage(1);
    }

    private OpinionModel getOpinionModel(String content) {
        OpinionModel opinionModel = new OpinionModel();
        opinionModel.setCreateDate("");
        opinionModel.setId("");
        opinionModel.setEditable(false);
        opinionModel.setParent(false);
        opinionModel.setContent(content);
        return opinionModel;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {//新建
            OpinionModel opinionModel = (OpinionModel) data.getSerializableExtra("opinion");
            for (int i = 0; i < opinionMap.get(editType).size(); i++) {
                if (opinionMap.get(editType).get(i).getId().equals(opinionModel.getOpinionFrameMark())) {
                    opinionMap.get(editType).remove(i);
                }
            }
            opinionMap.get(editType).add(opinionModel);
            adapterMap.get(editType).notifyDataSetChanged();
        } else if (requestCode == 2 && resultCode == 1) {//修改
            OpinionModel opinionModel = (OpinionModel) data.getSerializableExtra("opinion");
            for (int i = 0; i < opinionMap.get(editType).size(); i++) {
                if (opinionMap.get(editType).get(i).getId().equals(editRowId)) {
                    opinionMap.get(editType).get(i).setContent(opinionModel.getContent());
                    opinionMap.get(editType).get(i).setCreateDate(opinionModel.getCreateDate());
                }
            }
            adapterMap.get(editType).notifyDataSetChanged();
        } else if (requestCode == 101 && resultCode == 101) {//更新正文
            Log.e("是否待办", actor);
            if (actor.equals("todo") || actor.equals("待办")) {
                String path = data.getStringExtra("path");
                Log.e("原始上传正文地址", filePath);
                Log.e("修改上传正文地址", path);
                upLoadDocument(documentBean, filePath);
            }
        } else if (requestCode == 101) {//无需修改正文

        }
    }

    private String editType = "";
    private String editRowId = "";

    @Override
    public void add(int position, String type) {
        editType = type;
        Intent intent = new Intent(getContext(), CommentActivity.class);
        intent.putExtra("id", opinionMap.get(type).get(position).getOpinionFrameMark());
        intent.putExtra("documentId", guid);
        intent.putExtra("instanceguid", guid);
        intent.putExtra("isAdd", true);
        intent.putExtra("title", "新建" + type);
        startActivityForResult(intent, 1);
    }

    @Override
    public void edit(int position, String type) {
        editType = type;
        editRowId = opinionMap.get(type).get(position).getId();
        Intent intent = new Intent(getContext(), CommentActivity.class);
        intent.putExtra("id", opinionMap.get(type).get(position).getOpinionFrameMark());
        intent.putExtra("documentId", guid);
        intent.putExtra("instanceguid", guid);
        intent.putExtra("content", opinionMap.get(type).get(position).getContent());
        intent.putExtra("isAdd", false);
        intent.putExtra("rowId", editRowId);
        intent.putExtra("title", "修改" + type);
        startActivityForResult(intent, 2);
    }

    @Override
    public void delete(int position, String type) {
        editType = type;
        editRowId = opinionMap.get(type).get(position).getId();
        new Thread() {
            @Override
            public void run() {
                super.run();
                String msg = WebServiceUtils.getInstance().deleteComment(editRowId);
                //  {"succ":{"info":"删除意见成功","state":"true"}}
                if (msg.contains("成功")) {
                    handler.sendEmptyMessage(4);
                }
                CommentActivity.CommentRes commentRes = new Gson().fromJson(msg, CommentActivity.CommentRes.class);
                handler.obtainMessage(2, commentRes.getSucc().getInfo()).sendToTarget();
//                handler.obtainMessage(2,msg).sendToTarget();
            }
        }.start();
    }


    public void initAttachment(final List<AttachmentBean> attachmentList, CommentLinearLayout attachmentLayout) {
        AttachmentAdapter adapter = new AttachmentAdapter(getContext(), attachmentList);
        attachmentLayout.setAdapter(adapter);
        attachmentLayout.setOnItemClickListener(new CommentLinearLayout.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, Object obj, int position) {
//                downLoadAttachment(attachmentList.get(position).getAttachmentrow_guid(),attachmentList.get(position).getAttachment_name());
                if (attachmentList.get(position).getAttachment_extension().toLowerCase().equals("html")) {
                    openForm(attachmentList.get(position).getAttachment_description(), attachmentList.get(position).getAttachment_name());
                } else {
                    down(attachmentList.get(position).getUrl(), attachmentList.get(position).getAttachment_name(), false);
                }
            }
        });
    }


    public void downLoadAttachment(String attachmentId, String attachmentName) {
        fileName = attachmentName;
        RequestBody formBody = new FormBody.Builder()
                .add("attachmentrow_guid", attachmentId)
                .build();
        final Request request = new Request.Builder()
                .url(Constants.LOAD_FILE)
                .post(formBody)
                .build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = 6;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                filePath = FileUtils.getInstance().makeDir().getPath() + File.separator + fileName;
                try {
                    FileUtils.getInstance().saveToFile(filePath, inputStream);
                    Message message = new Message();
                    message.what = 5;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = 6;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void openForm(String description, String attachmentName) {
        String type = "";
        String instanceguid = "";
        int nameIndex = attachmentName.lastIndexOf("(");
        if (nameIndex >= 0) {
            attachmentName = attachmentName.substring(nameIndex);
            if (attachmentName.contains("主办文")) {
                type = "主办文";
            } else if (attachmentName.contains("主办文_协办")) {
                type = "主办文_协办";
            } else if (attachmentName.contains("结余资金")) {
                type = "结余资金";
            } else if (attachmentName.contains("一般发文")) {
                type = "一般发文";
            } else if (attachmentName.contains("指标文")) {
                type = "指标文";
            } else if (attachmentName.contains("市转文")) {
                type = "市转文";
            } else if (attachmentName.contains("市转文_协办")) {
                type = "市转文_协办";
            } else if (attachmentName.contains("局内传文")) {
                type = "局内传文";
            } else if (attachmentName.contains("局内传文_协办")) {
                type = "局内传文_协办";
            } else {
                ToastUtil.show("暂时无法打开该文种!", getContext());
                return;
            }
        } else {
            ToastUtil.show("暂时无法打开该文种!", getContext());
            return;
        }
        int idIndex = description.indexOf("instanceGUID=");

        if (idIndex > 0) {
            description = description.substring(idIndex + "instanceGUID=".length());
            Log.d("WebServiceManager", "  url=" + description);
            instanceguid = description.substring(0, description.indexOf("&"));
        } else {
            ToastUtil.show("无法获取表单信息!", getContext());
            return;
        }
        Log.d("WebServiceManager", "  instanceguid=" + instanceguid);
        Log.d("WebServiceManager", "  type=" + type);
        Intent intent = new Intent(getContext(), FormActivity.class);
        intent.putExtra("from","");
        intent.putExtra("type", type);
        intent.putExtra("actor", "办结");
        intent.putExtra("instanceguid", instanceguid);
//        /{0A2FCA25-FFFF-FFFF-F359-6F88FFFFCA20}
        startActivity(intent);
    }

    public void down(String url, String attachmentName, final boolean isDocument) {
//        url = " http://172.10.48.92:9998/risenetoabjcz/riseoffice/MobileDocServlet?instanceGuid={0A2FCA25-0000-0000-1BD9-635B00016673}&documentGUid={0A2FCA25-0000-0000-1BDA-382C0001667F}";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20102);
                ToastUtil.show("请允许本应用读取设备内存", getContext());
            }
        }
        handler.sendEmptyMessage(10);
        try {
            fileName = attachmentName;
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            OkHttpClient mOkHttpClient = new OkHttpClient();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = new Message();
                    message.what = 6;
                    handler.sendMessage(message);
                }

                @Override
                public void onResponse(Call call, Response response)  {
                    InputStream inputStream = response.body().byteStream();
                    if (isDocument) {
                        filePath = FileUtils.getInstance().makeDocumentDir().getPath() + File.separator + fileName;
                    } else {
                        filePath = FileUtils.getInstance().makeDir().getPath() + File.separator + fileName;
                    }
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        FileUtils.getInstance().saveToFile(filePath, inputStream);
                        if (isDocument&&quanXian.equals("")) {
                            handler.sendEmptyMessage(7);
                        } else {
                            handler.sendEmptyMessage(5);
                        }
                    } catch (Exception e) {
                        Message message = new Message();
                        message.what = 6;
                        handler.sendMessage(message);
                    }
                }
            });
        } catch (Exception e) {
            App.catchE(e);
        }

    }

    private String filePath, fileName;
    DocumentBean documentBean;
    public String actor;

    public void setActor(String actor) {
        this.actor = actor;
    }

    public void setDocumentBean(DocumentBean bean) {
        this.documentBean = bean;
    }

    //上传正文
    public void upLoadDocument(DocumentBean bean, String filePath) {
        Map<String, String> map = new HashMap<>();
//        String instanceGUID = request.getParameter(WorkflowConst.InstanceGUID);
//        String documentRowGUID = request.getParameter("DocumentRowGUID");
//        String templateGUID = request.getParameter("templateGUID");
//        String templateName = request.getParameter("templateName");
//        String documentTitle = request.getParameter("documentTitle");
//        documentTitle = URLDecoder.decode(documentTitle,"utf-8");
//        String imgID = request.getParameter("imgID");
//        String step = request.getParameter("step");
//        String fileName = request.getParameter("fileName");
        SharedPreferences sp = getContext().getSharedPreferences(Constants.SP, Context.MODE_PRIVATE);
        String userId = sp.getString(Constants.USER_ID, "");
        Log.e("上传地址", Constants.UP_LOAD_DOC);
        map.put("url", Constants.UP_LOAD_DOC);
        map.put("path", filePath);
        map.put("instanceGUID", guid);
        map.put("userId", userId);
        map.put("DocumentRowGUID", bean.getDOCUMENTROW_GUID());
        map.put("templateGUID", bean.getTEMPLATE_GUID());
        map.put("templateName", bean.getTEMPLATE_NAME());
        map.put("documentTitle", bean.getDOCUMENTTITLE());
        map.put("imgID", bean.getDOCID());
        map.put("step", bean.getSTEP());
        map.put("fileName", bean.getDOCUMENTFILENAME());
        map.put("documentTitle", bean.getDOCUMENTTITLE());
        try {
            ProgressLoadFile.docUpLoad(getContext(), map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.getCause();
                    Log.e("上传正文返回结果", "失败：" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();
                    Log.e("上传正文返回结果", "结果：" + s);
                    if (s.length() > 5 && s.substring(0, 5).contains("OK")) {
                        handler.sendEmptyMessage(8);
                    } else {
                        handler.obtainMessage(9, s).sendToTarget();
                    }
                }
            }, new File(filePath));
        } catch (Exception e) {
            e.getCause();
            Log.e("上传文件出现异常", e.getMessage());
        }
    }


    //需要加载腾讯浏览服务
    public void lookFile(boolean isDocument) {

        if (isDocument) {
            Intent intent = new Intent(getContext(), EditActivity.class);
            intent.putExtra("path", filePath);
            int index = filePath.lastIndexOf("/");
            String name = "";
            if (index > 1) {
                filePath.substring(index + 1);
            }
            intent.putExtra("filename", name);
            intent.putExtra("uploadurl", "");
            startActivityForResult(intent, 101);
        } else {
            Log.e("filePath", filePath);
            Log.e("fileName", fileName);
            Intent intent = new Intent(getContext(), FileReaderActivity.class);
            intent.putExtra("filePath", filePath);
            intent.putExtra("fileName", fileName);
            intent.putExtra("isDocument", isDocument);
            intent.putExtra("firstOpen", false);
            startActivity(intent);
        }
    }

    //跳转intent 打开
    public void lookFile1(boolean isDocument) {
        if (isDocument) {
            Intent intent = new Intent(getContext(), EditActivity.class);
            intent.putExtra("path", filePath);
            int index = filePath.lastIndexOf("/");
            String name = "";
            if (index > 1) {
                filePath.substring(index + 1);
            }
            if (actor.equals("todo") || actor.equals("待办")){
                intent.putExtra("TYPE", "edit");
            }else{
                intent.putExtra("TYPE", "read");
            }

            intent.putExtra("filename", name);
            intent.putExtra("uploadurl", "");

            startActivityForResult(intent, 101);
        } else {
            String end=fileName.substring(fileName.lastIndexOf('.')+1);
            if (end.equals("doc")||end.equals("docx")||end.equals("xls")||end.equals("xlsx")||end.equals("ppt")||end.equals("pptx"))
            {
                Intent intent = new Intent(getContext(), EditActivity.class);
                intent.putExtra("path", filePath);
                int index = filePath.lastIndexOf("/");
                String name = "";
                if (index > 1) {
                    filePath.substring(index + 1);
                }
                intent.putExtra("filename", name);
                intent.putExtra("uploadurl", "");
                intent.putExtra("TYPE", "read");
                startActivityForResult(intent, 101);
            }else {
                startActivity(FileUtils.getInstance().openFile(filePath, getActivity()));
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (viewDestroyed) {
                return;
            }
            switch (msg.what) {
                case 1:
                    for (int i = 0; i < frameMap.size(); i++) {
                        adapterMap.get(frameMap.get(i)).notifyDataSetChanged();
                    }
                    break;
                case 2:
                    ToastUtil.show(msg.obj.toString(), getContext());
                    break;
                case 3:
                    ToastUtil.show("未获取到该公文的详细信息！", getContext());
//                    getActivity().finish();
                    break;
                case 4://删除意见后刷新列表
                    for (int i = 0; i < opinionMap.get(editType).size(); i++) {
                        if (opinionMap.get(editType).get(i).getId().equals(editRowId)) {
                            OpinionModel opinionModel = new OpinionModel();
                            opinionModel.setParent(true);
                            opinionModel.setOpinionFrameMark(opinionMap.get(editType).get(i).getOpinionFrameMark());
                            opinionModel.setId(opinionMap.get(editType).get(i).getOpinionFrameMark());
                            opinionModel.setAddable(true);
                            opinionMap.get(editType).remove(i);
//
                            opinionMap.get(editType).add(opinionModel);
                            break;
                        }
                    }
                    adapterMap.get(editType).notifyDataSetChanged();
                    break;
                case 5:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20104);
                        } else {
                            lookFile1(false);
                        }
                    } else {
                        lookFile1(false);
                    }
                    break;
                case 6:
                    ToastUtil.show("网络错误", getActivity());
                    break;
                case 7:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20104);
                        } else {
                            lookFile1(true);
                        }
                    } else {
                        lookFile1(true);
                    }
                    break;
                case 8:
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    ToastUtil.show("正文修改成功", getActivity());
                    break;
                case 9:
                    ToastUtil.show(msg.obj.toString(), getActivity());
                    break;
                case 10:
                    ToastUtil.show("正在加载",getActivity());

            }
        }
    };
    private boolean viewDestroyed = false;
    protected void toWebIntent(String id){
        Intent intent = new Intent(getContext(),IntentWeb.class);

//        String url = "http://192.168.0.110:9998/risenetoabjcz/riseoffice/default/putCopyMobileAction.do?submitType=0&instanceGUID="+id+"&userid="+SpUtils.getInstance().getUserId()+"&mobale=1";//此处填链接
        String url = "http://10.123.27.194:9910/riseoffice/default/putCopyMobileAction.do?submitType=0" +
                "&instanceGUID="+id+"&userid="+SpUtils.getInstance().getUserId()+"&mobale=1";//此处填链接
        intent.putExtra("URL",url);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewDestroyed = true;
    }

}
