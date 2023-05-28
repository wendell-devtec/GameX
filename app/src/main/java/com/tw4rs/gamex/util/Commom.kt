package com.tw4rs.gamex.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object Commom {
    var CLONING_APP_DETECTION = true
    var TERMUX_DETECTION = true
    var VPN_DETECTION = true
    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val infos = connectivityManager.allNetworkInfo
        for (info in infos) {
            if (info.state == NetworkInfo.State.CONNECTED) return true
        }
        return false
    }
}