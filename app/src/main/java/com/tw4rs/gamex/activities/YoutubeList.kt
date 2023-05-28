/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.tw4rs.gamex.R
import com.tw4rs.gamex.adapter.YoutubeAdapter
import com.tw4rs.gamex.model.VideoModel
import com.tw4rs.gamex.util.BaseActivty

class YoutubeList : BaseActivty() {
    private var recyclerView: RecyclerView? = null
    private var user: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var adapter: YoutubeAdapter? = null
    var list: MutableList<VideoModel?>? = null
    private val userRef: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_list)
        init()
        recyclerView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        list = ArrayList()
        adapter = YoutubeAdapter(list as ArrayList<VideoModel?>)
        recyclerView!!.adapter = adapter
        reference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                (list as ArrayList<VideoModel?>).clear()
                for (dataSnapshot in snapshot.children) {

                    val model = dataSnapshot.getValue(VideoModel::class.java)
                    (list as ArrayList<VideoModel?>).add(model)

                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@YoutubeList, "Error: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }

    private fun init() {
        recyclerView = findViewById(R.id.recyclerView_lyt)
        val auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        reference =
            FirebaseDatabase.getInstance().reference.child("GameX").child("event").child("yt")
    }
}