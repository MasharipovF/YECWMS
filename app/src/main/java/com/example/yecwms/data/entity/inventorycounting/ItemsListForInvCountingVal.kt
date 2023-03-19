package com.example.yecwms.data.entity.inventorycounting


import com.google.gson.annotations.SerializedName

data class ItemsListForInvCountingVal(
    @SerializedName("@odata.context")
    val odataContext: String,
    @SerializedName("value")
    val value: List<ItemsListForInvCounting>
)

data class ItemsListForInvCounting(
    @SerializedName("BinCode")
    val binCode: String?,
    @SerializedName("BinEntry")
    val binEntry: Int?,
    @SerializedName("ItemCode")
    val itemCode: String,
    @SerializedName("ItemDescription")
    val itemDescription: String,
    @SerializedName("WarehouseCode")
    val warehouseCode: String
)
