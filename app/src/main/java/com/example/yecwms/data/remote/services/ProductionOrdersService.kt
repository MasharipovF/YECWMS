package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.productionorders.ProductionOrders
import com.example.yecwms.data.entity.productionorders.ProductionOrdersVal
import retrofit2.Response
import retrofit2.http.*

interface ProductionOrdersService {

    companion object {
        fun get(): ProductionOrdersService =
            ServiceBuilder.createService(ProductionOrdersService::class.java)
    }

    @POST("ProductionOrders")
    suspend fun addProductionOrder(
        @Body body: ProductionOrders
    ): Response<ProductionOrders>



    @GET("ProductionOrders")
    suspend fun getProductionOrdersList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "AbsoluteEntry,DocumentNumber,ItemNo,ProductionOrderStatus,StartDate,DueDate,PostingDate,Warehouse,ProductDescription,CustomerCode",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "AbsoluteEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<ProductionOrdersVal>

    @GET("sml.svc/PROD_ORDERS_EVENTS")
    suspend fun getEventsList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "AbsoluteEntry,DocumentNumber,ItemNo,ProductionOrderStatus,StartDate,DueDate,PostingDate,Warehouse,ProductDescription,CustomerCode,CustomerName",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "AbsoluteEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<ProductionOrdersVal>

    
    @POST("ProductionOrders({docEntry})/Cancel")
    suspend fun cancelProductionOrder(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("ProductionOrders({docEntry})")
    suspend fun getProductionOrder(
        @Path("docEntry") docEntry: Long
    ): Response<ProductionOrders>

    @PATCH("ProductionOrders({docEntry})")
    suspend fun updateProductionOrder(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: ProductionOrders
    ): Response<Any>
}