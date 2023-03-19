package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface PurchaseInvoicesService {

    companion object {
        fun get(): PurchaseInvoicesService =
            ServiceBuilder.createService(PurchaseInvoicesService::class.java)
    }

    @POST("PurchaseInvoices")
    suspend fun addPurchaseInvoice(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("PurchaseInvoices")
    suspend fun getPurchaseInvoicesList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @GET("sml.svc/PURCHASE_INV_LIST")
    suspend fun getOpenPurchaseInvoicesListSML(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = null,
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @POST("PurchaseInvoices({docEntry})/Close")
    suspend fun closePurchaseInvoice(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @POST("PurchaseInvoices({docEntry})/Cancel")
    suspend fun cancelPurchaseInvoice(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("PurchaseInvoices({docEntry})")
    suspend fun getPurchaseInvoice(
        @Path("docEntry") docEntry: Long
    ): Response<Document>

    @PATCH("PurchaseInvoices({docEntry})")
    suspend fun updatePurchaseInvoice(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: DocumentForPost
    ): Response<Any>
}