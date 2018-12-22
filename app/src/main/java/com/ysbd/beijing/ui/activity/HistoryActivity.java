package com.ysbd.beijing.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ysbd.beijing.R;
import com.ysbd.beijing.bean.ActorsBean;
import com.ysbd.beijing.bean.HistoryBean;
import com.ysbd.beijing.bean.RECIPIENTSACTORSBean;
import com.ysbd.beijing.recyclerView.OnBindView;
import com.ysbd.beijing.recyclerView.OnViewClickListener;
import com.ysbd.beijing.recyclerView.RecyclerViewAdapter;
import com.ysbd.beijing.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryActivity extends AppCompatActivity {
    private List<ActorsBean> actorsBeans;
    private List<HistoryBean> historyBeans;
    private RecyclerView rvHistory;
    private RecyclerViewAdapter adapter;
    private RecyclerView bingxing;
    private RecyclerViewAdapter adapter1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        rvHistory = findViewById(R.id.rv_history);
        actorsBeans = (List<ActorsBean>) getIntent().getSerializableExtra("actors");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvHistory.setLayoutManager(linearLayoutManager);
        historyBeans = new ArrayList<>();
        adapter = new RecyclerViewAdapter(historyBeans, R.layout.item_history, new OnBindView() {
            @Override
            public void bindView(int position, Object data, View itemView, OnViewClickListener.OnChildViewClickListener viewClickListener) {
                TextView tvNum = itemView.findViewById(R.id.tv_num);
                TextView tvSender = itemView.findViewById(R.id.tv_sender);
                TextView tvTime = itemView.findViewById(R.id.tv_time);
                TextView tvReceiver = itemView.findViewById(R.id.tv_receiver);
                bingxing = itemView.findViewById(R.id.recyclerView_bingxingyueban);
                tvNum.setText(historyBeans.get(position).getStep());
                tvSender.setText(historyBeans.get(position).getSender());
                tvTime.setText(historyBeans.get(position).getSendTime());
                if (historyBeans.get(position).getReceiver().equals("办结")){
                    tvReceiver.setVisibility(View.GONE);
                    bingxing.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(HistoryActivity.this);
                    bingxing.setLayoutManager(linearLayoutManager1);
                    final List<RECIPIENTSACTORSBean> recipientsactorsBeanList= actorsBeans.get(0).getRecipientsactorsBeans();
                    adapter1 = new RecyclerViewAdapter(recipientsactorsBeanList, R.layout.item_bingxingyueban, new OnBindView() {
                        @Override
                        public void bindView(int position, Object data, View itemView, OnViewClickListener.OnChildViewClickListener viewClickListener) {
                            TextView person = itemView.findViewById(R.id.person);
                            TextView  state= itemView.findViewById(R.id.state);
                            TextView date = itemView.findViewById(R.id.date);
                            person.setText(recipientsactorsBeanList.get(position).getRECIPIENTSACTORSINFO());
                            String status = null;
                            switch (recipientsactorsBeanList.get(position).getRECIPIENTSACTORSVALUE().getHandelStatus()) {
                                case 1:
                                    status="待办";
                                    break;
                                case 2:
                                    status="已阅";
                                    break;
                                case 3:
                                    status="暂存";
                                    break;
                                case 4:
                                    status="办结";
                                    break;
                            }
                            state.setText(status);

                        }
                    });
                    bingxing.setAdapter(adapter1);
                }else {
                    tvReceiver.setVisibility(View.VISIBLE);
                    bingxing.setVisibility(View.GONE);
                    tvReceiver.setText(historyBeans.get(position).getReceiver());
                }

            }
        });
        rvHistory.setAdapter(adapter);



        for (int i = actorsBeans.size()-1; i >0; i--) {
            HistoryBean historyBean = new HistoryBean();
            String senderId;
            String sendName;
            if (actorsBeans.get(i).getInfoBean().getProecssActor()!=null){
                senderId = actorsBeans.get(i).getInfoBean().getProecssActor().getPersonGUID();
                sendName = DBUtils.getPersonNameById(senderId);
                sendName=sendName+"(" + actorsBeans.get(i).getInfoBean().getDepartName() + ")";
            }else {
                continue;
            }
            historyBean.setSender(sendName);
            String receiverId;
            String receiverName;
            if (actorsBeans.get(i-1).getInfoBean().getProecssActor()!=null){
                receiverId = actorsBeans.get(i-1).getInfoBean().getProecssActor().getPersonGUID();
                int count = 0;
                do {
                    receiverName = DBUtils.getPersonNameById(receiverId);
                } while (TextUtils.isEmpty(receiverName) && count++ < 5);//如果没获取数据尝试多次获取直到获取数据或者获取次数到达10次

                receiverName=receiverName + "(" + actorsBeans.get(i-1).getInfoBean().getDepartName() + ")";

            }else if(i==1){

               receiverName="";
//                View view= LayoutInflater.from(this).inflate( R.layout.item_history,null);
//                bingxing=view.findViewById(R.id.recyclerView_bingxingyueban);
//
//                adapter1.notifyDataSetChanged();

            }else {

                receiverId = actorsBeans.get(i-2).getInfoBean().getProecssActor().getPersonGUID();
                int count = 0;
                do {
                    receiverName = DBUtils.getPersonNameById(receiverId);
                } while (TextUtils.isEmpty(receiverName) && count++ < 5);
                receiverName=receiverName + "(" + actorsBeans.get(i-2).getInfoBean().getDepartName() + ")";
            }
            if (i==1) {
                String status="";
                switch (actorsBeans.get(1).getInfoBean().getProecssActor().getHandelStatus()) {
                    case 1:
                        status="待办";
                        break;
                    case 2:
                        status="已阅";
                        break;
                    case 3:
                        status="暂存";
                        break;
                    case 4:
                        status="办结";
                        break;
                }
                receiverName=receiverName+status;
            }
            historyBean.setReceiver(receiverName);
            historyBean.setStep(actorsBeans.get(i).getInfoBean().getCurrentStep() + "");
            historyBean.setSendTime(actorsBeans.get(i).getInfoBean().getUpdateDateString());
            historyBeans.add(0, historyBean);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.fl_back)
    public void onViewClicked() {
        finish();
    }
}
