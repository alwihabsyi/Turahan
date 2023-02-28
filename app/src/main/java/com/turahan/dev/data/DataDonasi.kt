package com.turahan.dev.data

data class DataDonasiMakanan(
    val idUser: String? = null,
    val idDonasi: String? = null,
    val judulDonasi: String? = null,
    val alamatDonasi: String? = null,
    val tanggalDonasi: String? = null,
    val kategoriDonasi: String? = null,
    val statusDonasi: String? = null,
    val fotoDonasi: String? = null,
    val dropOffPickUp: String? = null
)

data class DataDonasiUang(
    val idUser: String? = null,
    val idDonasi: String? = null,
    val namaDonasi: String? = null,
    val metodePembayaran: String? = null,
    val tanggalDonasi: String? = null,
    val nominalDonasi: String? = null,
    val statusDonasi: String? = null
)
