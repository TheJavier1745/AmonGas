package com.amongas.amongas

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WifiConfigActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_config)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (url?.contains("/guardar") == true) {
                    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    cm.bindProcessToNetwork(null)

                    // ⏪ Volver a red original
                    Handler(Looper.getMainLooper()).postDelayed({
                        val app = applicationContext as MainApplication
                        app.originalNetwork?.let {
                            cm.bindProcessToNetwork(it)
                        }
                        finish()
                    }, 3000)
                }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            webView.loadDataWithBaseURL(
                "http://192.168.4.1",
                "<script>window.location.replace('http://192.168.4.1/');</script>",
                "text/html",
                "UTF-8",
                null
            )
        }, 30000) // Espera 30 segundos
    }
}
