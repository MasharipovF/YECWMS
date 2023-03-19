package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface SalesOrdersService {

    companion object {
        fun get(): SalesOrdersService =
            ServiceBuilder.createService(SalesOrdersService::class.java)
    }

    @POST("Orders")
    suspend fun addSalesOrder(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("Orders")
    suspend fun getSalesOrdersList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,DocDate,DocDueDate,CardCode,CardName,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @POST("Orders({docEntry})/Close")
    suspend fun closeSalesOrder(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("Orders({docEntry})")
    suspend fun getSalesOrder(
        @Path("docEntry") docEntry: Long
    ): Response<Document>

    @PATCH("Orders({docEntry})")
    suspend fun updateSalesOrder(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: DocumentForPost
    ): Response<Any>
}