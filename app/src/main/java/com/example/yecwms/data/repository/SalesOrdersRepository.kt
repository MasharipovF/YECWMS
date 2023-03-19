package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.SalesOrdersService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface SalesOrdersRepository {
    suspend fun getSalesOrderList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getSalesOrder(docEntry: Long): Any? //Document?
    suspend fun insertSalesOrder(salesOrder: DocumentForPost): Any? //Document?
    suspend fun updateSalesOrder(docEntry: Long, salesOrder: DocumentForPost): Any? //Document?
    suspend fun closeSalesOrder(docEntry: Long): Any? //Boolean?
}

class SalesOrdersRepositoryImpl(
    private val salesOrdersService: SalesOrdersService = SalesOrdersService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    SalesOrdersRepository {


    override suspend fun getSalesOrderList(
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

        Log.d("SALESORDERS", filterStringBuilder)

        val response = retryIO {
            salesOrdersService.getSalesOrdersList(
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
                if (isLoggedIn) getSalesOrderList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getSalesOrder(docEntry: Long): Any? {
        val response = retryIO { salesOrdersService.getSalesOrder(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun insertSalesOrder(salesOrder: DocumentForPost): Any? {
        val response = retryIO { salesOrdersService.addSalesOrder(salesOrder) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun updateSalesOrder(docEntry: Long, salesOrder: DocumentForPost): Any? {
        val response = retryIO { salesOrdersService.updateSalesOrder(docEntry = docEntry, body = salesOrder) }
        Log.d("UPDATE ITEM", salesOrder.toString())
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun closeSalesOrder(docEntry: Long): Any? {
        val response = retryIO { salesOrdersService.closeSalesOrder(docEntry) }
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