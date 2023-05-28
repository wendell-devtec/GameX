package com.tw4rs.gamex.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
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
import com.tw4rs.gamex.model.AppModel
import es.dmoral.toasty.Toasty
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class DiceGame : AppCompatActivity(), MaxAdListener {
    private var img1: ImageView? = null
    private var img2: ImageView? = null
    private var txtPoint: TextView? = null
    private var choose = 0
    private var userRef: DatabaseReference? = null
    private var interstitialAd: MaxInterstitialAd? = null
    private var retryAttempt = 0
    private var pointTv: Long = 0
    private var valot = 0
    private var pAds: SweetAlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dice_game)
        init()

        //ads
        interstitialAd = MaxInterstitialAd("d56da0e6a0715645", this)
        interstitialAd!!.setListener(this)

        // Load the first ad
        interstitialAd!!.loadAd()
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val user1 = mAuth.currentUser!!
        val userId = user1.uid
        userRef = database.reference.child("GameX").child("users").child(userId)
        data
    }

    private val data: Unit
        get() {
            userRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appModel = snapshot.getValue(AppModel::class.java)
                    pointTv = appModel!!.Coins.toLong()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(this@DiceGame, "Erro: " + error.message, Toast.LENGTH_SHORT, true)
                        .show()
                }
            })
        }

    private fun init() {
        val rolldice2 = findViewById<Button>(R.id.rolldice2)
        val rolldice3 = findViewById<Button>(R.id.rolldice3)
        val rolldice4 = findViewById<Button>(R.id.rolldice4)
        val rolldice5 = findViewById<Button>(R.id.rolldice5)
        val rolldice6 = findViewById<Button>(R.id.rolldice6)
        val rolldice7 = findViewById<Button>(R.id.rolldice7)
        val rolldice8 = findViewById<Button>(R.id.rolldice8)
        val rolldice9 = findViewById<Button>(R.id.rolldice9)
        val rolldice10 = findViewById<Button>(R.id.rolldice10)
        val rolldice11 = findViewById<Button>(R.id.rolldice11)
        val rolldice12 = findViewById<Button>(R.id.rolldice12)
        txtPoint = findViewById(R.id.txtDice)
        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        rolldice2.setOnClickListener {
            choose = 2
            playGame()
        }
        rolldice3.setOnClickListener {
            choose = 3
            playGame()
        }
        rolldice4.setOnClickListener {
            choose = 4
            playGame()
        }
        rolldice5.setOnClickListener {
            choose = 5
            playGame()
        }
        rolldice6.setOnClickListener {
            choose = 6
            playGame()
        }
        rolldice7.setOnClickListener {
            choose = 7
            playGame()
        }
        rolldice8.setOnClickListener {
            choose = 8
            playGame()
        }
        rolldice9.setOnClickListener {
            choose = 9
            playGame()
        }
        rolldice10.setOnClickListener {
            choose = 10
            playGame()
        }
        rolldice11.setOnClickListener {
            choose = 11
            playGame()
        }
        rolldice12.setOnClickListener {
            choose = 12
            playGame()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun playGame() {
        val value1 = randomDicevalue()
        val value2 = randomDicevalue()
        val res1 = resources.getIdentifier("dice_$value1", "drawable", packageName)
        val res2 = resources.getIdentifier("dice$value2", "drawable", packageName)
        img1!!.setImageResource(res1)
        img2!!.setImageResource(res2)
        valot = value1 + value2
        txtPoint!!.text = "Pontuação dos dados: $valot"
        compare()
    }

    private fun compare() {
        if (choose == valot) {
            pAds = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pAds!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pAds!!.titleText = "Carregando seu video"
            pAds!!.setCancelable(false)
            pAds!!.show()
            if (interstitialAd!!.isReady) {
                interstitialAd!!.showAd()
            }
        }
    }

    override fun onAdLoaded(ad: MaxAd) {
        retryAttempt = 0
    }

    override fun onAdDisplayed(ad: MaxAd) {
        pAds!!.dismissWithAnimation()
    }

    override fun onAdHidden(ad: MaxAd) {
        interstitialAd!!.loadAd()
        val pontosDice: Int = if (choose == 2 || choose == 3 || choose == 4 || choose == 5) {
            10
        } else if (choose == 6 || choose == 7 || choose == 8) {
            25
        } else if (choose == 9 || choose == 10 || choose == 11) {
            40
        } else {
            50
        }
        val currentCoins = pointTv
        val updatedCoins = currentCoins + pontosDice
        val map = HashMap<String, Any>()
        map["Coins"] = updatedCoins
        userRef!!.updateChildren(map).addOnCompleteListener {
            Toasty.success(
                this@DiceGame,
                "+ $pontosDice TWSCASH",
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
            this@DiceGame,
            "Erro ao carregar , tente novamente em poucos segundos",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        interstitialAd!!.loadAd()
        Toasty.error(
            this@DiceGame,
            "Não foi possível exibir o video , tente novamente",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }

    companion object {
        private val RANDOM = Random()
        fun randomDicevalue(): Int {
            return RANDOM.nextInt(6) + 1
        }
    }
}