package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface PurchaseDeliveriesService {

    companion object {
        fun get(): PurchaseDeliveriesService =
            ServiceBuilder.createService(PurchaseDeliveriesService::class.java)
    }

    @POST("Drafts")
    suspend fun insertPurchaseDeliveryDraft(
        @Body body: DocumentForPost
    ): Response<Document>

    @POST("PurchaseDeliveryNotes")
    suspend fun insertPurchaseDelivery(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("PurchaseDeliveryNotes")
    suspend fun getPurchaseDeliveriesList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,PaidToDate,PaidToDateSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @GET("PurchaseDeliveryNotes")
    suspend fun getPurchaseDeliveriesListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocNum,NumAtCard,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @POST("PurchaseDeliveryNotes({docEntry})/Cancel")
    suspend fun cancelPurchaseDelivery(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("PurchaseDeliveryNotes({docEntry})")
    suspend fun getPurchaseDelivery(
        @Path("docEntry") docEntry: Long
    ): Response<Document>
}