package com.ysbd.beijing.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ysbd.beijing.R;
import com.ysbd.beijing.bean.XiebanBean;

import java.util.List;

public class XieBan extends RecyclerView.Adapter<XieBan.ViewHolder> {

    private List<XiebanBean> xieban;

    private setOnItemClickListener mListener;

    public interface setOnItemClickListener{
        void OnItemClickListener(int pos);
    }
    public void setOnItemClickListener(setOnItemClickListener mListener){
        this.mListener=mListener;
    }

    public XieBan(List<XiebanBean> xieban) {
        this.xieban = xieban;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.xieban_yijian);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xieban, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String xiebanChushi=xieban.get(position).getChushiName();
        holder.textView.setText(xiebanChushi);
        if(mListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnItemClickListener(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return xieban.size();
    }


}
