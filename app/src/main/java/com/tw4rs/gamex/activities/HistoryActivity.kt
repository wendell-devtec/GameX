/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.activities


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.adapter.HistoryAdapter
import com.tw4rs.gamex.model.HistoryModel
import com.tw4rs.gamex.util.BaseActivty

class HistoryActivity : BaseActivty() {
    private var recyclerView: RecyclerView? = null
    private var user: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var adapter: HistoryAdapter? = null
    var list: MutableList<HistoryModel?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        init()
        recyclerView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        list = ArrayList()
        adapter = HistoryAdapter(list as ArrayList<HistoryModel?>)
        recyclerView!!.adapter = adapter
        reference!!.child(user!!.uid)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (dataSnapshot in snapshot.children) {
                            val model = dataSnapshot.getValue(
                                HistoryModel::class.java
                            )!!
                            if (model.status == "Pendente") {
                                (list as ArrayList<HistoryModel?>).add(model)
                            }
                        }
                        adapter!!.notifyDataSetChanged()
                    } else {
                        SweetAlertDialog(this@HistoryActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Poxa")
                            .setContentText("Você não possui saques Pendentes")
                            .setConfirmText("OK")
                            .setConfirmClickListener { obj: SweetAlertDialog -> obj.dismissWithAnimation() }
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@HistoryActivity, "Error: " + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            })
    }

    private fun init() {
        recyclerView = findViewById(R.id.recyclerView)
        val auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        reference = FirebaseDatabase.getInstance().reference.child("GameX").child("Premios")
    }
}