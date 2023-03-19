package com.example.yecwms.data.entity.items


import com.google.gson.annotations.SerializedName

data class ItemsSQLQueryVal(
    @SerializedName("odata.metadata")
    val odataMetadata: String? = null,
    @SerializedName("odata.nextLink")
    val odataNextLink: String? = null,
    @SerializedName("SqlText")
    val sqlText: String? = null,
    @SerializedName("value")
    val value: List<ItemsSQLQuery?>? = listOf()
) {
    data class ItemsSQLQuery(
        @SerializedName("ItemCode")
        val itemCode: String? = null,
        @SerializedName("ItemName")
        val itemName: String? = null,
        @SerializedName("OnHand")
        val onHand: Double? = 0.0,
        @SerializedName("IsCommited")
        val isCommited: Double? = 0.0,
        @SerializedName("TotalOnHand")
        val totalOnHand: Double? = 0.0,
        @SerializedName("Price")
        val price: Double? = 0.0,
        @SerializedName("SalUnitMsr")
        val salesUnitMeasure: String? = null
    )
}