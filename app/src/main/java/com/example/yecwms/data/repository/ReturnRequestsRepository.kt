package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.ReturnRequestsService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface ReturnRequestsRepository {
    suspend fun getReturnRequestList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getReturnRequest(docEntry: Long): Any? //Document?
    suspend fun insertReturnRequest(ReturnRequest: DocumentForPost): Any? //Document?
    suspend fun updateReturnRequest(docEntry: Long, ReturnRequest: DocumentForPost): Any? //Document?
    suspend fun closeReturnRequest(docEntry: Long): Any? //Boolean?
}

class ReturnRequestsRepositoryImpl(
    private val returnRequestsService: ReturnRequestsService = ReturnRequestsService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    ReturnRequestsRepository {


    override suspend fun getReturnRequestList(
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

        Log.d("ReturnRequests", filterStringBuilder)

        val response = retryIO {
            returnRequestsService.getReturnRequestsList(
                filter = filterStringBuilder,
                skipValue = skipValue
            )
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getReturnRequestList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getReturnRequest(docEntry: Long): Any? {
        val response = retryIO { returnRequestsService.getReturnRequest(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun insertReturnRequest(ReturnRequest: DocumentForPost): Any? {
        val response = retryIO { returnRequestsService.addReturnRequest(ReturnRequest) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun updateReturnRequest(docEntry: Long, ReturnRequest: DocumentForPost): Any? {
        val response = retryIO { returnRequestsService.updateReturnRequest(docEntry = docEntry, body = ReturnRequest) }
        Log.d("UPDATE ITEM", ReturnRequest.toString())
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun closeReturnRequest(docEntry: Long): Any? {
        val response = retryIO { returnRequestsService.closeReturnRequest(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
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