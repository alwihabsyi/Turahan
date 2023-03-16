package com.turahan.dev

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.turahan.dev.R
import com.turahan.dev.data.DataCampaign
import com.turahan.dev.data.DataDonasi

class CampaignDonateAdapter(
    private val dataset: ArrayList<DataDonasi>, private val eventHandling: (DataDonasi) -> Unit
) : RecyclerView.Adapter<CampaignDonateAdapter.CampaignViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        return CampaignViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_campaigncb, parent, false),
            eventHandling
        )
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        holder.onBindView(dataset[position])
    }

    inner class CampaignViewHolder(view: View, val eventHandling: (DataDonasi) -> Unit) :
        RecyclerView.ViewHolder(view) {
        val fotoDonasi = view.findViewById<ImageView>(R.id.ivCampaign)
        val judulDonasi = view.findViewById<TextView>(R.id.tvJudulCampaign)
        val tanggalDonasi = view.findViewById<TextView>(R.id.tvTanggalCampaign)
        val checkBox = view.findViewById<CheckBox>(R.id.checkboxCampaign)
        var isSelected: Boolean = false

        fun onBindView(data: DataDonasi) {
            if (data.fotoDonasi != "") {
                Picasso.get().load(data.fotoDonasi).into(fotoDonasi)
            } else {
                fotoDonasi.setImageResource(R.drawable.ic_campaign)
            }

            judulDonasi.text = data.judulDonasi
            tanggalDonasi.text = data.tanggalDonasi
            checkBox.isChecked = isSelected

            itemView.setOnClickListener {
                eventHandling(data).also {
                    checkBox.isChecked = !checkBox.isChecked
                    data.isSelected = checkBox.isChecked
                }
            }
        }

    }
}