/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.activities

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tw4rs.gamex.BuildConfig
import com.tw4rs.gamex.R

class AboutActivity : AppCompatActivity() {
    private var telegramView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val versionTv: TextView = findViewById(R.id.version)
        val versionName: String = BuildConfig.VERSION_NAME
        val version = "Versão $versionName"
        versionTv.text = version
        telegramView = findViewById(R.id.telegram)
        telegramView!!.movementMethod = LinkMovementMethod.getInstance()
    }
}