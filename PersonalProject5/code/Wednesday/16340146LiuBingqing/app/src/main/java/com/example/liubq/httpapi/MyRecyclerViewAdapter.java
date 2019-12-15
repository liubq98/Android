package com.example.liubq.httpapi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

public abstract class MyRecyclerViewAdapter<T> extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private int layoutId;
    private List<T> data;
    private OnItemClickListener onItemClickListener = null;

    public MyRecyclerViewAdapter(Context _context, int _layoutId, List<T> _data){
        context = _context;
        layoutId = _layoutId;
        data = _data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = MyViewHolder.get(context, parent, layoutId);
        return holder;
    }

    public abstract void convert(MyViewHolder holder, T t);

    public interface OnItemClickListener {
        void onClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        convert(holder, data.get(position)); // convert函数需要重写，下面会讲

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    public int getItemCount() {
        return data.size();
    }

    public List<T> getList() {
        return data;
    }

    public void remove(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public  T getItem(int i) {
        if (i >=0 && i < data.size())
            return data.get(i);
        return null;
    }

    //  添加数据
    public void addData(T temp, int position) {
//      在list中添加数据，并通知条目加入一条
        data.add(position, temp);
        //添加动画
        notifyItemInserted(position);
    }

    public void refresh(List<T> list_) {
        data = list_;
        notifyDataSetChanged();
    }
}
