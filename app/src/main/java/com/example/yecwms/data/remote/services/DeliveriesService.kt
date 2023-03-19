package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface DeliveriesService {

    companion object {
        fun get(): DeliveriesService =
            ServiceBuilder.createService(DeliveriesService::class.java)
    }

    @POST("Drafts")
    suspend fun insertDeliveryDraft(
        @Body body: DocumentForPost
    ): Response<Document>

    @POST("DeliveryNotes")
    suspend fun insertDelivery(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("DeliveryNotes")
    suspend fun getDeliveriesList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,PaidToDate,PaidToDateSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @GET("DeliveryNotes")
    suspend fun getDeliveriesListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocNum,NumAtCard,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @POST("DeliveryNotes({docEntry})/Cancel")
    suspend fun cancelDelivery(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("DeliveryNotes({docEntry})")
    suspend fun getDelivery(
        @Path("docEntry") docEntry: Long
    ): Response<Document>
}