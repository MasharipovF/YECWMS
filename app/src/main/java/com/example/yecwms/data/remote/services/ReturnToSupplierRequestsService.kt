package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface ReturnToSupplierRequestsService {

    companion object {
        fun get(): ReturnToSupplierRequestsService =
            ServiceBuilder.createService(ReturnToSupplierRequestsService::class.java)
    }

    @POST("GoodsReturnRequest")
    suspend fun addReturnToSupplierRequest(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("GoodsReturnRequest")
    suspend fun getReturnToSupplierRequestsList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,DocDate,DocDueDate,CardCode,CardName,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @POST("GoodsReturnRequest({docEntry})/Close")
    suspend fun closeReturnToSupplierRequest(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("GoodsReturnRequest({docEntry})")
    suspend fun getReturnToSupplierRequest(
        @Path("docEntry") docEntry: Long
    ): Response<Document>

    @PATCH("GoodsReturnRequest({docEntry})")
    suspend fun updateReturnToSupplierRequest(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: DocumentForPost
    ): Response<Any>
}