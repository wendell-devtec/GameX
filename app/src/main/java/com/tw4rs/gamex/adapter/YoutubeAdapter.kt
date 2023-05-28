package com.tw4rs.gamex.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tw4rs.gamex.R
import com.tw4rs.gamex.activities.YT_Activity
import com.tw4rs.gamex.adapter.YoutubeAdapter.YoutubeHolder
import com.tw4rs.gamex.model.VideoModel

class YoutubeAdapter(private val list: List<VideoModel?>) : RecyclerView.Adapter<YoutubeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_video,
                parent,
                false
            )
        return YoutubeHolder(view)
    }

    override fun onBindViewHolder(holder: YoutubeHolder, position: Int) {
        holder.title.text = list[position]!!.title
        holder.point.text = list[position]!!.point
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class YoutubeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val point: TextView = itemView.findViewById(R.id.tvPoints)

        init {
            itemView.setOnClickListener {
                val pos = layoutPosition
                val go = Intent(itemView.context, YT_Activity::class.java)
                go.putExtra("video_id", list[pos]!!.videoId)
                go.putExtra("timer", list[pos]!!.timer)
                go.putExtra("point", list[pos]!!.point)
                go.putExtra("id", list[pos]!!.id)
                go.putExtra("ads", list[pos]!!.ads)
                itemView.context.startActivity(go)
            }
        }
    }
}