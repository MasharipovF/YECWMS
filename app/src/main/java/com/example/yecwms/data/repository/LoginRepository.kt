package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.userdefaults.UserDefaults
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.UsersService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface LoginRepository {
    suspend fun requestLogin(companyDB: String, username: String, password: String): Any?
    suspend fun getUserDefaults(
        userCode: String
    ): UserDefaults?
}

class LoginRepositoryImpl(
) : LoginRepository {

    override suspend fun requestLogin(
        companyDB: String,
        username: String,
        password: String
    ): Any? {

        // THIS IS NEEDED FOR THIS. WHEN WE LOAD FIRST IN ENGLISH AND THEN IN RUSSIAN LANGUAGE, THEN RUSSIAN LANGUAGE ERROR STRINGS DISPLAYED CORRECTLY.
        // THAT IS WHY WE FIRST LOGIN IN ENGLISH, AND THE NEXT TIME LOGIN IN RUSSIAN
        val isFirstLogin = Preferences.firstLogin

        val loginService: LoginService = LoginService.get()

        val response = retryIO {
            loginService.requestLogin(
                LoginRequestDto(
                    companyDB,
                    password,
                    username,
                    if (isFirstLogin) null else 24
                )
            )
        }
        return if (response.isSuccessful) {
            Preferences.firstLogin = false
            response.body()
        } else {

            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getUserDefaults(
        userCode: String
    ): UserDefaults? {
        val userDefaultsService: UsersService = UsersService.get()

        val userDefResponse = retryIO {
            userDefaultsService.getUserDefaults(userCode = "UserCode eq '$userCode'")
        }
        return if (userDefResponse.isSuccessful) {
            userDefResponse.body()?.transform()
        } else {
            Log.d(
                "USERDEFAULTS",
                userDefResponse.errorBody()!!.string()
            )
            null
        }
    }

}