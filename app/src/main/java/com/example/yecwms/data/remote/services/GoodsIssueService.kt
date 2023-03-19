package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface GoodsIssueService {

    companion object {
        fun get(): GoodsIssueService =
            ServiceBuilder.createService(GoodsIssueService::class.java)
    }



    @POST("InventoryGenExits")
    suspend fun insertGoodsIssue(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("InventoryGenExits")
    suspend fun getGoodsIssuesList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,PaidToDate,PaidToDateSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @GET("InventoryGenExits")
    suspend fun getGoodsIssuesListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocNum,NumAtCard,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @POST("InventoryGenExits({docEntry})/Cancel")
    suspend fun cancelGoodsIssue(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("InventoryGenExits({docEntry})")
    suspend fun getGoodsIssue(
        @Path("docEntry") docEntry: Long
    ): Response<Document>
}