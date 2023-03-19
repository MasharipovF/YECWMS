package com.example.yecwms.data.remote.services



import com.example.yecwms.data.entity.inventory.InventoryOperations
import com.example.yecwms.data.entity.inventory.InventoryOperationsForPost
import com.example.yecwms.data.entity.inventory.InventoryOperationsVal
import com.example.yecwms.core.ServiceBuilder
import retrofit2.Response
import retrofit2.http.*

interface InventoryTransferService {

    companion object {
        fun get(): InventoryTransferService =
            ServiceBuilder.createService(InventoryTransferService::class.java)
    }

    @POST("StockTransfers")
    suspend fun addInventoryTransfer(
        @Body body: InventoryOperationsForPost
    ): Response<InventoryOperations>

    @GET("StockTransfers")
    suspend fun getInventoryTransfersList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocObjectCode,DocEntry,DocDate,DueDate,Comments,FromWarehouse,ToWarehouse,UpdateDate,DocNum,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<InventoryOperationsVal>


    @GET("StockTransfers")
    suspend fun getInventoryTransfersListWithOrder(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocObjectCode,DocEntry,DocDate,DueDate,Comments,FromWarehouse,ToWarehouse,UpdateDate,DocNum,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String? = "DocEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<InventoryOperationsVal>

    @GET("StockTransfers({docEntry})")
    suspend fun getInventoryTransfer(
        @Path("docEntry") docEntry: Long
    ): Response<InventoryOperations>

    @GET("StockTransferDrafts({docEntry})")
    suspend fun getInventoryTransferDraft(
        @Path("docEntry") docEntry: Long
    ): Response<InventoryOperations>
}