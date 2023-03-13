package com.turahan.dev.data

import com.turahan.dev.data.TransactionStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface TransactionService {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Authorization: Basic U0ItTWlkLXNlcnZlci02V3hNUTZ0bmFBTk9acndIRURBT2V5dXU6"
    )
    @GET(value = "v2/{order_id}/status")
    suspend fun getTransaction(@Path(value = "order_id") order_id: String) : Response<TransactionStatus>

}