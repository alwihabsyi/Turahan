package com.turahan.dev

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class rvArticleHomeAdapter(private val data: ArrayList<ArticleExpand.News>) : RecyclerView.Adapter<rvArticleHomeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rvArticleHomeAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = inflater.inflate(R.layout.rv_home_article, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = 3

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private var imgView: ImageView? = null
        private var txtTitle: TextView? = null
        private var txtDate: TextView? = null

        init {
            imgView = view.findViewById(R.id.ivDonasi)
            txtTitle = view.findViewById(R.id.tvJudulDonasi)
            txtDate = view.findViewById(R.id.articleDate)
        }

        fun bind(data: ArticleExpand.News) {
            imgView?.setImageResource(data.imgView)
            txtTitle?.text = data.txtTitle
            txtDate?.text = data.txtDate
        }

    }
}
