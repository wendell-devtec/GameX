package com.tw4rs.gamex.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.tw4rs.gamex.R
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private var emailEdit: EditText? = null
    private var passwordEdit: EditText? = null
    private var loginBtn: TextView? = null
    private var auth: FirebaseAuth? = null
    private var signupTv: TextView? = null
    private var forgotpass: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        auth = FirebaseAuth.getInstance()
        clickListener()
    }

    private fun init() {

        emailEdit = findViewById(R.id.email_input)
        passwordEdit = findViewById(R.id.pass)
        signupTv = findViewById(R.id.btn_register)
        loginBtn = findViewById(R.id.btn_login)
        forgotpass = findViewById(R.id.fgpass)
    }

    private fun clickListener() {
        forgotpass!!.setOnClickListener {
            val email = emailEdit!!.text.toString()
            if (email.isEmpty() || !validEmail(email)) {
                if (email.isEmpty()) {
                    emailEdit!!.error = getString(R.string.enter_email)
                } else {
                    emailEdit!!.error = getString(R.string.enter_valid_email)
                }
                emailEdit!!.requestFocus()
            }
            else {
                val pDialogData = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                pDialogData.progressHelper.barColor = Color.parseColor("#A5DC86")
                pDialogData.titleText = "Esqueceu senha... Carregando"
                pDialogData.setCancelable(false)
                pDialogData.show()
                auth!!.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task: Task<SignInMethodQueryResult> ->
                        val check = task.result.signInMethods!!.isNotEmpty()
                        if (!check) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Conta não encontrado, registrado nesse email",
                                Toast.LENGTH_SHORT
                            ).show()
                            pDialogData.dismissWithAnimation()
                        } else {
                            auth!!.sendPasswordResetEmail(email)
                                .addOnCompleteListener { task12: Task<Void?> ->
                                    if (task12.isSuccessful) {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            R.string.recovery_email,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        pDialogData.dismissWithAnimation()
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Erro encontrado, tente novamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        pDialogData.dismissWithAnimation()
                                    }
                                }
                        }
                    }
            }
        }
        signupTv!!.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }
        loginBtn!!.setOnClickListener {
            val email = emailEdit!!.text.toString()
            val password = passwordEdit!!.text.toString()
            if (TextUtils.isEmpty(email) || !validEmail(email)) {
                emailEdit!!.error = "Digite um email válido"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                passwordEdit!!.error = "Obrigátorio"
                return@setOnClickListener
            }
            signIn(email, password)
        }
    }

    private fun signIn(email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    //login success
                    val user = auth!!.currentUser!!
                    if (user.isEmailVerified) {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Verifique seu email , enviamos um link para confirmar sua conta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(baseContext, "Authentication failed." + task.exception!!.message,
                        Toast.LENGTH_SHORT).show()


                }
            }
    }

    private fun validEmail(email: String): Boolean {
        val m = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"
        val pattern = Pattern.compile(m, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.find()
    }
}