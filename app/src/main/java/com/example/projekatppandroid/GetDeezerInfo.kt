package com.example.projekatppandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

const val GETTING_CODE = "com.example.myfirstapp.MESSAGE"
var urlSent : String = ""
val allInfo  = Configuration()


class GetDeezerInfo : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_deezer_info)
        //Log.d("JSon string: ", jsonString)
        // pravimo url koji pozivamo da bi dobili odgovarajući token
        var urlAuthenticantion = "https://connect.deezer.com/oauth/auth.php?"
        urlAuthenticantion += "app_id=" + allInfo.getD_appID()
        urlAuthenticantion += "&redirect_uri=" + allInfo.getD_redirect()
        urlAuthenticantion += "&perms=" + allInfo.getD_perms()
        urlAuthenticantion += "&request_method=POST"

        val twLoading = findViewById<TextView>(R.id.textViewIspis)
        var webView = findViewById<WebView>(R.id.mojWebView)
        webView.settings.setJavaScriptEnabled(true)

        // webView nam omogućava tačno ono sto je bio problem, da prikazemo login screen za Dizer
        webView.webViewClient = object : WebViewClient() {
            // ako nismo overridovali ovu funkciju, onda cim bi otisli da neki drugi url, odmah bi nam otvorio
            // na veb-pregledaču, sa ovime i dalje ostajemo u našem webview-u (sa nekim dodatkom)
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                if (url != null) {
                    view?.loadUrl(url)
                }

                // problem je što u nekom trenutku želimo da prestanemo sa webview i da napredujemo dalje
                // sa aplikacijom, zato proveravam da li smo stigli do odredjenog ulr i ako jesmo, tu stajemo
                // (stajemo na redirect url jer čim korisnik izabere da se login, odma ga baca na tu stranu)
                if (url?.startsWith("https://theappreciationengine.com/DeezerAuthenticator_Controller?")!!){
                    urlSent = url.toString()
                    //textView.apply {
                    //    text = ""
                    //}
                    finish()
                    getDeezerInfo()
                    //return false
                }
                return true
            }
        }

        //making the textView invisible
        twLoading.alpha = 0.0f

        // funkcija koja započinje zapravo webview
        webView.loadUrl(urlAuthenticantion)

    }

    // naš "skok" na sledeći deo apliakcije
    fun getDeezerInfo()
    {
        val intent = Intent(this@GetDeezerInfo, CheckingDeezerCode::class.java).apply {
            putExtra(GETTING_CODE, urlSent)
        }
        startActivity(intent)
    }

}


