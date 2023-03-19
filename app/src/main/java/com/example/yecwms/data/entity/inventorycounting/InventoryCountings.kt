package com.example.yecwms.data.entity.inventorycounting


import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class InventoryCountingsVal(
    @SerializedName("value")
    @Expose
    var value: List<InventoryCountings>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)


data class InventoryCountings(
    @SerializedName("BranchID")
    val branchID: Int? = null,
    @SerializedName("DocumentNumber")
    val docNum: String? = null,
    @SerializedName("DocumentEntry")
    val docEntry: Long? = null,
    @SerializedName("DocumentStatus")
    val docStatus: String? = null,
    @SerializedName("CountDate")
    val countDate: String? = null,
    @SerializedName("U_proceeded")
    val proceeded: String? = null,
    @SerializedName("InventoryCountingLines")
    val inventoryCountingLines: List<InventoryCountingLine> = listOf()
)

data class InventoryCountingLine(
    @SerializedName("BinEntry")
    var binEntry: Int? = null,
    @SerializedName("BinCode")
    var binCode: String? = null,
    @SerializedName("CountedQuantity")
    var countedQuantity: Double=0.0,
    var userCountedQuantity: Double = 0.0,
    var initialCountedQuantity: Double = 0.0,
    var barCode: String = "",
    @SerializedName("ItemCode")
    var itemCode: String? = null,
    @SerializedName("ItemDescription")
    var itemName: String? = null,
    var itemForeignName: String? = null,
    var itemImage: Bitmap? = null,
    @SerializedName("WarehouseCode")
    var warehouseCode: String? = null,
    var manageBatchNumber: String? = null,
    var isQuantityChanged: Boolean = false,
    @SerializedName("InventoryCountingBatchNumbers")
    var inventoryCountingBatchNumbers: ArrayList<BatchNumbers> = arrayListOf(),

) {

    data class BatchNumbers(
        @SerializedName("BatchNumber")
        var BatchNumber: String? = null,
        @SerializedName("Quantity")
        var Quantity: Int
    )

}