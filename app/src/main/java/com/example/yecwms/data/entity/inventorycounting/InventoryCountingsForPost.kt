package com.example.yecwms.data.entity.inventorycounting


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class InventoryCountingsForPostVal(
    @SerializedName("value")
    @Expose
    var value: List<InventoryCountingsForPost>,
    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)


data class InventoryCountingsForPost(
    @SerializedName("U_proceeded")
    val proceeded: String? = null,
    @SerializedName("INV_POSTING_ROWSCollection")
    val inventoryCountingLines: List<InventoryCountingLineForPost> = listOf(),
    @SerializedName("U_Warehouse")
    val warehouseCode: String,
    @SerializedName("U_DocDate")
    val docDate: String,
    @SerializedName("U_User")
    val userName: String,
)

data class InventoryCountingLineForPost(
    @SerializedName("U_Quantity")
    val countedQuantity: Int,
    @SerializedName("U_ItemCode")
    val itemCode: String? = null,
    @SerializedName("U_ItemDescription")
    val itemName: String? = null,
    @SerializedName("U_BatchNumber")
    val batchNumber: String? = null,
)