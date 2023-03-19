package com.example.yecwms.data.entity.items


import com.google.gson.annotations.SerializedName

data class ItemsOnHandByWhsVal(
    @SerializedName("value")
    val value: List<ItemsOnHandByWhs>
)

data class ItemsOnHandByWhs(
    @SerializedName("BinLocationsActivated")
    val binLocationsActivated: Boolean? = false,
    @SerializedName("IsCommited")
    val isCommitted: Double? = 0.0,
    @SerializedName("ItemCode")
    val itemCode: String? = null,
    @SerializedName("ItemName")
    val itemName: String? = null,
    @SerializedName("OnHand")
    val onHand: Double? = 0.0,
    @SerializedName("WhsCode")
    val whsCode: String? = null,
    @SerializedName("WhsName")
    val whsName: String? = null,
    @SerializedName("BarCode")
    val barCodes: String? = "",

    @SerializedName("ManageBatchNumbers")
    var ManageBatchNumbers: String? = null,
    @SerializedName("ForeignName")
    var ForeignName: String? = null
)