package com.example.yecwms.data.remote.services


import com.example.yecwms.data.entity.reports.StockTransactionReportVal
import com.example.yecwms.core.ServiceBuilder

import retrofit2.Response
import retrofit2.http.*

interface ReportsService {

    companion object {
        fun get(): ReportsService =
            ServiceBuilder.createService(ReportsService::class.java)
    }

    @GET("sml.svc/STOCKTRANSACTIONREPORT")
    suspend fun getStockTransactionReport(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocDate,ObjectType,Warehouse,Quantity,InventoryUOM,UserCode",
        @Query("\$filter") filter: String? = "",
        @Query("\$orderby") order: String? = "DocDate asc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<StockTransactionReportVal>

    @GET("sml.svc/STOCKTRANSACTIONREPORT")
    suspend fun getStockOnDate(
        @Query("\$filter") filter: String? = null,
        @Query("\$apply") apply: String? = null
    ): Response<StockTransactionReportVal>



}