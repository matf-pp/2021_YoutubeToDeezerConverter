package com.example.projekatppandroid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.HttpResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpPost
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Integer.min
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.system.exitProcess


class Convert : AppCompatActivity() {

    companion object {
        const val TAG = "GoogleActivity"
        const val RC_SIGN_IN = 9001
    }

    val regex = Regex("\"videoId\":\"([^\"]+)\"")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert)

        var actionBar = supportActionBar

        actionBar!!.title = "ProjekatPPAndroid"
        actionBar.setDisplayHomeAsUpEnabled(true)

        var intent = getIntent()
        val songs = intent.getStringExtra(GETTING_PLAYLISTS)

        //var map : MutableMap<String, MutableList<String>> = mutableMapOf()
        //map = Gson().fromJson(songs, map.javaClass)

        var allPlaylists = Json { isLenient = true }.decodeFromString<MutableList<PlaylistForYoutube>>(songs.toString())

        val textView = findViewById<TextView>(R.id.pesme)

        val clientID = allInfo.getYt_clientID()
        val secret = allInfo.getYt_secret()
        val ourID = allInfo.getYt_ourID()
        val urlForAccessToken = "https://oauth2.googleapis.com/token"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(clientID)
                .requestIdToken(clientID)
                .requestScopes(Scope("https://www.googleapis.com/auth/youtube"))
                .requestScopes(Scope("https://www.googleapis.com/auth/youtube.force-ssl"))
                .requestScopes(Scope("https://www.googleapis.com/auth/youtubepartner"))
                .requestEmail()
                .build()

        val mGoogleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null){
            signOut(mGoogleSignInClient)
        }
        updateUI(null)

        signInButton.setOnClickListener{
            signIn(mGoogleSignInClient)
        }

        val signOutButton = findViewById<Button>(R.id.LogOut)
        signOutButton.setOnClickListener {
            signOut(mGoogleSignInClient)
        }

        val StartConvertionButton = findViewById<Button>(R.id.StartConvertion)
        StartConvertionButton.setOnClickListener{
            updateUIForConvertionBegin()
            val user = GoogleSignIn.getLastSignedInAccount(this)
            val grant = "authorization_code"
            if (user != null) {
                val l = (listOf("client_id" to clientID,
                        "code" to user.serverAuthCode,
                        "client_secret" to secret,
                        "redirect_uri" to "",
                        "grant_type" to grant))

                var accessTokenStringFormat = ourPostMethodList(l, urlForAccessToken)
                if (!accessTokenStringFormat.contains("Success") || accessTokenStringFormat.contains("Failure") || accessTokenStringFormat.contains("Error")){
                    // TODO loš je odgovor, mora da se obradi greska nekako
                    //Log.d("EXIT", "access_token")

                    val intent = Intent(this, Error::class.java).apply{
                        putExtra("Error", "Something went wrong, please try again!")
                    }
                    finish()
                    startActivity(intent)
                }

                accessTokenStringFormat = accessTokenStringFormat.drop(accessTokenStringFormat.indexOf("{"))
                accessTokenStringFormat = accessTokenStringFormat.dropLast(accessTokenStringFormat.length - accessTokenStringFormat.lastIndexOf("]"))

                //Log.d("bla", accessTokenStringFormat)


                // TODO: tipa if accessTokenStringFormat.contanintsss("error") onda error screen
                // TODO: isto tako za ono wrong sranje kod deezer (to je ono kad se vraćaš pa error)
                val accessTokenJsonFormat = Json{ isLenient = true; ignoreUnknownKeys = true }.decodeFromString<AccessTokenYoutube>(accessTokenStringFormat)

                var accessToken = accessTokenJsonFormat.access_token
                //Log.d("ACCESSTOKEN!", accessToken)
                var urlForInsertPlaylists = "https://www.googleapis.com/youtube/v3/playlists?part=snippet,status"
                urlForInsertPlaylists += "&key=" + ourID
                urlForInsertPlaylists += "&access_token=" + accessToken
                //Log.d("My app",accessToken + "\n---------------------------------------------------\n" + user.idToken)
//                findViewById<TextView>(R.id.pesme).apply {
//                    text = accessToken + "\n---------------------------------------------------\n" + user.idToken
//                }

                // todo : 401 error je ako korisnik nema yt kanal, jsuk


                for (playlist in allPlaylists){
                    // za svaki playlist prvo napravis
                    val title = playlist.title
                    val description : String = playlist.makeRandomDescription()
                    val status : String = if (playlist.status) "public" else "private"
                    val bodyJson = """ { "snippet" : { "title" : "$title", "description" : "$description"}, "status" : { "privacyStatus" : "$status"}}"""
                    var youtubePlaylistInfoString = ourPostMethodBody(bodyJson, urlForInsertPlaylists)
                    if (!youtubePlaylistInfoString.contains("Success") || youtubePlaylistInfoString.contains("Failure") || youtubePlaylistInfoString.contains("Error")){

                        if(youtubePlaylistInfoString.contains("401")){
                            val intent = Intent(this, Error::class.java).apply{
                                putExtra("Error", "You do not have youtube channel, please try again after you create one!")
                            }
                            startActivity(intent)
                           // Log.d("ERROR","You do not have youtube channel, please try again after you create one.")
                        }

                        else{
                            val intent = Intent(this, Error::class.java).apply{
                                putExtra("Error", "Something went wrong, please try again!")
                            }
                            startActivity(intent)

                        }

                        Log.d("ERROR", youtubePlaylistInfoString)
                        exitProcess(1)
                    }


                    youtubePlaylistInfoString = youtubePlaylistInfoString.drop(youtubePlaylistInfoString.indexOf("{"))
                    youtubePlaylistInfoString = youtubePlaylistInfoString.dropLast(youtubePlaylistInfoString.length - youtubePlaylistInfoString.lastIndexOf("]"))

                    val youtubePlaylistInfoJson = Json{ isLenient = true; ignoreUnknownKeys = true }.decodeFromString<YoutubePlaylistCreationInfo>(youtubePlaylistInfoString)
                    val playlistID = youtubePlaylistInfoJson.id
                    Log.d("playlistid", playlistID)
                    val n = playlist.allSongs.size
                    val groupSize = 3
                    // TODO : konkruentnost za array
                    val songIds = Array<String>(n, init = { "" })
                    // Thread pool mozda?
                    var i = 0
                    while (i < n){
                        val threadCount = min(groupSize, n - i)
                        val threads = Array<Thread>(threadCount, init = {
                            Thread(Runnable {
                                val song = playlist.allSongs[i + it]
                                val songTitle = song.artist.name + " - " + song.title
                                songIds[i + it] = getSongID(songTitle)
                            })
                        } )
                        threads.forEach {it.start()}
                        threads.forEach {it.join()}
                        i += threadCount
                    }

                    //Log.d("SongIds", songIds.contentToString())

                    var urlForSongInsertion = "https://www.googleapis.com/youtube/v3/playlistItems?"
                    urlForSongInsertion += "part=snippet"
                    urlForSongInsertion += "&key=" + ourID
                    urlForSongInsertion += "&access_token=" + accessToken

                    for (songid in songIds){
                        if (songid == "")
                            continue
                        val body =  """ { "snippet" : { "playlistId" : "$playlistID", "resourceId" : { "kind" : "youtube#video", "videoId" : "$songid"}}}"""
                        val res = ourPostMethodBody(body, urlForSongInsertion)
                        if (!res.contains("Success") || res.contains("Failure") || res.contains("Error")){
                            Log.d("Error", res)
                            // TODO : nesto nije htela pesma se ubaci
                            val intent = Intent(this, Error::class.java).apply{
                                putExtra("Error", "It is not possible to insert a song in playlist!")
                            }
                            startActivity(intent)
                        }
                    }
                    updateUIForConversionEnd()
                }
            }
        }
    }

    private fun updateUIForConvertionBegin() {
        val textView = findViewById<TextView>(R.id.pesme)
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        val signOutButton = findViewById<Button>(R.id.LogOut)
        val StartConvertionButton = findViewById<Button>(R.id.StartConvertion)
        val s = "Please wait while we make your playlists (This might take a while!)"
        textView.apply {
            text = s
        }
        signOutButton.alpha = 0.0f
        StartConvertionButton.alpha = 0.0f
        signInButton.alpha = 0.0f
    }

    private fun updateUIForConversionEnd(){
        val textView = findViewById<TextView>(R.id.pesme)
        val s = "Thank you for using our app. Check your youTube account for you newly created playlists."
        textView.apply {
            text = s
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getSongID(songTitle: String) : String {
        //Log.d("Song title", songTitle)
        var encoded = java.net.URLEncoder.encode(songTitle, "utf-8")
        var urlStr = "https://www.youtube.com/results?search_query=" + encoded
        //urltoSong = "https://www.youtube.com/results?search_query=Lana+Del+Rey+-+Art+Deco"
        //Log.d("encoded", encoded)
        //Log.d("Song url", urlStr)
        var videoId : String = ""

        try{
            val url = URL(urlStr)
            val connection : HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "curl/7.64.1")
            connection.inputStream.bufferedReader().use {
                for (line in it.lines()){
                    val match = regex.find(line)
                    if (match != null){
                        videoId = match.groupValues[1]
                        break
                    }
                }
            }
            //Log.d("videoID", videoId)
        }
        catch(e : MalformedURLException){
            // TODO: srediti
            Log.d("Error", e.stackTrace.toString())
            val intent = Intent(this, Error::class.java).apply{
                putExtra("Error", e.message)
            }
            startActivity(intent)
            //exitProcess(1)

        }

        return videoId
    }

    private fun signIn(mGoogleSignInClient: GoogleSignInClient) {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        onActivityResult(30, RC_SIGN_IN, signInIntent)
    }

    private fun signOut(mGoogleSignInClient: GoogleSignInClient) {
        updateUI(null)
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }


    private fun updateUI(user: GoogleSignInAccount?) {
        if (user != null){
            findViewById<TextView>(R.id.pesme).apply {
                var s : String = "Welcome back " + user.displayName + " ! \n"
                s  += "Would you like to continue with this account?"
                text = s
                //ourPostMethod(user)
            }
            findViewById<SignInButton>(R.id.sign_in_button).alpha = 0.0f
            findViewById<Button>(R.id.LogOut).alpha = 1.0f
            findViewById<Button>(R.id.StartConvertion).alpha = 1.0f
        }
        else {
            findViewById<TextView>(R.id.pesme).apply {
                var s : String = "Please login to one of your Google accounts."
                text = s
                //ourPostMethod(user)
            }
            findViewById<SignInButton>(R.id.sign_in_button).alpha = 1.0f
            findViewById<Button>(R.id.LogOut).alpha = 0.0f
            findViewById<Button>(R.id.StartConvertion).alpha = 0.0f
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(FragmentActivity.TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
            Log.d("error", e.stackTraceToString())
            val intent = Intent(this, Error::class.java).apply{
                putExtra("Error", e.message)
            }
            startActivity(intent)
            //exitProcess(1)
        }
    }

    private fun ourPostMethodBody(s: String, url: String): String {
        // s je body
        var res = ""
        val thread = Thread {
            val (x, y, result) = Fuel.post(url)
                    .body(s)
                    .responseString()
            res = result.toString()
        }
        thread.start()
        thread.join()
        return res
    }

    private fun ourPostMethodList(l: List<Pair<String, String?>>, url: String) : String {
        var res : String = ""
        val thread = Thread {
            val (x, y, result) = url
                    .httpPost(l)
                    .responseString()
            res = result.toString()
        }
        thread.start()
        thread.join()
        return res
    }
}

















