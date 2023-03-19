package com.example.yecwms.domain.dto.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("CompanyDB")
    @Expose
    var CompanyDB: String? = null,

    @SerializedName("Password")
    @Expose
    var Password: String? = null,

    @SerializedName("UserName")
    @Expose
    var UserName: String? = null,

    @SerializedName("Language")
    @Expose
    var Language: Int? = 24

)