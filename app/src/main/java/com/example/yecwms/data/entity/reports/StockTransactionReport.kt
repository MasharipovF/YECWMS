package com.example.yecwms.data.entity.reports


import com.google.gson.annotations.SerializedName

data class StockTransactionReportVal(
    @SerializedName("value")
    val value: List<StockTransactionReport>? = null
)

data class StockTransactionReport(
    @SerializedName("DocDate")
    val docDate: String? = null,
    @SerializedName("id__")
    val id: Int? = null,
    @SerializedName("ItemCode")
    val itemCode: String? = null,
    @SerializedName("ItemName")
    val itemName: String? = null,
    @SerializedName("ObjectType")
    val objectType: String? = null,
    @SerializedName("Quantity")
    val quantity: Double? = 0.0,
    @SerializedName("Warehouse")
    val warehouse: String? = null,
    @SerializedName("InventoryUOM")
    val inventoryUom: String? = null,
    @SerializedName("InventoryUOM2")
    val inventoryUom2: String? = null,
    @SerializedName("UserCode")
    val userCode: String? = null,
    @SerializedName("QuantityInPackage")
    val quantityInPackage: Double? = 0.0,
    @SerializedName("TotalQuantity")
    val totalQuantity: Double? = 0.0
)