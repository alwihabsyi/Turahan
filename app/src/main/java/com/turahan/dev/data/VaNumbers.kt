package com.turahan.dev.data

import com.google.gson.annotations.SerializedName

data class VaNumbers(
    @SerializedName("va_number")
    val va_number: String,
    @SerializedName("bank")
    val bank: String
)
