package com.example.todo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TodoStepDao {
    @Insert
    long insert(TodoStep step);

    @Delete
    void delete(TodoStep step);

    @Update
    void update(TodoStep step);

    @Query("Select * from step where taskId == :taskId")
    List<TodoStep> getAllSteps(int taskId);
}
