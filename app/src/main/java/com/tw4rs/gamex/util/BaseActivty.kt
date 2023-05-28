/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.util

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.model.AppModel
import es.dmoral.toasty.Toasty
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URL
import java.util.*

open class BaseActivty : AppCompatActivity() {
    private var userRef: DatabaseReference? = null
    var coin = 0
    var Ponts = 0
    var PontsS = 0
    var pontosTv: Long = 0
    var levelN = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vpn()
        checkAppCloning()
        antiTermux()
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val user1 = mAuth.currentUser!!
        val userId = user1.uid
        userRef = database.reference.child("GameX").child("users").child(userId)
        userRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val model = snapshot.getValue(AppModel::class.java)
                pontosTv = model!!.Coins.toLong()
                levelN = model.progress
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun vpn(): Boolean {
        if (!Commom.VPN_DETECTION) {
        } else {
            var iface = ""
            try {
                for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (networkInterface.isUp) iface = networkInterface.name
                    if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                        Toasty.error(
                            applicationContext,
                            resources.getString(R.string.msg_vpn_detected),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        return true
                    }
                }
            } catch (e1: SocketException) {
                e1.printStackTrace()
            }
        }
        return false
    }

    fun antiTermux() {
        if (Commom.TERMUX_DETECTION) {
            try {
                packageManager.getPackageInfo("com.termux", PackageManager.GET_META_DATA)
                Toasty.error(
                    applicationContext,
                    (resources.getString(R.string.msg_termux_detected) as CharSequence),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                moveTaskToBack(true)
                finish()
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
        }
    }

    fun checkAppCloning() {
        if (Commom.CLONING_APP_DETECTION) {
            val path = filesDir.path
            if (path.contains(DUAL_APP_ID_999)) {
                Toasty.error(
                    applicationContext,
                    (resources.getString(R.string.msg_cloning_app_detected) as CharSequence),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                moveTaskToBack(true)
                finish()
            } else if (getDotCount(path) > 2) {
                Toasty.error(
                    applicationContext,
                    (resources.getString(R.string.msg_cloning_app_detected) as CharSequence),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                moveTaskToBack(true)
                finish()
            }
        }
    }

    @SuppressLint("CutPasteId")
    fun upadateDate(i: Int) {
        if (i == 4) { // roleta
            val currentCoins = pontosTv
            val currentLevels = levelN
            val updateLevel = currentLevels + 1
            val updatedCoin = currentCoins + coin
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoin
            map["progress"] = updateLevel

            userRef!!.updateChildren(map).addOnCompleteListener {
                Toast.makeText(
                    applicationContext, "+ $coin TWSCASH", Toast.LENGTH_SHORT
                ).show()
            }
            findViewById<View>(R.id.play).isEnabled = true
            findViewById<View>(R.id.play).alpha = 1f
        } else if (i == 5) { //bubble
            val currentCoins = pontosTv
            val currentLevels = levelN
            val updateLevel = currentLevels + 1
            val updatedCoins = currentCoins + Ponts
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            map["progress"] = updateLevel

            userRef!!.updateChildren(map).addOnCompleteListener {
                Toast.makeText(
                    this@BaseActivty,
                    "+$Ponts TWSCASH ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (i == 8) { // scrath
            val currentCoins = pontosTv
            val currentLevels = levelN
            val updateLevel = currentLevels + 1
            val updatedCoins = currentCoins + PontsS
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            map["progress"] = updateLevel

            userRef!!.updateChildren(map).addOnCompleteListener {
                Toast.makeText(
                    this@BaseActivty,
                    "TWSCASH ADICIONADO",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    inner class isInternetActive : AsyncTask<Void?, Void?, String?>() {
        override fun doInBackground(vararg p0: Void?): String {
            var json: String
            try {
                //Url contain small android icon to check internet access
                // replace with your own url or with icon url
                //For more icons go to: https://icons.iconarchive.com/
                val strURL = "https://icons.iconarchive.com/"
                val url = URL(strURL)
                val urlConnection = url.openConnection()
                urlConnection.doOutput = true
                json = "success"
            } catch (e: Exception) {
                e.printStackTrace()
                json = "failed"
            }
            return json
        }

        override fun onPostExecute(s: String?) {
            if (s != null) {
                if (s == "success") {
                    Toast.makeText(this@BaseActivty, "Internet Conectada", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@BaseActivty, "Sem acesso a internet", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this@BaseActivty, "Sem internet", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onPreExecute() {
            Toast.makeText(this@BaseActivty, "Validando Conexão", Toast.LENGTH_SHORT).show()
            super.onPreExecute()
        }
    }

    private fun getDotCount(str: String): Int {
        var i = 0
        var i2 = 0
        while (i2 < str.length && i <= 2) {
            if (str[i2] == '.') {
                i++
            }
            i2++
        }
        return i
    }

    companion object {
        private const val DUAL_APP_ID_999 = "999"
    }
}