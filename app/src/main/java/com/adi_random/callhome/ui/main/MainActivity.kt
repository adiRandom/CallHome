package com.adi_random.callhome.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.adi_random.callhome.databinding.MainActivityBinding
import com.adi_random.callhome.ui.main.addreminder.AddReminder

class MainActivity : AppCompatActivity() {

    companion object {
        const val AddReminderModalTag = "add_reminder_modal"
    }

    private lateinit var binding: MainActivityBinding
    private lateinit var modal: AddReminder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        binding.showModalHandler = this
        modal = AddReminder.newInstance()
        setContentView(binding.root)

    }

    fun showAddReminderModal(view: View) {
        modal.show(supportFragmentManager, AddReminderModalTag)
    }
}