package com.example.todolist

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
//test
import android.util.Log

class DailyTaskCheckWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("WorkManager", "DailyTaskCheckWorker started.")
        val taskDao = TaskDatabase.getDatabase(applicationContext).taskDao()
        val taskCount = taskDao.getTaskCountSync()

        Log.d("WorkManager", "Number of tasks: $taskCount")

        if (taskCount > 0) {
            NotificationHelper.showNotification(
                applicationContext,
                "Hej ty!",
                "Wciąż masz zadania do zrobienia.."
            )
            Log.d("WorkManager", "Notification sent.")
        } else {
            Log.d("WorkManager", "No tasks available.")
        }

        return Result.success()
    }
}
