package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface CreditNotesService {

    companion object {
        fun get(): CreditNotesService =
            ServiceBuilder.createService(CreditNotesService::class.java)
    }

    @POST("CreditNotes")
    suspend fun addCreditNote(
        @Body body: DocumentForPost
    ): Response<Document>

    @GET("CreditNotes")
    suspend fun getCreditNotesList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocEntry,DocNum,NumAtCard,DocDate,DocDueDate,UpdateDate,UpdateTime,CardCode,CardName,DocTotal,DocTotalSys,PaidToDate,PaidToDateSys,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>


    @GET("CreditNotes")
    suspend fun getCreditNotesListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocNum,NumAtCard,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<DocumentsVal>

    @POST("CreditNotes({docEntry})/Cancel")
    suspend fun cancelCreditNote(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("CreditNotes({docEntry})")
    suspend fun getCreditNote(
        @Path("docEntry") docEntry: Long
    ): Response<Document>
}