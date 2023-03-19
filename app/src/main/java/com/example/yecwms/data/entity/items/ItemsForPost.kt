package com.example.yecwms.data.entity.items

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

/*
data class ItemsVal(
    @SerializedName("value")
    @Expose
    var items: List<Items>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)*/

data class ItemsForPost(
    @SerializedName("ItemCode")
    @Expose
    var ItemCode: String? = null,

    @SerializedName("ItemName")
    @Expose
    var ItemName: String? = null,

    @SerializedName("ItemType")
    @Expose
    var ItemType: String? = "itItems",


    @SerializedName("BarCode")
    @Expose
    var BarCode: String? = null,

    @SerializedName("ItemsGroupCode")
    @Expose
    var ItemsGroupCode: Int? = -1,

    @SerializedName("SalesUnit")
    @Expose
    var SalesUnit: String? = null,

    @SerializedName("PurchaseUnit")
    @Expose
    var PurchaseUnit: String? = null,

    @SerializedName("InventoryUOM")
    @Expose
    var InventoryUOM: String? = null,

    @SerializedName("QuantityOnStock")
    @Expose
    var TotalOnHand: Double = 0.0,

    @SerializedName("Series")
    @Expose
    var Series: Int? = null,

    @SerializedName("Valid")
    @Expose
    var Valid: String? = "tYES",

    @SerializedName("Frozen")
    @Expose
    var Frozen: String? = "tNO",

    @SerializedName("UoMGroupEntry")
    @Expose
    var UoMGroupEntry: Int? = -1,

    var UoMGroupName: String? = null,

    @SerializedName("InventoryUoMEntry")
    @Expose
    var InventoryUoMEntry: Int? = -1,

    @SerializedName("DefaultSalesUoMEntry")
    @Expose
    var SalesUoMEntry: Int? = -1,

    @SerializedName("DefaultPurchasingUoMEntry")
    @Expose
    var PurchasingUoMEntry: Int? = -1,

    @SerializedName("ItemBarCodeCollection")
    @Expose
    var ItemBarCodeCollection: List<ItemBarCodes> = listOf(),

    @SerializedName("ItemWarehouseInfoCollection")
    @Expose
    var ItemWarehouseInfoCollection: List<ItemWarehouseInfo> = listOf()
)
/*
data class ItemBarCodes(
    @SerializedName("AbsEntry")
    @Expose
    var AbsEntry: String? = null,

    @SerializedName("UoMEntry")
    @Expose
    var UoMEntry: String? = null,

    @SerializedName("Barcode")
    @Expose
    var Barcode: String? = null,

    @SerializedName("FreeText")
    @Expose
    var FreeText: String? = null
)

data class ItemWarehouseInfo(
    @SerializedName("WarehouseCode")
    @Expose
    var WarehouseCode: String? = null,

    var WarehouseName: String? = null,

    @SerializedName("InStock")
    @Expose
    var InStock: Double = 0.0
)*/