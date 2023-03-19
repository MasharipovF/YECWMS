package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.PurchaseReturnsService
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface PurchaseReturnsRepository {
    suspend fun getPurchaseReturnsList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getPurchaseReturn(docEntry: Long): Any? //Document?
    suspend fun insertPurchaseReturnDraft(PurchaseReturn: DocumentForPost): Any?// Document?
    suspend fun cancelPurchaseReturn(docEntry: Long): Any? //Boolean?
}

class PurchaseReturnsRepositoryImpl(
    private val purchaseReturnService: PurchaseReturnsService = PurchaseReturnsService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    PurchaseReturnsRepository {


    override suspend fun getPurchaseReturnsList(
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
            purchaseReturnService.getPurchaseReturnsList(filter = filterStringBuilder, skipValue = skipValue)
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getPurchaseReturnsList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getPurchaseReturn(docEntry: Long): Any? {
        val response = retryIO { purchaseReturnService.getPurchaseReturn(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getPurchaseReturn(docEntry)
                else return error

            } else return error
        }
    }

    override suspend fun insertPurchaseReturnDraft(PurchaseReturn: DocumentForPost): Any? {
        val response = purchaseReturnService.insertPurchaseReturnDraft(PurchaseReturn)
        Log.d("PurchaseReturnINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertPurchaseReturnDraft(PurchaseReturn)
                else return error

            } else return error
        }

    }

    override suspend fun cancelPurchaseReturn(docEntry: Long): Any? {
        val response = retryIO { purchaseReturnService.cancelPurchaseReturn(docEntry) }
        return if (response.isSuccessful) {
            true
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelPurchaseReturn(docEntry)
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