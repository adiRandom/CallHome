package com.adi_random.callhome.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.adi_random.callhome.databinding.MainActivityBinding
import com.adi_random.callhome.ui.main.addreminder.AddReminderFragment

class MainActivity : AppCompatActivity() {

    companion object {
        const val AddReminderModalTag = "add_reminder_modal"
    }

    private lateinit var binding: MainActivityBinding
    private lateinit var modal: AddReminderFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        binding.showModalHandler = this
        setContentView(binding.root)

    }

    fun showAddReminderModal(view: View) {
        AddReminderFragment.newInstance().show(supportFragmentManager, AddReminderModalTag)
    }


}