package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.productionorders.ProductionOrders
import com.example.yecwms.data.entity.productionorders.ProductionOrdersVal
import com.example.yecwms.data.repository.ProductionOrdersRepository
import com.example.yecwms.data.repository.ProductionOrdersRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse

interface ProductionOrderInteractor {

    suspend fun getProductionOrders(filter: String = ""): List<ProductionOrders>?
    suspend fun getMoreProductionOrders(
        filter: String = "",
        skipValue: Int
    ): List<ProductionOrders>?

    suspend fun getEvents(
        filter: String = "",
        skipValue: Int=0
    ): List<ProductionOrders>?

    suspend fun getProductionOrder(docEntry: Long): ProductionOrders?
    suspend fun insertProductionOrder(ProductionOrder: ProductionOrders): ProductionOrders?
    suspend fun updateProductionOrder(docEntry: Long, ProductionOrder: ProductionOrders): Boolean
    var errorMessage: String?
}

class ProductionOrderInteractorImpl : ProductionOrderInteractor {

    private val repository: ProductionOrdersRepository by lazy { ProductionOrdersRepositoryImpl() }
    //private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null


    override suspend fun getProductionOrders(filter: String): List<ProductionOrders>? {
        val response = repository.getProductionOrderList(filter = filter)
        return if (response is ProductionOrdersVal) {
            var prodOrderList = response.value
            prodOrderList
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getMoreProductionOrders(
        filter: String,
        skipValue: Int
    ): List<ProductionOrders>? {
        val response = repository.getProductionOrderList(filter = filter, skipValue = skipValue)
        return if (response is ProductionOrdersVal) {
            var prodOrderList = response.value
            prodOrderList
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getEvents(
        filter: String,
        skipValue: Int
    ): List<ProductionOrders>? {
        val response = repository.getEventsList(filter = filter, skipValue = skipValue)
        return if (response is ProductionOrdersVal) {
            var prodOrderList = response.value
            prodOrderList
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


    override suspend fun getProductionOrder(docEntry: Long): ProductionOrders? {
        val response = repository.getProductionOrder(docEntry)
        return if (response is ProductionOrders) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun insertProductionOrder(ProductionOrder: ProductionOrders): ProductionOrders? {
        val response = repository.insertProductionOrder(ProductionOrder)
        return if (response is ProductionOrders) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun updateProductionOrder(
        docEntry: Long,
        ProductionOrder: ProductionOrders
    ): Boolean {
        val response = repository.updatetProductionOrder(docEntry, ProductionOrder)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }
}