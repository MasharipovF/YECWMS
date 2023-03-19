package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.userdefaults.UserDefaultsResponseVal
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UsersService {

    companion object {
        fun get(): UsersService = ServiceBuilder.createService(UsersService::class.java)
    }


    @GET("sml.svc/USER_DEFAULTS")
    suspend fun getUserDefaults(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$filter") userCode: String,
    ): Response<UserDefaultsResponseVal>

}