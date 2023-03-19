package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.PurchaseDeliveriesService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface PurchaseDeliveriesRepository {
    suspend fun getPurchaseDeliveriesList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getPurchaseDelivery(docEntry: Long): Any? //Document?
    suspend fun insertPurchaseDeliveryDraft(PurchaseDelivery: DocumentForPost): Any?// Document?
    suspend fun insertPurchaseDelivery(PurchaseDelivery: DocumentForPost): Any?// Document?
    suspend fun cancelPurchaseDelivery(docEntry: Long): Any? //Boolean?
}

class PurchaseDeliveriesRepositoryImpl(
    private val purchaseDeliverieservice: PurchaseDeliveriesService = PurchaseDeliveriesService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    PurchaseDeliveriesRepository {


    override suspend fun getPurchaseDeliveriesList(
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
            purchaseDeliverieservice.getPurchaseDeliveriesList(filter = filterStringBuilder, skipValue = skipValue)
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getPurchaseDeliveriesList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getPurchaseDelivery(docEntry: Long): Any? {
        val response = retryIO { purchaseDeliverieservice.getPurchaseDelivery(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getPurchaseDelivery(docEntry)
                else return error

            } else return error
        }

    }

    override suspend fun insertPurchaseDeliveryDraft(PurchaseDelivery: DocumentForPost): Any? {
        val response = purchaseDeliverieservice.insertPurchaseDeliveryDraft(PurchaseDelivery)
        Log.d("PurchaseDeliveryINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertPurchaseDeliveryDraft(PurchaseDelivery)
                else return error

            } else return error
        }

    }

    override suspend fun insertPurchaseDelivery(PurchaseDelivery: DocumentForPost): Any? {
        val response = purchaseDeliverieservice.insertPurchaseDelivery(PurchaseDelivery)
        Log.d("PurchaseDeliveryINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertPurchaseDelivery(PurchaseDelivery)
                else return error

            } else return error
        }

    }

    override suspend fun cancelPurchaseDelivery(docEntry: Long): Any? {
        val response = retryIO { purchaseDeliverieservice.cancelPurchaseDelivery(docEntry) }
        return if (response.isSuccessful) {
            true
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelPurchaseDelivery(docEntry)
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