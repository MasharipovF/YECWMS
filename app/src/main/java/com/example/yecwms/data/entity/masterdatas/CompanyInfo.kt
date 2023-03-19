package com.example.yecwms.data.entity.masterdatas


import com.example.yecwms.core.GeneralConsts
import com.google.gson.annotations.SerializedName

data class CompanyInfo(

    @SerializedName("LocalCurrency")
    val localCurrency: String,

    @SerializedName("DirectIndirectRate")
    val directIndirectRate: String,
    val isDirectRateCalculation: Boolean = directIndirectRate != GeneralConsts.T_NO,

    @SerializedName("SystemCurrency")
    val systemCurrency: String,

    @SerializedName("AccuracyofQuantities")
    val accuracyofQuantities: Int = 6,

    @SerializedName("MeasuringAccuracy")
    val measuringAccuracy: Int = 6,

    @SerializedName("PercentageAccuracy")
    val percentageAccuracy: Int = 6,

    @SerializedName("PriceAccuracy")
    val priceAccuracy: Int = 6,

    @SerializedName("QueryAccuracy")
    val queryAccuracy: Int = 6,

    @SerializedName("RateAccuracy")
    val rateAccuracy: Int = 6,

    @SerializedName("TotalsAccuracy")
    val totalsAccuracy: Int = 6
)