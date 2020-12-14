package com.example.todo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.MyHolder> {

    private List<TodoList> mList;//数据源
    private int layout;
    View.OnClickListener listener;
    MyRecycleViewAdapter(List<TodoList> list, int _layout, View.OnClickListener _listener) {
        mList = list;
        layout = _layout;
        listener = _listener;
    }

    //创建ViewHolder并返回，后续item布局里控件都是从ViewHolder中取出
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将我们自定义的item布局R.layout.item_one转换为View
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        MyHolder holder = new MyHolder(view);
        //返回这个MyHolder实体
        return holder;
    }

    //通过方法提供的ViewHolder，将数据绑定到ViewHolder中
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bind(position);
    }


    //获取数据源总的条数
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 自定义的ViewHolder
     */
    class MyHolder extends RecyclerView.ViewHolder {

        Button btn;
        int fid;
        String name;
        public MyHolder(View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id._1btn);
            btn.setOnClickListener(listener);

        }

        public void bind(int position)
        {
            fid = mList.get(position).id;
            name = mList.get(position).name;
            btn.setText(name);
        }

        public int getId()
        {
            return fid;
        }

        public String getName()
        {
            return name;
        }

    }
}


