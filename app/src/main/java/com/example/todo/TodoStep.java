package com.example.todo;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "step")
public class TodoStep {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "taskId")
    int taskId;

    @ColumnInfo(name = "text")
    String text;

    public TodoStep() {}

    public TodoStep(int tkid,String txt)
    {
        taskId = tkid;
        text = txt;
    }

}
