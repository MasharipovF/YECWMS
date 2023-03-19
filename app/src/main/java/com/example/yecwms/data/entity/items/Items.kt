package com.example.yecwms.data.entity.items

import android.graphics.Bitmap
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class ItemsVal(
    @SerializedName("value")
    @Expose
    var items: List<Items>
)

data class Items(
    @SerializedName("ItemCode")
    @Expose
    var ItemCode: String? = null,

    @SerializedName("ItemName")
    @Expose
    var ItemName: String? = null,

    @SerializedName("Currency")
    var Currency: String? = null,

    @SerializedName("ItemType")
    @Expose
    var ItemType: String? = "itItems",

    @SerializedName("Type")
    @Expose
    var Type: Int = 0,

    @SerializedName("SupplierCode")
    @Expose
    var SupplierCode: String? = null,

    @SerializedName("SupplierName")
    @Expose
    var SupplierName: String? = null,

    @SerializedName("ForeignName")
    @Expose
    var ForeignName: String? = null,

    @SerializedName("BarCode")
    @Expose
    var BarCode: String? = null,

    @SerializedName("ManageBatchNumbers")
    @Expose
    var ManageBatchNumbers: String? = null,

    @SerializedName("BatchNumber")
    @Expose
    var BatchNumber: String? = null,

    @SerializedName("QuantityOnStockByBatch")
    @Expose
    var QuantityOnStockByBatch: Double = 0.0,

    @SerializedName("U_brutto")
    @Expose
    var Brutto: Double = 0.0,

    @SerializedName("U_tara")
    @Expose
    var Tare: Double = 0.0,

    @SerializedName("U_bobin")
    @Expose
    var Bobbin: Double = 0.0,

    @SerializedName("U_asft")
    @Expose
    var Quality: String? = null,

    @SerializedName("U_boyi")
    @Expose
    var Height: String? = null,

    @SerializedName("U_eni")
    @Expose
    var Width: String? = null,

    @SerializedName("ManageSerialNumbers")
    @Expose
    var ManageSerialNumbers: String? = null,

    @SerializedName("ItemsGroupCode")
    @Expose
    var ItemsGroupCode: Int? = -1,

    var ItemsGroupName: String? = null,

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

    @SerializedName("QuantityOnStockByCurrentWhs")
    @Expose
    var OnHandCurrentWhs: Double = 0.0,

    @SerializedName("QuantityOnStockByCurrentBin")
    @Expose
    var OnHandCurrentBin: Double = 0.0,

    @SerializedName("CommittedByCurrentWhs")
    @Expose
    var CommittedCurrentWhs: Double = 0.0,

    @SerializedName("PlannedQty")
    @Expose
    var PlannedQty: Double = 0.0,

    @SerializedName("IssuedQty")
    @Expose
    var IssuedQty: Double = 0.0,

    @SerializedName("ReturnedQty")
    @Expose
    var ReturnedQty: Double = 0.0,

    @SerializedName("NecessaryQty")
    @Expose
    var NecessaryQty: Double = 0.0,

    @SerializedName("BinLocationsActivated")
    @Expose
    var BinLocationsActivated: Boolean = false,

    @SerializedName("BinCode")
    @Expose
    var BinCode: String? = null,

    @SerializedName("BinAbsEntry")
    @Expose
    var BinAbsEntry: Int? = null,

    @SerializedName("DiscountApplied")
    @Expose
    var DiscountApplied: Double = 0.0,

    @SerializedName("DiscountType")
    @Expose
    var DiscountType: String? = "N",

    @SerializedName("DiscountLineNum")
    @Expose
    var DiscountLineNum: Int = 0,

    @SerializedName("Price")
    @Expose
    var Price: Double = 0.0,

    var DiscountedPrice: Double = 0.0,

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
    var ItemWarehouseInfoCollection: List<ItemWarehouseInfo> = listOf(),

    @SerializedName("ItemPrices")
    @Expose
    var ItemPrices: List<ItemPrices> = listOf()
)

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
    var InStock: Double = 0.0,

    @SerializedName("Committed")
    @Expose
    var Committed: Double = 0.0
)

data class ItemPrices(
    @SerializedName("AdditionalCurrency1")
    var additionalCurrency1: Any? = null,
    @SerializedName("AdditionalCurrency2")
    var additionalCurrency2: Any? = null,
    @SerializedName("AdditionalPrice1")
    var additionalPrice1: Double = 0.0,
    @SerializedName("AdditionalPrice2")
    var additionalPrice2: Double = 0.0,
    @SerializedName("BasePriceList")
    var basePriceList: Int = 0,
    @SerializedName("Currency")
    var currency: Any? = null,
    @SerializedName("Factor")
    var factor: Double = 0.0,
    @SerializedName("Price")
    var price: Double = 0.0,
    @SerializedName("PriceList")
    var priceList: Int = 0,
    @SerializedName("UoMPrices")
    var uoMPrices: List<Any> = listOf()
)