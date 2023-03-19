package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.ReportsService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface ReportsRepository {
    suspend fun getStockTransactionReport(
        itemCode: String? = null,
        fromDate: String? = null,
        toDate: String? = null,
        whsCode: String? = null,
        skipValue: Int = 0
    ): Any? //StockTransactionReportVal?

    suspend fun getStockOnDate(
        itemCode: String? = null,
        whsCode: String? = null,
        date: String,
        isBeginningDate: Boolean = true
    ): Any?
}

class ReportsRepositoryImpl(
    private val stockTransactionReportService: ReportsService = ReportsService.get(),
    private val loginService: LoginService = LoginService.get()

) :
    ReportsRepository {


    override suspend fun getStockTransactionReport(
        itemCode: String?,
        fromDate: String?,
        toDate: String?,
        whsCode: String?,
        skipValue: Int
    ): Any? {

        var filterString: String? = null

        if (itemCode != null) filterString = "ItemCode eq '$itemCode'"
        if (fromDate != null) filterString += if (filterString != null) " and DocDate ge '$fromDate'" else "DocDate ge '$fromDate'"
        if (toDate != null) filterString += if (filterString != null) " and DocDate le '$toDate'" else "DocDate le '$toDate'"
        if (whsCode != null) filterString += if (filterString != null) " and Warehouse eq '$whsCode'" else "Warehouse eq '$whsCode'"

        Log.d("STOCK", "filterstring $filterString")

        val response = retryIO {
            stockTransactionReportService.getStockTransactionReport(
                filter = filterString,
                skipValue = skipValue
            )
        }


        return if (response.isSuccessful) {
            Log.d("STOCK", response.body().toString())
            response.body()
        } else {
            Log.d("STOCK", response.errorBody()!!.string())

            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getStockTransactionReport(
                    itemCode, fromDate, toDate, whsCode, skipValue
                )
                else return error

            } else return error
        }
    }

    override suspend fun getStockOnDate(
        itemCode: String?,
        whsCode: String?,
        date: String,
        isBeginningDate: Boolean
    ): Any? {
        var applyString: String? = "aggregate(Quantity with sum as TotalQuantity)"
        var filterString = if (isBeginningDate)
            "DocDate lt '$date'"
        else
            "DocDate le '$date'"

        if (whsCode != null) filterString +=  " and Warehouse eq '$whsCode'"
        if (itemCode != null) filterString +=  " and ItemCode eq '$itemCode'"

        Log.d("STOCKk", filterString)

        val response = retryIO {
            stockTransactionReportService.getStockOnDate(
                filter = filterString,
                apply = applyString
            )
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.d("STOCKk", response.errorBody()!!.string())
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getStockOnDate(itemCode, whsCode, date, isBeginningDate)
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