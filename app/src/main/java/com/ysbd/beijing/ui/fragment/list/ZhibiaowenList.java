package com.ysbd.beijing.ui.fragment.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ysbd.beijing.R;
import com.ysbd.beijing.recyclerView.OnBindView;
import com.ysbd.beijing.recyclerView.OnViewClickListener;
import com.ysbd.beijing.recyclerView.RecyclerViewAdapter;
import com.ysbd.beijing.ui.activity.FormActivity;
import com.ysbd.beijing.ui.bean.TodoBean;
import com.ysbd.beijing.utils.CommentFormUtils;
import com.ysbd.beijing.utils.DateFormatUtil;
import com.ysbd.beijing.utils.WebServiceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by lcjing on 2018/9/11.
 */

public class ZhibiaowenList extends BaseListFragment {

    @BindView(R.id.page)
    TextView tvPage;
    @BindView(R.id.count)
    TextView tvCount;
    Unbinder unbinder;
    @BindView(R.id.ll_null)
    LinearLayout llNull;
    private RecyclerView recyclerView;
    private List<TodoBean> list;
    private RecyclerViewAdapter adapter;
    private String type, id;
    private String actor;

    public static ZhibiaowenList getInstance(String type, String actor) {
        ZhibiaowenList fragment = new ZhibiaowenList();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putSerializable("actor", actor);
        fragment.setArguments(args);
        return fragment;
    }

    public void setActor(String actor) {
        list.clear();
        this.actor = actor;
        initView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("type");
        actor = getArguments().getString("actor");
        id = CommentFormUtils.getFormId(type);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_query_list, container, false);
        recyclerView = inflate.findViewById(R.id.recyclerView);
        unbinder = ButterKnife.bind(this, inflate);
        inflate.findViewById(R.id.zhibiaowen_bar).setVisibility(View.VISIBLE);
        initToDo1();
        initView();
        return inflate;
    }


    private void initToDo1() {
        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerViewAdapter(list, R.layout.zhibiaowen_query_item, new OnBindView() {
            @Override
            public void bindView(int position, Object data, View itemView, OnViewClickListener.OnChildViewClickListener viewClickListener) {
                TextView tvNum = itemView.findViewById(R.id.tv_num);
                TextView tvNian = itemView.findViewById(R.id.tv_nian);
                TextView tvHao = itemView.findViewById(R.id.tv_hao);
                TextView tvBiaoti = itemView.findViewById(R.id.tv_biaoti);
                TextView tvChushizi = itemView.findViewById(R.id.tv_chushizi);
                TextView tvChushihao = itemView.findViewById(R.id.tv_chushihao);
                TextView tvNigaoren = itemView.findViewById(R.id.tv_nigaoren);
                TextView tvUser = itemView.findViewById(R.id.tv_user);
                TextView tvYudeng = itemView.findViewById(R.id.tv_yudengwen_hao);
                TextView tvNigaoriqi = itemView.findViewById(R.id.tv_nigaoriqi);
                TextView tvXianbanriqi = itemView.findViewById(R.id.tv_xianbanriqi);
                LinearLayout root = itemView.findViewById(R.id.zhibiaowen_bar);
                tvNum.setText(list.get(position).getRN());
                tvHao.setText(list.get(position).getHAO());
                tvNian.setText(list.get(position).getNIAN());
                tvChushizi.setText(list.get(position).getCHUSHI_ZI());
                tvChushihao.setText(list.get(position).getHQZBW_CHUSHI_HAO());
//                tvChushihao.setText(list.get(position).getYIBANFAWEN_CHUSHI_HAO());
                tvNigaoren.setText(list.get(position).getNIGAO());
                tvNigaoriqi.setText(DateFormatUtil.subDate1(list.get(position).getNIGAORIQI()));
                tvXianbanriqi.setText(DateFormatUtil.subDate1(list.get(position).getXIANBANSHIJIAN()));
                tvBiaoti.setText(list.get(position).getBIAOTI());
                tvUser.setText(list.get(position).getDQNAME());
                tvYudeng.setText(list.get(position).getYDWWENHAO());
                if (position % 2 == 0) {
                    root.setBackgroundColor(0xffffffff);
                } else {
                    root.setBackgroundColor(0xeeeeeeee);
                }

                tvUser.setVisibility(View.GONE);
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(int position) {
                setLiveTime();
                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.putExtra("from","");

                intent.putExtra("type", type);
                intent.putExtra("actor", actor);
                intent.putExtra("instanceguid", list.get(position).getWORKFLOWINSTANCE_GUID());
                startActivity(intent);
            }
        });
    }

    private void initView() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String s = WebServiceUtils.getInstance().findTodoFiles(id, type, page, actor);
                List<TodoBean> todoBeans = new Gson().fromJson(s, new TypeToken<List<TodoBean>>() {
                }.getType());
                if (todoBeans != null) {
                    list.clear();
                    list.addAll(todoBeans);
                    handler.sendEmptyMessage(1);
                }else {
                    handler.sendEmptyMessage(2);
                }

            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (viewDestroyed) {
                return;
            }
            switch (msg.what) {
                case 1:
                    if (list.size()>0) {
                        llNull.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        llNull.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    tvPage.setText(page);
                    tvCount.setText(" ");
                    break;
                case 2:
                    if (list.size()>0) {
                        llNull.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        llNull.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
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

    String page = "1";

    @OnClick({R.id.last, R.id.next})
    public void onViewClicked(View view) {
        int p = Integer.parseInt(page);
        switch (view.getId()) {
            case R.id.last:
                if (p > 1) {
                    p--;
                    page = p + "";
                    initView();
                }
                break;
            case R.id.next:
                if (list.size() >= 10) {
                    p++;
                    page = p + "";
                    initView();
                }
                break;
        }
    }
}
