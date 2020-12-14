package com.example.todo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodoTaskRV extends RecyclerView.Adapter<TodoTaskRV.MyHolder>  {

    private ArrayList<TodoTask> mList;//数据源
    OnClickListener listener;
    int layout;
    CompoundButton.OnCheckedChangeListener check;
    TodoTaskRV(ArrayList<TodoTask> list, int _layout, OnClickListener _listener, CompoundButton.OnCheckedChangeListener _check) {
        mList = list;
        layout = _layout;
        listener = _listener;
        check = _check;
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
        holder.bind(mList.get(position));
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

        CheckBox cb;
        CheckBox star;
        Button btn;

        TodoTask task;
        public MyHolder(View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id._1btn);
            cb = itemView.findViewById(R.id.check);
            star = itemView.findViewById(R.id.star);

            setIsRecyclable(false);
        }

        public void bind(TodoTask _task)
        {
            task = _task;
            cb.setChecked(task.isDone);
            star.setChecked(task.isStared);

            btn.setText(task.toString());
            if(task.isDone)
                btn.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线

            btn.setOnClickListener(listener);
            cb.setOnCheckedChangeListener(check);
            star.setOnCheckedChangeListener(check);
        }
    }
}


