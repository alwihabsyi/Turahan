package com.turahan.dev

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.turahan.dev.data.DataDonasiMakanan

class DonationHistoryAdapter(private val dataset: ArrayList<DataDonasiMakanan>) :
    RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        return DonationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_volunteer,parent,false)
        )
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        holder.onBindView(dataset[position])
    }

    inner class DonationViewHolder(view: View): RecyclerView.ViewHolder(view){
        val fotoDonasi = view.findViewById<ImageView>(R.id.ivDonasi)
        val judulDonasi = view.findViewById<TextView>(R.id.tvJudulDonasi)
        val tanggalDonasi = view.findViewById<TextView>(R.id.tvTanggalDonasi)
        val kategoriDonasi = view.findViewById<TextView>(R.id.tvKategoriDonasi)
        val statusDonasi = view.findViewById<TextView>(R.id.tvStatusDonasi)

        fun onBindView(data: DataDonasiMakanan){
            Picasso
                .get()
                .load(data.fotoDonasi)
                .into(fotoDonasi)
            judulDonasi.text = data.judulDonasi
            tanggalDonasi.text = data.tanggalDonasi
            kategoriDonasi.text = data.kategoriDonasi
            statusDonasi.text = data.statusDonasi
        }
    }
}