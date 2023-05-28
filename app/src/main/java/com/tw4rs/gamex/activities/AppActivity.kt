package com.tw4rs.gamex.activities

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.applovin.sdk.AppLovinSdk
import com.appodeal.ads.Appodeal
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.onesignal.OneSignal
import com.tw4rs.gamex.R


class AppActivity : AppCompatActivity() {
    private val mAuthListener: AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    private var contentScreen: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        FirebaseApp.initializeApp(this)

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        contentScreen = findViewById(R.id.contentScreen)
        mAuth = FirebaseAuth.getInstance()

        Appodeal.initialize(this, "ca1e577767d9595e08bf475a7d0eb90daee606fcff583483",Appodeal.INTERSTITIAL or Appodeal.REWARDED_VIDEO or Appodeal.BANNER)
        Appodeal.isInitialized( Appodeal.INTERSTITIAL)
        Appodeal.isAutoCacheEnabled(Appodeal.INTERSTITIAL)

        AppLovinSdk.getInstance( this ).mediationProvider = "max"
        AppLovinSdk.getInstance( this ).initializeSdk {
            // AppLovin SDK is initialized, start loading ads
        }


        val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val deviceAdmin = ComponentName(this, AppActivity::class.java)
        try {
            devicePolicyManager.addUserRestriction(
                deviceAdmin,
                UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES
            )
        } catch (ex: SecurityException) {
            Log.e(ContentValues.TAG, ex.message!!)
        }
    }

    override fun onStart() {
        super.onStart()
        user = mAuth!!.currentUser
        if (user != null) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // release listener in onStop
    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener)
        }
    }

    companion object {
        private const val ONESIGNAL_APP_ID = "1eba2460-d7e4-485e-9652-ef063ec6f1ea"
    }
}