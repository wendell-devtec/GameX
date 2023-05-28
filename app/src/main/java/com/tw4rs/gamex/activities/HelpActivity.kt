package com.tw4rs.gamex.activities


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tw4rs.gamex.R
import com.tw4rs.gamex.app.WebActivity

class HelpActivity : AppCompatActivity() {
    private var valor = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        val about = findViewById<CardView>(R.id.about)
        about.setOnClickListener {
            valor = 4
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra("value", valor)
            startActivity(intent)
        }
        val yt = findViewById<ConstraintLayout>(R.id.chanel)
        yt.setOnClickListener {
            var url = "https://www.youtube.com/c/TW4RSDEV"
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        val terms = findViewById<CardView>(R.id.terms)
        terms.setOnClickListener {
            valor = 3
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra("value", valor)
            startActivity(intent)
        }
        val privacy = findViewById<CardView>(R.id.privacy)
        privacy.setOnClickListener {
            valor = 1
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra("value", valor)
            startActivity(intent)
        }
        val faq = findViewById<CardView>(R.id.faq)
        faq.setOnClickListener {
            valor = 2
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra("value", valor)
            startActivity(intent)
        }
        val email = findViewById<CardView>(R.id.email)
        email.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "tw4rs.dev2020@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Preciso de Suporte com o GameX")
            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Venho por meio da opção de contato por email para pedi ajuda sobre : "
            )
            startActivity(Intent.createChooser(emailIntent, "Preciso de Suporte com o GameX"))
        }
        val siteView = findViewById<CardView>(R.id.tw4rs)
        siteView.setOnClickListener {
            var url = "https://tw4rs.com"
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        val instagramView = findViewById<CardView>(R.id.instagram)
        instagramView.setOnClickListener {
            var url = "https://www.instagram.com/gamex_app/"
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        val telegramView = findViewById<CardView>(R.id.telegram)

        telegramView.setOnClickListener {
            var url = "https://t.me/+WxzIegaGqVczYzcx"
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }
}