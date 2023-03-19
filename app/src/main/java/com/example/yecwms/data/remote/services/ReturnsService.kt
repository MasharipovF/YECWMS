package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface ReturnsService {

    companion object {
        fun get(): ReturnsService =
            ServiceBuilder.createService(ReturnsService::class.java)
    }

    @POST("Drafts")
    suspend fun insertReturnDraft(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("Returns")
    suspend fun getReturnsList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,PaidToDate,PaidToDateSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @GET("Returns")
    suspend fun getReturnsListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocNum,NumAtCard,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @POST("Returns({docEntry})/Cancel")
    suspend fun cancelReturn(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("Returns({docEntry})")
    suspend fun getReturn(
        @Path("docEntry") docEntry: Long
    ): Response<Document>
}