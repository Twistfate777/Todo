package com.example.todo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

@Entity(tableName = "list")
public class TodoList {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "listName")
    public String name;

    @ColumnInfo(name = "bg")
    public int bg;

    public TodoList(String _name)
    {
        name = _name;
    }


    public TodoList()
    {

    }

    @Ignore
    public TodoList(String name,int id)
    {

    }

    @Ignore
    public TodoList(int _id)
    {
        id = _id;
    }


    @Override
    public String toString() {
        return name;
    }
}
