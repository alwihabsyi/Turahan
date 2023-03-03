package com.turahan.dev.user.profile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.turahan.dev.R
import com.turahan.dev.data.DataDonasiMakanan

class DonationHistoryAdapter(private val dataset: ArrayList<DataDonasiMakanan>, val eventHandling: (DataDonasiMakanan) -> Unit) :
    RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        return DonationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_volunteer,parent,false), eventHandling
        )
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        holder.onBindView(dataset[position])
    }

    inner class DonationViewHolder(view: View, val eventHandling: (DataDonasiMakanan) -> Unit): RecyclerView.ViewHolder(view){
        val fotoDonasi = view.findViewById<ImageView>(R.id.ivDonasi)
        val judulDonasi = view.findViewById<TextView>(R.id.tvJudulDonasi)
        val tanggalDonasi = view.findViewById<TextView>(R.id.tvTanggalDonasi)
        val kategoriDonasi = view.findViewById<TextView>(R.id.tvKategoriDonasi)
        val statusDonasi = view.findViewById<TextView>(R.id.tvStatusDonasi)
        val bg = view.findViewById<ImageView>(R.id.bgStatus)

        fun onBindView(data: DataDonasiMakanan){
            if(data.fotoDonasi != null){
                Picasso
                    .get()
                    .load(data.fotoDonasi)
                    .into(fotoDonasi)
            }
            judulDonasi.text = data.judulDonasi
            tanggalDonasi.text = data.tanggalDonasi
            kategoriDonasi.text = data.dropOffPickUp
            statusDonasi.text = data.statusDonasi

            if(data.statusDonasi == "Success"){
                bg.setBackgroundColor(Color.parseColor("#39FC03"))
            }

            itemView.setOnClickListener { eventHandling(data) }
        }
    }
}