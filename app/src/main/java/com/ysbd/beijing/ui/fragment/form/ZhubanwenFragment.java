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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ysbd.beijing.App;
import com.ysbd.beijing.R;
import com.ysbd.beijing.bean.XiebanBean;
import com.ysbd.beijing.ui.activity.FormActivity;
import com.ysbd.beijing.ui.adapter.XieBan;
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

public class ZhubanwenFragment extends BaseFormFragment {

    @BindView(R.id.shouwenriqi)
    TextView shouwenriqi;
    @BindView(R.id.nian)
    TextView nian;
    @BindView(R.id.hao)
    TextView hao;
    @BindView(R.id.laiwendanwei)
    TextView laiwendanwei;
    @BindView(R.id.yuanwenzihao)
    TextView yuanwenzihao;
    @BindView(R.id.wenjianmingcheng)
    TextView biaoti;
    @BindView(R.id.zhubandanwei)
    TextView zhubandanwei;

    @BindView(R.id.xianbanriqi)
    TextView xianbanriqi;
    @BindView(R.id.miji)
    TextView miji;
    @BindView(R.id.chushichengbanren)
    TextView chushichengbanren;
    @BindView(R.id.lianxidianhua)
    TextView lianxidianhua;
    @BindView(R.id.baoguanqixian)
    TextView baoguanqixian;
    @BindView(R.id.mijiwen)
    TextView mijiwen;
    @BindView(R.id.guidangren)
    TextView guidangren;
    @BindView(R.id.guidangqiri)
    TextView guidangriqi;
    @BindView(R.id.jianyaoqingkuang)
    TextView jianyaoqingkuang;
    //    @BindView(R.id.chuzhangqianzi)
//    TextView chuzhangqianzi;
    @BindView(R.id.chengbaoneirong)
    View chengbaoneirong;
    Unbinder unbinder;
    @BindView(R.id.cl_julingdao)
    CommentLinearLayout clJulingdao;//局领导意见
    @BindView(R.id.cl_nibanyijian)
    CommentLinearLayout clNibanyijian;//拟办意见
    @BindView(R.id.cl_yusuanchuzhangyijian)
    CommentLinearLayout clYusuanchuzhangyijian;//预算处长意见
    @BindView(R.id.cl_yusuanchuyijian)
    CommentLinearLayout clYusuanchuyijian;//预算处意见
    @BindView(R.id.cl_chuzhangpishi)
    CommentLinearLayout clChuzhangpishi;//处长批示
    @BindView(R.id.cl_qitarenyijian)
    CommentLinearLayout clQitarenyijian;//其他人意见
    @BindView(R.id.cl_attachment)
    CommentLinearLayout clAttachment;//附件列表
    @BindView(R.id.zhongdianducha)
    TextView zhongdianducha;
    @BindView(R.id.huanji)
    TextView huanji;
    @BindView(R.id.beizhu)
    TextView beizhu;
    @BindView(R.id.cl_chuzhangqianzi)
    CommentLinearLayout clChuzhangqianzi;//处长签字
    @BindView(R.id.gongwen_copy_zhubanwen)
    TextView gongwenCopyZhubanwen;
    @BindView(R.id.xiebanchushi)
    RecyclerView xiebanchushi;

    //    private List<OpinionModel> julingdao;
//    private List<OpinionModel> niban;
//    private List<OpinionModel> yusuanchuzhang;
//    private List<OpinionModel> yusuanchu;
//    private List<OpinionModel> chuzhangpishi;
//    private List<OpinionModel> qitaren;
//    private CommentAdapter julingdaoAdapter;
//    private CommentAdapter nibanAdapter;
//    private CommentAdapter yusuanchuzhangAdapter;
//    private CommentAdapter yusuanchuAdapter;
//    private CommentAdapter chuzhangpishiAdapter;
//    private CommentAdapter qitarenAdapter;
    private LoadingDialog loadingDialog;

    public static ZhubanwenFragment getInstance(String jsonData, String actor) {
        ZhubanwenFragment fragment = new ZhubanwenFragment();
        Bundle args = new Bundle();
        args.putString("jsonData", jsonData);
        args.putString("actor", actor);
        fragment.setArguments(args);
        return fragment;
    }

    public ZhubanwenFragment() {
    }

    String id;
    XieBan xieBanAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String jsonData = getArguments().getString("jsonData");
        actor = getArguments().getString("actor");
        View view = inflater.inflate(R.layout.fragment_zhubanwen, null);
        unbinder = ButterKnife.bind(this, view);
        FormActivity.FileIdBean bean = new Gson().fromJson(jsonData, FormActivity.FileIdBean.class);
        id = bean.getInstanceguid();
        if (actor.equals("todo") || actor.equals("待办")) {//默认共公文拷贝隐藏,如果是待办状态,显示按钮
            gongwenCopyZhubanwen.setVisibility(View.VISIBLE);
        }
        initComment();
        getData();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        xiebanchushi.setLayoutManager(layoutManager);
        xieBanAdapter = new XieBan(xieban);
        xieBanAdapter.setOnItemClickListener(new XieBan.setOnItemClickListener() {
            @Override
            public void OnItemClickListener(int pos) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FormActivity.class);
                intent.putExtra("type", "主办文_协办");
                intent.putExtra("instanceguid", xieban.get(pos).getChushiGUID());
                intent.putExtra("actor", actor);
                intent.putExtra("from", "隐藏");
                startActivity(intent);
            }
        });
        xiebanchushi.setAdapter(xieBanAdapter);
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

    private String formName = "主办文";

    private void initComment() {
        Map<String, CommentLinearLayout> layoutMap = new HashMap<>();

        layoutMap.put("局领导意见", clJulingdao);
        layoutMap.put("拟办意见", clNibanyijian);
        layoutMap.put("预算处处长意见", clYusuanchuzhangyijian);
        layoutMap.put("预算处意见", clYusuanchuyijian);
        layoutMap.put("处长批示", clChuzhangpishi);
        layoutMap.put("处长签字", clChuzhangqianzi);
        layoutMap.put("其他人意见", clQitarenyijian);
        List<String> frames = new ArrayList<>();
        frames.add("局领导意见");
        frames.add("拟办意见");
        frames.add("预算处处长意见");
        frames.add("预算处意见");
        frames.add("处长批示");
        frames.add("处长签字");
        frames.add("其他人意见");
        initData(layoutMap, frames, id, formName);
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
                    String data = msg.obj.toString();
                    if (data.length() > 4) {
                        ZhuBanwenBean banwenBean = new Gson().fromJson(data, ZhuBanwenBean.class);
                        getXiebanGuid(banwenBean);
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

    List<XiebanBean> xieban = new ArrayList<>();
    Map<String, String> xiebanGUID = new HashMap<>();

    private void getXiebanGuid(ZhuBanwenBean bean) {
        if (bean.getWorkflow_sub() != null && !TextUtils.isEmpty(bean.getWorkflow_sub())) {
            String xieban_guid;
            xieban_guid = bean.getWorkflow_sub();
            String[] sub = xieban_guid.split(";");
            int num = sub.length;
            if (num > 1) {
                for (int i = 0; i < num; i++) {
                    String strings[] = sub[i].split(",");
                    xiebanGUID.put(strings[0], strings[1]);
                }
            } else {
                String strings[] = sub[0].split(",");
                xiebanGUID.put(strings[0], strings[1]);
            }
            for (String key : xiebanGUID.keySet()) {
                XiebanBean xiebanBean = new XiebanBean();
                xiebanBean.setChushiName(xiebanGUID.get(key));
                xiebanBean.setChushiGUID(key);
                xieban.add(xiebanBean);
            }
            xieBanAdapter.notifyDataSetChanged();
        }
    }


//    private void initHuiqian2(String sub) {
//        StringBuffer sb = new StringBuffer();
//        String[] s = sub.split(";");
//        if (s.length > 0) {
//            for (int i = 0; i < s.length; i++) {
//                String[] ss = s[i].split(",");
//                if (ss.length == 2) {
//                    sb.append(ss[1]).append("\n");
//                }
//            }
//
//        }
//        xiebanchushi.setText(sb.toString());
//    }


    public void setData(final ZhuBanwenBean zhuBanwenBean) {
        guidangren.setText(zhuBanwenBean.getXianbanriqi());//////////////////////////////////////傻逼**********
        shouwenriqi.setText(DateFormatUtil.subDate(zhuBanwenBean.getRecievedate()));
        nian.setText(zhuBanwenBean.getNian());
        hao.setText(zhuBanwenBean.getHao());
        laiwendanwei.setText(zhuBanwenBean.getLaiwendanwei());
        yuanwenzihao.setText(zhuBanwenBean.getYuanwenzihao());
//        biaoti.setText(zhuBanwenBean.get);
        zhubandanwei.setText(zhuBanwenBean.getZhubanchushi());
//        xiebanchushi.setText(zhuBanwenBean.getXiebanchushi());
//        initHuiqian2(zhuBanwenBean.getWorkflow_sub());
        xianbanriqi.setText(DateFormatUtil.subDate(zhuBanwenBean.getXianbandate()));

        miji.setText(zhuBanwenBean.getMiji());
        chushichengbanren.setText(zhuBanwenBean.getChengbaoren());//
        lianxidianhua.setText(zhuBanwenBean.getLianxidianhua());
        baoguanqixian.setText(zhuBanwenBean.getBaoguanqixian());
        mijiwen.setText(zhuBanwenBean.getMijiwen());
        guidangriqi.setText(zhuBanwenBean.getGuidangriqi());
        if (zhuBanwenBean.getJianyaoqingkuang() != null) {
            jianyaoqingkuang.setText(Html.fromHtml(zhuBanwenBean.getJianyaoqingkuang()));
        }

//        chuzhangqianzi.setText(zhuBanwenBean.getChuzhangyijian());
//        chengbaoneirong.setText(zhuBanwenBean.getChengbaoneirong());
//        biaoti.setText(zhuBanwenBean.getWenjianmingcheng());
        if (zhuBanwenBean.getWenjianmingcheng() != null) {
            biaoti.setText(Html.fromHtml(zhuBanwenBean.getWenjianmingcheng()));
        }
        zhongdianducha.setText(zhuBanwenBean.getZhongdianduban());
        huanji.setText(zhuBanwenBean.getHuanji());
        beizhu.setText(zhuBanwenBean.getBeizhu());
        initAttachment(zhuBanwenBean.getAttachment(), clAttachment);
        if (zhuBanwenBean.getDocumentcb() != null) {
            chengbaoneirong.setVisibility(View.VISIBLE);
            chengbaoneirong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                    String type=bean.getDocumentcb().getDOCUMENTFILENAME().substring(bean.getDocumentcb().getDOCUMENTFILENAME().lastIndexOf("."+1));
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


    @OnClick(R.id.gongwen_copy_zhubanwen)
    public void onViewClicked() {
        toWebIntent(id);
    }
}
