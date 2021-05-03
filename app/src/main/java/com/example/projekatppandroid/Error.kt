package com.example.projekatppandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.concurrent.schedule

@Suppress("DEPRECATION")
class Error : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        var actionBar = supportActionBar

        actionBar!!.title = ""
        actionBar.setDisplayHomeAsUpEnabled(true)

        var intent = getIntent()
        val error = intent.getStringExtra("Error")

        val textView = findViewById<TextView>(R.id.Error).apply {
            text = error
        }


        Timer("SettingUp", false).schedule(2000){
            action()
        }

    }

    private fun action() {
        val intent = Intent(this, MainActivity::class.java).apply{
        }
        startActivity(intent)
    }


}


