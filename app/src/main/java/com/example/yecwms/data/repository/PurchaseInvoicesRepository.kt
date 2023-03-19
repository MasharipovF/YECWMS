package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.PurchaseInvoicesService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface PurchaseInvoicesRepository {
    suspend fun getPurchaseInvoicesList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        isReserveInvoice: Boolean? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getOpenPurchaseInvoicesListSML(
        filter: String = "",
        skipValue: Int = 0,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getPurchaseInvoice(docEntry: Long): Any? //Document?
    suspend fun insertPurchaseInvoice(PurchaseInvoice: DocumentForPost): Any?// Document?
    suspend fun cancelPurchaseInvoice(docEntry: Long): Any? //Boolean?
}

class PurchaseInvoicesRepositoryImpl(
    private val purchaseInvoiceservice: PurchaseInvoicesService = PurchaseInvoicesService.get(),
    private val loginService: LoginService = LoginService.get()

) :
    PurchaseInvoicesRepository {


    override suspend fun getPurchaseInvoicesList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        isReserveInvoice: Boolean?,
        dateFrom: String?,
        dateTo: String?
    ): Any? {

        var filterStringBuilder =
            if (docStatus != null) "(contains(CardName, '$filter') or contains(DocNum, '$filter')) and DocumentStatus eq '$docStatus'"
            else "(contains(CardName, '$filter') or contains(DocNum, '$filter'))"

        filterStringBuilder += when (isReserveInvoice) {
            true -> " and ReserveInvoice eq '${GeneralConsts.T_YES}'"
            false -> " and ReserveInvoice eq '${GeneralConsts.T_NO}'"
            else -> ""
        }

        if (Preferences.defaultWhs != null) filterStringBuilder += " and U_whs eq '${Preferences.defaultWhs}'"

        if (dateFrom != null)
            filterStringBuilder += " and DocDate ge '$dateFrom'"

        if (dateTo != null)
            filterStringBuilder += " and DocDate le '$dateTo'"

        Log.d("INVOICES", filterStringBuilder)

        val response = retryIO {
            purchaseInvoiceservice.getPurchaseInvoicesList(
                filter = filterStringBuilder,
                skipValue = skipValue
            )
        }

        Log.d("INVOICES", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getPurchaseInvoicesList(filter, skipValue, docStatus, isReserveInvoice, dateFrom, dateTo)
                else return error

            } else return error
        }
    }

    override suspend fun getOpenPurchaseInvoicesListSML(
        filter: String,
        skipValue: Int,
        dateFrom: String?,
        dateTo: String?
    ): Any? {
        var filterStringBuilder =
             "(contains(CardName, '$filter') or contains(DocNum, '$filter'))"



        if (Preferences.defaultWhs != null) filterStringBuilder += " and U_whs eq '${Preferences.defaultWhs}'"

        if (dateFrom != null)
            filterStringBuilder += " and DocDate ge '$dateFrom'"

        if (dateTo != null)
            filterStringBuilder += " and DocDate le '$dateTo'"

        Log.d("INVOICES", filterStringBuilder)

        val response = retryIO {
            purchaseInvoiceservice.getOpenPurchaseInvoicesListSML(
                filter = filterStringBuilder,
                skipValue = skipValue
            )
        }

        Log.d("INVOICES", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getOpenPurchaseInvoicesListSML(filter, skipValue,  dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getPurchaseInvoice(docEntry: Long): Any? {
        val response = retryIO { purchaseInvoiceservice.getPurchaseInvoice(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getPurchaseInvoice(docEntry)
                else return error

            } else return error
        }

    }

    override suspend fun insertPurchaseInvoice(PurchaseInvoice: DocumentForPost): Any? {
        val response = purchaseInvoiceservice.addPurchaseInvoice(PurchaseInvoice)
        Log.d("PurchaseInvoiceINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertPurchaseInvoice(PurchaseInvoice)
                else return error

            } else return error
        }

    }

    override suspend fun cancelPurchaseInvoice(docEntry: Long): Any? {
        val response = retryIO { purchaseInvoiceservice.cancelPurchaseInvoice(docEntry) }
        return if (response.isSuccessful) {
            true
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelPurchaseInvoice(docEntry
                )
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