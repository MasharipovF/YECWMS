package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.repository.LoginRepository
import com.example.yecwms.data.repository.LoginRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse
import com.example.yecwms.domain.dto.login.LoginResponseDto

interface LoginInteractor {
    suspend fun requestLogin(companyDB: String, username: String, password: String): Boolean
    var errorMessage: String?
}

class LoginInteractorImpl() : LoginInteractor {

    private val repository: LoginRepository by lazy { LoginRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun requestLogin(
        companyDB: String,
        username: String,
        password: String
    ): Boolean {
        val response = repository.requestLogin(companyDB, username, password)
        return if (response is LoginResponseDto) {
            Preferences.sessionID = response.SessionId
            Preferences.companyDB = companyDB
            Preferences.userName = username
            Preferences.userPassword = password
            Preferences.defaultWhs = null
            Preferences.batchPrefix= null
            Preferences.salesPerson = null
            //Preferences.userCode = null
            Log.d("USER_INTERACTOR", "sessionId is ${response.SessionId}")

            val usrDefaults = repository.getUserDefaults(username)
            if (usrDefaults != null) {
                Preferences.defaultWhs = usrDefaults.whsCode
                Preferences.defaultWhsName = usrDefaults.whsName
                Preferences.batchPrefix = usrDefaults.batchPrefix?:""
                Preferences.defaultCustomer = usrDefaults.cardCode
                Preferences.putBranchesIntoPref(usrDefaults.branches)
                Preferences.defaultAccount = usrDefaults.cashAccount
                if (usrDefaults.salesPersonCode != null) {
                    Preferences.putSalesPersonIntoPref(
                        usrDefaults.salesPersonCode,
                        usrDefaults.salesPersonName.toString()
                    )
                }
                Log.wtf("USER DEFAULTS", usrDefaults.toString())
            }

            true
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            false
        }
    }


}