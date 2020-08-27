package com.adi_random.callhome.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.adi_random.callhome.R
import com.adi_random.callhome.databinding.MainActivityBinding
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment

const val ERROR_NOTIFICATION_CHANNEL = "error_notification-channel"

class MainActivity : AppCompatActivity() {

    companion object {
        const val AddReminderModalTag = "add_reminder_modal"

        //        The id of the reminder that has an error
        const val ReminderErrorIdParam = "reminder_error_param"
    }

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        binding.showModalHandler = this
        setContentView(binding.root)

        createNotificationChannels()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    fun showAddReminderModal(view: View) {
        AddReminderFragment.newInstance().show(supportFragmentManager, AddReminderModalTag)
    }


    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.error_notification_channel_name)
            val descriptionText = getString(R.string.error_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ERROR_NOTIFICATION_CHANNEL, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}