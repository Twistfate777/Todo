package com.example.todo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface TodoListDao {
    @Insert
    long insert(TodoList list);

    @Query("Select * from list")
    List<TodoList> getAllInfo();

    @Update
    int update(TodoList list);

    @Query("Select listName from list where id == :id")
    String getListName(int id);

    @Query("Delete from list where id =:id")
    int delete(int id);

    @Query("Update list set bg = :bg where id == :id")
    void setBG(int id,int bg);

    @Query("Select bg from list where id == :id")
    int getBG(int id);
}
