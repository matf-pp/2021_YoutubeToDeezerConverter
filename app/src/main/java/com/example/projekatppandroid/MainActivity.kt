package com.example.projekatppandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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



//var appID = "469842"
//var redirect = "https://theappreciationengine.com/DeezerAuthenticator_Controller"
//var perms = "basic_access,email"
//var url = "https://connect.deezer.com/oauth/auth.php?"
//url += "app_id=" + appID.toString()
//url += "&redirect_uri=" + redirect
//url += "&perms=" + perms

//val url1 = "https://connect.deezer.com/oauth/auth.php?app_id=469842&redirect_uri=https://theappreciationengine.com/DeezerAuthenticator_Controller&perms=basic_access,email"
//val text = URL(url1).readText()
////println(text)

//val url1 = "https://connect.deezer.com/oauth/auth.php?app_id=469842&redirect_uri=https://theappreciationengine.com/DeezerAuthenticator_Controller&perms=basic_access,email"
