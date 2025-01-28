package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationsSwitch: Switch
    private lateinit var clearTasksButton: Button
    private lateinit var taskDatabase: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Inicjalizacja bazy danych
        taskDatabase = TaskDatabase.getDatabase(this)

        notificationsSwitch = findViewById(R.id.notifications_switch)
        clearTasksButton = findViewById(R.id.clear_all_tasks_button)

        // Ustawienie stanu przełącznika powiadomień
        updateNotificationsSwitchState()

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                NotificationHelper.enableNotifications(this)
            } else {
                NotificationHelper.disableNotifications(this)
            }
        }

        // Obsługa przycisku do czyszczenia bazy danych
        clearTasksButton.setOnClickListener {
            showClearTasksConfirmationDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Aktualizacja stanu przełącznika powiadomień po wznowieniu aktywności
        updateNotificationsSwitchState()
    }

    private fun updateNotificationsSwitchState() {
        notificationsSwitch.isChecked = NotificationHelper.isNotificationsEnabled(this)
    }

    private fun showClearTasksConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Potwierdzenie")
            .setMessage("Czy na pewno chcesz usunąć wszystkie zadania?")
            .setPositiveButton("Tak") { _, _ ->
                // Usuwanie wszystkich zadań
                GlobalScope.launch(Dispatchers.IO) {
                    taskDatabase.taskDao().deleteAllTasks()  // Tutaj używasz TaskDatabase
                    runOnUiThread {
                        Toast.makeText(this@SettingsActivity, "Wszystkie zadania zostały usunięte", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Anuluj", null)
            .create()
        dialog.show()
    }
}