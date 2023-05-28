package com.tw4rs.gamex.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tw4rs.gamex.R
import es.dmoral.toasty.Toasty
import java.util.*

open class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var auth: FirebaseAuth? = null
    private var deviceID: String? = null
    private var nameEt: EditText? = null
    private var emailEt: EditText? = null
    private var pass: EditText? = null
    private var pass1: EditText? = null
    private var pDialog3: SweetAlertDialog? = null
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        init()
    }

    private fun init() {
        nameEt = findViewById(R.id.username_input)
        emailEt = findViewById(R.id.email_et)
        pass = findViewById(R.id.pass)
        pass1 = findViewById(R.id.pass1)
        val btnregister = findViewById<TextView>(R.id.btn_registeraccount)
        btnregister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_registeraccount) {
            val name = nameEt!!.text.toString()
            val email = emailEt!!.text.toString()
            val password = pass!!.text.toString()
            val confirmPass = pass1!!.text.toString()
            if (name.isEmpty()) {
                nameEt!!.error = "Obrigatório"
                return
            }
            if (email.isEmpty()) {
                emailEt!!.error = "Obrigatório"
                return
            }
            if (password.isEmpty()) {
                pass!!.error = "Obrigatório"
                return
            }
            if (confirmPass.isEmpty() || password != confirmPass) {
                pass1!!.error = "Senha inválida"
                return
            }
            queryAccountExistence(email, password)
        }
    }

    private fun queryAccountExistence(email: String, password: String) {
        pDialog3 = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog3!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog3!!.titleText = "Carregando o registro da sua conta"
        pDialog3!!.setCancelable(false)
        pDialog3!!.show()
        val ref = FirebaseDatabase.getInstance().reference.child("GameX").child("device_id")
        val query = ref.orderByChild("deviceId").equalTo(deviceID)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //device already registered
                    pDialog3!!.dismissWithAnimation()
                    Toasty.warning(
                        this@RegisterActivity,
                        "Já existe conta nesse aparelho",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                } else {
                    //device id not found
                    createAccount(email, password)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun createAccount(email: String, password: String) {
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = auth!!.currentUser
                    auth!!.currentUser?.sendEmailVerification()?.addOnCompleteListener { task1: Task<Void?> ->
                        if (task1.isSuccessful) {
                            pDialog3!!.dismissWithAnimation()
                            updateUi(user, email)
                        } else {
                            Toasty.error(
                                this@RegisterActivity,
                                "Erro:" + task1.exception!!.message,
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                } else {
                    pDialog3!!.dismissWithAnimation()
                    //Registration failed:
                    Toasty.error(
                        this@RegisterActivity,
                        "Error: " + task.exception!!.message ,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }
    }

    private fun updateUi(user: FirebaseUser?, email: String) {
        val map = HashMap<String, Any?>()
        map["name"] = nameEt!!.text.toString()
        map["email"] = email
        map["uid"] = user!!.uid
        map["Coins"] = 0
        map["my_ref_id"] = uniqueId
        map["Status"] = 0
        map["deviceID"] = deviceID
        val reference = FirebaseDatabase.getInstance().reference.child("GameX").child("users")
        reference.child(user.uid)
            .setValue(map)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Toasty.success(
                        this@RegisterActivity,
                        "Registrado verifique seu email",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toasty.error(
                        this@RegisterActivity,
                        "Error: " +
                                task.exception!!.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }
    }

    private val uniqueId: String
        get() {
            val character = "abcdefghijklmnopqrstuvwxyz1234567890"
            val salt = StringBuilder()
            while (salt.length < 5) {
                val index = (Random().nextFloat() * character.length).toInt()
                salt.append(character[index])
            }
            return salt.toString()
        }
}