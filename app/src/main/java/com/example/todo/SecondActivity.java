package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;

import android.os.Bundle;
import android.util.Log;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;

public class SecondActivity extends AppCompatActivity {
    String TAG = "SecondActivity";

    static String listName;
    static int fid;
    static List<TodoTask> taskNames = new ArrayList<TodoTask>();
    TaskFragment taskFragment = new TaskFragment();

    static TodoTaskDao dao;
    static TodoDeadLineDao dDao;
    static TodoListDao listDao;
    static AlertDialog dialog;

    static AlertDialog ddDialog;

    private AlarmManagerUtils alarmManagerUtils;  //提醒服务
    CheckBox btn_setdd;

    private AlertDialog bgChosen;
    private int bg;
    private AlertDialog rename_dialog;

    TaskBuilder taskBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        GlobleData gd = GlobleData.getInstance(this);
        TodoListDataBase listDataBase = GlobleData.getDB();
        dao = listDataBase.mTodoTaskDao();
        dDao = listDataBase.mTodoDeadLineDao();
        listDao = listDataBase.mTodoListDao();
        alarmManagerUtils = AlarmManagerUtils.getInstance(SecondActivity.this);
        final ImageButton setting = findViewById(R.id.setting);
        registerForContextMenu(setting);

        //设置标题
        Intent intent = getIntent();
        listName = intent.getStringExtra("name");
        fid = intent.getIntExtra("id",-1);
        TextView title = findViewById(R.id._title);
        title.setText(listName);

        //BACK键
        ImageButton back = findViewById(R.id.SencondBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("delete",false);
                setResult(1,intent);
                finish();
            }
        });
        taskBuilder = new TaskBuilder();
        initialBGinitialBGChosen();
        LoadTask();
    }

    private void initialBGinitialBGChosen()
    {
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.bg_chosen,null);

        customizeDialog.setView(dialogView);
        bgChosen = customizeDialog.create();

        dialogView.findViewById(R.id.bg1).setOnClickListener(new BGChosen());
        dialogView.findViewById(R.id.bg2).setOnClickListener(new BGChosen());
        dialogView.findViewById(R.id.bg3).setOnClickListener(new BGChosen());
        dialogView.findViewById(R.id.bg4).setOnClickListener(new BGChosen());
        dialogView.findViewById(R.id.bg5).setOnClickListener(new BGChosen());
    }

    private void ChosenBG()
    {
        bgChosen.show();
    }

    class BGChosen implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            findViewById(R.id.SecondMainLayout).setBackground(v.getBackground());
            final int id = v.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listDao.setBG(fid,id);
                }
            }).start();
            bgChosen.dismiss();
        }
    }


    //新建Task
    class TaskBuilder implements View.OnClickListener
    {
        EditText input;
        DatePicker datePicker;
        public TaskBuilder()
        {
            ImageButton add = findViewById(R.id.create);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });

            input = new EditText(SecondActivity.this);

            datePicker = new DatePicker();

            final AlertDialog.Builder customizeDialog =
                    new AlertDialog.Builder(SecondActivity.this);
            final View dialogView = LayoutInflater.from(SecondActivity.this)
                    .inflate(R.layout.input_task,null);
            Button confirm = dialogView.findViewById(R.id.cfbtn);

            input = dialogView.findViewById(R.id._1btn);
            customizeDialog.setView(dialogView);
            customizeDialog.setTitle("请输入任务名称");
            dialog = customizeDialog.create();
            confirm.setOnClickListener(this);

            btn_setdd = dialogView.findViewById(R.id.setdd);
            btn_setdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        SecondActivity.ddDialog.show();
                    }
                    else
                    {
                        resetBtn();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            final String text = input.getText().toString();
            final TodoTask task = new TodoTask(text,fid);
            taskNames.add(task);
            taskFragment.adapter.notifyItemChanged(taskNames.size());
            task.deadLineID = -1;
            if(btn_setdd.isChecked())
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.widget.DatePicker dp = datePicker.dp;
                        TimePicker tp = datePicker.tp;
                        TodoDeadLine td = new TodoDeadLine(dp.getYear(),dp.getMonth(),dp.getDayOfMonth(),tp.getHour(),tp.getMinute());
                        task.deadLineID = (int)dDao.insert(td);
                        final long ms_per_day = 1000*3600*24;
                        long ms = datePicker.ms;
                        task.noteDate = ms;
                        long systemTime = System.currentTimeMillis();
                        long zero = systemTime/(ms_per_day)*(ms_per_day) - TimeZone.getDefault().getRawOffset();//当天时间的00:00
                        if(task.noteDate > zero)
                        {
                            long delta = (ms-zero)/(ms_per_day);//输入时间据app开启时间的天数

                            Log.d("dateTest", String.format("sys:%s noteDate:%s delta:%s",systemTime,ms,delta));
                            if(delta == 0)
                            {
                                Log.d("dateTest", "今天");
                            }
                            else if(delta == 1)
                            {
                                Log.d("dateTest", "明天");
                            }
                            else if(delta <7)
                            {
                                Log.d("dateTest", "本周");
                            }
                        }
                        task.id = (int)dao.insert(task);
                        PendingIntent pendingIntent= alarmManagerUtils.createPendingIntent(task.id,task.fid,task.deadLineID);
                        //pendingIntent也是intent的一种，不过是延迟执行
                        alarmManagerUtils.StartPIWork(pendingIntent,dp.getYear(),dp.getMonth(),dp.getDayOfMonth(),tp.getHour(),tp.getMinute());
                    }
                }).start();
            }
            else
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        task.id =(int)dao.insert(task);
                    }
                }).start();
            }
            resetBtn();
            input.setText("");
            dialog.dismiss();
        }

        void resetBtn()
        {
            btn_setdd.setChecked(false);
            btn_setdd.setText("提醒我");
        }

        class DatePicker
        {
            android.widget.DatePicker dp;
            TimePicker tp;
            long ms;
            public DatePicker()
            {
                final AlertDialog.Builder customizeDialog =
                        new AlertDialog.Builder(SecondActivity.this);
                final View dialogView = LayoutInflater.from(SecondActivity.this)
                        .inflate(R.layout.tp_and_dp,null);

                dp = dialogView.findViewById(R.id.dp);
                tp = dialogView.findViewById(R.id.tp);

                customizeDialog.setView(dialogView);
                customizeDialog.setNegativeButton("取消", null);
                customizeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int dY = dp.getYear();
                        int dM = dp.getMonth();
                        int dD = dp.getDayOfMonth();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(dY, dM, dD,tp.getHour(),tp.getMinute(),0);
                        ms = calendar.getTimeInMillis();
                        btn_setdd.setText(String.format("%d年%d月%d日 到期",dY,dM+1,dD));
                    }
                });
                ddDialog = customizeDialog.create();
            }
        }
    }

    private void LoadTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                taskNames = dao.getAllInfo(fid);

                taskFragment.tasks = taskNames;
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,taskFragment,TAG).commit();

                final View _layout = LayoutInflater.from(SecondActivity.this)
                        .inflate(R.layout.bg_chosen,null);

                try {
                    bg = listDao.getBG(fid);

                    findViewById(R.id.SecondMainLayout).setBackground(_layout.findViewById(bg).getBackground());
                }
                catch (Exception e)
                {

                }

            }
        }).start();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.second_option_menu,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changeBG :
                ChosenBG();
                return true;
            case R.id.sortByLen:
                taskNames.sort(new Comparator<TodoTask>() {
                    @Override
                    public int compare(TodoTask o1, TodoTask o2) {
                        return o1.name.length() - o2.name.length();
                    }
                });
                return true;
            case R.id.sortByStart:
                taskNames.sort(new Comparator<TodoTask>() {
                    @Override
                    public int compare(TodoTask o1, TodoTask o2) {
                        return o1.name.charAt(0) - o2.name.charAt(0);
                    }
                });
                taskFragment.adapter.notifyDataSetChanged();
                return true;
            case R.id.deleteList:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<taskNames.size();i++)
                        {
                            TodoTask t = taskNames.get(i);
                            if(t.deadLineID > -1)
                                dDao.delete(t.deadLineID);
                        }
                        taskNames.clear();
                        dao.deleteAll(fid);
                        listDao.delete(fid);
                        Intent intent = new Intent();
                        intent.putExtra("delete",true);
                        intent.putExtra("fid",fid);
                        setResult(1,intent);
                        finish();
                    }
                }).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<taskNames.size();i++)
                    dao.update(taskNames.get(i));
            }
        }).start();
    }

}