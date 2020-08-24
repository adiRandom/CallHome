package com.adi_random.callhome.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.adi_random.callhome.databinding.MainActivityBinding
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment
import com.adi_random.callhome.ui.main.addreminder.ITimePipupCallback

class MainActivity : AppCompatActivity(),ITimePipupCallback {

    companion object {
        const val AddReminderModalTag = "add_reminder_modal"
    }

    private lateinit var binding: MainActivityBinding
    private lateinit var modal: AddReminderFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        binding.showModalHandler = this
        modal = AddReminderFragment.newInstance()
        setContentView(binding.root)

    }

    fun showAddReminderModal(view: View) {
        modal.show(supportFragmentManager, AddReminderModalTag)
    }

//    Callback for time picker dialog
    override fun onTimePicked(hour: Int, minute: Int) {
        modal.onTimePicked(hour, minute)
    }
    //    Callback for time picker dialog
    override fun onDayPicked(day: Int) {
        modal.onDayPicked(day)
    }
}