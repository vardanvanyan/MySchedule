package com.example.myschedule;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import java.util.List;

@Dao
public interface DataDao {
    @Insert
    void insert(CategoryModel dataModel);

    @Insert
    void insert(TaskModel dataModel);

    @Delete
    void delete(TaskModel taskModel);

    @Query("SELECT * FROM CategoryModel")
    List<CategoryModel> getAllData();

    @Query("SELECT * FROM TaskModel")
    List<TaskModel> getAllDataTask();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceTask(TaskModel... taskModel);
}
