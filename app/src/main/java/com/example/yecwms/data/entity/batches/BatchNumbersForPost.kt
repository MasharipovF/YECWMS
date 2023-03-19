package com.example.yecwms.data.entity.batches

import com.google.gson.annotations.SerializedName

data class BatchNumbersForPost(
    @SerializedName("BatchNumber")
    var BatchNumber: String? = null,

    @SerializedName("Quantity")
    var Quantity: Double,

    @SerializedName("ManufacturerSerialNumber")
    var ManufacturerSerialNumber: String? = null,

    @SerializedName("InternalSerialNumber")
    var InternalSerialNumber: String? = null
)
