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

@Suppress("DEPRECATION")
class Error : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        var actionBar = supportActionBar

        actionBar!!.title = "ProjekatPPAndroid"
        actionBar.setDisplayHomeAsUpEnabled(true)

        var intent = getIntent()
        val error = intent.getStringExtra("Error")

        val textView = findViewById<TextView>(R.id.Error).apply {
            text = error
        }

        //TODO: moze da pravi problemm, proveriti!
        activity()

    }

    private fun activity() {

        var mRunnable = Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        var mHandler = Handler()

        mHandler.postDelayed(mRunnable, 4000)
        mHandler.removeCallbacksAndMessages(null)

    }

}


