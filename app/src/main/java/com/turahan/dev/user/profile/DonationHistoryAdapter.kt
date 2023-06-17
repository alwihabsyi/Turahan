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
import com.turahan.dev.data.DataDonasi

class DonationHistoryAdapter(
    private val dataset: ArrayList<DataDonasi>,
    private val eventHandling: (DataDonasi) -> Unit
) :
    RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        return DonationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_volunteer, parent, false),
            eventHandling
        )
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        holder.onBindView(dataset[position])
    }

    inner class DonationViewHolder(view: View, val eventHandling: (DataDonasi) -> Unit) :
        RecyclerView.ViewHolder(view) {
        val fotoDonasi: ImageView = view.findViewById<ImageView>(R.id.ivDonasi)
        val judulDonasi = view.findViewById<TextView>(R.id.tvJudulDonasi)
        val tanggalDonasi = view.findViewById<TextView>(R.id.tvTanggalDonasi)
        val kategoriDonasi = view.findViewById<TextView>(R.id.tvKategoriDonasi)
        val statusDonasi = view.findViewById<TextView>(R.id.tvStatusDonasi)
        val bg = view.findViewById<ImageView>(R.id.bgStatus)

        fun onBindView(data: DataDonasi) {
            if (data.fotoDonasi != "") {
                Picasso
                    .get()
                    .load(data.fotoDonasi)
                    .into(fotoDonasi)
            } else {
                fotoDonasi.setImageResource(R.drawable.cash_ic)
            }

            judulDonasi.text = data.judulDonasi
            tanggalDonasi.text = data.tanggalDonasi
            kategoriDonasi.text = data.dropOffPickUp
            statusDonasi.text = data.statusDonasi

            if (data.statusDonasi == "Success") {
                bg.setBackgroundColor(Color.parseColor("#39FC03"))
            } else if (data.statusDonasi == "Processed") {
                bg.setBackgroundColor(Color.parseColor("#FFFFC107"))
            } else if (data.statusDonasi == "Pending") {
                bg.setBackgroundColor(Color.parseColor("#9403A9F4"))
            }

            itemView.setOnClickListener { eventHandling(data) }
        }
    }
}