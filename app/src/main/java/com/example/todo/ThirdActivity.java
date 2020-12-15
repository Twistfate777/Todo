package com.example.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    private static String TAG = "ThirdActivity";
    Button dd;
    TodoTaskDao dao;
    TodoDeadLineDao ddao;
    TodoStepDao sDao;

    TimePicker tp;
    DatePicker dp;
    AlertDialog dialog;

    ArrayList<TodoStep> todoSteps;
    static RecyclerView.Adapter adapter;
    static RecyclerView list;
    private TodoTask task;
    private EditText editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        final Intent intent = getIntent();
        task = (TodoTask) intent.getSerializableExtra("task");

        GlobleData gd = GlobleData.getInstance(this);
        TodoListDataBase listDataBase = GlobleData.getDB();
        dao = listDataBase.mTodoTaskDao();
        ddao = listDataBase.mTodoDeadLineDao();
        sDao = listDataBase.mTodoStepDao();


        //数据适配
        todoSteps = new ArrayList<TodoStep>();
        adapter = new TodoStepAdapter(todoSteps,R.layout.todo_step,null,null);
        list = findViewById(R.id.rv);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        //绑定back键
        ImageButton btn_back = findViewById(R.id.ThirdBack);
        final Intent backIntent = new Intent();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dao.update(task);
                        backIntent.putExtra("taskID",task.id);
                        backIntent.putExtra("task",task);
                        setResult(1,backIntent);
                        finish();
                    }
                }).start();
            }
        });

        //设置标题
        final TextView title = findViewById(R.id.listname);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String listName = GlobleData.getDB().mTodoListDao().getListName(task.fid);
                title.setText(listName);
            }
        }).start();

        //设置任务名次
        final EditText taskEditor = findViewById(R.id.taskname);
        taskEditor.setText(task.name);
        taskEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        //重命名Task名字
        taskEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    String txt = taskEditor.getText().toString();
                    task.name = txt;
                    backIntent.putExtra("newTaskName",task.name);
                }
                return false;
            }
        });


        //设置"提醒我"初始文本
        dd = findViewById(R.id.dd);
        initialDpAndTp();
        dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

            }
        });
        //再此输入下一步
        initialInputNextStepBtn();
        //设置添加到我的一天
        Button btn_add2Today = findViewById(R.id.add_to_today);
        btn_add2Today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        task.isToday = true;
                        dao.update(task);
                    }
                }).start();
                Toast.makeText(ThirdActivity.this,"添加成功!",Toast.LENGTH_SHORT).show();
            }
        });

        //检查数据库内是否有数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(task.deadLineID != -1)
                {
                    TodoDeadLine td = ddao.searchDeadLine(task.deadLineID);
                    dd.setText(String.format("%d年%d月%d日 到期",td.year,td.month+1,td.date));
                }
                else
                {
                    dd.setText("设置提醒时间");
                }
            }
        }).start();

        ImageButton delete = findViewById(R.id.deleteTask);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dao.delete(task.id);
                        ddao.delete(task.deadLineID);
                        Intent intent = new Intent();
                        intent.putExtra("delete",true);
                        intent.putExtra("taskID",task.id);
                        setResult(1,intent);
                        finish();
                    }
                }).start();
            }
        });

        LoadStepsFromDB();

    }

    //从数据库读取 step并添加到数据适配器内
    private void LoadStepsFromDB() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<TodoStep> stepList = sDao.getAllSteps(task.id);
                if(stepList.size() > 0) todoSteps.addAll(stepList);
                task = dao.getTask(task.name, task.fid);
                initialComment();
                adapter.notifyDataSetChanged();
            }
        });
        t.start();

    }

    //触碰时隐藏小键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }

    //初始化备注按钮
    private void initialComment()
    {
        editor = findViewById(R.id.coment);

        if(task != null && task.comment == null)
            editor.setText("在此添加备注");
        else
            editor.setText(task.comment);

        CardView card = findViewById(R.id.cardview);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThirdActivity.this,CommentActivity.class);
                intent.putExtra("text",editor.getText().toString());
                startActivityForResult(intent,1);
            }
        });

        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThirdActivity.this,CommentActivity.class);
                intent.putExtra("text",editor.getText().toString());
                startActivityForResult(intent,1);
            }
        });


    }

    //初始化"在此输入下一步"按钮
    private void initialInputNextStepBtn() {
        final EditText stepInput = findViewById(R.id.nextStep);
        stepInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    stepInput.setText("");
                }
                else
                {
                    stepInput.setText("在此输入下一步");
                }
            }
        });

        stepInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_NULL || actionId ==EditorInfo.IME_ACTION_NEXT)
                {
                    final String txt = stepInput.getText().toString();
                    stepInput.clearFocus();
                    final int id;
                    final TodoStep step = new TodoStep(task.id,txt);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            step.id =(int)sDao.insert(step);
                        }
                    }).start();
                    todoSteps.add(step);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    void resetDD()
    {
        dd.setText("设置提醒时间");
    }

    //获取备注返回的文本
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            final String text = data.getStringExtra("text").toString();
            editor.setText(text);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    task.comment = text;
                }
            }).start();
        }
    }

    //初始化时间选择器
    private void initialDpAndTp() {
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.tp_and_dp,null);

        dp = dialogView.findViewById(R.id.dp);
        tp = dialogView.findViewById(R.id.tp);

        customizeDialog.setView(dialogView);
        customizeDialog.setNegativeButton("取消", null);
        customizeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int y = dp.getYear();
                final int m = dp.getMonth();
                final int d = dp.getDayOfMonth();
                final int h = tp.getHour();
                final int minut = tp.getMinute();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //新建dd
                        if(task.deadLineID == -1) {
                            task.deadLineID = (int) ddao.insert(new TodoDeadLine(y, m, d, h, minut));
                            dao.updateDD(task.id,task.deadLineID);
                        }
                        else
                        {
                            //TODO (取消之前的闹钟)
                            ddao.update(task.deadLineID,y,m,d,h,minut);
                        }

                        AlarmManagerUtils am = AlarmManagerUtils.getInstance(ThirdActivity.this);
                        PendingIntent pi =  am.createPendingIntent(task.id,task.fid,task.deadLineID);
                        am.StartPIWork(pi,y,m,d,h,minut);
                    }
                }).start();
                dd.setText(String.format("%d年%d月%d日 到期",y,m+1,d));
            }
        });
        dialog = customizeDialog.create();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao.update(task);
            }
        }).start();
    }
}