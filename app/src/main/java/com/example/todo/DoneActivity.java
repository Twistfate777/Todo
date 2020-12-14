package com.example.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class DoneActivity extends AppCompatActivity {
    final String TAG = "DoneActivity";
    List<TodoTask> tasks = new ArrayList<TodoTask>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        final TaskFragment fa = TaskFragment.newInstance();

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GlobleData gl = GlobleData.getInstance(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TodoTask> t_tasks = GlobleData.getDB().mTodoTaskDao().getAllDoneTask();
                fa.tasks = tasks = t_tasks;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fg, fa, "f1")
                        .commit();
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<tasks.size();i++) GlobleData.getDB().mTodoTaskDao().update(tasks.get(i));
            }
        }).start();
    }
}