package com.tw4rs.gamex.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tw4rs.gamex.R

class NoInternetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
        val button = findViewById<Button>(R.id.button3)
        button.setOnClickListener {
            val intent = Intent(this@NoInternetActivity, AppActivity::class.java)
            startActivity(intent)
        }
    }
}