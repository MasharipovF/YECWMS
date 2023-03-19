package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.discount.DiscountByDocTotalVal
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DiscountsServices {

    companion object {
        fun get(): DiscountsServices = ServiceBuilder.createService(DiscountsServices::class.java)
    }


    @GET("U_DOCTOTAL_DISCOUNT")
    suspend fun getDiscountByDocTotal(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=500",
        @Query("\$filter") filter: String = "U_dateFrom ne NULL",
        @Query("\$orderby") order: String = "U_sum asc"
        ): Response<DiscountByDocTotalVal>


}