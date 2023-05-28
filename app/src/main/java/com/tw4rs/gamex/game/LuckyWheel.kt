/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.game

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.tw4rs.gamex.R
import com.tw4rs.gamex.activities.NoInternetActivity
import com.tw4rs.gamex.util.BaseActivty
import rubikstudio.library.LuckyWheelView
import rubikstudio.library.model.LuckyItem
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class LuckyWheel : BaseActivty(), MaxRewardedAdListener {
    private var todayString: String? = null
    var data: MutableList<LuckyItem> = ArrayList()
    private lateinit var rewardedAd: MaxRewardedAd
    private var retryAttempt = 0
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rewardedAd = MaxRewardedAd.getInstance("b29cb157261fea94", this)
        rewardedAd.setListener(this)
        rewardedAd.loadAd()
        val handler = Handler(mainLooper)
        val runnable: Runnable = object : Runnable {
            override fun run() {
                val manager = applicationContext
                    .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = manager.activeNetworkInfo
                if (null != activeNetwork) {
                    activeNetwork.type
                } else {
                    val intent = Intent(this@LuckyWheel, NoInternetActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isAcceptingText) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
        setContentView(R.layout.activity_lucky_wheel)
        val luckyWheelView = findViewById<LuckyWheelView>(R.id.luckyWheel)
        findViewById<View>(R.id.play).isEnabled = true
        findViewById<View>(R.id.play).alpha = 1f
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        todayString = year.toString() + "" + month + "" + day
        val spinChecks = getSharedPreferences("SPINCHECK", 0)
        val luckyItem1 = LuckyItem()
        luckyItem1.text = "10"
        luckyItem1.color = Color.parseColor("#161616")
        data.add(luckyItem1)
        val luckyItem2 = LuckyItem()
        luckyItem2.text = "25"
        luckyItem2.color = ContextCompat.getColor(applicationContext, R.color.Spinwell140)
        data.add(luckyItem2)
        val luckyItem3 = LuckyItem()
        luckyItem3.text = "10"
        luckyItem3.color = Color.parseColor("#161616")
        data.add(luckyItem3)
        val luckyItem4 = LuckyItem()
        luckyItem4.text = "50"
        luckyItem4.color = ContextCompat.getColor(applicationContext, R.color.Spinwell140)
        data.add(luckyItem4)
        val luckyItem5 = LuckyItem()
        luckyItem5.text = "15"
        luckyItem5.color = Color.parseColor("#161616")
        data.add(luckyItem5)
        val luckyItem6 = LuckyItem()
        luckyItem6.text = "20"
        luckyItem6.color = ContextCompat.getColor(applicationContext, R.color.Spinwell140)
        data.add(luckyItem6)
        val luckyItem7 = LuckyItem()
        luckyItem7.text = "45"
        luckyItem7.color = Color.parseColor("#161616")
        data.add(luckyItem7)
        val luckyItem8 = LuckyItem()
        luckyItem8.text = "70"
        luckyItem8.color = ContextCompat.getColor(applicationContext, R.color.Spinwell140)
        data.add(luckyItem8)
        luckyWheelView.setData(data)
        luckyWheelView.setRound(randomRound)
        findViewById<View>(R.id.play).setOnClickListener {
            val index = randomIndex
            luckyWheelView.startLuckyWheelWithTargetIndex(index)
            val spins = spinChecks.edit()
            spins.putBoolean(todayString, true)
            spins.apply()
            findViewById<View>(R.id.play).isEnabled = false
            findViewById<View>(R.id.play).alpha = .5f
        }
        luckyWheelView.setLuckyRoundItemSelectedListener { index: Int ->
            if (index == 1) {
                coin = 10
            }
            if (index == 2) {
                coin = 25
            }
            if (index == 3) {
                coin = 10
            }
            if (index == 4) {
                coin = 50
            }
            if (index == 5) {
                coin = 15
            }
            if (index == 6) {
                coin = 20
            }
            if (index == 7) {
                coin = 45
            }
            if (index == 8) {
                coin = 70
            }
            if (rewardedAd.isReady) {
                rewardedAd.showAd()
            }
        }
    }

    private val randomIndex: Int
        get() {
            val ind = intArrayOf(1, 2, 3, 4, 5, 6, 7)
            val rand = Random().nextInt(ind.size)
            return ind[rand]
        }
    private val randomRound: Int
        get() {
            val rand = Random()
            return rand.nextInt(7)
        }

    // MAX Ad Listener
    override fun onAdLoaded(maxAd: MaxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis(
            2.0.pow(6.coerceAtMost(retryAttempt).toDouble()).toLong()
        )
        Handler(Looper.getMainLooper()).postDelayed({ rewardedAd.loadAd() }, delayMillis)
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

    override fun onRewardedVideoStarted(ad: MaxAd) {}
    override fun onRewardedVideoCompleted(ad: MaxAd) {}
    override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
        upadateDate(4)
    }
}