package com.example.projekatppandroid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.stringify
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

class Converte : AppCompatActivity() {

    @Serializable
    data class YoutubePlaylistCreation(val snippet : Snippet, val status : Status)

    @Serializable
    data class Snippet(val title : String, val description : String)

    @Serializable
    data class Status(val privacyStatus : String)

    companion object {
        const val TAG = "GoogleActivity"
        const val RC_SIGN_IN = 9001
    }

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

        var allPlaylists = Json { isLenient = true }.decodeFromString<MutableList<PlaylistForYoutube>>(songs.toString())

//        val textView = findViewById<TextView>(R.id.pesme).apply {
//            var tmp = ""
//
//            for (k in res){
//                tmp = tmp + k.title + " : " + k.allSongs + "\n"
//            }
//
//            text = tmp
//        }

        val clientID = getString(R.string.clientID)
        val secret = getString(R.string.secret)
        val ourID = getString(R.string.ourID)
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

        signInButton.setOnClickListener{
            signIn(mGoogleSignInClient)
        }

        val signOutButton = findViewById<Button>(R.id.LogOut)
        signOutButton.setOnClickListener {
            signOut(mGoogleSignInClient)
        }

        val StartConvertionButton = findViewById<Button>(R.id.StartConvertion)
        StartConvertionButton.setOnClickListener{
            val user = GoogleSignIn.getLastSignedInAccount(this)
            val grant = "authorization_code"
            if (user != null) {
                val l = (listOf("client_id" to clientID,
                        "code" to user.serverAuthCode,
                        "client_secret" to secret,
                        "redirect_uri" to "",
                        "grant_type" to grant))

                var accessTokenStringFormat = ourPostMethodList(l, urlForAccessToken)
                accessTokenStringFormat = accessTokenStringFormat.replace("[", "{")
                accessTokenStringFormat = accessTokenStringFormat.replace("]", "}")

                // TODO: tipa if accessTokenStringFormat.contanintsss("error") onda error screen
                // TODO: isto tako za ono wrong sranje kod deezer (to je ono kad se vraćaš pa error)
                val accessTokenJsonFormat = Json{ isLenient = true; ignoreUnknownKeys = true }.decodeFromString<Success>(accessTokenStringFormat)

                var accessToken = accessTokenJsonFormat.Success.access_token
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
                    val playlistInfo = YoutubePlaylistCreation(Snippet(title, description), Status(status))
                    try {
                        var l = Json.encodeToString(YoutubePlaylistCreation.serializer(), playlistInfo)
                        var list = listOf("snippet" to listOf("title" to title, "description" to description),
                                "status" to listOf("privacyStatus" to status))
                        val bodyJson = """ { "snippet" : { "title" : "$title", "description" : "$description"}, "status" : { "privacyStatus" : "$status"}}"""
                        //Log.d("LISTAAAA", list.toString())
                        var response = ourPostMethodBody(bodyJson, urlForInsertPlaylists)
                        Log.d("Our Program", response)
                    }
                    catch (e : SerializationException){
                        // TODO: i ovo treba lepse nekako
                        Log.d("Our program", "Something went wrong while serializing, please try again")
                    }
                    //var response = ourPostMethodJsonString(l, urlForInsertPlaylists)

                }
            }
        }



    }

    private fun signIn(mGoogleSignInClient: GoogleSignInClient) {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        onActivityResult(30, RC_SIGN_IN, signInIntent)
    }

    private fun signOut(mGoogleSignInClient: GoogleSignInClient) {
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
        }
    }

    private fun ourPostMethodBody(s: String, url: String): String {
        // s je JSONString od klase neke
        var res = ""
        val thread = Thread {
            val (x, y, result) = Fuel.post(url)
                    .body(s)
                    .response()
//            findViewById<TextView>(R.id.pesme).apply {
//                text = result.toString()
//            }
            res += s
            res += x.toString()
            res += y.toString()
        }
        thread.start()
        thread.join()
        return res
    }

    private fun ourPostMethodList(l: List<Pair<String, String?>>, url : String) : String {
        var res : String = ""
        val thread = Thread {
            val (x, y, result) = url
                    .httpPost(l)
                    .responseString()
//            findViewById<TextView>(R.id.pesme).apply {
//                text = result.toString()
//            }
            res = result.toString()
            Log.d("My tast", x.toString() + "\n\n\n" + y.toString())
        }
        thread.start()
        thread.join()
        return res
    }
}

















