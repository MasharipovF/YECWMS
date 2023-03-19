package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.inventory.InventoryOperations
import com.example.yecwms.data.entity.inventory.InventoryOperationsForPost
import com.example.yecwms.data.entity.inventory.InventoryOperationsVal
import retrofit2.Response
import retrofit2.http.*

interface InventoryTransferRequestService {

    companion object {
        fun get(): InventoryTransferRequestService =
            ServiceBuilder.createService(InventoryTransferRequestService::class.java)
    }

    @POST("InventoryTransferRequests")
    suspend fun addInventoryTransferRequest(
        @Body body: InventoryOperationsForPost
    ): Response<InventoryOperations>

    @GET("InventoryTransferRequests")
    suspend fun getInventoryTransferRequestsList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocObjectCode,DocEntry,DocDate,DueDate,Comments,FromWarehouse,ToWarehouse,U_finalWhs, UpdateDate,DocNum,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<InventoryOperationsVal>

    @GET("InventoryTransferRequests/\$count")
    suspend fun getInventoryTransferRequestsCount(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$filter") filter: String = "",
    ): Response<Int>

    @PATCH("InventoryTransferRequests({docEntry})")
    suspend fun updateInventoryRequest(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: InventoryOperationsForPost
    ): Response<Any>

    @POST("InventoryTransferRequests({docEntry})/Close")
    suspend fun closeInventoryTransferRequest(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("InventoryTransferRequests({docEntry})")
    suspend fun getInventoryTransferRequest(
        @Path("docEntry") docEntry: Long
    ): Response<InventoryOperations>
}