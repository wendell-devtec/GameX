package com.tw4rs.gamex.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.appodeal.ads.Appodeal
import com.appodeal.ads.InterstitialCallbacks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.onesignal.OneSignal
import com.tw4rs.gamex.R
import com.tw4rs.gamex.ReedemList
import com.tw4rs.gamex.game.*
import com.tw4rs.gamex.model.AppModel
import com.tw4rs.gamex.model.EventModel
import com.tw4rs.gamex.model.TotalModel
import com.tw4rs.gamex.util.BaseActivty
import es.dmoral.toasty.Toasty
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow


open class HomeActivity : BaseActivty(), View.OnClickListener, MaxAdListener {

    var txtCoins: TextView? = null
    private lateinit var userRef: DatabaseReference
    private lateinit var userRef1: DatabaseReference
    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0
    private var pAds: SweetAlertDialog? = null
    private var pointTv = 0
    private lateinit var totalRef: DatabaseReference
    private var totalTv: TextView? = null
    private var levelTV: TextView? = null
    private var version = 0
    private var userId: String? = null
    private var pixKey: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        //ads
        interstitialAd = MaxInterstitialAd("d56da0e6a0715645", this)
        interstitialAd.setListener(this)

        // Load the first ad
        interstitialAd.loadAd()
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val user1 = mAuth.currentUser
        userId = user1!!.uid
        userRef = database.reference.child("users").child(user1.uid)
        init()
        databaseData
    }

    private val databaseData: Unit
        get() {
            val pDialogData = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialogData.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialogData.titleText = "Carregando seus dados"
            pDialogData.setCancelable(false)
            pDialogData.show()
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appmodel = snapshot.getValue(AppModel::class.java)!!
                    pointTv = appmodel.Coins
                    txtCoins!!.text = pointTv.toString()
                    if (!snapshot.child("pix_chave").exists()) {
                        val editText = EditText(this@HomeActivity)
                        editText.hint = "PIX AQUI!"
                        editText.setHintTextColor(ContextCompat.getColor(this@HomeActivity,R.color.black_text1))
                        editText.setTextColor(ContextCompat.getColor(this@HomeActivity,R.color.black_text1))
                        val dialog =
                            SweetAlertDialog(this@HomeActivity, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Adicione sua chave Pix para continuar usando o app")
                                .setConfirmText("ADICIONAR CHAVE PIX")
                                .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                                    pixKey = editText.text.toString()
                                    if (TextUtils.isEmpty(pixKey)) {
                                        Toasty.error(
                                            this@HomeActivity,
                                            "Preencha sua chave Pix para poder continuar",
                                            Toasty.LENGTH_SHORT,
                                            true
                                        ).show()
                                        return@setConfirmClickListener
                                    }
                                    val map = HashMap<String, Any>()
                                    map["pix_chave"] = pixKey!!
                                    userRef.updateChildren(map)
                                        .addOnCompleteListener { }
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                .setCancelText("SAIR")
                                .setCancelClickListener {
                                    Toasty.error(
                                        this@HomeActivity,
                                        "Preencha sua chave Pix para poder continuar",
                                        Toasty.LENGTH_SHORT,
                                        true
                                    ).show()
                                }
                        dialog.setCustomView(editText)
                        dialog.setCancelable(false)
                        dialog.show()
                    }

                    if (!snapshot.child("status_block").exists() || !snapshot.child("punishment").exists()  ) {
                        val map = HashMap<String, Any>()
                        map["status_block"] = true
                        map["punishment"] = 0
                        map["level"] = "graveto"
                        map["progress"] = 0
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }

                    if (!snapshot.child("level").exists()){
                        val map = HashMap<String, Any>()
                        map["level"] = "graveto"
                        map["progress"] = 0
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    val refid = appmodel.my_ref_id
                    if (!snapshot.child("my_ref_id").exists() || refid!!.isEmpty()) {
                        val mapRef = HashMap<String, Any>()
                        mapRef["my_ref_id"] = uniqueId
                        userRef.updateChildren(mapRef)
                            .addOnCompleteListener { }
                    }

                    pDialogData.dismissWithAnimation()
                    status
                    level
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@HomeActivity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
            userRef1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val model = snapshot.getValue(EventModel::class.java)!!
                    version = model.versao
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@HomeActivity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
            totalRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val totalModel = snapshot.getValue(TotalModel::class.java)
                    val total = totalModel?.wait
                    totalTv!!.text = total.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@HomeActivity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
        }

    private  val level: Unit
    get() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appModel = snapshot.getValue(AppModel::class.java)!!
                levelN = appModel.progress
                levelTV!!.text = appModel.level

                when {
                    levelN >= 70 -> {
                        val levelString = "Gafanhoto"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >= 250 -> {
                        val levelString = "EXPERT"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >= 600 -> {
                        val levelString = "EXPERT1"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN == 1200 -> {
                        val levelString = "EXPERT2"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >= 2500 -> {
                        val levelString = "EXPERT3"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >= 5000 -> {
                        val levelString = "TWS1"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN == 5600 -> {
                        val levelString = "TWS2"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >= 6500 -> {
                        val levelString = "TWS3"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >=7000 -> {
                        val levelString = "BRONZE"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >= 10000 -> {
                        val levelString = "PRATA"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN >= 30000 -> {
                        val levelString = "OURO"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                    levelN > 50000 -> {
                        val levelString = "MASTER"

                        val map = HashMap<String, Any>()
                        map["level"] = levelString
                        userRef.updateChildren(map)
                            .addOnCompleteListener { }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toasty.error(
                    this@HomeActivity,
                    "Erro: " + error.message,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        })
    }

    private val status: Unit
        get() {
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appModel = snapshot.getValue(AppModel::class.java)!!
                    val i = appModel.Coins
                    val block = appModel.status_block
                    val vpn = vpn()
                    if (!block!!) {
                        val pDialog = SweetAlertDialog(this@HomeActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Ops!! , Verificação de conta")
                                .setContentText("Conta bloqueada ")
                                .setConfirmText("SAIR")
                                .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                                    sweetAlertDialog.dismissWithAnimation()
                                    finishAffinity()
                                }
                        pDialog.setCancelable(false)
                        pDialog.show()
                    }
                    if (i > 1320000 && block) {
                        val pPoint =
                            SweetAlertDialog(this@HomeActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Ops!! , Verificação de conta")
                                .setContentText(
                                    "Nosso sistema detectou , que há pontuação inválida na conta " +
                                            "Selecione uma das opções"
                                )
                                .setConfirmText("Zerar Pontos")
                                .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                                    val map = HashMap<String, Any>()
                                    map["Coins"] = 0
                                    map["status_block"] = true
                                    userRef.updateChildren(map)
                                        .addOnCompleteListener { }
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                .setCancelText("SAIR")
                                .setCancelClickListener { sweetAlertDialog: SweetAlertDialog ->
                                    val map = HashMap<String, Any>()
                                    map["status_block"] = false
                                    userRef.updateChildren(map)
                                        .addOnCompleteListener { }
                                    sweetAlertDialog.dismissWithAnimation()
                                    finishAffinity()
                                }
                        pPoint.setCancelable(false)
                        pPoint.show()
                    }
                    if (vpn && block) {
                        val pDialog =
                            SweetAlertDialog(this@HomeActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Sua conta está fazendo uso de VPN")
                                .setConfirmText("OK")
                                .setConfirmClickListener { obj: SweetAlertDialog -> obj.dismissWithAnimation() }
                        pDialog.setCancelable(false)
                        pDialog.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@HomeActivity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
        }

    private fun init() {

        val lnReedem = findViewById<LinearLayout>(R.id.reedemLayout)
        lnReedem.setOnClickListener(this)
        val lnHistory = findViewById<LinearLayout>(R.id.historicoLayout)
        lnHistory.setOnClickListener(this)
        val lnProfile = findViewById<LinearLayout>(R.id.PerfilLayout)
        lnProfile.setOnClickListener(this)
        val lnHelp = findViewById<LinearLayout>(R.id.helpLayout)
        lnHelp.setOnClickListener(this)
        val lnDice = findViewById<ConstraintLayout>(R.id.lnDice)
        val ln01 = findViewById<ConstraintLayout>(R.id.ln01)
        val lnRasp = findViewById<ConstraintLayout>(R.id.raspadinha)
        val lnLucky = findViewById<ConstraintLayout>(R.id.luckyln)
        val lnBuble = findViewById<ConstraintLayout>(R.id.lnBuble)
        val lnInvite = findViewById<ConstraintLayout>(R.id.lnInvite)
        val lnCheck = findViewById<ConstraintLayout>(R.id.lnCheck)
        val lnVDP = findViewById<ConstraintLayout>(R.id.lnVDP)
        val lnTictoe = findViewById<ConstraintLayout>(R.id.lnTictoe)
        val lnApp = findViewById<ConstraintLayout>(R.id.lnApp)
        val ln02 = findViewById<ConstraintLayout>(R.id.ln17)
        lnApp.setOnClickListener(this)
        lnTictoe.setOnClickListener(this)
        lnVDP.setOnClickListener(this)
        lnCheck.setOnClickListener(this)
        lnInvite.setOnClickListener(this)
        lnBuble.setOnClickListener(this)
        lnDice.setOnClickListener(this)
        lnLucky.setOnClickListener(this)
        lnRasp.setOnClickListener(this)
        ln01.setOnClickListener(this)
        ln02.setOnClickListener(this)
        levelTV =findViewById(R.id.txtLevelN)
        totalTv = findViewById(R.id.payAmount)
        txtCoins = findViewById(R.id.txtCoin)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.ln17){
            val pAdsDeal = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pAdsDeal.progressHelper.barColor = Color.parseColor("#A5DC86")
            pAdsDeal.titleText = "Opção beta - INSTÁVEL PODERÁ NÃO FUNCIONAR"
            pAdsDeal.setCancelable(true)
            pAdsDeal.show()
             Appodeal.show(this, Appodeal.INTERSTITIAL)
                Appodeal.muteVideosIfCallsMuted(true)
            Appodeal.setInterstitialCallbacks(object : InterstitialCallbacks {
                    override fun onInterstitialLoaded(isPrecache: Boolean) {
                        // Called when interstitial is loaded
                    }

                    override fun onInterstitialFailedToLoad() {
                        Toasty.error(
                            this@HomeActivity,
                            "Não foi possível carregar o Vídeo",
                            Toasty.LENGTH_LONG,
                            true
                        ).show()
                    }

                    override fun onInterstitialShown() {
                        // Called when interstitial is shown
                        pAdsDeal.dismissWithAnimation()
                    }

                    override fun onInterstitialShowFailed() {
                        Toasty.error(
                            this@HomeActivity,
                            "Não foi possível exibir o video , tente novamente",
                            Toasty.LENGTH_LONG,
                            true
                        ).show()
                    }

                    override fun onInterstitialClicked() {
                        // Called when interstitial is clicked
                    }

                    override fun onInterstitialClosed() {
                        val currentCoins = pointTv
                        val currentLevels = levelN
                        val updateLevel = currentLevels + 1
                        val updatedCoins = currentCoins + 35
                        val map = HashMap<String, Any>()
                        map["Coins"] = updatedCoins
                        map["progress"] = updateLevel
                        userRef.updateChildren(map).addOnCompleteListener {
                            Toasty.success(
                                this@HomeActivity,
                                "+35 TWSCASH",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onInterstitialExpired() {
                        // Called when interstitial is expired
                    }
                })
        }
        if (id == R.id.lnApp) {
            startActivity(Intent(this, Terms::class.java))
        }
        if (id == R.id.lnTictoe) {
            startActivity(Intent(this, Tictoe::class.java))
        }
        if (id == R.id.helpLayout) {
            startActivity(Intent(this, HelpActivity::class.java))
        }
        if (id == R.id.PerfilLayout) {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        if (id == R.id.reedemLayout) {
            startActivity(Intent(this@HomeActivity, ReedemList::class.java))
        }
        if (id == R.id.lnVDP) {
            startActivity(Intent(this, YoutubeList::class.java))
        }
        if (id == R.id.lnCheck) {
            val pDialog3 = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog3.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog3.titleText = "Carregando video para entrar"
            pDialog3.setCancelable(true)
            pDialog3.show()
            if (interstitialAd.isReady) {
                interstitialAd.showAd()
                interstitialAd.setListener(object : MaxAdListener {
                    override fun onAdLoaded(ad: MaxAd) {}
                    override fun onAdDisplayed(ad: MaxAd) {
                        pDialog3.dismissWithAnimation()
                    }

                    override fun onAdHidden(ad: MaxAd) {
                        interstitialAd.loadAd()
                        startActivity(Intent(this@HomeActivity, CheckInActivity::class.java))
                    }

                    override fun onAdClicked(ad: MaxAd) {}
                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                })
            }
        }
        if (id == R.id.lnInvite) {
            startActivity(Intent(this, InviteActivity::class.java))
        }
        if (id == R.id.lnBuble) {
            startActivity(Intent(this, BubleGame::class.java))
        }
        if (id == R.id.lnDice) {
            startActivity(Intent(this, DiceGame::class.java))
        }
        if (id == R.id.luckyln) {
            startActivity(Intent(this, LuckyWheel::class.java))
        }
        if (id == R.id.historicoLayout) {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        if (id == R.id.ln01) {
            pAds = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pAds!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pAds!!.titleText = "Carregando o video , caso demore click para tentar de novo"
            pAds!!.setCancelable(true)
            pAds!!.show()
            if (interstitialAd.isReady) {
                interstitialAd.showAd()
            }
        }
        if (id == R.id.raspadinha) {
            startActivity(Intent(this, ScratchGame::class.java))
        }
    }

    ///Max
    override fun onAdLoaded(ad: MaxAd) {
        retryAttempt = 0.0
    }

    override fun onAdDisplayed(ad: MaxAd) {
        pAds!!.dismissWithAnimation()
    }

    override fun onAdHidden(ad: MaxAd) {
        interstitialAd.loadAd()
        val currentCoins = pointTv
        val currentLevels = levelN
        val updateLevel = currentLevels + 1
        val updatedCoins = currentCoins + 30
        val map = HashMap<String, Any>()
        map["Coins"] = updatedCoins
        map["progress"] = updateLevel
        userRef.updateChildren(map).addOnCompleteListener {
            Toasty.success(
                this@HomeActivity,
                "+30 TWSCASH",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onAdClicked(ad: MaxAd) {}
    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis(
            2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong()
        )
        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd.loadAd() }, delayMillis)
        Toasty.error(
            this@HomeActivity,
            "Erro ao carregar , tente novamente em poucos segundos",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        interstitialAd.loadAd()
        Toasty.error(
            this@HomeActivity,
            "Não foi possível exibir o video , tente novamente",
            Toasty.LENGTH_LONG,
            true
        ).show()
    }

    protected val uniqueId: String
        get() {
            val character = "abcdefghijklmnopqrstuvwxyz1234567890"
            val salt = StringBuilder()
            while (salt.length < 8) {
                val index = (Random().nextFloat() * character.length).toInt()
                salt.append(character[index])
            }
            return salt.toString()
        }

    companion object {
        private const val ONESIGNAL_APP_ID = "1eba2460-d7e4-485e-9652-ef063ec6f1ea"
    }
}