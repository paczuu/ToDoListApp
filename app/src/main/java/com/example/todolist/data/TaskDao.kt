package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.Task

@Dao
interface TaskDao {

    // Pobiera wszystkie zadania w porządku malejącym po ID
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Task>>

    // Pobiera jedno zadanie po ID
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): LiveData<Task>

    // Wstawia zadanie, zastępując je w przypadku konfliktu (np. jeśli ID jest już w bazie)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    // Aktualizuje istniejące zadanie
    @Update
    suspend fun updateTask(task: Task): Int

    // Usuwa jedno zadanie
    @Delete
    suspend fun deleteTask(task: Task): Int

    // Usuwa wszystkie zadania
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    // Zlicza liczbę wszystkich zadań
    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCount(): LiveData<Int>

    // Zlicza liczbę wszystkich zadań (synchronizacja dla WorkManagera)
    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCountSync(): Int
}