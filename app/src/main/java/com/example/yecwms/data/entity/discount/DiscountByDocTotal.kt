package com.example.yecwms.data.entity.discount


import com.google.gson.annotations.SerializedName

data class DiscountByDocTotalVal(
    @SerializedName("odata.metadata")
    val odataMetadata: String? = null,
    @SerializedName("value")
    val value: List<DiscountByDocTotal>? = null
) {
    data class DiscountByDocTotal(
        @SerializedName("Code")
        val code: Int? = null,
        @SerializedName("Name")
        val name: String? = null,
        @SerializedName("U_dateFrom")
        val uDateFrom: String? = null,
        @SerializedName("U_dateTo")
        val uDateTo: String? = null,
        @SerializedName("U_percent")
        val uPercent: Double? = 0.0,
        @SerializedName("U_sum")
        val uSum: Double? = 0.0
    )
}