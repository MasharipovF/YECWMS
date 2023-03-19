package com.example.yecwms.domain.dto.error


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    @Expose
    val error: Error
)

data class Error(
    @SerializedName("code")
    @Expose
    val code: Int,
    @SerializedName("message")
    @Expose
    val message: Message
)

data class Message(
    @SerializedName("lang")
    @Expose
    val lang: String,
    @SerializedName("value")
    @Expose
    val value: String
)