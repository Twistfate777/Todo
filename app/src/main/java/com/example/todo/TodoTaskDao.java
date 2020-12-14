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
public interface TodoTaskDao {
    @Insert
    long insert(TodoTask task);

    @Query("Select * from task")
    List<TodoTask> getAllTask();

    @Query("Select * from task where fid = (:fid)")
    List<TodoTask> getAllInfo(int fid);

    @Query("Select * from task where isStared = 1")
    List<TodoTask> getAllImportant();

    @Query("Select * from task where isToday = 1")
    List<TodoTask> getAllToday();

    @Query("Select * from task where fid =(:fid) and taskName=(:taskName)")
    TodoTask getTask(String taskName,int fid);

    @Query("Select * from task where fid =:fid and id = :id")
    TodoTask getTask(int fid,int id);

    @Query("update task set deadLineID = :ddID where id ==:id ")
    int updateDD(int id,int ddID);

    @Query("delete from task where id ==:id")
    int delete(int id);

    @Query("delete from task where fid == :fid")
    int deleteAll(int fid);

    @Query("select * from task where isDone == 1")
    List<TodoTask> getAllDoneTask();

    @Update
    void update(TodoTask task);
}
