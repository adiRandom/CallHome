package com.adi_random.callhome.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adi_random.callhome.R


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragmentNoPermissions.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragmentNoPermissions : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_no_permissions, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MainFragmentNoPermissions()

    }
}