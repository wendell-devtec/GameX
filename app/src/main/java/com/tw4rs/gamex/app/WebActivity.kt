package com.tw4rs.gamex.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.tw4rs.gamex.R

class WebActivity : AppCompatActivity() {
    private var theWebPage: WebView? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        theWebPage = findViewById(R.id.webview)
        theWebPage = WebView(this)
        theWebPage!!.settings.javaScriptEnabled = true
        theWebPage!!.settings.pluginState = WebSettings.PluginState.ON
        setContentView(theWebPage)
        val intent = intent
        when (intent.getIntExtra("value", 0)) {
            1 -> {
                theWebPage!!.loadUrl("https://tw4rs.com/apps/privacy.php")
            }
            2 -> {
                theWebPage!!.loadUrl("https://tw4rs.com/apps/faq.html")
            }
            3 -> {
                theWebPage!!.loadUrl("https://tw4rs.com/apps/terms.html")
            }
            4 -> {
                theWebPage!!.loadUrl("https://tw4rs.com/apps/aboutus.html")
            }
        }
    }

}