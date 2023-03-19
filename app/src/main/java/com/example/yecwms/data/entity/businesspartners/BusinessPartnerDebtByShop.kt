package com.example.yecwms.data.entity.businesspartners


import com.google.gson.annotations.SerializedName

data class BusinessPartnerDebtByShopVal(
    @SerializedName("odata.metadata")
    val odataMetadata: String? = null,
    @SerializedName("value")
    val value: List<BusinessPartnerDebtByShop?>? = null
)

data class BusinessPartnerDebtByShop(
    @SerializedName("CardCode")
    val CardCode: String? = null,
    @SerializedName("Debt")
    val Debt: Double = 0.0,
    @SerializedName("MultiplyBy")
    val MultiplyBy: Double = 0.0,
    @SerializedName("Shop")
    val Shop: String? = null,
)
