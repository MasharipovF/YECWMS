package com.example.yecwms.data.remote.services


import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.inventorycounting.InventoryCountings
import com.example.yecwms.data.entity.inventorycounting.InventoryCountingsForPost
import com.example.yecwms.data.entity.inventorycounting.InventoryCountingsVal
import com.example.yecwms.data.entity.inventorycounting.ItemsListForInvCountingVal
import retrofit2.Response
import retrofit2.http.*

interface InventoryCountingsService {

    companion object {
        fun get(): InventoryCountingsService =
            ServiceBuilder.createService(InventoryCountingsService::class.java)
    }
    
    
    @POST("INVENTARIZATSIYA")
    suspend fun insertInventoryCounting(
        @Body body: InventoryCountingsForPost
    ): Response<InventoryCountingsForPost>

    @GET("InventoryCountings")
    suspend fun getInventoryCountingsList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocumentEntry,DocumentNumber,CountDate,CountTime,DocumentStatus",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "DocumentEntry desc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<InventoryCountingsVal>

    @PATCH("InventoryCountings({docEntry})")
    suspend fun updateInventoryCounting(
        @Header("B1S-ReplaceCollectionsOnPatch") replaceCollection: Boolean = true,
        @Path("docEntry") docEntry: Long,
        @Body body: InventoryCountingsForPost
    ): Response<Any>

    @POST("InventoryCountings({docEntry})/Close")
    suspend fun closeInventoryCounting(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @POST("InventoryCountings({docEntry})/Cancel")
    suspend fun cancelInventoryCounting(
        @Path("docEntry") docEntry: Long
    ): Response<Any>

    @GET("InventoryCountings({docEntry})")
    suspend fun getInventoryCounting(
        @Path("docEntry") docEntry: Long
    ): Response<InventoryCountings>

    @GET("InventoryCountings")
    suspend fun checkIfDocumentIsInserted(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = "DocumentEntry, DocumentStatus",
        @Query("\$filter") filter: String = ""
    ): Response<InventoryCountingsVal>

    @GET("sml.svc/ITEMSONHANDFORINVENTORYCOUNTING")
    suspend fun getItemsForBasketList(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Header("Prefer") maxPage: String = "odata.maxpagesize=500000",
        @Query("\$select") fields: String = "ItemCode,ItemDescription,BinCode,WarehouseCode,BinEntry",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "ItemDescription asc",
    ): Response<ItemsListForInvCountingVal>


    
}