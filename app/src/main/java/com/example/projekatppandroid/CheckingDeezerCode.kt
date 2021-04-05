package com.example.projekatppandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class CheckingDeezerCode : AppCompatActivity()
{

    fun checkURL(url : String)
    {
        if (url.contains("user_denied")){
            // TODO: vrati nazad ili tako nesto (ili prekini ceo program)
            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
                val denied = "You have denied our aplication necessary permissions to go foward."
                text =  denied
            }
        }
        else{
            val myURL = URL(url)
            val myConn: HttpURLConnection = myURL.openConnection() as HttpURLConnection
            myConn.setRequestMethod("GET")

            System.out.println("Response Code: " + myConn.getResponseCode())
            val `in`: InputStream = BufferedInputStream(myConn.getInputStream())
            val response: String = org.apache.commons.io.IOUtils.toString(`in`, "UTF-8")
            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
                val denied = "You have denied our aplication necessary permissions to go foward."
                text =  response
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checking_deezer_code)


        val urlGiven = intent.getStringExtra(GETTING_CODE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
            text =  urlGiven
        }

        //checkURL(urlGiven.toString())

    }
}