package com.example.yecwms.data.entity.businesspartners


import com.google.gson.annotations.SerializedName

data class BusinessPartnerTotalDebtVal(
    @SerializedName("odata.metadata")
    val odataMetadata: String? = null,
    @SerializedName("value")
    val value: List<BusinessPartnerTotalDebt?>? = null
)

data class BusinessPartnerTotalDebt(
    @SerializedName("Debt")
    val Debt: Double = 0.0,
    @SerializedName("MultiplyBy")
    val MultiplyBy: Double = 0.0,
    @SerializedName("Shop")
    val Shop: String? = null,
)
