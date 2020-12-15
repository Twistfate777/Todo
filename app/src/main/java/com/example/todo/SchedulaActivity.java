package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SchedulaActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    List<TodoTask> list = new ArrayList<TodoTask>();
    List<TodoTask> allTask = new ArrayList<TodoTask>();
    TaskFragment fragment = TaskFragment.newInstance();
    TodoTaskDao dao;
    Button date_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedula);

        GlobleData gl = GlobleData.getInstance(this);
        dao = GlobleData.getDB().mTodoTaskDao();

        ImageButton back = findViewById(R.id.SencondBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = new ArrayList<TodoTask>();

        date_menu = findViewById(R.id.date_menu);
        date_menu.setText("全部");



        registerForContextMenu(date_menu);

        new Thread(new Runnable() {
            @Override
            public void run() {
                allTask = dao.getAllTask();
                for(int i=0;i<allTask.size();i++) list.add(allTask.get(i));
                fragment = TaskFragment.newInstance();
                fragment.tasks = list;
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,fragment,"sc").commit();
            }
        }).start();

    }

    void LoadList(int inflateDay)
    {
        final long currentTime = System.currentTimeMillis();
        list.clear();
        for(int i=0;i<allTask.size();i++)
        {
            TodoTask task = allTask.get(i);

            //如果选择，全部直接添加
            if(inflateDay == -1) list.add(task);

            //否则根据delta判断距今天数,选择性添加
            else if(task.noteDate > currentTime)
            {
                Log.d("sch", task.name);
                long zero = currentTime/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
                long delta = (task.noteDate - zero)/(1000*3600*24);



                //本周
                if(inflateDay == 7)
                {
                    if(delta <= 7)
                    {
                        list.add(task);
                    }
                }
                //稍后
                else if(inflateDay >7)
                {
                    list.add(task);
                }

                //任意delta天
                else if(inflateDay == delta)
                {
                    list.add(task);
                }
            }
        }

        fragment.adapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        View v = (View)buttonView;
        final TaskFragment.TaskAdapter.TaskHolder holder = (TaskFragment.TaskAdapter.TaskHolder)fragment.rv.getChildViewHolder((View)v.getParent().getParent());
        final TodoTask task = holder.task;
        switch (v.getId()) {
            case R.id.star:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        task.isStared = isChecked;
                        GlobleData.getDB().mTodoTaskDao().update(task);
                    }
                }).start();
                //adapter.notifyDataSetChanged();
                break;

            case R.id.check:
                if (isChecked) {
                    list.remove(task);
                    //adapter.notifyDataSetChanged();
                    Toast.makeText(this, "任务完成!", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            GlobleData.getDB().mTodoTaskDao().delete(task.id);
                        }
                    }).start();
                }
                break;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.date_menu,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        date_menu.setText(item.getTitle());
        list.clear();
        switch (item.getItemId())
        {
            case R.id.all:
                LoadList(-1);
                break;
            case R.id.today:
                LoadList(0);
                break;
            case R.id.afterday:
                LoadList(1);
                break;
            case R.id.this_week:
                LoadList(7);
                break;
            case R.id.late:
                LoadList(8);
                break;
        }
        fragment.adapter.notifyDataSetChanged();
        return true;
    }


        @Override
        protected void onStop () {
        super.onStop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size();i++){
                    dao.update(list.get(i));
                }
            }
        }).start();
    }

}