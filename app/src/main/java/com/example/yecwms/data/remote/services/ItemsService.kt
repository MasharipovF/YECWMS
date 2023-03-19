package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.CrossJoin
import com.example.yecwms.data.entity.discount.DiscountByQuantityVal
import com.example.yecwms.data.entity.items.*
import com.example.yecwms.data.entity.masterdatas.BarCodesVal
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ItemsService {

    companion object {

        fun get(): ItemsService = ServiceBuilder.createService(ItemsService::class.java)
    }


    @GET("Items")
    suspend fun getFilteredItems(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String = "ItemCode,ItemName,ForeignName,QuantityOnStock,SalesUnit,InventoryUOM,ItemWarehouseInfoCollection",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "ItemName asc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<ItemsVal>

    @GET("Items")
    suspend fun getFilteredItemsWithPrices(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String = "ItemCode,ItemName,ForeignName,QuantityOnStock,SalesUnit,InventoryUOM,ItemWarehouseInfoCollection,ItemPrices",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "ItemName asc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<ItemsVal>

    @POST("QueryService_PostQuery")
    suspend fun getFilteredItemsCrossJoin(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=10000",
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Body body: CrossJoin
    ): Response<ItemsCrossJoinVal>

    @GET("sml.svc/ITEMS_LISTParameters(P_CardCode='{cardcode}', P_Date='{date}', P_PriceList={pricelist}, P_WhsCode='{whscode}')/ITEMS_LIST")
    suspend fun getFilteredItemsViaSML(
        @Header("Prefer") maxPage: String? = null,
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Path("cardcode") cardcode: String,
        @Path("date") date: String,
        @Path("pricelist") pricelist: Int,
        @Path("whscode") whscode: String,
        @Query("\$filter") filter: String = "",
        @Query("\$skip") skipValue: Int = 0
    ): Response<ItemsVal>


    @GET("sml.svc/ITEMS_DISCOUNT_QUANTITYParameters(P_CardCode='{cardcode}',P_ItemCode='{itemcode}', P_LineNum={linenum})/ITEMS_DISCOUNT_QUANTITY")
    suspend fun getItemWithDiscountByQuantity(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Header("Prefer") maxPage: String = "odata.maxpagesize=10000",
        @Path("cardcode") cardcode: String = "*1",
        @Path("itemcode") itemcode: String,
        @Path("linenum") linenum: Int = 0,
        @Query("\$orderby") orderby: String = "Amount asc"
    ): Response<DiscountByQuantityVal>


    @GET("Items('{itemcode}')")
    suspend fun getItemInfo(
        @Path("itemcode") itemcode: String,
        @Query("\$select") fields: String = "ItemCode,ItemName,ForeignName,ItemsGroupCode,SalesUnit,PurchaseUnit,InventoryUOM,QuantityOnStock,Series,Valid,Frozen,UoMGroupEntry,InventoryUoMEntry,DefaultSalesUoMEntry,DefaultPurchasingUoMEntry,ItemWarehouseInfoCollection,ItemPrices"
    ): Response<Items>

    @GET("BarCodes")
    suspend fun checkIfPhoneBarCodeExists(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Header("Prefer") maxPage: String = "odata.maxpagesize=1",
        @Query("\$select") fields: String = "ItemNo,Barcode",
        @Query("\$filter") filter: String = ""
    ): Response<BarCodesVal>

    @GET("ItemImages('{itemcode}')/\$value")
    suspend fun getItemImage(
        @Path("itemcode") itemcode: String,
    ): Response<ResponseBody>

    @POST("Items")
    suspend fun addNewItem(
        @Body body: ItemsForPost
    ): Response<Items>


    /*
    {
    "SqlCode": "itemsOnHandByWhs",
    "SqlName": "Items OnHand quantity by Warehouse",
    "SqlText": "SELECT T0.\"ItemCode\", T0.\"OnHand\" FROM OITW T0  INNER JOIN OITM T1 ON T0.\"ItemCode\" = T1.\"ItemCode\" WHERE T0.\"WhsCode\" =:whsCode AND T1.\"validFor\"='Y'"
    }

     */

    /*@GET("SQLQueries('itemsOnHandByWhs')/List")
    suspend fun getAllItemsOnHandByWhs(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=1000000",
        @Query("whsCode") whsCode1: String
    ): Response<ItemsSQLQueryVal>*/

    @GET("BarCodes")
    suspend fun getItemByBarCode(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$filter") filter: String = "",
        @Query("\$skip") skipValue: Int = 0
    ): Response<BarCodesVal>

    @GET("SQLQueries('itemsByWhsAndPrices')/List")
    suspend fun getAllItemsOnHandByWhsAndPrice(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=100",
        @Query("itemCode") itemCode: String,
        @Query("whsCode") whsCode: String,
        @Query("priceListCode") priceListCode: Int
    ): Response<ItemsSQLQueryVal>

    @GET("sml.svc/ITEMSONHANDBYWHS")
    suspend fun getItemOnHandByWarehouses(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Header("Prefer") maxPage: String = "odata.maxpagesize=1000000",
        @Query("\$select") fields: String = "ItemCode,ForeignName,OnHand,IsCommited,BinLocationsActivated,WhsCode,WhsName,BarCode, ManageBatchNumbers",
        @Query("\$filter") filter: String? = null,
        @Query("\$orderby") order: String = "WhsCode asc",
    ): Response<ItemsOnHandByWhsVal>


    @GET("sml.svc/ITEMSONHANDBYBINLOCATIONS")
    suspend fun getItemOnHandByBinLocations(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String? = null,
        @Query("\$filter") filter: String? = null,
        @Query("\$skip") skipValue: Int = 0,
        @Query("\$orderby") order: String = "OnHandQty desc, BinAbsEntry asc"
    ): Response<ItemsOnHandByBinLocationsVal>

}