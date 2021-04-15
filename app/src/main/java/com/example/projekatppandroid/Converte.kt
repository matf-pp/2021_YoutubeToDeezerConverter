package com.example.projekatppandroid

import com.example.projekatppandroid.PlaylistForYoutube
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.fromJson

class Converte : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converte)

        var actionBar = supportActionBar

        actionBar!!.title = "ProjekatPPAndroid"
        actionBar.setDisplayHomeAsUpEnabled(true)

        var intent = getIntent()
        val songs = intent.getStringExtra(GETTING_PLAYLISTS)

        //var map : MutableMap<String, MutableList<String>> = mutableMapOf()
        //map = Gson().fromJson(songs, map.javaClass)

        var res = Json { isLenient = true }.decodeFromString<MutableList<PlaylistForYoutube>>(songs.toString())

        val textView = findViewById<TextView>(R.id.pesme).apply {
            var tmp = ""

            for (k in res){
                tmp = tmp + k.title + " : " + k.allSongs + "\n"
            }

            text = tmp
        }
    }
}

















