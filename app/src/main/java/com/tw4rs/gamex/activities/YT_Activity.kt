/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.activities

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.tw4rs.gamex.R
import com.tw4rs.gamex.model.AppModel
import es.dmoral.toasty.Toasty
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class YT_Activity : AppCompatActivity(), MaxAdListener {
    private var timers: CountDownTimer? = null
    var progressBars: CircularProgressBar? = null
    var tvTimer: TextView? = null
    var newtimer = 0
    var video_id: String? = null
    var id: String? = null
    private var point: String? = null
    private var timer = 0
    private var retryAttempt = 0
    private var youTubePlayerView: YouTubePlayerView? = null
    private lateinit var interstitialAd: MaxInterstitialAd
    private var pYT: SweetAlertDialog? = null
    private lateinit var userRef: DatabaseReference
    private var pointTv = 0
    var ads: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yt)

        //ads
        interstitialAd = MaxInterstitialAd("d56da0e6a0715645", this)
        interstitialAd.setListener(this)

        // Load the first ad
        interstitialAd.loadAd()
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val user1 = mAuth.currentUser
        val userId = user1!!.uid
        userRef = database.reference.child("GameX").child("users").child(userId)
        databaseData


        timer = intent.getStringExtra("timer")!!.toInt()
        point = intent.getStringExtra("point")
        video_id = intent.getStringExtra("video_id")
        id = intent.getStringExtra("id")
        ads = intent.getStringExtra("ads")
        tvTimer = findViewById(R.id.tvTimer)
        progressBars = findViewById(R.id.progressBars)
        newtimer = timer * 1000
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        //lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView!!.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(video_id!!, 1f)
            }
        })
        timers = object : CountDownTimer(newtimer.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secs = millisUntilFinished / 1000
                progressBars!!.progress = 60f
                if (tvTimer != null) {
                    tvTimer!!.text = secs.toString()
                }
            }

            override fun onFinish() {
                showvideo()
            }
        }.start()
    }

    private val databaseData: Unit
        get() {
            val ytdialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            ytdialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            ytdialog.titleText = "Carregando seus dados"
            ytdialog.setCancelable(false)
            ytdialog.show()
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appModel = snapshot.getValue(AppModel::class.java)
                    if (appModel != null) {
                        pointTv = appModel.Coins
                    }
                    ytdialog.dismissWithAnimation()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@YT_Activity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
        }

    private fun showvideo() {
        pYT = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pYT!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pYT!!.titleText = "Carregando...."
        pYT!!.setCancelable(true)
        pYT!!.show()
        if (interstitialAd.isReady) {
            interstitialAd.showAd()
        }
    }

    ///Max
    override fun onAdLoaded(ad: MaxAd) {
        retryAttempt = 0
    }

    override fun onAdDisplayed(ad: MaxAd) {
        pYT!!.dismissWithAnimation()
    }

    override fun onAdHidden(ad: MaxAd) {
        interstitialAd.loadAd()
        val currentCoins = pointTv
        val updatedCoins = currentCoins + point!!.toInt()
        val map = HashMap<String, Any>()
        map["Coins"] = updatedCoins
        userRef.updateChildren(map).addOnCompleteListener {
            Toasty.success(
                this@YT_Activity,
                "TWSCASH",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onAdClicked(ad: MaxAd) {}
    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis(
            2.0.pow(6.coerceAtMost(retryAttempt).toDouble()).toLong()
        )
        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd.loadAd() }, delayMillis)
        Toasty.error(
            this@YT_Activity,
            "Erro ao carregar , tente novamente em poucos segundos",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        interstitialAd.loadAd()
        Toasty.error(
            this@YT_Activity,
            "Não foi possível exibir o video , tente novamente",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timers!!.cancel()
    }
}