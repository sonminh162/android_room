package com.lifetime.otherroomexample;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Insert
    void insert(Task task);

    @Delete
    void delete(Task task);

    @Update
    void update(Task task);

    @Query("SELECT * FROM task ORDER BY task_field DESC")
    List<Task> sortDataAtoZ();

    @Query("SELECT * FROM task ORDER BY task_field ASC")
    List<Task> sortDataZtoA();

    @Query("SELECT * FROM task WHERE task_field LIKE '%' || :taskName || '%'")
    List<Task> searchTaskByTaskName(String taskName);
}
