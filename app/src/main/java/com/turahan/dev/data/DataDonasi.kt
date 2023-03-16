package com.turahan.dev.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataDonasi(
    val idUser: String? = null,
    val idDonasi: String? = null,
    val judulDonasi: String? = null,
    val alamatDonasi: String? = null,
    val tanggalDonasi: String? = null,
    val kategoriDonasi: String? = null,
    val statusDonasi: String? = null,
    val fotoDonasi: String? = null,
    val dropOffPickUp: String? = null,
    val titleCampaign: String? = null,
    val fotoBukti: String? = null,
    var isSelected: Boolean? = null
): Parcelable
