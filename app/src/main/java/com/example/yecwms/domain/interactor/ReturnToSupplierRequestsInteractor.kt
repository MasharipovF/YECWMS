package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.entity.items.ItemsOnHandByWhsVal
import com.example.yecwms.data.repository.*
import com.example.yecwms.domain.dto.error.ErrorResponse

interface ReturnToSupplierRequestInteractor {

    suspend fun getReturnToSupplierRequests(
        filter: String = "",
        skipValue: Int=0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getReturnToSupplierRequest(docEntry: Long): Document?
    suspend fun insertReturnToSupplierRequest(ReturnToSupplierRequest: DocumentForPost): Document?
    suspend fun updateReturnToSupplierRequest(docEntry: Long, ReturnToSupplierRequest: DocumentForPost): Boolean
    suspend fun closeReturnToSupplierRequest(docEntry: Long): Boolean
    var errorMessage: String?
}

class ReturnToSupplierRequestInteractorImpl : ReturnToSupplierRequestInteractor {

    private val repository: ReturnToSupplierRequestsRepository by lazy { ReturnToSupplierRequestsRepositoryImpl() }
    private val itemsRepo: ItemsRepository by lazy { ItemsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getReturnToSupplierRequests(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getReturnToSupplierRequestList(
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


    override suspend fun getReturnToSupplierRequest(docEntry: Long): Document? {
        val response = repository.getReturnToSupplierRequest(docEntry)
        return if (response is Document) {
            Log.d("ReturnToSupplierRequest", response.toString())
            response.DocumentLines = mapRowItemsOnHandQuantity(response)
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

    private suspend fun mapRowItemsOnHandQuantity(source: Document): List<DocumentLines> {
        val resultList = source.DocumentLines

        val listOfWarehouses: ArrayList<String> = arrayListOf()
        source.DocumentLines.forEach {
            var repeatedWarehouse = false

            listOfWarehouses.forEach { warehouse ->
                if (warehouse == it.WarehouseCode)
                    repeatedWarehouse = true
            }

            if (!repeatedWarehouse)
                listOfWarehouses.add(it.WarehouseCode!!)
        }


        listOfWarehouses.forEach { warehouse ->

            val itemsResponse = itemsRepo.getItemsListOnHandByWarehouse(warehouse)

            if (itemsResponse is ItemsOnHandByWhsVal) {

                for (sourceItem in resultList) {
                    for (item in itemsResponse.value) {
                        if (item?.itemCode == sourceItem.ItemCode && warehouse == sourceItem.WarehouseCode) {
                            sourceItem.OnHand = item!!.onHand
                            sourceItem.Committed = item.isCommitted
                            sourceItem.InitialQuantity = sourceItem.Quantity!!
                            sourceItem.BinLocationsActivated = item.binLocationsActivated!!
                            sourceItem.BarCode = item.barCodes?:""
                            break
                        }
                    }
                }

            } else {
                errorMessage = (itemsResponse as ErrorResponse).error.message.value
            }
            Log.d("SALESORDER", "filterlist INSIDE  ${resultList.toString()}")
        }

        Log.d("SALESORDER", "filterlist OUTSIDE ${resultList.toString()}")
        return resultList
    }


    override suspend fun insertReturnToSupplierRequest(ReturnToSupplierRequest: DocumentForPost): Document? {
        val response = repository.insertReturnToSupplierRequest(ReturnToSupplierRequest)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun updateReturnToSupplierRequest(docEntry: Long, ReturnToSupplierRequest: DocumentForPost): Boolean {
        val response = repository.updateReturnToSupplierRequest(docEntry, ReturnToSupplierRequest)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }

    override suspend fun closeReturnToSupplierRequest(docEntry: Long): Boolean {
        val response = repository.closeReturnToSupplierRequest(docEntry)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }
}