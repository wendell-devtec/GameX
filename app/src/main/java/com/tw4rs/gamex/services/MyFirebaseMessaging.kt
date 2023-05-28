/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tw4rs.gamex.R

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessaging : FirebaseMessagingService() {
    var PRIVATE_MODE = 0
    private var pref: SharedPreferences? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notification_title = remoteMessage.notification!!.title
        val notification_body = remoteMessage.notification!!.body
        val action = remoteMessage.notification!!.clickAction
        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        if (getBoolean("notifications")) {
            val mBuilder = NotificationCompat.Builder(
                this,
                getString(R.string.default_notification_channel_id)
            )
                .setSmallIcon(R.drawable.gamex)
                .setColor(ContextCompat.getColor(this,R.color.colorAccent))
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setVibrate(longArrayOf(1000, 1000))
                .setLights(Color.RED, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
            val mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    getString(R.string.default_notification_channel_id), getString(
                        R.string.notification_channel_name
                    ), NotificationManager.IMPORTANCE_HIGH
                )
                channel.enableLights(true)
                channel.lightColor = Color.RED
                channel.setShowBadge(true)
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                mNotifyMgr.createNotificationChannel(channel)
            }
            if (action != null) {
                if (PAYOUT_TYPE == action) {
                    val txn_id = remoteMessage.data["txn_id"]
                    if (txn_id != null) {
                        val resultIntent = Intent(action)
                        resultIntent.putExtra("txn_id", txn_id)
                        @SuppressLint("UnspecifiedImmutableFlag") val resultPendingIntent =
                            PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        mBuilder.setContentIntent(resultPendingIntent)
                        val mNotificationId = System.currentTimeMillis().toInt()
                        mNotifyMgr.notify(mNotificationId, mBuilder.build())
                    }
                }
            }
        }
    }

    fun getBoolean(PREF_NAME: String?): Boolean {
        return pref!!.getBoolean(PREF_NAME, true)
    }

    companion object {
        private const val PAYOUT_TYPE = "com.tw4rs.gamex.PAYOUT_NOTIFICATION"
        private const val PREF_NAME = "gamex_app"
    }
}