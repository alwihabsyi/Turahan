package com.turahan.dev.data

import com.google.gson.annotations.SerializedName

data class TransactionStatus(
    @SerializedName("va_numbers")
    val vaNumbers: List<VaNumbers>,
    @SerializedName("gross_amount")
    val gross_amount: String,
    @SerializedName("transaction_time")
    val transaction_time: String,
    @SerializedName("expiry_time")
    val expiry_time: String,
    @SerializedName("transaction_status")
    val transaction_status: String
)
