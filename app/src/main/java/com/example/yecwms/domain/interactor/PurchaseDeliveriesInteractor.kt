package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.repository.PurchaseDeliveriesRepository
import com.example.yecwms.data.repository.PurchaseDeliveriesRepositoryImpl
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse

interface PurchaseDeliveriesInteractor {

    suspend fun getPurchaseDeliveries(
        filter: String = "",
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getMorePurchaseDeliveries(
        filter: String = "",
        skipValue: Int,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getPurchaseDelivery(docEntry: Long): Document?
    suspend fun insertPurchaseDeliveryDraft(PurchaseDelivery: DocumentForPost): Document?
    suspend fun insertPurchaseDelivery(PurchaseDelivery: DocumentForPost): Document?

    val errorMessage: String?
}

class PurchaseDeliveriesInteractorImpl : PurchaseDeliveriesInteractor {

    private val repository: PurchaseDeliveriesRepository by lazy { PurchaseDeliveriesRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getPurchaseDeliveries(
        filter: String,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getPurchaseDeliveriesList(
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

    override suspend fun getMorePurchaseDeliveries(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response =
            repository.getPurchaseDeliveriesList(
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

    override suspend fun getPurchaseDelivery(docEntry: Long): Document? {
        val response = repository.getPurchaseDelivery(docEntry)
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

    override suspend fun insertPurchaseDeliveryDraft(PurchaseDelivery: DocumentForPost): Document? {
        val response = repository.insertPurchaseDeliveryDraft(PurchaseDelivery)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun insertPurchaseDelivery(PurchaseDelivery: DocumentForPost): Document? {
        val response = repository.insertPurchaseDelivery(PurchaseDelivery)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}