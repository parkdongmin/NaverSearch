package com.example.naversearch

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_raw.view.*


class RecyclerViewAdapter(val homefeed: Homefeed): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    //아이템의 갯수
    override fun getItemCount(): Int {
        return homefeed.items.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_raw, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(homefeed.items[position])
    }


    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data : Item){
            //
            Glide.with(view.context).load(data.image)
                .apply(RequestOptions().override(100, 100))
                .apply(RequestOptions.centerCropTransform())
                .into(view.imageView)

            itemView.textView_title.text = "제목 : ${data.title}"
            itemView.textView_date.text = "출시 : ${data.pubDate}"
            itemView.textView_usrRating.text = "평점 : ${data.userRating}"

            //클릭시 웹사이트 연결
            itemView.setOnClickListener {
                val webpage = Uri.parse("${data.link}")
                val webIntent = Intent(Intent.ACTION_VIEW, webpage)
                view.getContext().startActivity(webIntent)
            }
        }
    }

}