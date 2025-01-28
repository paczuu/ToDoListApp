package com.example.todolist

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.work.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import android.app.NotificationManager
import androidx.appcompat.app.AlertDialog
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWorkManager()
        NotificationHelper.createNotificationChannel(applicationContext)

        // Set up the Toolbar as the ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)

        // Obsługa kliknięcia w menu boczne
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers() // Zamknięcie menu bocznego po kliknięciu
                    true
                }
                R.id.menu_tasks -> {  // Dodajemy obsługę kliknięcia na element do MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers() // Zamknięcie menu bocznego po kliknięciu
                    true
                }
                R.id.motivation -> {  // Obsługa kliknięcia na element "Motywacja"
                    val intent = Intent(this, MotivationActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers() // Zamknięcie menu bocznego po kliknięciu
                    true
                }
                else -> false
            }
        }

        // Check if the app has notification permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (!notificationManager.areNotificationsEnabled()) {
                requestNotificationPermission()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    // Function to ask for notification permission
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Powiadomienia")
                .setMessage("Chcesz otrzymywać powiadomienia?")
                .setPositiveButton("Zezwól") { _, _ ->
                    // Open notification settings for the user to enable notifications
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivity(intent)
                }
                .setNegativeButton("Nie teraz", null)
                .create()
            dialog.show()
        }
    }

    // Utworzenie cyklicznej pracy dla WorkManagera
    private fun setupWorkManager() {
        val workRequest = PeriodicWorkRequestBuilder<DailyTaskCheckWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyTaskCheck",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
