package com.example.projekatppandroid

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

const val GETTING_CODE = "com.example.myfirstapp.MESSAGE"
var urlSent : String = ""
val allInfo  = allImportantDeezerInfomation()


class GetDeezerInfo : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_deezer_info)

        // pravimo url koji pozivamo da bi dobili odgovarajući token
        var urlAuthenticantion = "https://connect.deezer.com/oauth/auth.php?"
        urlAuthenticantion += "app_id=" + allInfo.getappID()
        urlAuthenticantion += "&redirect_uri=" + allInfo.getredirect()
        urlAuthenticantion += "&perms=" + allInfo.getperms()
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
                //textView.apply {
                //    text = url
                //}

                // problem je što u nekom trenutku želimo da prestanemo sa webview i da napredujemo dalje
                // sa aplikacijom, zato proveravam da li smo stigli do odredjenog ulr i ako jesmo, tu stajemo
                // (stajemo na redirect url jer čim korisnik izabere da se login, odma ga baca na tu stranu)
                if (url?.startsWith("https://theappreciationengine.com/DeezerAuthenticator_Controller?")!!){
                    urlSent = url.toString()
                    //textView.apply {
                    //    text = ""
                    //}
                    getDeezerInfo()
                }
                return true
            }
        }

        //making the textView invisible
        twLoading.alpha = 0.0f

        // funkcija koja započinje zapravo webview
        webView.loadUrl(urlAuthenticantion)



        //  mozda pomogne u buducnosti ne znam
//        val uri: Uri = Uri.parse(url) // missing 'http://' will cause crashed
//
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        startActivity(intent)

        //val queue = Volley.newRequestQueue(this)

// Request a string response from the provided URL.



 //Request a string response from the provided URL.
//        val stringRequest = StringRequest(
//            Request.Method.GET, newUrl,
//              Response.Listener<String?> {
//                fun onResponse(response: String) {
//                    textView.apply {
//                        text = "Response is: " + response.substring(0, 500)
//                    }
//                    // Display the first 500 characters of the response string.
//                }
//            }, object : Response.ErrorListener {
//                override fun onErrorResponse(error: VolleyError?) {
//                    textView.apply {
//                        if (error != null) {
//                            text = error.message.toString()
//                        }
//                        else {
//                            text = "nije"
//                        }
//                    }
//                }
//            })
//        queue.add(stringRequest)



//        val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
//            // Handle the returned Uri
//        }

        //startActivity(Intent(Intent.ACTION_MAIN, Uri.parse(url)))


//        var res : String = "blabla"
//        val webpage: Uri = Uri.parse(url)
//        val intent = Intent(Intent.ACTION_VIEW, webpage)
//        //res = sendBroadcast(intent).toString()
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivity(intent).toString()
//        }

        //res = intent.dataString.toString()

//        textView.apply {
//            text = res
//        }


//        intent = Intent(Intent.ACTION_VIEW)
//        intent.setData(Uri.parse(url))
//        ////intent.data
//        onActiv
//        startActivity(intent)
//
//        textView.apply {
//            text =
//        }


//        var trackId = 757807
//        val uri = "http://www.deezer.com/track/$trackId"
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setData(Uri.parse(uri))
//        startActivity(intent)

       // val thread = Thread {
//            try {
//                val myURL = URL(url)
//                //val content = myURL.readText()
//                val myConn: HttpURLConnection = myURL.openConnection() as HttpURLConnection
//                myConn.setRequestMethod("GET")
//
//                System.out.println("Response Code: " + myConn.getResponseCode())
//                val `in`: InputStream = BufferedInputStream(myConn.getInputStream())
//                val response: String = org.apache.commons.io.IOUtils.toString(`in`, "UTF-8")
//                //myConn.connect()
//                textView.apply {
//                    text = response
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//        thread.start()
//        
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


