/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.tw4rs.gamex.adapter.ReedemAdapter
import com.tw4rs.gamex.model.WithdrawModel

class ReedemList : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    var reference: DatabaseReference? = null
    var adapter: ReedemAdapter? = null
    var list: MutableList<WithdrawModel?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reedem_list)
        init()
        recyclerView!!.layoutManager = GridLayoutManager(
            this,
            3
        )
        list = ArrayList()
        adapter = ReedemAdapter(list as ArrayList<WithdrawModel?>)
        recyclerView!!.adapter = adapter
        reference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                (list as ArrayList<WithdrawModel?>).clear()
                for (dataSnapshot in snapshot.children) {
                    val model = dataSnapshot.getValue(
                        WithdrawModel::class.java
                    )
                    (list as ArrayList<WithdrawModel?>).add(model)
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ReedemList, "Error: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }

    private fun init() {
        recyclerView = findViewById(R.id.recyclerView_lrd)
        reference = FirebaseDatabase.getInstance().reference.child("GameX").child("rewards")
    }
}