package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.documents.DocumentsVal
import com.example.yecwms.data.entity.items.ItemsOnHandByWhsVal
import com.example.yecwms.data.repository.*
import com.example.yecwms.domain.dto.error.ErrorResponse

interface PurchaseInvoicesInteractor {

    suspend fun getPurchaseInvoices(
        filter: String = "",
        docStatus: String? = null,
        isReserveInvoice: Boolean? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getMorePurchaseInvoices(
        filter: String = "",
        skipValue: Int,
        docStatus: String? = null,
        isReserveInvoice: Boolean? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getOpenPurchaseInvoicesSML(
        filter: String = "",
        skipValue: Int = 0,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<Document>?

    suspend fun getPurchaseInvoice(docEntry: Long): Document?
    suspend fun insertPurchaseInvoice(PurchaseInvoice: DocumentForPost): Document?
    val errorMessage: String?
}

class PurchasesInvoicesInteractorImpl : PurchaseInvoicesInteractor {

    private val repository: PurchaseInvoicesRepository by lazy { PurchaseInvoicesRepositoryImpl() }
    private val itemsRepo: ItemsRepository by lazy { ItemsRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getPurchaseInvoices(
        filter: String,
        docStatus: String?,
        isReserveInvoice: Boolean?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response = repository.getPurchaseInvoicesList(
            filter = filter,
            docStatus = docStatus,
            isReserveInvoice = isReserveInvoice,
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

    override suspend fun getMorePurchaseInvoices(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        isReserveInvoice: Boolean?,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response =
            repository.getPurchaseInvoicesList(
                filter = filter,
                skipValue = skipValue,
                docStatus = docStatus,
                isReserveInvoice = isReserveInvoice,
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

    override suspend fun getOpenPurchaseInvoicesSML(
        filter: String,
        skipValue: Int,
        dateFrom: String?,
        dateTo: String?
    ): List<Document>? {
        val response =
            repository.getOpenPurchaseInvoicesListSML(
                filter = filter,
                skipValue = skipValue,
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

    override suspend fun getPurchaseInvoice(docEntry: Long): Document? {
        val response = repository.getPurchaseInvoice(docEntry)
        return if (response is Document) {
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


    override suspend fun insertPurchaseInvoice(PurchaseInvoice: DocumentForPost): Document? {
        val response = repository.insertPurchaseInvoice(PurchaseInvoice)
        return if (response is Document) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}