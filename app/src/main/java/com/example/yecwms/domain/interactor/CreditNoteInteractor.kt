package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.repository.CreditNotesRepository
import com.example.yecwms.data.repository.CreditNotesRepositoryImpl
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse

interface CreditNoteInteractor {

    suspend fun getCreditNotes(
        filter: String = "",
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getMoreCreditNotes(
        filter: String = "",
        skipValue: Int,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getCreditNote(docEntry: Long): Document?
    suspend fun insertCreditNote(CreditNote: DocumentForPost): Document?
    val errorMessage: String?
}

class CreditNoteInteractorImpl : CreditNoteInteractor {

    private val repository: CreditNotesRepository by lazy { CreditNotesRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getCreditNotes(
        filter: String,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getCreditNoteList(
            filter = filter,
            docStatus = docStatus,
            dateFrom = dateFrom,
            dateTo = dateTo
        )
        return if (response is DocumentsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getMoreCreditNotes(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getCreditNoteList(
            filter = filter,
            skipValue = skipValue,
            docStatus = docStatus,
            dateFrom = dateFrom,
            dateTo = dateTo
        )
        return if (response is DocumentsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getCreditNote(docEntry: Long): Document? {
        val response = repository.getCreditNote(docEntry)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
        /*val priceLists = masterDataRepo.getPriceLists()?.value
        val bpGroups = masterDataRepo.getBpGroups(null)?.value

        result?.GroupName = Mappers.mapBpGroupCodeToName(bpGroups, result?.GroupCode)
        result?.PriceListName = Mappers.mapPriceListCodeToName(priceLists, result?.PriceListCode)*/

    }

    override suspend fun insertCreditNote(CreditNote: DocumentForPost): Document? {
        Log.d("CREDITNOTE", CreditNote.toString())
        val response = repository.insertCreditNote(CreditNote)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}