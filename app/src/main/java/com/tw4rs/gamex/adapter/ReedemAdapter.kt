/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.adapter

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.activities.HomeActivity
import com.tw4rs.gamex.adapter.ReedemAdapter.ReedemHolder
import com.tw4rs.gamex.model.AppModel
import com.tw4rs.gamex.model.TotalModel
import com.tw4rs.gamex.model.WithdrawModel
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*

class ReedemAdapter(private val list: List<WithdrawModel?>) : RecyclerView.Adapter<ReedemHolder>() {
    private var b: String? = null
    private var a: String? = null
    var datareedem: String? = null
    var hourreedem: String? = null
    private var user: FirebaseUser? = null
    private var context: Context? = null
    private var userRef: DatabaseReference? = null
    private var totalRef: DatabaseReference? = null
    private var wait = 0
    private var pointTv = 0
    private var pixEt: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReedemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.reedem_item,
                parent,
                false
            )
        context = parent.context
        return ReedemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReedemHolder, position: Int) {
        holder.valor.text = "R$" + list[position]!!.valor
        holder.pontos.text = "-" + list[position]!!.point
    }

    override fun getItemId(i: Int): Long {
        return 20
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ReedemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val valor: TextView = itemView.findViewById(R.id.amount)
        val pontos: TextView = itemView.findViewById(R.id.points)
        private fun loadData() {
            val pDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Carregando seus dados"
            pDialog.setCancelable(false)
            pDialog.show()
            userRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appModel = snapshot.getValue(AppModel::class.java)!!
                    pointTv = appModel.Coins
                    pixEt = appModel.pix_chave
                    pDialog.dismissWithAnimation()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(context!!, "Erro: " + error.message, Toast.LENGTH_SHORT, true)
                        .show()
                }
            })
            totalRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val totalModel = snapshot.getValue(TotalModel::class.java)
                    wait = totalModel!!.wait
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(context!!, "Erro: " + error.message, Toast.LENGTH_SHORT, true)
                        .show()
                }
            })
        }

        private fun sendPaymentToDatabse() {
            val withdrawRef =
                FirebaseDatabase.getInstance().reference.child("GameX").child("Premios").child(
                    user!!.uid
                )
            val key = withdrawRef.push().key
            val map = HashMap<String, Any?>()
            map["credencial"] = pixEt
            map["id"] = key
            map["status"] = "Pendente"
            map["uid"] = user!!.uid
            map["date"] = datareedem
            map["hora"] = hourreedem
            map["valor"] = b
            map["pontos"] = pointTv


            //Map User
            val upadate = pointTv.toLong()
            val updaCoins = upadate - a!!.toInt()
            val updateW = (wait + 1).toLong()


            //maps Firebase
            val userMap = HashMap<String, Any>()
            userMap["Coins"] = updaCoins
            userRef!!.updateChildren(userMap).addOnCompleteListener {
                Toasty.success(
                    context!!, "Parabéns , aguarde o pagamento", Toasty.LENGTH_SHORT, true
                ).show()
            }
            assert(key != null)
            withdrawRef.child(key!!).setValue(map).addOnCompleteListener {
                SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("LEMBRETE")
                    .setContentText("PAGAMENTOS PODEM SER EFETUADOS EM POUCO TEMPO , OU ATÉ O PROXIMO DIA 20 APÓS O PEDIDO , Pagamentos Em nome de TW4RS TEC")
                    .setConfirmText("OK, estou no aguardo")
                    .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                        sweetAlertDialog.dismissWithAnimation()
                        context!!.startActivity(Intent(context, HomeActivity::class.java))
                    }
                    .show()
            }

            //maps Firebase
            val totalMap = HashMap<String, Any>()
            totalMap["wait"] = updateW
            totalRef!!.updateChildren(totalMap).addOnCompleteListener {
                Toasty.success(
                    context!!, "Parabéns , aguarde o pagamento", Toasty.LENGTH_SHORT, true
                ).show()
            }
        }

        init {
            val database = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()
            user = auth.currentUser
            userRef = database.reference.child("GameX").child("users").child(user!!.uid)
            totalRef = database.reference.child("GameX").child("event").child("Total")
            loadData()
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val formatador1 = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            datareedem = dateFormat.format(currentDate)
            hourreedem = formatador1.format(currentDate)
            itemView.setOnClickListener {
                val pos = layoutPosition
                a = list[pos]!!.point
                b = list[pos]!!.valor
                if (a!!.toInt() > pointTv) {
                    Toasty.error(
                        context!!,
                        "Não possui TWSCASH suficiente para o saque",
                        Toasty.LENGTH_SHORT,
                        true
                    ).show()
                    return@setOnClickListener
                }
                sendPaymentToDatabse()
            }
        }
    }
}