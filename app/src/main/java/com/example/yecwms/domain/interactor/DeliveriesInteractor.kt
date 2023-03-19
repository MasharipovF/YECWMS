package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.repository.DeliveriesRepository
import com.example.yecwms.data.repository.DeliveriesRepositoryImpl
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse

interface DeliveriesInteractor {

    suspend fun getDeliveries(
        filter: String = "",
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getMoreDeliveries(
        filter: String = "",
        skipValue: Int,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getDelivery(docEntry: Long): Document?
    suspend fun insertDeliveryDraft(Delivery: DocumentForPost): Document?
    suspend fun insertDelivery(delivery: DocumentForPost): Document?
    val errorMessage: String?
}

class DeliveriesInteractorImpl : DeliveriesInteractor {

    private val repository: DeliveriesRepository by lazy { DeliveriesRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getDeliveries(
        filter: String,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getDeliveriesList(
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

    override suspend fun getMoreDeliveries(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response =
            repository.getDeliveriesList(
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

    override suspend fun getDelivery(docEntry: Long): Document? {
        val response = repository.getDelivery(docEntry)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }


    }

    override suspend fun insertDeliveryDraft(Delivery: DocumentForPost): Document? {
        val response = repository.insertDeliveryDraft(Delivery)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun insertDelivery(delivery: DocumentForPost): Document? {
        val response = repository.insertDelivery(delivery)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}