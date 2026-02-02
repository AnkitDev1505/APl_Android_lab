package com.ab15.layouts


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    var etName: EditText? = null
    var btnSubmit: Button? = null
    var tvResult: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etName = findViewById<EditText?>(R.id.etName)
        btnSubmit = findViewById<Button?>(R.id.btnSubmit)
        tvResult = findViewById<TextView?>(R.id.tvResult)

        btnSubmit!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val name = etName!!.getText().toString()

                if (name.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter your name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    tvResult!!.setText("Hello " + name + " 👋")
                }
            }
        })
    }
}