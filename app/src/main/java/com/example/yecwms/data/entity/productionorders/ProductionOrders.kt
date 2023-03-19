package com.example.yecwms.data.entity.productionorders


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductionOrdersVal(
    @SerializedName("value")
    @Expose
    var value: List<ProductionOrders>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class ProductionOrders(
    @SerializedName("AbsoluteEntry")
    var docEntry: Int = -1,
    @SerializedName("ClosingDate")
    var closingDate: String = "",
    @SerializedName("CompletedQuantity")
    var completedQuantity: Double = 0.0,
    @SerializedName("CreationDate")
    var creationDate: String = "",
    @SerializedName("DocumentNumber")
    var docNum: Int = 0,
    @SerializedName("DueDate")
    var dueDate: String = "",
    @SerializedName("InventoryUOM")
    var inventoryUOM: String = "",
    @SerializedName("ItemNo")
    var itemCode: String = "",
    @SerializedName("PlannedQuantity")
    var plannedQuantity: Double = 0.0,
    @SerializedName("PostingDate")
    var postingDate: String? = null,
    @SerializedName("Priority")
    var priority: Int = 0,
    @SerializedName("ProductDescription")
    var itemName: String = "",
    @SerializedName("ProductionOrderStatus")
    var productionOrderStatus: String = "",
    @SerializedName("ProductionOrderType")
    var productionOrderType: String = "",
    @SerializedName("RejectedQuantity")
    var rejectedQuantity: Double = 0.0,
    @SerializedName("ReleaseDate")
    var releaseDate: String = "",
    @SerializedName("StartDate")
    var startDate: String = "",
    @SerializedName("UoMEntry")
    var uoMEntry: Int = 0,
    @SerializedName("UserSignature")
    var userSignature: Int = 0,
    @SerializedName("Warehouse")
    var warehouse: String = "",
    @SerializedName("CustomerCode")
    var customerCode: String?,
    @SerializedName("CustomerName")
    var customerName: String?,
    @SerializedName("ProductionOrderOriginEntry")
    var ProductionOrderOriginEntry: Long? = null,
    @SerializedName("ProductionOrderOriginNumber")
    var ProductionOrderOriginNumber: String = "",
    var cardCode: String? = null,
    var cardName: String? = null,
    var salesOrderDate: String? = null
)