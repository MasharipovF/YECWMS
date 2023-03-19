package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.ReturnToSupplierRequestsService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface ReturnToSupplierRequestsRepository {
    suspend fun getReturnToSupplierRequestList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getReturnToSupplierRequest(docEntry: Long): Any? //Document?
    suspend fun insertReturnToSupplierRequest(ReturnToSupplierRequest: DocumentForPost): Any? //Document?
    suspend fun updateReturnToSupplierRequest(docEntry: Long, ReturnToSupplierRequest: DocumentForPost): Any? //Document?
    suspend fun closeReturnToSupplierRequest(docEntry: Long): Any? //Boolean?
}

class ReturnToSupplierRequestsRepositoryImpl(
    private val returnToSupplierRequestsService: ReturnToSupplierRequestsService = ReturnToSupplierRequestsService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    ReturnToSupplierRequestsRepository {


    override suspend fun getReturnToSupplierRequestList(
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

        Log.d("ReturnToSupplierRequests", filterStringBuilder)

        val response = retryIO {
            returnToSupplierRequestsService.getReturnToSupplierRequestsList(
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
                if (isLoggedIn) getReturnToSupplierRequestList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getReturnToSupplierRequest(docEntry: Long): Any? {
        val response = retryIO { returnToSupplierRequestsService.getReturnToSupplierRequest(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun insertReturnToSupplierRequest(ReturnToSupplierRequest: DocumentForPost): Any? {
        val response = retryIO { returnToSupplierRequestsService.addReturnToSupplierRequest(ReturnToSupplierRequest) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun updateReturnToSupplierRequest(docEntry: Long, ReturnToSupplierRequest: DocumentForPost): Any? {
        val response = retryIO { returnToSupplierRequestsService.updateReturnToSupplierRequest(docEntry = docEntry, body = ReturnToSupplierRequest) }
        Log.d("UPDATE ITEM", ReturnToSupplierRequest.toString())
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun closeReturnToSupplierRequest(docEntry: Long): Any? {
        val response = retryIO { returnToSupplierRequestsService.closeReturnToSupplierRequest(docEntry) }
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