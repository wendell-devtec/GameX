package com.tw4rs.gamex.activities

import android.content.ClipboardManager
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.model.AppModel
import es.dmoral.toasty.Toasty

class InviteActivity : AppCompatActivity() {
    private var user: FirebaseUser? = null
    private var oppositeUID: String? = null
    private var referCodeTv: TextView? = null
    private var referGetTextId: EditText? = null
    private var redeemBtn: Button? = null
    var reference: DatabaseReference? = null
    private var pDialog: SweetAlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)
        init()
        reference = FirebaseDatabase.getInstance().reference.child("GameX").child("users")
        loadData()
        redeemAvailability()
        clickListener()
    }

    private fun redeemAvailability() {
        reference!!.child(user!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChild("redeemed")) {

                        //Here to check if value is true then user already redeemed/
                        val isAvailable = snapshot.child("redeemed").getValue(
                            Boolean::class.java
                        )!!
                        if (isAvailable) {
                            redeemBtn!!.visibility = View.GONE
                            redeemBtn!!.isEnabled = false
                        } else { // if false user is new and not redeemed
                            redeemBtn!!.isEnabled = true
                            redeemBtn!!.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun init() {
        referCodeTv = findViewById(R.id.referCodeTv)
        redeemBtn = findViewById(R.id.redeemBtn)
        referGetTextId = findViewById(R.id.referGetTextId)
        val auth = FirebaseAuth.getInstance()
        user = auth.currentUser
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun loadData() {
        reference!!.child(user!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val referCode = snapshot.child("my_ref_id").getValue(
                        String::class.java
                    )
                    referCodeTv!!.text = referCode
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@InviteActivity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
    }

    private fun clickListener() {
        findViewById<View>(R.id.copyButton).setOnClickListener {
            val clipboard =
                applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = referCodeTv!!.text.toString()
            Toasty.success(this, "Copiado com sucesso", Toast.LENGTH_SHORT, true).show()
        }
        redeemBtn!!.setOnClickListener {
            val inputCode = referGetTextId!!.text.toString()
            if (TextUtils.isEmpty(inputCode)) {
                Toasty.error(this@InviteActivity, "Código Inválido", Toast.LENGTH_SHORT, true)
                    .show()
                return@setOnClickListener
            }
            if (inputCode == referCodeTv!!.text.toString()) {
                Toasty.warning(
                    this@InviteActivity, "Você não pode usar seu proprio código",
                    Toast.LENGTH_SHORT, true
                ).show()
                return@setOnClickListener
            }
            pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog!!.titleText = "Carregando seu prêmio de recompensa"
            pDialog!!.setCancelable(false)
            pDialog!!.show()
            redeemQuery(inputCode)
        }
    }

    private fun redeemQuery(inputCode: String) {
        val query = reference!!.orderByChild("my_ref_id").equalTo(inputCode)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    oppositeUID = dataSnapshot.key
                    reference!!
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val model = snapshot.child(oppositeUID!!).getValue(
                                    AppModel::class.java
                                )
                                val myModel = snapshot.child(user!!.uid).getValue(
                                    AppModel::class.java
                                )!!
                                assert(model != null)
                                val coins = model?.Coins
                                val updatedCoins = coins!!.plus(150)
                                val myCoins = myModel.Coins.toLong()
                                val myUpdate = myCoins + 50
                                val map = HashMap<String, Any>()
                                map["Coins"] = updatedCoins
                                val myMap = HashMap<String, Any>()
                                myMap["Coins"] = myUpdate
                                myMap["redeemed"] = true
                                reference!!.child(oppositeUID!!).updateChildren(map)
                                reference!!.child(user!!.uid).updateChildren(myMap)
                                    .addOnCompleteListener {
                                        pDialog!!.dismiss()
                                        Toasty.success(
                                            this@InviteActivity,
                                            "Parabéns Por ter sido convidado",
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toasty.error(
                    this@InviteActivity,
                    "Erro: " + error.message,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        })
    }
}