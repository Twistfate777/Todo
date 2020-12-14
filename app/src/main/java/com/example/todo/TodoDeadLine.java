package com.example.todo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "deadLine")
public class TodoDeadLine {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name ="month")
    public int month;

    @ColumnInfo(name = "date")
    public int date;

    @ColumnInfo(name = "hour")
    public int hour;

    @ColumnInfo(name = "minute")
    public int minute;

    public TodoDeadLine(int y,int m,int d,int h,int _minute)
    {
        year = y;
        month = m;
        date = d;
        hour = h;
        minute = _minute;
    }


    public TodoDeadLine()
    {

    }

}
