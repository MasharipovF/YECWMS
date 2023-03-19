package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface PurchaseOrdersService {

    companion object {
        fun get(): PurchaseOrdersService =
            ServiceBuilder.createService(PurchaseOrdersService::class.java)
    }

    @POST("PurchaseOrders")
    suspend fun addPurchaseOrder(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("PurchaseOrders")
    suspend fun getPurchaseOrdersList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,DocDate,DocDueDate,CardCode,CardName,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @POST("PurchaseOrders({docEntry})/Close")
    suspend fun closePurchaseOrder(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("PurchaseOrders({docEntry})")
    suspend fun getPurchaseOrder(
        @Path("docEntry") docEntry: Long
    ): Response<Document>

    @PATCH("PurchaseOrders({docEntry})")
    suspend fun updatePurchaseOrder(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: DocumentForPost
    ): Response<Any>
}