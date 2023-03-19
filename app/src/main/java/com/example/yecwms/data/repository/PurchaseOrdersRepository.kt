package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.PurchaseOrdersService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface PurchaseOrdersRepository {
    suspend fun getPurchaseOrderList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getPurchaseOrder(docEntry: Long): Any? //Document?
    suspend fun insertPurchaseOrder(PurchaseOrder: DocumentForPost): Any? //Document?
    suspend fun updatePurchaseOrder(docEntry: Long, PurchaseOrder: DocumentForPost): Any? //Document?
    suspend fun closePurchaseOrder(docEntry: Long): Any? //Boolean?
}

class PurchaseOrdersRepositoryImpl(
    private val purchaseOrdersService: PurchaseOrdersService = PurchaseOrdersService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    PurchaseOrdersRepository {


    override suspend fun getPurchaseOrderList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): Any? {

        var filterStringBuilder =
            if (docStatus != null) "(contains(CardName, '$filter') or contains(DocNum, '$filter')) and DocumentStatus eq '$docStatus'"
            else "(contains(CardName, '$filter') or contains(DocNum, '$filter'))"



        if (dateFrom != null)
            filterStringBuilder += " and DocDate ge '$dateFrom'"

        if (dateTo != null)
            filterStringBuilder += " and DocDate le '$dateTo'"

        Log.d("PurchaseORDERS", filterStringBuilder)

        val response = retryIO {
            purchaseOrdersService.getPurchaseOrdersList(
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
                if (isLoggedIn) getPurchaseOrderList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getPurchaseOrder(docEntry: Long): Any? {
        val response = retryIO { purchaseOrdersService.getPurchaseOrder(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun insertPurchaseOrder(PurchaseOrder: DocumentForPost): Any? {
        val response = retryIO { purchaseOrdersService.addPurchaseOrder(PurchaseOrder) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun updatePurchaseOrder(docEntry: Long, PurchaseOrder: DocumentForPost): Any? {
        val response = retryIO { purchaseOrdersService.updatePurchaseOrder(docEntry = docEntry, body = PurchaseOrder) }
        Log.d("UPDATE ITEM", PurchaseOrder.toString())
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun closePurchaseOrder(docEntry: Long): Any? {
        val response = retryIO { purchaseOrdersService.closePurchaseOrder(docEntry) }
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