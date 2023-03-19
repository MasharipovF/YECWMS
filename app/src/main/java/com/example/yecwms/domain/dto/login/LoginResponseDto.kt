package com.example.yecwms.domain.dto.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("odata.metadata")
    @Expose
    var meta: String? = null,

    @SerializedName("SessionId")
    @Expose
    var SessionId: String? = null,

   @SerializedName("Version")
    @Expose
    var Version: String? = null,

    @SerializedName("SessionTimeout")
    @Expose
    var SessionTimeout: Int? = null
)