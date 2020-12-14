package com.example.todo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "task")
public class TodoTask implements Serializable {

    @ColumnInfo(name = "fid")
    public int fid;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "taskName")
    public String name;

    @ColumnInfo(name = "isDone")
    public boolean isDone;

    @ColumnInfo(name = "isStared")
    public boolean isStared;

    @ColumnInfo(name = "isToday")
    public boolean isToday;

    @ColumnInfo(name = "deadLineID")
    public int deadLineID;

    @ColumnInfo(name = "noteDate")
    public long noteDate;

    @ColumnInfo(name = "comment")
    public String comment;

    public TodoTask(String _name, int _fid)
    {
        name = _name;
        fid = _fid;
    }

    public TodoTask(){}

    @Ignore
    public TodoTask(String _name)
    {
        name = _name;
    }

    @Ignore
    public TodoTask(int _id)
    {
        id = _id;
    }


    @Override
    public String toString() {
        return name;
    }
}
