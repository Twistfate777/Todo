package com.example.todo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TodoStepAdapter extends RecyclerView.Adapter<TodoStepAdapter.MyHolder> {

    private ArrayList<TodoStep> mList;//数据源
    View.OnClickListener listener;
    int layout;
    CompoundButton.OnCheckedChangeListener check;
    TodoStepAdapter(ArrayList<TodoStep> list, int _layout, View.OnClickListener _listener, CompoundButton.OnCheckedChangeListener _check) {
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

        String txt;
        int id;
        EditText et;
        public MyHolder(View itemView) {
            super(itemView);
            et = itemView.findViewById(R.id.Text);
            setIsRecyclable(false);
        }

        public void bind(TodoStep step)
        {
            txt = step.text;
            id = step.id;
            et.setText(txt);
        }

    }
}


