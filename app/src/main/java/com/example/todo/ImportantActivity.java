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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ImportantActivity extends AppCompatActivity {
    private static String TAG = "Important";
    public List<TodoTask>important = new ArrayList<TodoTask>();
    TodoListDataBase listDataBase;
    static TodoTaskDao dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important);

        important = new ArrayList<TodoTask>();

        //BACK键
        ImageButton back = findViewById(R.id.SencondBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobleData.TodoTaskUpdate(important);
                finish();
            }
        });


        GlobleData gl = GlobleData.getInstance(this);
        LoadFromDB();
    }

    private void LoadFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDataBase = GlobleData.getDB();
                dao = listDataBase.mTodoTaskDao();
                important = dao.getAllImportant();
                TaskFragment taskFragment = new TaskFragment();
                taskFragment.tasks = important;
                getSupportFragmentManager().beginTransaction().replace(R.id.fl,taskFragment,TAG).commit();
            }
        }).start();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LoadFromDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}