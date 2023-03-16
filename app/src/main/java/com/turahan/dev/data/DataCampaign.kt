package com.turahan.dev.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataCampaign(
    val idCampaign: String? = null,
    val campaignDate: String? = null,
    val campaignTitle: String? = null,
    val campaignDescription: String? = null,
    val campaignPhoto: String? = null
): Parcelable
