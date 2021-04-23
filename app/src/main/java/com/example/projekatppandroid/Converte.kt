package com.example.projekatppandroid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

class Converte : AppCompatActivity() {

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

        var res = Json { isLenient = true }.decodeFromString<MutableList<PlaylistForYoutube>>(songs.toString())

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
        val urlForAccessToken = "https://oauth2.googleapis.com/token"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(clientID)
                .requestIdToken(clientID)
                .requestScopes(Scope("https://www.googleapis.com/auth/youtube"))
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
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                ourPostMethod(account, clientID, secret, urlForAccessToken)

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

    private fun ourPostMethod(user: GoogleSignInAccount, clientID : String, secret : String, url : String) : String {
        var res : String = ""
        val thread = Thread {
            val grant : String = "authorization_code"
            val (x, y, result) = url
                    .httpPost(listOf("client_id" to clientID,
                            "code" to user.serverAuthCode,
                            "client_secret" to secret,
                            "redirect_uri" to "",
                            "grant_type" to grant))
                    .responseString()
            findViewById<TextView>(R.id.pesme).apply {
                //ourPostMethod(user)
                //val s1  = BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))
                text = result.toString()
            }
            res = result.toString()
        }
        thread.start()
        thread.join()
        return res
    }
}

















