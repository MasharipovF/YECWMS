package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.entity.items.ItemsOnHandByWhsVal
import com.example.yecwms.data.repository.*
import com.example.yecwms.domain.dto.error.ErrorResponse

interface PurchaseOrdersInteractor {

    suspend fun getPurchaseOrders(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getPurchaseOrder(docEntry: Long): Document?
    suspend fun insertPurchaseOrder(PurchaseOrder: DocumentForPost): Document?
    suspend fun updatePurchaseOrder(docEntry: Long, PurchaseOrder: DocumentForPost): Boolean
    suspend fun closePurchaseOrder(docEntry: Long): Boolean
    var errorMessage: String?
}

class PurchaseOrdersInteractorImpl : PurchaseOrdersInteractor {

    private val repository: PurchaseOrdersRepository by lazy { PurchaseOrdersRepositoryImpl() }
    private val itemsRepo: ItemsRepository by lazy { ItemsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getPurchaseOrders(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getPurchaseOrderList(
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


    override suspend fun getPurchaseOrder(docEntry: Long): Document? {
        val response = repository.getPurchaseOrder(docEntry)
        return if (response is Document) {
            Log.d("PurchaseOrder", response.toString())
            response.DocumentLines = mapRowItemsOnHandQuantity(response)
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
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
                        if (item.itemCode == sourceItem.ItemCode && warehouse == sourceItem.WarehouseCode) {
                            sourceItem.OnHand = item.onHand
                            sourceItem.Committed = item.isCommitted
                            sourceItem.InitialQuantity = sourceItem.Quantity!!
                            sourceItem.BinLocationsActivated = item.binLocationsActivated!!
                            sourceItem.BarCode = item.barCodes ?: ""
                            sourceItem.ManageBatchNumbers = item.ManageBatchNumbers
                            /*TODOsourceItem.Forma = item.Forma
                            sourceItem.Forma2 = item.Forma2
                            sourceItem.Parametr= item.Parametr
                            sourceItem.Volume = item.Volume*/
                            break
                        }
                    }
                }

            } else {
                errorMessage = (itemsResponse as ErrorResponse).error.message.value
            }
            Log.d("PurchaseOrder", "filterlist INSIDE  ${resultList.toString()}")
        }

        Log.d("PurchaseOrder", "filterlist OUTSIDE ${resultList.toString()}")
        return resultList
    }


    override suspend fun insertPurchaseOrder(PurchaseOrder: DocumentForPost): Document? {
        val response = repository.insertPurchaseOrder(PurchaseOrder)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun updatePurchaseOrder(docEntry: Long, PurchaseOrder: DocumentForPost): Boolean {
        val response = repository.updatePurchaseOrder(docEntry, PurchaseOrder)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }

    override suspend fun closePurchaseOrder(docEntry: Long): Boolean {
        val response = repository.closePurchaseOrder(docEntry)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }
}