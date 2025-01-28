package com.example.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.todolist.databinding.ActivityTaskDetailsBinding

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId == -1) {
            Toast.makeText(this, "Invalid Task", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        taskViewModel.getTaskById(taskId).observe(this) { task ->
            task?.let { taskItem ->
                binding.taskTitle.text = taskItem.title
                binding.taskDescription.text = taskItem.description

                binding.editButton.setOnClickListener {
                    // Open AddEditTaskFragment for editing
                    AddEditTaskFragment.newInstance(taskItem.id).show(supportFragmentManager, "EDIT_TASK")
                }

                binding.deleteButton.setOnClickListener {
                    taskViewModel.deleteTask(taskItem)
                    Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}