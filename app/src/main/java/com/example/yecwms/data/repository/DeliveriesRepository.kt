package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.DeliveriesService
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface DeliveriesRepository {
    suspend fun getDeliveriesList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getDelivery(docEntry: Long): Any? //Document?
    suspend fun insertDeliveryDraft(Delivery: DocumentForPost): Any?// Document?
    suspend fun insertDelivery(delivery: DocumentForPost): Any?// Document?
    suspend fun cancelDelivery(docEntry: Long): Any? //Boolean?
}

class DeliveriesRepositoryImpl(
    private val deliverieservice: DeliveriesService = DeliveriesService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    DeliveriesRepository {


    override suspend fun getDeliveriesList(
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
            deliverieservice.getDeliveriesList(filter = filterStringBuilder, skipValue = skipValue)
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getDeliveriesList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getDelivery(docEntry: Long): Any? {
        val response = retryIO { deliverieservice.getDelivery(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getDelivery(docEntry)
                else return error

            } else return error
        }
    }

    override suspend fun insertDeliveryDraft(Delivery: DocumentForPost): Any? {
        val response = deliverieservice.insertDeliveryDraft(Delivery)
        Log.d("DeliveryINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertDeliveryDraft(Delivery)
                else return error

            } else return error
        }

    }

    override suspend fun insertDelivery(delivery: DocumentForPost): Any? {
        val response = deliverieservice.insertDelivery(delivery)
        Log.d("DeliveryINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertDelivery(delivery)
                else return error

            } else return error
        }
    }

    override suspend fun cancelDelivery(docEntry: Long): Any? {
        val response = retryIO { deliverieservice.cancelDelivery(docEntry) }
        return if (response.isSuccessful) {
            true
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelDelivery(docEntry)
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