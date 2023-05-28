package com.tw4rs.gamex.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tw4rs.gamex.R
import com.tw4rs.gamex.adapter.BubbleAdapter.BubbleViewHolder
import java.util.*

class BubbleAdapter(var onBubbleClickListnear: (Int) -> Unit) :
    RecyclerView.Adapter<BubbleViewHolder>() {
    private var randomnumber = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.lyt_bubble,
                parent,
                false
            )
        return BubbleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BubbleViewHolder, position: Int) {
        var coin = 0
        val random1 = Random().nextInt(position + 1) + 1
        val random2 = Random().nextInt(position + 1) + 1
        val random4 = Random().nextInt(position + 1) + 1
        val random3 = Random().nextInt(position + 1) + 1
        Log.d(TAG, "onBindViewHolder: $random1")
        when {
            random1 == position -> {
                Log.d(TAG, "onBindViewHolder:yes11====== $random1")
            }
            random2 == position -> {
                Log.d(TAG, "onBindViewHolder:yes22====== $random2")
            }
            random3 == position -> {
                Log.d(TAG, "onBindViewHolder:yes33====== $random2")
            }
            random4 == position -> {
                Log.d(TAG, "onBindViewHolder:yes44====== $random2")
            }
            else -> {
                val min = "0"
                val min1 = min.toInt()
                val max = "45"
                val max1 = max.toInt()
                randomnumber = Random().nextInt(max1 - min1 + 1) + min1
                holder.tvnumer.text = randomnumber.toString()
                coin = randomnumber
            }
        }
        val finalCoin = coin
        holder.imgfront.setOnClickListener {
            Log.d(TAG, "onBindViewHoldervvv: $finalCoin")
            holder.imgfront.visibility = View.GONE
            onBubbleClickListnear(finalCoin)

        }
    }

    override fun getItemCount(): Int {
        return 20
    }

    class BubbleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvnumer: TextView = itemView.findViewById(R.id.tvNumber)
        val imgfront: ImageView = itemView.findViewById(R.id.imageviewfront)

    }

    companion object {
        private const val TAG = "bubbleadapter"
    }
}