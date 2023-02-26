package com.turahan.dev.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.turahan.dev.R

class OnBoardingAdapter: RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    private var onBoardingItems: MutableList<OnBoardingItem> = mutableListOf()

    inner class OnBoardingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.text_title)
        var description: TextView = itemView.findViewById(R.id.text_description)
        var image: ImageView = itemView.findViewById(R.id.img_slide_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.slide_item_container, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return onBoardingItems.size
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        val data = onBoardingItems[position]
        holder.title.text = data.title
        holder.description.text = data.description
        holder.image.setImageResource(data.image)

    }
    fun setNewItem(newDataSet: List<OnBoardingItem>) {
        onBoardingItems.clear()
        onBoardingItems.addAll(newDataSet)
        this.notifyDataSetChanged()
    }
}