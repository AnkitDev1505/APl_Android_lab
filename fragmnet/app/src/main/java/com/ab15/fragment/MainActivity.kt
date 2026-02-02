package com.ab15.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var btnFragment1: Button
    private lateinit var btnFragment2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ✅ NOW IT WORKS

        btnFragment1 = findViewById(R.id.btnFragment1)
        btnFragment2 = findViewById(R.id.btnFragment2)

        // Load default fragment
        loadFragment(FragmentOne())

        btnFragment1.setOnClickListener {
            loadFragment(FragmentOne())
        }

        btnFragment2.setOnClickListener {
            loadFragment(FragmentTwo())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}