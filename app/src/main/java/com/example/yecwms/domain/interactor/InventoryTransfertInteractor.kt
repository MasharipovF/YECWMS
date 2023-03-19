package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.data.entity.inventory.InventoryOperations
import com.example.yecwms.data.entity.inventory.InventoryOperationsForPost
import com.example.yecwms.data.entity.inventory.InventoryOperationsLines
import com.example.yecwms.data.entity.inventory.InventoryOperationsVal
import com.example.yecwms.data.entity.items.ItemsOnHandByWhsVal
import com.example.yecwms.data.repository.*
import com.example.yecwms.domain.dto.error.ErrorResponse

interface InventoryTransferInteractor {

    suspend fun getInventoryTransfersList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        whsCode: String,
        getMyRequests: Boolean
    ): List<InventoryOperations>?


    suspend fun getInventoryTransfer(docEntry: Long): InventoryOperations?
    suspend fun insertInventoryTransfer(request: InventoryOperationsForPost): InventoryOperations?
    val errorMessage: String?
}

class InventoryTransferInteractorImpl : InventoryTransferInteractor {

    private val repository: InventoryTransferRepository by lazy { InventoryTransferRepositoryImpl() }
    private val itemsRepo: ItemsRepository by lazy { ItemsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getInventoryTransfersList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        whsCode: String,
        getMyRequests: Boolean
    ): List<InventoryOperations>? {
        val response =
            repository.getInventoryTransfersList(
                filter = filter,
                skipValue = skipValue,
                docStatus = docStatus,
                whsCode = whsCode,
                getMyRequests = getMyRequests
            )
        return if (response is InventoryOperationsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


    override suspend fun getInventoryTransfer(docEntry: Long): InventoryOperations? {
        val response = repository.getInventoryTransfer(docEntry)
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

            val itemsResponse = itemsRepo.getItemsListOnHandByWarehouse(warehouse)

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


    /*private suspend fun mapRowItemsOnHandQuantity(source: InventoryOperations): List<InventoryOperationsLines> {
        val resultList = arrayListOf<InventoryOperationsLines>()

        val filterList: ArrayList<String> = arrayListOf()
        source.inventoryOperationsLines.forEach {
            filterList.add(it.itemCode!!)
        }

        Log.d("SALESORDER", "filterlist ${filterList.toString()}")


        val itemsResponse = itemsRepo.getAllItemsByWhss(source.fromWarehouse!!)
        Log.d("SALESORDER", "response ${itemsResponse.toString()}")

        if (itemsResponse is ItemsVal) {

            for (sourceItem in source.inventoryOperationsLines) {
                for (item in itemsResponse.items) {
                    if (item.ItemCode == sourceItem.itemCode) {
                        sourceItem.OnHand = item.OnHandCurrentWhs
                        sourceItem.Committed = item.CommittedCurrentWhs
                        sourceItem.InitialQuantity = sourceItem.quantity!!
                        sourceItem.BarCode = item.BarCode ?: ""
                        break
                    }
                }
                resultList.add(sourceItem)
            }

        } else {
            errorMessage = (itemsResponse as ErrorResponse).error.message.value
        }
        Log.d("SALESORDER", "filterlist ${resultList.toString()}")

        return resultList
    }*/


    override suspend fun insertInventoryTransfer(request: InventoryOperationsForPost): InventoryOperations? {
        val response = repository.insertInventoryTransfer(request)
        return if (response is InventoryOperations) {
            response
        } else {

            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

}