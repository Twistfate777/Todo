package com.example.todo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TodoList.class,TodoTask.class,TodoStep.class,TodoDeadLine.class},version = 1,exportSchema = false)
public abstract class TodoListDataBase extends RoomDatabase
{
    public abstract TodoListDao mTodoListDao();
    public abstract TodoTaskDao mTodoTaskDao();
    public abstract TodoDeadLineDao mTodoDeadLineDao();
    public abstract TodoStepDao mTodoStepDao();
}
