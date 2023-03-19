package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.data.entity.inventory.InventoryOperations
import com.example.yecwms.data.entity.inventory.InventoryOperationsForPost
import com.example.yecwms.data.entity.inventory.InventoryOperationsLines
import com.example.yecwms.data.entity.inventory.InventoryOperationsVal
import com.example.yecwms.data.entity.items.ItemsOnHandByWhsVal
import com.example.yecwms.data.repository.*
import com.example.yecwms.domain.dto.error.ErrorResponse

interface InventoryTransferRequestInteractor {

    suspend fun getInventoryTransferRequests(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String? = null
    ): List<InventoryOperations>?

    suspend fun getInventoryTransferRequestsCount(
        docStatus: String? = null,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String? = null
    ): Int?

    suspend fun getInventoryTransferRequest(docEntry: Long): InventoryOperations?
    suspend fun updateInventoryRequest(docEntry: Long, request: InventoryOperationsForPost): Boolean
    suspend fun insertInventoryTransferRequest(request: InventoryOperationsForPost): InventoryOperations?
    suspend fun closeInventoryTransferRequest(docEntry: Long): Boolean
    val errorMessage: String?
}

class InventoryTransferRequestInteractorImpl : InventoryTransferRequestInteractor {

    private val repository: InventoryTransferRequestRepository by lazy { InventoryTransferRequestRepositoryImpl() }
    private val itemsRepo: ItemsRepository by lazy { ItemsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getInventoryTransferRequests(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String?
    ): List<InventoryOperations>? {
        val response =
            repository.getInventoryTransferRequestsList(
                filter = filter,
                skipValue = skipValue,
                docStatus = docStatus,
                whsCode = whsCode,
                getMyRequests = getMyRequests,
                getProceeded = getProceeded
            )
        return if (response is InventoryOperationsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getInventoryTransferRequestsCount(
        docStatus: String?,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String?
    ): Int? {
        val response =
            repository.getInventoryTransferRequestsCount(
                docStatus = docStatus,
                whsCode = whsCode,
                getMyRequests = getMyRequests,
                getProceeded = getProceeded
            )
        return if (response is Int) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getInventoryTransferRequest(docEntry: Long): InventoryOperations? {
        val response = repository.getInventoryTransferRequest(docEntry)
        return if (response is InventoryOperations) {
            response.inventoryOperationsLines = mapRowItemsOnHandQuantity(response)
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

    private suspend fun mapRowItemsOnHandQuantity(source: InventoryOperations): List<InventoryOperationsLines> {
        val resultList = source.inventoryOperationsLines

        val listOfWarehouses: ArrayList<String> = arrayListOf()
        source.inventoryOperationsLines.forEach {
            var repeatedWarehouse = false

            listOfWarehouses.forEach { warehouse ->
                if (warehouse == it.fromWarehouse)
                    repeatedWarehouse = true
            }

            if (!repeatedWarehouse)
                listOfWarehouses.add(it.fromWarehouse!!)
        }


        listOfWarehouses.forEach { warehouse ->

            val itemsResponse = itemsRepo.getItemsListOnHandByWarehouse(whsCode = warehouse)

            if (itemsResponse is ItemsOnHandByWhsVal) {

                for (sourceItem in resultList) {
                    for (item in itemsResponse.value) {
                        if (item?.itemCode == sourceItem.itemCode && warehouse == sourceItem.fromWarehouse) {
                            sourceItem.OnHand = item!!.onHand?.toBigDecimal()
                            sourceItem.Committed = item.isCommitted?.toBigDecimal()
                            sourceItem.InitialQuantity = sourceItem.quantity!!

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


    override suspend fun updateInventoryRequest(
        docEntry: Long,
        request: InventoryOperationsForPost
    ): Boolean {
        val response = repository.updatetInventoryRequest(docEntry, request)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }

    override suspend fun insertInventoryTransferRequest(request: InventoryOperationsForPost): InventoryOperations? {
        val response = repository.insertInventoryTransferRequest(request)
        return if (response is InventoryOperations) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun closeInventoryTransferRequest(docEntry: Long): Boolean {
        val response = repository.closeInventoryTransferRequest(docEntry)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }
}