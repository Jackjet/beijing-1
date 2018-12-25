package com.ysbd.beijing.ui.fragment.form;


import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ysbd.beijing.App;
import com.ysbd.beijing.R;
import com.ysbd.beijing.ui.activity.FormActivity;
import com.ysbd.beijing.ui.bean.FileIdBean;
import com.ysbd.beijing.ui.bean.form.JuNeiChuanWenBean;
import com.ysbd.beijing.utils.Constants;
import com.ysbd.beijing.utils.FileUtils;
import com.ysbd.beijing.utils.ToastUtil;
import com.ysbd.beijing.utils.WebServiceUtils;
import com.ysbd.beijing.view.CommentLinearLayout;
import com.ysbd.beijing.view.LoadingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class JuneichuanwenXiebanFragment extends BaseFormFragment {


    @BindView(R.id.qicaoriqi)
    TextView qicaoriqi;
    @BindView(R.id.nian)
    TextView nian;
    @BindView(R.id.hao)
    TextView hao;
    @BindView(R.id.zhubanchushi)
    TextView zhubanchushi;
    @BindView(R.id.wenjianmingcheng)
    TextView wenjianmingcheng;
    @BindView(R.id.cl_julingdao)
    CommentLinearLayout clJulingdao;
    @BindView(R.id.cl_chuzhangpishi)
    CommentLinearLayout clChuzhangpishi;
    @BindView(R.id.cl_qitarenyijian)
    CommentLinearLayout clQitarenyijian;
    @BindView(R.id.chengbaoneirong)
    LinearLayout chengbaoneirong;
    @BindView(R.id.chengbanren)
    TextView chengbanren;
    @BindView(R.id.lianxidianhua)
    TextView lianxidianhua;
    @BindView(R.id.gongwen_copy_juneichuanwen)
    TextView gongwenCopyJuneichuanwen;
    @BindView(R.id.cl_attachment)
    CommentLinearLayout clAttachment;
    Unbinder unbinder;
    @BindView(R.id.xiebanchushi)
    TextView xiebanchushi;

    private LoadingDialog loadingDialog;
    private String guid_main;

    public JuneichuanwenXiebanFragment() {
        // Required empty public constructor
    }

    public static JuneichuanwenXiebanFragment getInstance(String guid, String actor, String quanXian) {
        JuneichuanwenXiebanFragment fragment = new JuneichuanwenXiebanFragment();
        Bundle args = new Bundle();
        args.putString("guid", guid);
        args.putString("actor", actor);
        args.putString("quanxian", quanXian);
        fragment.setArguments(args);
        return fragment;
    }

    private String quanXian;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_juneichuanwen_xieban, container, false);
        unbinder = ButterKnife.bind(this, view);
        guid = getArguments().getString("guid");
        actor = getArguments().getString("actor");
        quanXian = getArguments().getString("quanxian");
        Map<String, CommentLinearLayout> layoutMap = new HashMap<>();
        layoutMap.put("处长批示", clChuzhangpishi);
        layoutMap.put("局领导批示", clJulingdao);
        layoutMap.put("其他人意见", clQitarenyijian);
        List<String> frames = new ArrayList<>();
        frames.add("处长批示");
        frames.add("局领导批示");
        frames.add("其他人意见");
        if (actor.equals("todo") || actor.equals("待办")) {//默认共公文拷贝隐藏,如果是待办状态,显示按钮
            gongwenCopyJuneichuanwen.setVisibility(View.VISIBLE);
        }
        initData(layoutMap, frames, guid, formName, quanXian);

        getData();
        return view;
    }

    String guid;
    String formName = "局内传文_协办";


    private void getData() {
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getChildFragmentManager());
        FileIdBean fileIdBean = new FileIdBean();
        fileIdBean.setInstanceguid(guid);
        fileIdBean.setUserid(getContext().getSharedPreferences(Constants.SP, Context.MODE_PRIVATE).getString(Constants.USER_ID, ""));
        final String jsonData = new Gson().toJson(fileIdBean);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data;
                int count = 0;
                do {
                    data = WebServiceUtils.getInstance().findToDoFileInfo(jsonData);/////////流程测试
                } while (TextUtils.isEmpty(data) && count++ < 10);//如果没获取数据尝试多次获取直到获取数据或者获取次数到达10次
//                mHandler.obtainMessage(2, data).sendToTarget();
                if (data.length() < 3) {
                    mHandler.sendEmptyMessage(3);
                    return;
                }
                JuNeiChuanWenBean banwenBean = null;
                try {
                    data = data.replace("<![CDATA[", "");
                    data = data.replace("]]>", "");
                    data = data.replace("&#13;&#10;", "");
                    data = data.replace("&#32;", " ");
                    banwenBean = new Gson().fromJson(data, JuNeiChuanWenBean.class);
                    mHandler.obtainMessage(1, banwenBean).sendToTarget();
                    if (banwenBean.getMenus() != null && getActivity() != null) {
                        ((FormActivity) getActivity()).addMenus(banwenBean.getMenus());
                    }
                    if (banwenBean.getActors() != null && getActivity() != null) {
                        ((FormActivity) getActivity()).setActors(banwenBean.getActors());
                    }
                    initCommentDate(banwenBean.getCurrentComment(), banwenBean.getComment());
                } catch (Exception e) {
                    App.catchE(e);
                    mHandler.sendEmptyMessage(3);
                    return;
                }
                if (banwenBean == null) {
                    mHandler.sendEmptyMessage(3);
                    return;
                }
                if (getActivity() == null) {
                    return;
                }

            }
        }).start();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (viewDestroyed) {
                return;
            }
            switch (msg.what) {
                case 1:
                    JuNeiChuanWenBean bean = (JuNeiChuanWenBean) msg.obj;
                    guid_main = bean.getWorkflow_main();
                    setFormData(bean);
                    if (loadingDialog != null) {
                        loadingDialog.cancel();
                    }
                    break;
                case 2:
                    ToastUtil.show(msg.obj.toString(), getContext());
                    break;
                case 3:
                    ToastUtil.show("未获取到该公文的详细信息！", getContext());
//                    getActivity().finish();
                    if (loadingDialog != null) {
                        loadingDialog.cancel();
                    }
                    break;
            }
        }
    };


    private void setFormData(final JuNeiChuanWenBean bean) {
        if (bean.getWenjianmingcheng() != null) {
            wenjianmingcheng.setText(Html.fromHtml(bean.getWenjianmingcheng()));
        }
        chengbanren.setText(bean.getChengbanren());
        qicaoriqi.setText(bean.getShouwenriqi());
        lianxidianhua.setText(bean.getLianxidianhua());
        hao.setText(bean.getChushi_zi() + "   " + bean.getHao());
        nian.setText(bean.getNian());
        zhubanchushi.setText(bean.getZhubanchushi());
        ((ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setText(bean.getZhubanchushi());

        initAttachment(bean.getAttachment(), clAttachment);
        xiebanchushi.setText(bean.getXiebanchushi());

        if (bean.getDocumentcb() != null) {
            chengbaoneirong.setVisibility(View.VISIBLE);
            chengbaoneirong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                    String type=bean.getDocumentcb().getDOCUMENTFILENAME().substring(bean.getDocumentcb().getDOCUMENTFILENAME().lastIndexOf("."+1));
                    down(bean.getDocumentcb().getUrl(), bean.getDocumentcb().getName()
                            + ".doc", true);
                    setDocumentBean(bean.getDocumentcb());
                }
            });
            chengbaoneirong.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20102);
                    } else {
                        upLoadDocument(bean.getDocumentcb(),
                                FileUtils.getInstance().makeDocumentDir().getPath() + File.separator + "cb正文.doc");

                    }
                    return true;
                }
            });
        } else {
            chengbaoneirong.setVisibility(View.INVISIBLE);
        }
    }

    private boolean viewDestroyed = false;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.zhubanchushi, R.id.gongwen_copy_juneichuanwen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zhubanchushi:
                String[] split = guid_main.split(",");
                guid_main = split[0];
                Intent intent = new Intent();
                intent.setClass(getActivity(), FormActivity.class);
                intent.putExtra("type", "局内传文");
                intent.putExtra("instanceguid", guid_main);
                intent.putExtra("actor", actor);
                intent.putExtra("from", "隐藏");
                startActivity(intent);
                break;
            case R.id.gongwen_copy_juneichuanwen:
                toWebIntent(guid);
                break;
        }
    }
}
