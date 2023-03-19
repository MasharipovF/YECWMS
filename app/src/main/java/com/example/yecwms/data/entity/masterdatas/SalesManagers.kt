package com.example.yecwms.data.entity.masterdatas


import com.google.gson.annotations.SerializedName

data class SalesManagersVal(
    @SerializedName("odata.nextLink")
    val nextLink: String? = null,
    @SerializedName("value")
    val value: List<SalesManagers>? = null
)

data class SalesManagers(
    @SerializedName("SalesEmployeeCode")
    val salesEmployeeCode: Int? = null,
    @SerializedName("SalesEmployeeName")
    val salesEmployeeName: String? = null,
    @SerializedName("Mobile")
    val salesEmployeeMobile: String? = null,

    )