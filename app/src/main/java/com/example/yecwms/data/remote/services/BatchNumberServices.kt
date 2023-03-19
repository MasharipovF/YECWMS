package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BatchNumberServices {

    companion object {
        fun get(): BatchNumberServices = ServiceBuilder.createService(BatchNumberServices::class.java)
    }

    @GET("SQLQueries('batches')/List")
    suspend fun getBatchNumbers(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=1000000",
        @Query("itemCode") ItemCode: String,
        @Query("whsCode") WhsCode: String,
    ): Response<BatchNumbersVal>

}