package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface ReturnRequestsService {

    companion object {
        fun get(): ReturnRequestsService =
            ServiceBuilder.createService(ReturnRequestsService::class.java)
    }

    @POST("ReturnRequest")
    suspend fun addReturnRequest(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("ReturnRequest")
    suspend fun getReturnRequestsList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,DocDate,DocDueDate,CardCode,CardName,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @POST("ReturnRequest({docEntry})/Close")
    suspend fun closeReturnRequest(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("ReturnRequest({docEntry})")
    suspend fun getReturnRequest(
        @Path("docEntry") docEntry: Long
    ): Response<Document>

    @PATCH("ReturnRequest({docEntry})")
    suspend fun updateReturnRequest(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: DocumentForPost
    ): Response<Any>
}