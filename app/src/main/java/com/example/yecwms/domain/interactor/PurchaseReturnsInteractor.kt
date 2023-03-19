package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.repository.PurchaseReturnsRepository
import com.example.yecwms.data.repository.PurchaseReturnsRepositoryImpl
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse

interface PurchaseReturnsInteractor {

    suspend fun getPurchaseReturns(
        filter: String = "",
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getMorePurchaseReturns(
        filter: String = "",
        skipValue: Int,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getPurchaseReturn(docEntry: Long): Document?
    suspend fun insertPurchaseReturnDraft(PurchaseReturn: DocumentForPost): Document?
    val errorMessage: String?
}

class PurchaseReturnsInteractorImpl : PurchaseReturnsInteractor {

    private val repository: PurchaseReturnsRepository by lazy { PurchaseReturnsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getPurchaseReturns(
        filter: String,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getPurchaseReturnsList(
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

    override suspend fun getMorePurchaseReturns(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response =
            repository.getPurchaseReturnsList(
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

    override suspend fun getPurchaseReturn(docEntry: Long): Document? {
        val response = repository.getPurchaseReturn(docEntry)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }


    }

    override suspend fun insertPurchaseReturnDraft(PurchaseReturn: DocumentForPost): Document? {
        val response = repository.insertPurchaseReturnDraft(PurchaseReturn)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}