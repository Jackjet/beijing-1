package com.ysbd.beijing.ui.fragment.form;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.ysbd.beijing.ui.bean.form.ShizhuanwenBean;
import com.ysbd.beijing.utils.Constants;
import com.ysbd.beijing.utils.DateFormatUtil;
import com.ysbd.beijing.utils.FileUtils;
import com.ysbd.beijing.utils.ToastUtil;
import com.ysbd.beijing.utils.WebServiceUtils;
import com.ysbd.beijing.view.CommentLinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ShizhuanwenXiebanFragment extends BaseFormFragment {


    Unbinder unbinder;
    @BindView(R.id.shouwenriqi)
    TextView shouwenriqi;
    @BindView(R.id.nian)
    TextView nian;
    @BindView(R.id.hao)
    TextView hao;
    @BindView(R.id.bioaoti)
    TextView bioaoti;
    @BindView(R.id.yuanwenzihao)
    TextView yuanwenzihao;
    @BindView(R.id.zhubanchushi)
    TextView zhubanchushi;
    @BindView(R.id.xiebanchushi)
    TextView xiebanchushi;
    @BindView(R.id.xianbanriqi)
    TextView xianbanriqi;
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
    @BindView(R.id.gongwen_copy_shizhuanwen)
    TextView gongwenCopyShizhuanwen;
    @BindView(R.id.cl_attachment)
    CommentLinearLayout clAttachment;

    public static ShizhuanwenXiebanFragment getInstance(String guid, String actor) {
        ShizhuanwenXiebanFragment fragment = new ShizhuanwenXiebanFragment();
        Bundle args = new Bundle();
        args.putString("guid", guid);
        args.putString("actor", actor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shizhuanwen_xieban, null);
        unbinder = ButterKnife.bind(this, view);
        guid = getArguments().getString("guid");
        actor = getArguments().getString("actor");
        if (actor.equals("todo") || actor.equals("待办")) {//默认共公文拷贝隐藏,如果是待办状态,显示按钮
            gongwenCopyShizhuanwen.setVisibility(View.VISIBLE);
        }
        initComment();
        getData();

        return view;
    }

    String guid;
    String formName = "市转文_协办";


    private void initComment() {
        Map<String, CommentLinearLayout> layoutMap = new HashMap<>();
//        layoutMap.put("承办人意见", );
        layoutMap.put("局领导意见", clJulingdao);
        layoutMap.put("处长意见", clChuzhangpishi);
        layoutMap.put("其他人意见", clQitarenyijian);
        List<String> frames = new ArrayList<>();
        frames.add("局领导意见");
        frames.add("处长意见");
        frames.add("其他人意见");
        initData(layoutMap, frames, guid, formName);
    }

    private void getData() {
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
                    data = WebServiceUtils.getInstance().findToDoFileInfo(jsonData);
                } while (TextUtils.isEmpty(data) && count++ < 10);//如果没获取数据尝试多次获取直到获取数据或者获取次数到达10次
                try {
                    data = data.replace("<![CDATA[", "");
                    data = data.replace("]]>", "");
                    data = data.replace("&#13;&#10;", "");
                    data = data.replace("&#32;", " ");
                    ShizhuanwenBean banwenBean = new Gson().fromJson(data, ShizhuanwenBean.class);
                    mHandler.obtainMessage(1, banwenBean).sendToTarget();
                    if (getActivity() != null) {
                        if (banwenBean.getMenus() != null) {
                            ((FormActivity) getActivity()).addMenus(banwenBean.getMenus());
                        }
                        if (banwenBean.getActors() != null) {
                            ((FormActivity) getActivity()).setActors(banwenBean.getActors());
                        }
                    }
                    initCommentDate(banwenBean.getCurrentComment(), banwenBean.getComment());

                } catch (Exception e) {
                    App.catchE(e);
                }

            }
        }).start();
    }

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

    private void setFormData(final ShizhuanwenBean bean) {
        beizhu.setText(bean.getBeizhu());
        hao.setText(bean.getHao());
        lianxidianhua.setText(bean.getLianxidianhua());
        nian.setText(bean.getNian());
        shouwenriqi.setText(DateFormatUtil.subDate(bean.getShouwenriqi()));
        if (bean.getWenjianmingcheng() != null) {
            bioaoti.setText(Html.fromHtml(bean.getWenjianmingcheng()));
        }
        xianbanriqi.setText(DateFormatUtil.subDate(bean.getXianbanriqi()));
//        xiebanchushi.setText(bean.getXiebanchushi());
        initHuiqian2(bean.getWorkflow_main());
        yuanwenzihao.setText(bean.getYuanwenzihao());

        chushichengbanren.setText(bean.getChengbanren());


        initAttachment(bean.getAttachment(), clAttachment);

        if (bean.getDocumentcb() != null) {
            chengbaoneirong.setVisibility(View.VISIBLE);
            chengbaoneirong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                    String type=bean.getDocumentcb().getDOCUMENTFILENAME().substring(bean.getDocumentcb().getDOCUMENTFILENAME().lastIndexOf("."+1));
                    setDocumentBean(bean.getDocumentcb());
                    down(bean.getDocumentcb().getUrl(), bean.getDocumentcb().getName()
                            + ".doc", true);

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
            chengbaoneirong.setVisibility(View.GONE);
        }
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
                    try {
                        ShizhuanwenBean bean = (ShizhuanwenBean) msg.obj;
                        setFormData(bean);
                    } catch (Exception e) {
                        ToastUtil.show("表单数据异常!", getContext());
                        getActivity().finish();
                    }
                    break;
            }
        }
    };


    private boolean viewDestroyed = false;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        viewDestroyed = true;
    }

    @OnClick(R.id.gongwen_copy_shizhuanwen)
    public void onViewClicked() {
        toWebIntent(guid);
    }
}
