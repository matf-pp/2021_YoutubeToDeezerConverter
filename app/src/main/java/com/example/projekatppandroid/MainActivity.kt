package com.example.projekatppandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_main)

    }


    fun getDeezerInfo(view : View) {
        val intent = Intent(this, GetDeezerInfo::class.java).apply {
        }
        startActivity(intent)
    }

    fun sendMessage(view : View) {
        //val editText = findViewById<EditText>(R.id.editTextTextPersonName)
        //val message = editText.text.toString()

        //val intent = Intent(this, DisplayMessageActivity::class.java).apply {
        //    putExtra(EXTRA_MESSAGE, message)
        //}
        //startActivity(intent)
    }

}


