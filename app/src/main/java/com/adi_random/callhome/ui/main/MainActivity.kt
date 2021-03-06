package com.adi_random.callhome.ui.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.adi_random.callhome.R
import com.adi_random.callhome.databinding.MainActivityBinding
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment
import com.adi_random.callhome.ui.main.utils.CallLogPermissionDialog
import com.adi_random.callhome.ui.settings.SettingsActivity
import com.adi_random.callhome.worker.CallHistoryWatchWorker
import java.util.concurrent.TimeUnit

const val ERROR_NOTIFICATION_CHANNEL = "error_notification-channel"
const val REMINDERS_NOTIFICATION_CHANNEL = "reminder_notification_channel"

class MainActivity : AppCompatActivity() {

    companion object {
        const val AddReminderModalTag = "add_reminder_modal"

        //        The id of the reminder that has an error
        const val ReminderErrorIdExtra = "reminder_error_param"

        const val PermissionEducationalUiTag = "educational_ui"
    }

    private lateinit var binding: MainActivityBinding
    private var errorReminderId: Long? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                onPermissionGranted()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        binding.showModalHandler = this
        setContentView(binding.root)

        //Create the appbar
        setSupportActionBar(binding.toolbar)

        createNotificationChannels()

        //Check if the activity started due to a reminder error
        //Get the reminder id and pass it to the fragment

        if (intent.hasExtra(ReminderErrorIdExtra))
            errorReminderId = intent.getLongExtra(ReminderErrorIdExtra, 0)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragmentNoPermissions.newInstance())
            .addToBackStack(null)
            .commit()

        checkCallLogPermission()

    }

    fun showAddReminderModal(view: View) {
        AddReminderFragment.newInstance().show(supportFragmentManager, AddReminderModalTag)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_menu_item -> {
//                Show the settings activity
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showEducationalUi() {
        CallLogPermissionDialog.newInstance().setCallback {
            requestPermissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
        }.show(
            supportFragmentManager,
            PermissionEducationalUiTag
        )
    }

    private fun checkCallLogPermission() {
        when {
            checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
            -> onPermissionGranted()
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG) -> showEducationalUi()
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
            }
        }
    }


    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//            The error channel

            val errorName = getString(R.string.error_notification_channel_name)
            val errorDescriptionText = getString(R.string.error_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val errorChannel =
                NotificationChannel(ERROR_NOTIFICATION_CHANNEL, errorName, importance).apply {
                    description = errorDescriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(errorChannel)


            // The reminder channel
            val reminderName = getString(R.string.reminder_notification_channel_name)
            val reminderDescription = getString(R.string.reminder_notification_channel_description)
            val reminderChannel = NotificationChannel(
                REMINDERS_NOTIFICATION_CHANNEL,
                reminderName,
                importance
            ).apply {
                description = reminderDescription
            }

            //Register the channel
            notificationManager.createNotificationChannel(reminderChannel)

        }
    }

    private fun registerWorker() {
        //Register worker
        val workRequest =
            PeriodicWorkRequestBuilder<CallHistoryWatchWorker>(30, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun onPermissionGranted() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance(errorReminderId))
            .addToBackStack(null)
            .commit()
        registerWorker()
    }


}