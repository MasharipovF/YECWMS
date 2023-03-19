package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.Preferences
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.domain.dto.login.LoginResponseDto
import com.example.yecwms.util.retryIO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    companion object {
        fun get(): LoginService = ServiceBuilder.createLoginService()

        suspend fun reLogin(): Boolean {
            val loginService = ServiceBuilder.createLoginService()
            val response = retryIO {
                loginService.requestLogin(
                    LoginRequestDto(
                        Preferences.companyDB,
                        Preferences.userPassword,
                        Preferences.userName
                    )
                )
            }
            return if (response.isSuccessful) {
                Preferences.sessionID = response.body()?.SessionId
                true
            } else {
                false
            }
        }

    }

    @POST("Login")
    suspend fun requestLogin(@Body body: LoginRequestDto): Response<LoginResponseDto>

}