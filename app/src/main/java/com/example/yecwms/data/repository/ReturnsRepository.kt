package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.ReturnsService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface ReturnsRepository {
    suspend fun getReturnsList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getReturn(docEntry: Long): Any? //Document?
    suspend fun insertReturnDraft(Return: DocumentForPost): Any?// Document?
    suspend fun cancelReturn(docEntry: Long): Any? //Boolean?
}

class ReturnsRepositoryImpl(
    private val returnsService: ReturnsService = ReturnsService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    ReturnsRepository {


    override suspend fun getReturnsList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): Any? {

        var filterStringBuilder =
            if (docStatus != null) "(contains(CardName, '$filter') or contains(DocNum, '$filter')) and DocumentStatus eq '$docStatus'"
            else "(contains(CardName, '$filter') or contains(DocNum, '$filter'))"

        if (Preferences.defaultWhs != null) filterStringBuilder += " and U_whs eq '${Preferences.defaultWhs}'"

        if (dateFrom != null)
            filterStringBuilder += " and DocDate ge '$dateFrom'"

        if (dateTo != null)
            filterStringBuilder += " and DocDate le '$dateTo'"

        val response = retryIO {
            returnsService.getReturnsList(filter = filterStringBuilder, skipValue = skipValue)
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getReturnsList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getReturn(docEntry: Long): Any? {
        val response = retryIO { returnsService.getReturn(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getReturn(docEntry)
                else return error

            } else return error
        }

    }

    override suspend fun insertReturnDraft(Return: DocumentForPost): Any? {
        val response = returnsService.insertReturnDraft(Return)
        Log.d("ReturnINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertReturnDraft(Return)
                else return error

            } else return error
        }

    }

    override suspend fun cancelReturn(docEntry: Long): Any? {
        val response = retryIO { returnsService.cancelReturn(docEntry) }
        return if (response.isSuccessful) {
            true
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelReturn(docEntry)
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