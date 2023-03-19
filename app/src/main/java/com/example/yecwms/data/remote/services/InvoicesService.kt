package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface InvoicesService {

    companion object {
        fun get(): InvoicesService =
            ServiceBuilder.createService(InvoicesService::class.java)
    }

    @POST("Invoices")
    suspend fun addInvoice(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("Invoices")
    suspend fun getInvoicesList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,PaidToDate,PaidToDateSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @GET("Invoices")
    suspend fun getInvoicesListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocNum,NumAtCard,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @POST("Invoices({docEntry})/Cancel")
    suspend fun cancelInvoice(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("Invoices({docEntry})")
    suspend fun getInvoice(
        @Path("docEntry") docEntry: Long
    ): Response<Document>
}