package com.ysbd.beijing.ui.fragment.form;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.ysbd.beijing.ui.bean.form.ZhuBanwenBean;
import com.ysbd.beijing.utils.DateFormatUtil;
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
 * Created by lcjing on 2018/8/8.
 */

public class ZhubanwenXiebanFragment extends BaseFormFragment {


    @BindView(R.id.shouwenriqi)
    TextView shouwenriqi;
    @BindView(R.id.nian)
    TextView nian;
    @BindView(R.id.hao)
    TextView hao;
    @BindView(R.id.yuanwenzihao)
    TextView yuanwenzihao;
    @BindView(R.id.xiebanchushi)
    TextView xiebanchushi;

    @BindView(R.id.cl_julingdao)
    CommentLinearLayout clJulingdao;
    @BindView(R.id.cl_chuzhangpishi)
    CommentLinearLayout clChuzhangpishi;
    @BindView(R.id.cl_qitarenyijian)
    CommentLinearLayout clQitarenyijian;
    @BindView(R.id.chengbaoneirong)
    LinearLayout chengbaoneirong;
    @BindView(R.id.chushichengbanren)
    TextView chushichengbanren;
    @BindView(R.id.lianxidianhua)
    TextView lianxidianhua;
    @BindView(R.id.beizhu)
    TextView beizhu;
    @BindView(R.id.gongwen_copy_zhubanwen)
    TextView gongwenCopyZhubanwen;
    @BindView(R.id.cl_attachment)
    CommentLinearLayout clAttachment;
    @BindView(R.id.zhubanchushi)
    TextView zhubanchushi;
    @BindView(R.id.wenjianmingcheng)
    TextView wenjianmingcheng;
    @BindView(R.id.xianbanshijian)
    TextView xianbanshijian;
    private LoadingDialog loadingDialog;

    Unbinder unbinder;

    public static ZhubanwenXiebanFragment getInstance(String jsonData, String actor) {
        ZhubanwenXiebanFragment fragment = new ZhubanwenXiebanFragment();
        Bundle args = new Bundle();
        args.putString("jsonData", jsonData);
        args.putString("actor", actor);
        fragment.setArguments(args);
        return fragment;
    }

    public ZhubanwenXiebanFragment() {
    }

    String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String jsonData = getArguments().getString("jsonData");
        actor = getArguments().getString("actor");
        View view = inflater.inflate(R.layout.fragment_zhubanwen_xieban, null);
        unbinder = ButterKnife.bind(this, view);
        FormActivity.FileIdBean bean = new Gson().fromJson(jsonData, FormActivity.FileIdBean.class);
        id = bean.getInstanceguid();
        if (actor.equals("todo") || actor.equals("待办")) {//默认共公文拷贝隐藏,如果是待办状态,显示按钮
            gongwenCopyZhubanwen.setVisibility(View.VISIBLE);
        }
        initComment();
        getData();
        return view;
    }

    private void getData() {
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getChildFragmentManager());
        final String jsonData = getArguments().getString("jsonData");
        new Thread(new Runnable() {
            @Override
            public void run() {
//                WebServiceUtils.getInstance().findToDoFileInfo();
                String data;
                int count = 0;
                do {
                    data = WebServiceUtils.getInstance().findToDoFileInfo(jsonData);/////////流程测试
                } while (TextUtils.isEmpty(data) && count++ < 10);//如果没获取数据尝试多次获取直到获取数据或者获取次数到达10次
                data = data.replace("<![CDATA[", "");
                data = data.replace("]]>", "");
                data = data.replace("&#13;&#10;", "");
                data = data.replace("&#32;", " ");
                mHandler.obtainMessage(1, data).sendToTarget();
                ZhuBanwenBean banwenBean = new Gson().fromJson(data, ZhuBanwenBean.class);
                try {
                    if (banwenBean.getMenus() != null && getActivity() != null) {
                        ((FormActivity) getActivity()).addMenus(banwenBean.getMenus());
                    }
                    if (banwenBean.getActors() != null && getActivity() != null) {
                        ((FormActivity) getActivity()).setActors(banwenBean.getActors());
                    }
                    initCommentDate(banwenBean.getCurrentComment(), banwenBean.getComment());

                } catch (Exception e) {
                    App.catchE(e);
                }
            }
        }).start();
    }

    private String formName = "主办文_协办";

    private void initComment() {
        Map<String, CommentLinearLayout> layoutMap = new HashMap<>();

        layoutMap.put("局领导意见", clJulingdao);
        layoutMap.put("处长批示", clChuzhangpishi);
        layoutMap.put("其他人意见", clQitarenyijian);
        List<String> frames = new ArrayList<>();
        frames.add("局领导意见");
        frames.add("处长批示");
        frames.add("其他人意见");
        initData(layoutMap, frames, id, formName);
    }

    private String guid_main;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (viewDestroyed) {
                return;
            }
            switch (msg.what) {
                case 1:
                    String data = msg.obj.toString();
                    if (data.length() > 4) {
                        ZhuBanwenBean banwenBean = new Gson().fromJson(data, ZhuBanwenBean.class);
                        guid_main = banwenBean.getWorkflow_main();
                        setData(banwenBean);
                    }
                    if (loadingDialog != null) {
                        loadingDialog.cancel();
                    }
                    break;
                case 2:
                    ToastUtil.show(msg.obj.toString(), getContext());
                    break;
            }


        }
    };


    private void initHuiqian2(String main) {
        StringBuffer sb = new StringBuffer();
        String[] s = main.split(";");
        if (s.length > 0) {
            for (int i = 0; i < s.length; i++) {
                String[] ss = s[i].split(",");
                if (ss.length == 2) {
                    sb.append(ss[1]).append("\n");
                }
            }

        }
        zhubanchushi.setText(sb.toString());
    }


    public void setData(final ZhuBanwenBean zhuBanwenBean) {
        shouwenriqi.setText(DateFormatUtil.subDate(zhuBanwenBean.getRecievedate()));
        nian.setText(zhuBanwenBean.getNian());
        hao.setText(zhuBanwenBean.getHao());
        wenjianmingcheng.setText(zhuBanwenBean.getWenjianmingcheng());
        yuanwenzihao.setText(zhuBanwenBean.getYuanwenzihao());
        zhubanchushi.setText(zhuBanwenBean.getZhubanchushi());
        initHuiqian2(zhuBanwenBean.getWorkflow_main());
        xiebanchushi.setText(zhuBanwenBean.getXiebanchushi());
        chushichengbanren.setText(zhuBanwenBean.getChengbaoren());//
        lianxidianhua.setText(zhuBanwenBean.getLianxidianhua());
        xianbanshijian.setText(DateFormatUtil.subDate(zhuBanwenBean.getExpiredate_xb()));

        beizhu.setText(zhuBanwenBean.getBeizhu());
        initAttachment(zhuBanwenBean.getAttachment(), clAttachment);
        if (zhuBanwenBean.getDocumentcb() != null) {
            chengbaoneirong.setVisibility(View.VISIBLE);
            chengbaoneirong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDocumentBean(zhuBanwenBean.getDocumentcb());
                    down(zhuBanwenBean.getDocumentcb().getUrl(), zhuBanwenBean.getDocumentcb().getName()
                            + ".doc", true);

                }
            });
            chengbaoneirong.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20102);
                    } else {
                        upLoadDocument(zhuBanwenBean.getDocumentcb(),
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
        viewDestroyed = true;
    }


    @OnClick({R.id.gongwen_copy_zhubanwen, R.id.zhubanchushi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zhubanchushi:
                String[] split = guid_main.split(",");
                guid_main = split[0];
                Intent intent = new Intent();
                intent.setClass(getActivity(), FormActivity.class);
                intent.putExtra("type", "主办文");
                intent.putExtra("instanceguid", guid_main);
                intent.putExtra("actor", actor);
                intent.putExtra("from", "隐藏");
                startActivity(intent);
                break;
            case R.id.gongwen_copy_zhubanwen:
                toWebIntent(id);
                break;

        }

    }
}
