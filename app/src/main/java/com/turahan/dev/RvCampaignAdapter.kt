package com.turahan.dev

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.turahan.dev.R
import com.turahan.dev.data.DataCampaign
import com.turahan.dev.data.DataDonasi

class RvCampaignAdapter(
    private val dataset: ArrayList<DataCampaign>,
    private val eventHandling: (DataCampaign) -> Unit
) :
    RecyclerView.Adapter<RvCampaignAdapter.CampaignViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        return CampaignViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_campaign, parent, false),
            eventHandling
        )
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        holder.onBindView(dataset[position])
    }

    inner class CampaignViewHolder(view: View, val eventHandling: (DataCampaign) -> Unit) :
        RecyclerView.ViewHolder(view) {
        val fotoDonasi = view.findViewById<ImageView>(R.id.ivCampaign)
        val judulDonasi = view.findViewById<TextView>(R.id.tvJudulCampaign)
        val tanggalDonasi = view.findViewById<TextView>(R.id.tvTanggalCampaign)

        fun onBindView(data: DataCampaign) {
            if (data.campaignPhoto != "") {
                Picasso
                    .get()
                    .load(data.campaignPhoto)
                    .into(fotoDonasi)
            } else {
                fotoDonasi.setImageResource(R.drawable.ic_campaign)
            }

            judulDonasi.text = data.campaignTitle
            tanggalDonasi.text = data.campaignDate

            itemView.setOnClickListener { eventHandling(data) }
        }
    }
}