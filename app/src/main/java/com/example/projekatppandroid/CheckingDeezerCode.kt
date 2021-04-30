package com.example.projekatppandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class response(val success : Int, val resString : String)

const val GETTING_PLAYLISTS = "code_for_playlists!"


class CheckingDeezerCode : AppCompatActivity(), AdapterView.OnItemClickListener {
    var mapCheckedPlayLists : MutableMap<String, MutableList<String>> = mutableMapOf()
    var mapa : MutableMap<String, MutableList<String>> = mutableMapOf()

    var allUserPlaylists : MutableList<PlaylistForYoutube> = mutableListOf()
    var sentUserPlaylists : MutableList<PlaylistForYoutube> = mutableListOf()
    val listFlags: List<Boolean> = listOf()


    private var listView: ListView? = null
    private var arrayAdapter: ArrayAdapter<String>? = null

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
                val intent = Intent(this, Error::class.java).apply{
                    putExtra("Error", e.localizedMessage)
                }
                startActivity(intent)
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
        //val mapa : MutableMap<String, MutableList<String>> = mutableMapOf()
        if (returnval.success == -1){
            // TODO: vrati nazad ili tako nesto (ili prekini ceo program)
//            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                text =  returnval.resString
//            }
            //exitProcess(-1)
            val intent = Intent(this, Error::class.java).apply{
                putExtra("Error", returnval.resString)
            }
            startActivity(intent)

        } else {
            var urlAccessToken = "https://connect.deezer.com/oauth/access_token.php?"
            urlAccessToken += "app_id=" + allInfo.getappID()
            urlAccessToken += "&secret=" + allInfo.getsecret()
            urlAccessToken += "&code=" + returnval.resString
            urlAccessToken += "&request_method=POST"
            urlAccessToken += "&output=json"
            var res = ourGetRequest(urlAccessToken)
//            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                   text = res
//            }
            val acc_token = Json { isLenient = true }.decodeFromString<AccessTokenForDeezer>(res).getAccess_token()
//            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                   text = acc_token
//            }

            var urlForPlaylists = "https://api.deezer.com/user/me/playlists?"
            urlForPlaylists += "access_token=" + acc_token
            // TODO : moze wrong input mora to se popravi
            // ako je ubacen post metod on misli da dodajemo pesme, zato ne radi
            //urlForPlaylists += "&request_method=POST"
            urlForPlaylists += "&output=json"
            res = ourGetRequest(urlForPlaylists)
            if (res.contains("wrong input")){
                // TODO: salje na stranicu onu
            }
            val allPlaylistsInfo : AllPlaylists = Json{ isLenient = true; ignoreUnknownKeys = true}.decodeFromString<AllPlaylists>(res)

            //TODO: šta ako ne izabere nijednu plajlistu? to se mora obraditi
//            if(allPlaylistsInfo == null){
//                val intent = Intent(this, Error::class.java).apply{
//                    putExtra("Error", "Morate imati bar jednu plejlistu!")
//                }
//                startActivity(intent)
//            }

            for (currentPlaylistInfo : PlaylistInfo in allPlaylistsInfo.data){
                var playlistSize = currentPlaylistInfo.nb_tracks
                var urlForPlaylist = currentPlaylistInfo.tracklist + "?limit=" + playlistSize + "&output=json"
                urlForPlaylist += "&access_token=" + acc_token
                res = ourGetRequest(urlForPlaylist)
                var currentPlaylist : Playlist = Json{isLenient = true; ignoreUnknownKeys = true}.decodeFromString<Playlist>(res)
//                val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                    text = currentPlaylistInfo.title
//                }

                var l  : MutableList<Song> = mutableListOf()

                for (currentSong : Song in currentPlaylist.data){
//                    var currentSongName = currentSong.title
//                    var currentSongArtist = currentSong.artist
//                    var fullSongName = currentSongArtist.name + " - " + currentSongName
                    l.add(currentSong)
                }

                var playlistNewName = currentPlaylistInfo.title + " made by " + currentPlaylistInfo.creator.name
                var latestPlaylist : PlaylistForYoutube =
                    PlaylistForYoutube(playlistNewName, "", l, currentPlaylistInfo.creator, currentPlaylistInfo.public)
                latestPlaylist.makeRandomDescription()
                allUserPlaylists.add(latestPlaylist)

                //mapa[currentPlaylistName] = l
            }

//            val textView = findViewById<TextView>(R.id.editTextTextPersonName).apply {
//                text = mapa.keys.toString()
//            }

            var names : MutableList<String> = mutableListOf()

            for (playlist in allUserPlaylists) {
                names.add(playlist.title)
            }

            listView = findViewById(R.id.multiple_list_view)
            arrayAdapter = ArrayAdapter(applicationContext,
                android.R.layout.simple_list_item_multiple_choice,
                names)
            listView?.adapter = arrayAdapter
            listView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            listView?.onItemClickListener = this


            //Ovo se odnosi na dugme CONVERT
            var btnConvert = findViewById<Button>(R.id.converteBtn)
            btnConvert.setOnClickListener{
                val intent = Intent(this, Converte::class.java).apply{
                    putExtra(GETTING_PLAYLISTS, Json.encodeToString(sentUserPlaylists))
                    //(GETTING_PLAYLISTS, sentUserPlaylists.toTypedArray())
                }
                startActivity(intent)
            }

        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items:String = parent?.getItemAtPosition(position) as String

        for(pl in allUserPlaylists){
            if(pl.title == items && sentUserPlaylists.contains(pl)){
                sentUserPlaylists.remove(pl)
            }
            else if (pl.title == items){
                sentUserPlaylists.add(pl)
            }
        }

        Toast.makeText(applicationContext, "Playlist name : $items", Toast.LENGTH_LONG).show()

    }

}