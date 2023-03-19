package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface PurchaseReturnsService {

    companion object {
        fun get(): PurchaseReturnsService =
            ServiceBuilder.createService(PurchaseReturnsService::class.java)
    }

    @POST("Drafts")
    suspend fun insertPurchaseReturnDraft(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("PurchaseReturns")
    suspend fun getPurchaseReturnsList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,PaidToDate,PaidToDateSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @GET("PurchaseReturns")
    suspend fun getPurchaseReturnsListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocNum,NumAtCard,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @POST("PurchaseReturns({docEntry})/Cancel")
    suspend fun cancelPurchaseReturn(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("PurchaseReturns({docEntry})")
    suspend fun getPurchaseReturn(
        @Path("docEntry") docEntry: Long
    ): Response<Document>
}