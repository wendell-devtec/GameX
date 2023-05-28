package com.tw4rs.gamex.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.model.AppModel
import es.dmoral.toasty.Toasty

class ProfileActivity : AppCompatActivity() {
    private var deleteTv: TextView? = null
    private var nameTv: TextView? = null
    private var emailTv: TextView? = null
    private var shareTv: TextView? = null
    private var logoutTv: TextView? = null
    private var aboutTV: TextView? = null
    private var pixTV: TextView? = null
    private var keyTv: TextView? = null
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var reference: DatabaseReference? = null
    private var withdrawl: DatabaseReference? = null
    private var pixKey: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()
        data
        clickListener()
    }

    private fun clickListener() {
        aboutTV!!.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AboutActivity::class.java
                )
            )
        }
        deleteTv!!.setOnClickListener {
            val sweetAlertDialog =
                SweetAlertDialog(this@ProfileActivity, SweetAlertDialog.PROGRESS_TYPE)
            sweetAlertDialog.changeAlertType(SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.titleText = "Você tem certeza?"
            sweetAlertDialog.contentText =
                "Ao excluir não irá ter nenhum dado e muito menos pontuação e ganhos."
            sweetAlertDialog.setConfirmButton("Excluir") { sweetAlertDialog12: SweetAlertDialog ->
                sweetAlertDialog12.dismissWithAnimation()
                val user = FirebaseAuth.getInstance().currentUser!!
                user.delete()
                    .addOnCompleteListener { task: Task<Void?> ->
                        if (task.isSuccessful) {
                            Toasty.info(
                                this@ProfileActivity,
                                "Conta Deletada",
                                Toasty.LENGTH_SHORT,
                                true
                            ).show()
                            reference!!.child(user.uid).removeValue()
                            withdrawl!!.child(user.uid).removeValue()
                            auth!!.signOut()
                            finishAffinity()
                        }
                    }
            }
            sweetAlertDialog.setCancelButton("NÃO") { obj: SweetAlertDialog -> obj.dismissWithAnimation() }
            sweetAlertDialog.show()
        }
        logoutTv!!.setOnClickListener {
            auth!!.signOut()
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            finish()
        }
        keyTv!!.setOnClickListener {
            val editText = EditText(this@ProfileActivity)
            editText.hint = "PIX AQUI!"
            editText.setHintTextColor(ContextCompat.getColor(this,R.color.black_text1))
            editText.setTextColor(ContextCompat.getColor(this,R.color.black_text1))
            val dialog = SweetAlertDialog(this@ProfileActivity, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Adicione sua chave Pix para sacar")
                .setConfirmText("ADICIONAR CHAVE PIX")
                .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                    pixKey = editText.text.toString()
                    if (TextUtils.isEmpty(pixKey)) {
                        Toasty.error(
                            this@ProfileActivity,
                            "Preencha sua chave Pix para poder continuar",
                            Toasty.LENGTH_SHORT,
                            true
                        ).show()
                        return@setConfirmClickListener
                    }
                    val map = HashMap<String, Any>()
                    map["pix_chave"] = pixKey!!
                    reference!!.child(user!!.uid).updateChildren(map)
                        .addOnCompleteListener { }
                    sweetAlertDialog.dismissWithAnimation()
                }
                .setCancelText("SAIR")
                .setCancelClickListener { obj: SweetAlertDialog -> obj.dismissWithAnimation() }
            dialog.setCustomView(editText)
            dialog.setCancelable(false)
            dialog.show()
        }
        shareTv!!.setOnClickListener {
            val shareBody = """GameX - o app em que você ganha e se diverte aproveite  ${
                getString(
                    R.string.app_name
                )
            } Na playstore
https://play.google.com/store/apps/details?id=$packageName"""
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            intent.type = "text/plain"
            startActivity(intent)
        }
    }

    private val data: Unit
        get() {
            val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Carregando seus dados"
            pDialog.setCancelable(false)
            pDialog.show()
            reference!!.child(user!!.uid).addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appModel = snapshot.getValue(AppModel::class.java)!!
                    nameTv!!.text = appModel.Name
                    emailTv!!.text = user!!.email
                    pixTV!!.text = "CHAVE PIX: " + appModel.pix_chave
                    pDialog.dismissWithAnimation()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@ProfileActivity,
                        "Erro: " + error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
        }

    private fun init() {
        nameTv = findViewById(R.id.nameTv)
        emailTv = findViewById(R.id.emailTv)
        shareTv = findViewById(R.id.shareTv)
        logoutTv = findViewById(R.id.logoutTv)
        deleteTv = findViewById(R.id.deletCard)
        aboutTV = findViewById(R.id.version)
        pixTV = findViewById(R.id.pixTv)
        keyTv = findViewById(R.id.keyTv)
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        reference = FirebaseDatabase.getInstance().reference.child("GameX").child("users")
        withdrawl = FirebaseDatabase.getInstance().reference.child("GameX").child("Saques")
    }

    override fun onBackPressed() {
        finish()
    }
}