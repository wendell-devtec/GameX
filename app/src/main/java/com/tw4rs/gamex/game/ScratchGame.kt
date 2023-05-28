/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.game

import android.content.ContentValues
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.tw4rs.gamex.R
import com.tw4rs.gamex.util.BaseActivty
import dev.skymansandy.scratchcardlayout.listener.ScratchListener
import dev.skymansandy.scratchcardlayout.ui.ScratchCardLayout
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class ScratchGame : BaseActivty(), MaxRewardedAdListener {
    private var randomnumber = 0
    private var popup = false
    private var showpopup = true
    private var imgback: ImageView? = null
    private var scretchcard: ScratchCardLayout? = null
    private var tvNumber: TextView? = null
    private lateinit var rewardedAd: MaxRewardedAd
    private var retryAttempt = 0
    private var progres: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch_game)
        initMain()
        rewardedAd = MaxRewardedAd.getInstance("b29cb157261fea94", this)
        rewardedAd.setListener(this)
        rewardedAd.loadAd()
    }

    private fun initMain() {
        scretchcard = findViewById(R.id.scretchcard)
        imgback = findViewById(R.id.imgback)
        tvNumber = findViewById(R.id.tvNumber)
        setScretchCard()
    }

    private fun setScretchCard() {
        scretchcard!!.resetScratch()
        popup = false
        val randomcard = Random().nextInt(4)
        Log.d(ContentValues.TAG, "setScretchCard: $randomcard")
        when (randomcard) {
            0 -> {
                scretchcard!!.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.s1))
                imgback!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ss1))
            }
            1 -> {
                scretchcard!!.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.s2))
                imgback!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ss2))
            }
            2 -> {
                scretchcard!!.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.s3))
                imgback!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ss3))
            }
            3 -> {
                scretchcard!!.setScratchDrawable(ContextCompat.getDrawable(this, R.drawable.s4))
                imgback!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ss4))
            }
            else -> {}
        }
        val min = "5"
        val min1 = min.toInt()
        val max = "60"
        val max1 = max.toInt()
        randomnumber = Random().nextInt(max1 - min1 + 1) + min1
        tvNumber!!.text = randomnumber.toString()
        scretchcard!!.setScratchListener(object : ScratchListener {
            override fun onScratchStarted() {
                Log.d(ContentValues.TAG, "onScratchStarted: start")
            }

            override fun onScratchProgress(scratchCardLayout: ScratchCardLayout, atLeastScratchedPercent: Int) {
                Log.d(ContentValues.TAG, "onScratchProgress: $atLeastScratchedPercent")
                if (atLeastScratchedPercent > 5) {
                    if (!popup) {
                        popup = true
                        scretchcard!!.revealScratch()
                        if (showpopup) {
                            showpopup = false
                            showPopup(randomnumber)
                        }
                        Toast.makeText(
                            applicationContext,
                            "Revelado sua pontuação",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onScratchComplete() {
                Log.d(ContentValues.TAG, "onScratchComplete: complete")
            }
        })
    }

    private fun showPopup(i: Int) {
        val sweetAlertDialog = SweetAlertDialog(this@ScratchGame, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.titleText = "Parabéns você ganhou$i"
        sweetAlertDialog.contentText = "Clique em Coletar para adicionar seus pontos"
        sweetAlertDialog.setConfirmButton("Coletar") { sweetAlertDialog1: SweetAlertDialog ->
            sweetAlertDialog1.dismissWithAnimation()
            showads()
            PontsS = i
        }.show()
    }

    private fun showads() {
        setScretchCard()
        progres = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        progres!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        progres!!.titleText = "Mostrando Ads"
        progres!!.setCancelable(false)
        progres!!.show()
        if (rewardedAd.isReady) {
            rewardedAd.showAd()
        }
    }

    // MAX Ad Listener
    override fun onAdLoaded(maxAd: MaxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0
        progres!!.dismissWithAnimation()
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis(
            2.0.pow(6.coerceAtMost(retryAttempt).toDouble()).toLong()
        )
        Handler(Looper.getMainLooper()).postDelayed({ rewardedAd.loadAd() }, delayMillis)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Não Foi possivel carregar o Video")
        alertDialog.setMessage("Tente novamente Ou mais tarde")
        alertDialog.setPositiveButton("OK") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alertDialog.show()
        alertDialog.create()
    }

    override fun onAdDisplayFailed(maxAd: MaxAd, error: MaxError) {
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd.loadAd()
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Não Foi possivel carregar o Video")
        alertDialog.setMessage("Tente novamente Ou mais tarde")
        alertDialog.setPositiveButton("OK") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alertDialog.show()
        alertDialog.create()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {}
    override fun onAdClicked(maxAd: MaxAd) {}
    override fun onAdHidden(maxAd: MaxAd) {
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd.loadAd()
    }

    override fun onRewardedVideoStarted(maxAd: MaxAd) {}
    override fun onRewardedVideoCompleted(maxAd: MaxAd) {}
    override fun onUserRewarded(maxAd: MaxAd, maxReward: MaxReward) {
        // Rewarded ad was displayed and user should receive the reward
        upadateDate(8)
    }
}