package com.tw4rs.gamex.services

import android.provider.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIDService : FirebaseMessagingService() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    override fun onCreate() {
        super.onCreate()
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference.child("GameX").child("users")
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val refreshedToken = FirebaseMessaging.getInstance().token
        if (mAuth!!.currentUser != null) {
            val uiid = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
            mDatabase!!.child(mAuth!!.currentUser!!.uid).child(uiid).child("token")
                .setValue(refreshedToken)
        }
    }
}