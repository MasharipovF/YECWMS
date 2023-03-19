package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.repository.ReturnsRepository
import com.example.yecwms.data.repository.ReturnsRepositoryImpl
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse

interface ReturnsInteractor {

    suspend fun getReturns(
        filter: String = "",
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getMoreReturns(
        filter: String = "",
        skipValue: Int,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getReturn(docEntry: Long): Document?
    suspend fun insertReturnDraft(Return: DocumentForPost): Document?
    val errorMessage: String?
}

class ReturnsInteractorImpl : ReturnsInteractor {

    private val repository: ReturnsRepository by lazy { ReturnsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getReturns(
        filter: String,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getReturnsList(
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

    override suspend fun getMoreReturns(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response =
            repository.getReturnsList(
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

    override suspend fun getReturn(docEntry: Long): Document? {
        val response = repository.getReturn(docEntry)
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

    override suspend fun insertReturnDraft(Return: DocumentForPost): Document? {
        val response = repository.insertReturnDraft(Return)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}