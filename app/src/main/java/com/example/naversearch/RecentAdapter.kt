package com.example.naversearch

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class RecentAdapter(private val context: Context, private var his: ArrayList<HistoryEntity>) :
    RecyclerView.Adapter<RecentAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recent, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return his.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(his[position])

        holder.itemView.setOnClickListener {
            val intent= Intent( context,MainActivity::class.java)
            intent.putExtra("title","${his[position].title}")
            context.startActivity(intent)
        }
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val title = itemView?.findViewById<TextView>(R.id.recent_title)

        fun bind(his: HistoryEntity) {
            title?.text = his.title
        }

    }

    fun deleteTitle(){
        this.his.removeAt(10)
    }
}