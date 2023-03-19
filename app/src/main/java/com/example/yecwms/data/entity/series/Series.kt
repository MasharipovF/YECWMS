package com.example.yecwms.data.entity.series


import com.google.gson.annotations.SerializedName

data class SeriesVal(
    @SerializedName("value")
    var value: List<Series>,
    @SerializedName("odata.nextLink")
    var nextlink: String
)

data class Series(
    @SerializedName("Document")
    val document: String? = null,
    @SerializedName("DocumentSubType")
    val documentSubType: String? = null,
    @SerializedName("Series")
    val series: Int? = null,
    @SerializedName("Name")
    val name: String? = null
)