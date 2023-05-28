package com.tw4rs.gamex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tw4rs.gamex.R
import com.tw4rs.gamex.adapter.HistoryAdapter.HistoryHolder
import com.tw4rs.gamex.model.HistoryModel

class HistoryAdapter(private val list: List<HistoryModel?>) :
    RecyclerView.Adapter<HistoryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.history_items,
                parent,
                false
            )
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.phoneTv.text = list[position]!!.credencial
        holder.valor.text = list[position]!!.valor
        holder.status.text = list[position]!!.status
        holder.data.text = list[position]!!.date
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val phoneTv: TextView
        val valor: TextView
        val status: TextView
        val data: TextView

        init {
            phoneTv = itemView.findViewById(R.id.phone)
            data = itemView.findViewById(R.id.dataTV)
            valor = itemView.findViewById(R.id.amount)
            status = itemView.findViewById(R.id.statusTV)
        }
    }
}