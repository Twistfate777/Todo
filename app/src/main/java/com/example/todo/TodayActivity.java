package com.example.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TodayActivity extends AppCompatActivity {
    public String TAG = "Today";
    public static List<TodoTask>today = new ArrayList<TodoTask>();
    TodoListDataBase listDataBase;
    static TodoTaskDao dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        //BACK键
        ImageButton back = findViewById(R.id.SencondBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobleData.TodoTaskUpdate(today);
                finish();
            }
        });

        TextView title = findViewById(R.id._title);
        title.setText(R.string.my_one_day);

        GlobleData.getInstance(this);
        LoadFromDB();
    }

    private void LoadFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDataBase = GlobleData.getDB();
                dao = listDataBase.mTodoTaskDao();
                TaskFragment taskFragment = new TaskFragment();
                today = dao.getAllToday();
                taskFragment.tasks = today;
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,taskFragment,TAG).commit();
                if(taskFragment.adapter != null)
                taskFragment.adapter.notifyDataSetChanged();
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<today.size();i++)
                    dao.update(today.get(i));
            }
        }).start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LoadFromDB();
    }
}