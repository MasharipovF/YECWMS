package com.example.yecwms.data.repository

import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.remote.services.DiscountsServices
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface DiscountsRepository {

    suspend fun getDiscountByDocTotal(): Any?
}

class DiscountsRepositoryImpl(
    private val discountsServices: DiscountsServices = DiscountsServices.get(),
    private val loginService: LoginService = LoginService.get()
) : DiscountsRepository {


    override suspend fun getDiscountByDocTotal(): Any? {
        val response = retryIO { discountsServices.getDiscountByDocTotal() }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getDiscountByDocTotal()
                else return error

            } else return error
        }
    }

    private suspend fun reLogin(): Boolean {
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