package com.example.todo;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "MainActivity";
    static List<TodoList> todoLists = new ArrayList<TodoList>();
    public static TodoListDao dao;
    static RecyclerView.Adapter adapter;
    static RecyclerView recyclerView;

    @Override
    @SuppressWarnings("DEPRECATION")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadListFromDB();
        adapter = new MyRecycleViewAdapter(todoLists,R.layout.todo_list,this);
        recyclerView = findViewById(R.id.mainRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ListBuilder listBuilder = new ListBuilder();

        Button important = findViewById(R.id.important);
        important.setOnClickListener(this);

        Button today = findViewById(R.id.today);
        today.setOnClickListener(this);

        Button done = findViewById(R.id.garbage_btn);
        done.setOnClickListener(this);

        Button scehdual = findViewById(R.id.schedula);
        scehdual.setOnClickListener(this);


    }

    //read db,初始化清单
    private void LoadListFromDB()
    {
        GlobleData gl = GlobleData.getInstance(getApplicationContext());
        dao = gl.getDB().mTodoListDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO:按照index排序
                List<TodoList> t = dao.getAllInfo();
                for(int i=0;i<t.size();i++)
                {
                    TodoList l = t.get(i);
                    if(!todoLists.contains(l))
                    {
                        todoLists.add(l);
                    }
                }
                MainActivity.adapter.notifyDataSetChanged();
            }
        }).start();

    }

    //点击清单按钮 跳转事件.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();

        switch (id)
        {
            case R.id.schedula:
                intent.setClass(MainActivity.this,SchedulaActivity.class);
                startActivity(intent);
                break;
            case R.id.important:
                intent.setClass(MainActivity.this,ImportantActivity.class);
                startActivity(intent);
                break;
            case R.id.today:
                intent.setClass(MainActivity.this,TodayActivity.class);
                startActivity(intent);
                break;
            case R.id.garbage_btn:
                intent.setClass(MainActivity.this,DoneActivity.class);
                startActivity(intent);
                break;
            default:
                intent.setClass(MainActivity.this,SecondActivity.class);

                Button btn = (Button)v;
                String txt = btn.getText().toString();

                MyRecycleViewAdapter.MyHolder holder =(MyRecycleViewAdapter.MyHolder) recyclerView.getChildViewHolder((View) v.getParent());
                //传递TODO
                intent.putExtra("name",holder.getName());
                intent.putExtra("id",holder.getId());
                startActivityForResult(intent,1);
        }

    }

    //新建List 的确定按钮 事件
    class ListBuilder implements DialogInterface.OnClickListener {
        AlertDialog dialog;
        EditText input;
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final String text = input.getText().toString();
            updateDB(text);
            dialog.dismiss();
        }
        public ListBuilder() {
            input = new EditText(MainActivity.this);
            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
            input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO)
                    {
                        final String text = input.getText().toString();
                        updateDB(text);
                        dialog.dismiss();
                    }
                    Log.d(TAG,actionId + " ");
                    return false;
                }
            });

            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("新建清单")
                    .setIcon(R.mipmap.logo_icon)
                    .setView(input)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", this)
                    .create();

            final Button btn_add = findViewById(R.id.btn_add);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });
        }
    }

    //从db读数据 更新列表.
    void updateDB(final String text) {
        final TodoList list = new TodoList(text);
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.id = (int)dao.insert(list);
            }
        }).start();

        todoLists.add(list);
        adapter.notifyDataSetChanged();
    }

    //获取Task返回的结果，判断是否要删除/更改名称.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean delete = data.getBooleanExtra("delete",false);
        if(delete)
        {
            int fid = data.getIntExtra("fid",-1);
            for(int i=0;i<todoLists.size();i++)
            {
                TodoList l = todoLists.get(i);
                if(l.id == fid)
                {
                    todoLists.remove(l);
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }


}
