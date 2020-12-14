package com.example.todo;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

public class GlobleData {
    private static GlobleData instance;
    private static TodoListDataBase database;
    private GlobleData(Context content)
    {
        database = Room.databaseBuilder(
                content.getApplicationContext(),
                TodoListDataBase.class,
                "Todo"
        ).build();
    }

    public static GlobleData getInstance(Context context)
    {
        if(instance != null) return instance;
        else return instance = new GlobleData(context);
    }

    public static TodoListDataBase getDB()
    {
        return database;
    }

    public static void TodoTaskUpdate(final List<TodoTask> tasks)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<tasks.size();i++)
                    database.mTodoTaskDao().update(tasks.get(i));
            }
        }).start();
    }

}
