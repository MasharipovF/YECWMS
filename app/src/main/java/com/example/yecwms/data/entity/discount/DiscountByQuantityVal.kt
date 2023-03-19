package com.example.yecwms.data.entity.discount


import com.google.gson.annotations.SerializedName

data class DiscountByQuantityVal(
    @SerializedName("value")
    val value: List<DiscountByQuantity?>? = null
)
data class DiscountByQuantity(
    @SerializedName("Amount")
    val amount: Double = 0.0,
    @SerializedName("CardCode")
    val cardCode: String? = null,
    @SerializedName("Discount")
    val discount: Double = 0.0,
    @SerializedName("ItemCode")
    val itemCode: String? = null,
    @SerializedName("SPP1LNum")
    val sPP1LNum: Int? = null,
    @SerializedName("SPP2LNum")
    val sPP2LNum: Int? = null
)