package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.inventorycounting.*
import com.example.yecwms.data.repository.*
import com.example.yecwms.domain.dto.error.ErrorResponse

interface InventoryCountingInteractor {

    suspend fun getInventoryCountingsList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        whsCode: String,
    ): List<InventoryCountings>?


    suspend fun getInventoryCounting(docEntry: Long): InventoryCountings?
    suspend fun updateInventoryCounting(
        docEntry: Long,
        request: InventoryCountingsForPost
    ): Boolean

    suspend fun insertInventoryCounting(request: InventoryCountingsForPost): InventoryCountingsForPost?
    suspend fun checkIfDocumentIsInserted(mobileAppId: String): List<InventoryCountings>?
    //suspend fun closeInventoryCounting(docEntry: Long): Boolean
    //suspend fun cancelInventoryCounting(docEntry: Long): Boolean
    suspend fun getAllItemsToBasket(whsCode: String): List<InventoryCountingLine>?
    val errorMessage: String?
}

class InventoryCountingInteractorImpl : InventoryCountingInteractor {

    private val repository: InventoryCountingsRepository by lazy { InventoryCountingsRepositoryImpl() }
    private val itemsRepo: ItemsRepository by lazy { ItemsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getInventoryCountingsList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        whsCode: String,
    ): List<InventoryCountings>? {
        val response =
            repository.getInventoryCountingsList(
                filter = filter,
                skipValue = skipValue,
                docStatus = docStatus,
                whsCode = whsCode
            )
        return if (response is InventoryCountingsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getInventoryCounting(docEntry: Long): InventoryCountings? {
        val response = repository.getInventoryCounting(docEntry)
        return if (response is InventoryCountings) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun updateInventoryCounting(
        docEntry: Long,
        request: InventoryCountingsForPost
    ): Boolean {
        val response = repository.updateInventoryCounting(docEntry, request)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }

    override suspend fun insertInventoryCounting(request: InventoryCountingsForPost): InventoryCountingsForPost? {
        val response = repository.insertInventoryCounting(request)
        return if (response is InventoryCountingsForPost) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun checkIfDocumentIsInserted(mobileAppId: String): List<InventoryCountings>? {
        val response = repository.checkIfDocumentIsInserted(mobileAppId)
        return if (response is InventoryCountingsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

/*
    override suspend fun closeInventoryCounting(docEntry: Long): Boolean {
        val response = repository.closeInventoryCounting(docEntry)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }

    override suspend fun cancelInventoryCounting(docEntry: Long): Boolean {
        val response = repository.cancelInventoryCounting(docEntry)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }*/

    override suspend fun getAllItemsToBasket(whsCode: String): List<InventoryCountingLine>? {
        val response =
            repository.getAllItemsToBasket(
                whsCode = whsCode
            )

        return if (response is ItemsListForInvCountingVal) {
            val docLines: ArrayList<InventoryCountingLine> = arrayListOf()
            response.value.forEach {
                val docLine = InventoryCountingLine()
                docLine.itemCode = it.itemCode
                docLine.itemName = it.itemDescription
                docLine.warehouseCode = it.warehouseCode
                docLine.binCode = it.binCode
                docLine.binEntry = it.binEntry
                docLine.countedQuantity = 0.0
                docLine.userCountedQuantity = 0.0

                docLines.add(docLine)
            }
            docLines

        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }
}