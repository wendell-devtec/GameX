/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.activities.HomeActivity
import com.tw4rs.gamex.model.AppModel
import es.dmoral.toasty.Toasty
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class Tictoe : AppCompatActivity(), MaxAdListener {
    private val arrayButton = arrayOfNulls<Button>(10)
    private var vez = "X"
    private var jogadas = 0
    private val matriz = arrayOfNulls<String>(10)
    private var interstitialAd: MaxInterstitialAd? = null
    private var retryAttempt = 0
    private var textView: TextView? = null
    private lateinit var userRef: DatabaseReference
    private var pointTv = 0

    private  var tvPoint : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tictoe)

        //ads
        interstitialAd = MaxInterstitialAd("d56da0e6a0715645", this)
        interstitialAd!!.setListener(this)

        // Load the first ad
        interstitialAd!!.loadAd()
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val user1 = mAuth.currentUser!!
        val userId = user1.uid
        tvPoint = findViewById(R.id.pointTv)


        userRef = database.reference.child("GameX").child("users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val appmodel = snapshot.getValue(AppModel::class.java)!!
                pointTv = appmodel.Coins
                tvPoint!!.text = pointTv.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toasty.error(
                    this@Tictoe,
                    "Erro: " + error.message,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        })
        inicializaButtons()
        onClickButtons()

    }

    private fun inicializaButtons() {
        arrayButton[1] = findViewById<View>(R.id.btn1) as Button
        arrayButton[2] = findViewById<View>(R.id.btn2) as Button
        arrayButton[3] = findViewById<View>(R.id.btn3) as Button
        arrayButton[4] = findViewById<View>(R.id.btn4) as Button
        arrayButton[5] = findViewById<View>(R.id.btn5) as Button
        arrayButton[6] = findViewById<View>(R.id.btn6) as Button
        arrayButton[7] = findViewById<View>(R.id.btn7) as Button
        arrayButton[8] = findViewById<View>(R.id.btn8) as Button
        arrayButton[9] = findViewById<View>(R.id.btn9) as Button
        textView = findViewById(R.id.textView1)

    }

    private fun onClickButtons() {
        for (x in 1..9) {
            arrayButton[x]!!.setOnClickListener { jogada(x) }
            matriz[x] = ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun jogada(x: Int) {
        if (matriz[x] == "") {
            matriz[x] = vez
            jogadas++
            if (vez == "X") {
                vez = "O"
                textView!!.text = "É a vez do O"
            } else {
                vez = "X"
                textView!!.text = "É a vez do X"
            }
        }
        exibirButtons()
        verifica()
    }

    private fun exibirButtons() {
        for (x in 1..9) {
            arrayButton[x]!!.text = matriz[x]
        }
    }

    private fun verifica() {
        if (matriz[1] == matriz[2] && matriz[1] == matriz[3] && matriz[1] != "") {
            ganhador(matriz[1])
            return
        }
        if (matriz[4] == matriz[5] && matriz[4] == matriz[6] && matriz[4] != "") {
            ganhador(matriz[4])
            return
        }
        if (matriz[7] == matriz[8] && matriz[7] == matriz[9] && matriz[7] != "") {
            ganhador(matriz[7])
            return
        }
        if (matriz[1] == matriz[4] && matriz[1] == matriz[7] && matriz[1] != "") {
            ganhador(matriz[1])
            return
        }
        if (matriz[2] == matriz[5] && matriz[2] == matriz[8] && matriz[2] != "") {
            ganhador(matriz[2])
            return
        }
        if (matriz[3] == matriz[6] && matriz[3] == matriz[9] && matriz[3] != "") {
            ganhador(matriz[3])
            return
        }
        if (matriz[1] == matriz[5] && matriz[1] == matriz[9] && matriz[1] != "") {
            ganhador(matriz[1])
            return
        }
        if (matriz[3] == matriz[5] && matriz[3] == matriz[7] && matriz[3] != "") {
            ganhador(matriz[3])
            return
        }
        if (jogadas == 9) {
            ganhador("")
        }
    }

    private fun ganhador(ganhador: String?) {
        val builder = SweetAlertDialog(this@Tictoe, SweetAlertDialog.SUCCESS_TYPE)
        builder.titleText = "Resultado"
        builder.confirmText = "Novo Jogo"
        if (ganhador == "") {
            builder.contentText = "DEU VELHA!"
            if (interstitialAd!!.isReady) {
                interstitialAd!!.showAd()
                interstitialAd!!.setListener(object : MaxAdListener {
                    ///Max
                    override fun onAdLoaded(ad: MaxAd) {
                        retryAttempt = 0
                    }

                    override fun onAdDisplayed(ad: MaxAd) {}
                    override fun onAdHidden(ad: MaxAd) {
                        interstitialAd!!.loadAd()
                    }

                    override fun onAdClicked(ad: MaxAd) {}
                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                        retryAttempt++
                        val delayMillis = TimeUnit.SECONDS.toMillis(
                            2.0.pow(6.coerceAtMost(retryAttempt).toDouble())
                                .toLong()
                        )
                        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd!!.loadAd() }, delayMillis)
                        Toasty.error(
                            this@Tictoe,
                            "Erro ao carregar , tente novamente em poucos segundos",
                            Toasty.LENGTH_LONG,
                            true
                        ).show()
                    }

                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                        interstitialAd!!.loadAd()
                        Toasty.error(
                            this@Tictoe,
                            "Não foi possível exibir o video , tente novamente",
                            Toasty.LENGTH_LONG,
                            true
                        ).show()
                    }
                })
            }
        } else {
            if (ganhador == "X") {
                builder.contentText = "\"X\" é o vencedor"
                if (interstitialAd!!.isReady) {
                    interstitialAd!!.showAd()
                }
            } else {
                builder.contentText = "\"O\" é o vencedor"
                if (interstitialAd!!.isReady) {
                    interstitialAd!!.showAd()
                }
            }
        }
        builder.setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
            jogadas = 0
            for (x in 1..9) {
                matriz[x] = ""
            }
            exibirButtons()
            sweetAlertDialog.dismissWithAnimation()
        }
        builder.cancelText = "Sair"
        builder.setCancelClickListener { sweetAlertDialog: SweetAlertDialog ->
            startActivity(Intent(this, HomeActivity::class.java))
            sweetAlertDialog.dismissWithAnimation()
            if (interstitialAd!!.isReady) {
                interstitialAd!!.showAd()
                interstitialAd!!.setListener(object : MaxAdListener {
                    ///Max
                    override fun onAdLoaded(ad: MaxAd) {
                        retryAttempt = 0
                    }

                    override fun onAdDisplayed(ad: MaxAd) {}
                    override fun onAdHidden(ad: MaxAd) {
                        interstitialAd!!.loadAd()
                        startActivity(Intent(this@Tictoe, HomeActivity::class.java))
                    }

                    override fun onAdClicked(ad: MaxAd) {}
                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                        retryAttempt++
                        val delayMillis = TimeUnit.SECONDS.toMillis(
                            2.0.pow(6.coerceAtMost(retryAttempt).toDouble()).toLong()
                        )
                        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd!!.loadAd() }, delayMillis)
                        Toasty.error(
                            this@Tictoe,
                            "Erro ao carregar , tente novamente em poucos segundos",
                            Toasty.LENGTH_LONG,
                            true
                        ).show()
                    }

                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                        interstitialAd!!.loadAd()
                        Toasty.error(
                            this@Tictoe,
                            "Não foi possível exibir o video , tente novamente",
                            Toasty.LENGTH_LONG,
                            true
                        ).show()
                    }
                })
            }
        }
            .show()
    }

    ///Max
    override fun onAdLoaded(ad: MaxAd) {
        retryAttempt = 0
    }

    override fun onAdDisplayed(ad: MaxAd) {
        //  pAds.dismissWithAnimation();
    }

    override fun onAdHidden(ad: MaxAd) {
        interstitialAd!!.loadAd()
        val currentCoins = pointTv
        val updatedCoins = currentCoins + 45
        val map = HashMap<String, Any>()
        map["Coins"] = updatedCoins
        userRef.updateChildren(map).addOnCompleteListener {
            Toasty.success(
                this@Tictoe,
                "+45 TWSCASH",
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
        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd!!.loadAd() }, delayMillis)
        Toasty.error(
            this@Tictoe,
            "Erro ao carregar , tente novamente em poucos segundos",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        interstitialAd!!.loadAd()
        Toasty.error(
            this@Tictoe,
            "Não foi possível exibir o video , tente novamente",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }
}