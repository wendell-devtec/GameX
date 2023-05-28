/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.game

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.tw4rs.gamex.R
import com.tw4rs.gamex.adapter.BubbleAdapter
import com.tw4rs.gamex.util.BaseActivty
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class BubleGame : BaseActivty(), MaxAdListener {
    private var interstitialAd: MaxInterstitialAd? = null
    private var retryAttempt = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buble_game)
        interstitialAd = MaxInterstitialAd("d56da0e6a0715645", this)
        interstitialAd!!.setListener(this)

        // Load the first ad
        interstitialAd!!.loadAd()
        val recyclerView = findViewById<RecyclerView>(R.id.rvBubble)
        val bubbleAdapter = BubbleAdapter { i: Int ->
            Log.d(ContentValues.TAG, "onCreate: $i")
            if (i == 0) {
                showLoosePopup()
            } else {
                showPopup(i)
            }
        }
        recyclerView.adapter = bubbleAdapter
    }

    private fun showPopup(i: Int) {
        val sweetAlertDialog = SweetAlertDialog(this@BubleGame, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.titleText = "Parabéns você ganhou"
        sweetAlertDialog.contentText = "Clique em Coletar para adicionar seus pontos"
        sweetAlertDialog.setConfirmButton("Coletar") { sweetAlertDialog1: SweetAlertDialog ->
            sweetAlertDialog1.dismissWithAnimation()
            if (interstitialAd!!.isReady) {
                interstitialAd!!.showAd()
            }
            Ponts = i
        }.show()
    }

    private fun showLoosePopup() {
        val sweetAlertDialog1 = SweetAlertDialog(this@BubleGame, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog1.changeAlertType(SweetAlertDialog.ERROR_TYPE)
        sweetAlertDialog1.setCancelable(false)
        sweetAlertDialog1.titleText = "Não foi dessa vez tente novamente"
        sweetAlertDialog1.contentText = "Tente novamente para ganhar pontos"
        sweetAlertDialog1.setConfirmButton("Ok") { sweetAlertDialog2: SweetAlertDialog ->
            sweetAlertDialog2.dismissWithAnimation()
            if (interstitialAd!!.isReady) {
                interstitialAd!!.showAd()
            }
        }.show()
    }

    // MAX Ad Listener
    override fun onAdLoaded(maxAd: MaxAd) {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        // Interstitial ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis(
            2.0.pow(6.coerceAtMost(retryAttempt).toDouble()).toLong()
        )
        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd!!.loadAd() }, delayMillis)
    }

    override fun onAdDisplayFailed(maxAd: MaxAd, error: MaxError) {
        // Interstitial ad failed to display. We recommend loading the next ad
        interstitialAd!!.loadAd()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {}
    override fun onAdClicked(maxAd: MaxAd) {}
    override fun onAdHidden(maxAd: MaxAd) {
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd!!.loadAd()
        upadateDate(5)
    }
}