package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.repository.*
import com.example.yecwms.domain.dto.error.ErrorResponse

interface GoodsIssueInteractor {

    suspend fun getGoodsIssues(
        filter: String = "",
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getMoreGoodsIssues(
        filter: String = "",
        skipValue: Int,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getGoodsIssue(docEntry: Long): Document?
    suspend fun insertGoodsIssue(GoodsIssue: DocumentForPost): Document?

    val errorMessage: String?
}

class GoodsIssueInteractorImpl : GoodsIssueInteractor {

    private val repository: GoodsIssueRepository by lazy { GoodsIssueRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getGoodsIssues(
        filter: String,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getGoodsIssuesList(
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

    override suspend fun getMoreGoodsIssues(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response =
            repository.getGoodsIssuesList(
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

    override suspend fun getGoodsIssue(docEntry: Long): Document? {
        val response = repository.getGoodsIssue(docEntry)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }



    override suspend fun insertGoodsIssue(GoodsIssue: DocumentForPost): Document? {
        val response = repository.insertGoodsIssue(GoodsIssue)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}