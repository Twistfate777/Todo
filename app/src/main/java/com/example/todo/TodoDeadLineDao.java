package com.example.todo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface TodoDeadLineDao {
    @Insert
    long insert(TodoDeadLine list);

    @Query("Select * from deadLine")
    List<TodoDeadLine> getAllInfo();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPerson(TodoDeadLine todoList);

    @Query("Update deadLine set year=:y,month =:m,date = :d,hour = :h,minute = :mt where id = :id")
    int update(int id,int y,int m,int d,int h,int mt);

    @Query("Delete from deadLine where id == :id")
    int delete(int id);

    @Query("Select COUNT(*) from deadLine")
    int getCount();

    @Query("Select * from deadLine where id == :id")
    TodoDeadLine searchDeadLine(int id);
}
