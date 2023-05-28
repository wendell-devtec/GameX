package com.tw4rs.gamex.activities


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.model.AppModel
import es.dmoral.toasty.Toasty
import java.util.*

class CheckInActivity : AppCompatActivity() {
    private var pointTv = 0
    private var userRef: DatabaseReference? = null
    private var todayString: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val user1 = mAuth.currentUser!!
        val userId = user1.uid
        userRef = database.reference.child("GameX").child("users").child(userId)
        databaseData
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val weekday = calendar[Calendar.DAY_OF_WEEK]
        todayString = year.toString() + "" + month + "" + day
        val sun = findViewById<View>(R.id.btSun) as Button
        val mon = findViewById<View>(R.id.btMon) as Button
        val tue = findViewById<View>(R.id.btTue) as Button
        val wed = findViewById<View>(R.id.btWed) as Button
        val thu = findViewById<View>(R.id.btThu) as Button
        val fri = findViewById<View>(R.id.btFri) as Button
        val sat = findViewById<View>(R.id.btSat) as Button
        sun.isEnabled = false
        mon.isEnabled = false
        tue.isEnabled = false
        wed.isEnabled = false
        thu.isEnabled = false
        fri.isEnabled = false
        sat.isEnabled = false
        sun.setOnClickListener{
            sunCheck()
        }
        mon.setOnClickListener {
            monCheck()
        }
        tue.setOnClickListener {
            tueCheck()
        }
        wed.setOnClickListener {
            wedCheck()
        }
        thu.setOnClickListener {
            thuCheck()
        }
        fri.setOnClickListener {
            friCheck()
        }
        sat.setOnClickListener {
            satCheck()
        }

        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (weekday == 1) {
            if (currentDay) {
                sun.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_filled_rounded_color,
                    null
                )
            } else {
                sun.isEnabled = true
                sun.alpha = 0f
                sun.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_bg, null)
                sun.setTextColor(Color.WHITE)
            }
        } else if (weekday == 2) {
            if (currentDay) {
                mon.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_filled_rounded_color,
                    null
                )
            } else {
                mon.isEnabled = true
                mon.alpha = 1f
                mon.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_bg, null)
                mon.setTextColor(Color.WHITE)
            }
        } else if (weekday == 3) {
            if (currentDay) {
                tue.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_filled_rounded_color,
                    null
                )
            } else {
                tue.isEnabled = true
                tue.alpha = 1f
                tue.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_bg, null)
                tue.setTextColor(Color.WHITE)
            }
        } else if (weekday == 4) {
            if (currentDay) {
                wed.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_filled_rounded_color,
                    null
                )
            } else {
                wed.isEnabled = true
                wed.alpha = 1f
                wed.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_bg, null)
                wed.setTextColor(Color.WHITE)
            }
        } else if (weekday == 5) {
            if (currentDay) {
                thu.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_filled_rounded_color,
                    null
                )
            } else {
                thu.isEnabled = true
                thu.alpha = 1f
                thu.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_bg, null)
                thu.setTextColor(Color.WHITE)
            }
        } else if (weekday == 6) {
            if (currentDay) {
                fri.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_filled_rounded_color,
                    null
                )
            } else {
                fri.isEnabled = true
                fri.alpha = 1f
                fri.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_bg, null)
                fri.setTextColor(Color.WHITE)
            }
        } else if (weekday == 7) {
            if (currentDay) {
                sat.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_filled_rounded_color,
                    null
                )
            } else {
                sat.isEnabled = true
                sat.alpha = 1f
                sat.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.rounded_bg, null)
                sat.setTextColor(Color.WHITE)
            }
        }
    }

    private fun monCheck() {
        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (!currentDay) {
            Toast.makeText(this, "10 TWSCAHS!", Toast.LENGTH_SHORT).show()
            val daily = dailyChecks.edit()
            daily.putBoolean(todayString, true)
            daily.apply()
            val currentCoins = pointTv
            val updatedCoins = currentCoins + 10
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            userRef!!.updateChildren(map).addOnCompleteListener {
                Toasty.success(
                    this@CheckInActivity,
                    "+30 TWSCASH",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Já recebeu Bônus de hoje", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tueCheck() {
        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (!currentDay) {
            Toast.makeText(this, "10 TWSCAHS!", Toast.LENGTH_SHORT).show()
            val daily = dailyChecks.edit()
            daily.putBoolean(todayString, true)
            daily.apply()
            val currentCoins = pointTv
            val updatedCoins = currentCoins + 10
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            userRef!!.updateChildren(map).addOnCompleteListener {
                Toasty.success(
                    this@CheckInActivity,
                    "+30 TWSCASH",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Já recebeu Bônus de hoje", Toast.LENGTH_SHORT).show()
        }
    }

    private fun wedCheck() {
        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (!currentDay) {
            Toast.makeText(this, "20 TWSCAHS!", Toast.LENGTH_SHORT).show()
            val daily = dailyChecks.edit()
            daily.putBoolean(todayString, true)
            daily.apply()
            val currentCoins = pointTv
            val updatedCoins = currentCoins + 20
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            userRef!!.updateChildren(map).addOnCompleteListener {
                Toasty.success(
                    this@CheckInActivity,
                    "+30 TWSCASH",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Já recebeu Bônus de hoje", Toast.LENGTH_SHORT).show()
        }
    }

    private fun thuCheck() {
        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (!currentDay) {
            Toast.makeText(this, "20 TWSCAHS!", Toast.LENGTH_SHORT).show()
            val daily = dailyChecks.edit()
            daily.putBoolean(todayString, true)
            daily.apply()
            val currentCoins = pointTv
            val updatedCoins = currentCoins + 20
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            userRef!!.updateChildren(map).addOnCompleteListener {
                Toasty.success(
                    this@CheckInActivity,
                    "+20 TWSCASH",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Já recebeu Bônus de hoje", Toast.LENGTH_SHORT).show()
        }
    }

    private fun friCheck() {
        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (!currentDay) {
            Toast.makeText(this, "30 TWSCAHS!", Toast.LENGTH_SHORT).show()
            val daily = dailyChecks.edit()
            daily.putBoolean(todayString, true)
            daily.apply()
            val currentCoins = pointTv
            val updatedCoins = currentCoins + 30
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            userRef!!.updateChildren(map).addOnCompleteListener {
                Toasty.success(
                    this@CheckInActivity,
                    "+30 TWSCASH",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Já recebeu Bônus de hoje", Toast.LENGTH_SHORT).show()
        }
    }

    private fun satCheck() {
        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (!currentDay) {
            Toast.makeText(this, "30 TWSCAHS!", Toast.LENGTH_SHORT).show()
            val daily = dailyChecks.edit()
            daily.putBoolean(todayString, true)
            daily.apply()
            val currentCoins = pointTv
            val updatedCoins = currentCoins + 30
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            userRef!!.updateChildren(map).addOnCompleteListener {
                Toasty.success(
                    this@CheckInActivity,
                    "+30 TWSCASH",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Já recebeu Bônus de hoje", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sunCheck() {
        val dailyChecks = getSharedPreferences("DAILYCHECKS", 0)
        val currentDay = dailyChecks.getBoolean(todayString, false)
        if (!currentDay) {
            Toast.makeText(this, "50 TWSCAHS!", Toast.LENGTH_SHORT).show()
            val daily = dailyChecks.edit()
            daily.putBoolean(todayString, true)
            daily.apply()
            val currentCoins = pointTv
            val updatedCoins = currentCoins + 50
            val map = HashMap<String, Any>()
            map["Coins"] = updatedCoins
            userRef!!.updateChildren(map).addOnCompleteListener {
                Toasty.success(
                    this@CheckInActivity,
                    "+30 TWSCASH",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Já recebeu Bônus de hoje", Toast.LENGTH_SHORT).show()
        }
    }

    private val databaseData: Unit
        get() {
            userRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appModel = snapshot.getValue(AppModel::class.java)!!
                    pointTv = appModel.Coins
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@CheckInActivity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
        }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}