package com.example.yecwms.domain.dto.error


import com.google.gson.annotations.SerializedName

data class ErrorResponseV2(
    @SerializedName("error")
    val error: ErrorV2? = null
)

data class ErrorV2(
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("message")
    val message: String? = null
)