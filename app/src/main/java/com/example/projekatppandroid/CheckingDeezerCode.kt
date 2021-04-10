package com.example.projekatppandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.parse
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess

data class response(val success : Int, val resString : String)

class CheckingDeezerCode : AppCompatActivity()
{


    fun ourGetRequest(url : String) : String
    {
        var response : String = ""
        val thread = Thread {
            try {
                val myURL = URL(url)
                val myConn: HttpURLConnection = myURL.openConnection() as HttpURLConnection
                myConn.setRequestMethod("GET")

                //System.out.println("Response Code: " + myConn.getResponseCode())
                val `in`: InputStream = BufferedInputStream(myConn.getInputStream())
                response = org.apache.commons.io.IOUtils.toString(`in`, "UTF-8")
//            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                text = response
//            }
            }
            catch (e : Exception){
                // TODO: neka vesta greške! (something went wrong, please try again)
            }
        }
        thread.start()
        thread.join()
        return response
    }

    fun checkURL(url : String) : response
    {
        if (url.contains("user_denied")){
            val denied = "You have denied our aplication necessary permissions to go foward."
            return response(-1, denied)
        }
        else{
            var res = url.split("code=")
//            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                val denied = "You have denied our aplication necessary permissions to go foward."
//                text = res[1]
//            }
            return response(1, res[1])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checking_deezer_code)


        val urlGiven = intent.getStringExtra(GETTING_CODE)

        // Capture the layout's TextView and set the string as its text
        //val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
        //    text =  urlGiven
        //}

        val returnval = checkURL(urlGiven.toString())
        val mapa : MutableMap<String, MutableList<String>> = mutableMapOf()
        if (returnval.success == -1){
            // TODO: vrati nazad ili tako nesto (ili prekini ceo program)
            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
                text =  returnval.resString
            }
            //exitProcess(-1)
        } else {
            var urlAccessToken = "https://connect.deezer.com/oauth/access_token.php?"
            urlAccessToken += "app_id=" + allInfo.getterappID()
            urlAccessToken += "&secret=" + allInfo.gettersecret()
            urlAccessToken += "&code=" + returnval.resString
            urlAccessToken += "&request_method=POST"
            urlAccessToken += "&output=json"
            var res = ourGetRequest(urlAccessToken)
//            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                   text = res
//            }
            val acc_token = Json { isLenient = true }.decodeFromString<AccessToken>(res).getAccess_token()
            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
                   text = acc_token
            }

            var urlForPlaylists = "https://api.deezer.com/user/me/playlists?"
            urlForPlaylists += "access_token=" + acc_token
            // ako je ubacen post metod on misli da dodajemo pesme, zato ne radi
            //urlForPlaylists += "&request_method=POST"
            urlForPlaylists += "&output=json"
            res = ourGetRequest(urlForPlaylists)
            val allPlaylistsInfo : AllPlaylists = Json{ isLenient = true; ignoreUnknownKeys = true}.decodeFromString<AllPlaylists>(res)
            for (currentPlaylistInfo : PlaylistInfo in allPlaylistsInfo.data){
                res = ourGetRequest(currentPlaylistInfo.tracklist)
                var currentPlaylist : Playlist = Json{isLenient = true; ignoreUnknownKeys = true}.decodeFromString<Playlist>(res)
                var currentPlaylistName = currentPlaylistInfo.title

                var l  : MutableList<String> = mutableListOf()

                while (true){
                    for (currentSong : Song in currentPlaylist.data){
                        var currentSongName = currentSong.title
                        var currentSongArtist = currentSong.artist
                        var fullSongName = currentSongArtist.name + " - " + currentSongName
                        l.add(fullSongName)
                    }
                    res = ourGetRequest(currentPlaylist.next.toString())
                    if (!res.contains("next")){
                        break
                    }
                    currentPlaylist = Json { isLenient = true; ignoreUnknownKeys = true;  }.decodeFromString<Playlist>(res)
                }

                mapa[currentPlaylistName] = l
            }

        }
    }
}