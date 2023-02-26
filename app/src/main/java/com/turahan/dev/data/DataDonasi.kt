package com.turahan.dev.data

data class DataDonasiMakanan(
    val idDonasi: String? = null,
    val namaDonasi: String? = null,
    val tanggalDonasi: String? = null,
    val kategoriDonasi: String? = null
)

data class DataDonasiUang(
    val idDonasi: String? = null,
    val namaDonasi: String? = null,
    val tanggalDonasi: String? = null,
    val nominalDonasi: String? = null
)
