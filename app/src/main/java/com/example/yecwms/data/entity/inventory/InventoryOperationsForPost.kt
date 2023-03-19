package com.example.yecwms.data.entity.inventory


import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InventoryOperationsForPostVal(
    @SerializedName("value")
    @Expose
    var value: List<InventoryOperations>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class InventoryOperationsForPost(
    @SerializedName("DocEntry")
    var docEntry: Long? = null,
    @SerializedName("DocDate")
    var docDate: String? = null,
    @SerializedName("DueDate")
    var dueDate: String? = null,
    @SerializedName("Comments")
    var comments: Any? = null,
    @SerializedName("FromWarehouse")
    var fromWarehouse: String? = null,
    @SerializedName("ToWarehouse")
    var toWarehouse: String? = null,
    @SerializedName("UpdateDate")
    var updateDate: String? = null,
    @SerializedName("DocNum")
    var docNum: Int? = null,
    @SerializedName("DocumentStatus")
    var documentStatus: String? = null,
    @SerializedName("StockTransferLines")
    var inventoryOperationsLines: List<InventoryOperationsLinesForPost>? = null
)


data class InventoryOperationsLinesForPost(
    @SerializedName("LineNum")
    var lineNum: Int? = null,
    @SerializedName("DocEntry")
    var docEntry: Int? = null,
    @SerializedName("ItemCode")
    var itemCode: String? = null,
    @SerializedName("ItemDescription")
    var itemDescription: String? = null,
    @SerializedName("Quantity")
    var quantity: BigDecimal? = null,
    @SerializedName("WarehouseCode")
    var toWarehouse: String? = null,
    @SerializedName("FromWarehouseCode")
    var fromWarehouse: String? = null,
    @SerializedName("BaseType")
    var baseType: String? = null,
    @SerializedName("BaseLine")
    var baseLine: Any? = null,
    @SerializedName("BaseEntry")
    var baseEntry: Any? = null,
    @SerializedName("BatchNumbers")
    @Expose
    var BatchNumbers: List<BatchNumbersForPost>? = null,

   /* @SerializedName("U_Manufacturer")
    var U_Manufacturer: String? = null,

    @SerializedName("U_Lot")
    var U_Lot: String? = null,

    @SerializedName("U_ProdOrder")
    var U_ProdOrder: Long? = null,

    @SerializedName("PackageQuantity")
    var PackageQuantity: BigDecimal? = null,

    @SerializedName("U_FabricShape")
    var U_FabricShape: String? = null,

    @SerializedName("U_Pus")
    var U_Pus: String? = null,

    @SerializedName("U_Ayar")
    var U_Ayar: String? = null*/
)
