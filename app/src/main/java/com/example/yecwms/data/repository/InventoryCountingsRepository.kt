package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.inventorycounting.InventoryCountingsForPost
import com.example.yecwms.data.remote.services.InventoryCountingsService
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO
import com.google.gson.Gson

interface InventoryCountingsRepository {
    suspend fun getInventoryCountingsList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        whsCode: String
    ): Any? //DocumentsVal?

    suspend fun getInventoryCounting(docEntry: Long): Any? //Document?
    suspend fun updateInventoryCounting(
        docEntry: Long,
        request: InventoryCountingsForPost
    ): Any? //Document?

    suspend fun insertInventoryCounting(request: InventoryCountingsForPost): Any?// Document?
    suspend fun checkIfDocumentIsInserted(mobileAppId: String): Any? //Document?
    suspend fun closeInventoryCounting(docEntry: Long): Any? //Boolean?
    suspend fun cancelInventoryCounting(docEntry: Long): Any? //Boolean?
    suspend fun getAllItemsToBasket(whsCode: String): Any?
}

class InventoryCountingsRepositoryImpl(
    private val inventoryService: InventoryCountingsService = InventoryCountingsService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    InventoryCountingsRepository {


    override suspend fun getInventoryCountingsList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        whsCode: String
    ): Any? {
        var filterStringBuilder = "contains(DocumentNumber, '$filter')"

        if (docStatus != null) filterStringBuilder =
            "$filterStringBuilder and DocumentStatus eq '$docStatus'"

        val response = retryIO {
            inventoryService.getInventoryCountingsList(
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
                if (isLoggedIn) getInventoryCountingsList(
                    filter,
                    skipValue,
                    docStatus,
                    whsCode
                )
                else return error

            } else return error
        }
    }

    override suspend fun getInventoryCounting(docEntry: Long): Any? {
        val response = retryIO { inventoryService.getInventoryCounting(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getInventoryCounting(docEntry)
                else return error

            } else return error
        }
    }


    override suspend fun updateInventoryCounting(
        docEntry: Long,
        request: InventoryCountingsForPost
    ): Any? {
        val response =
            retryIO {
                inventoryService.updateInventoryCounting(
                    docEntry = docEntry,
                    body = request
                )
            }
        Log.d("UPDATE ITEM", request.toString())
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) updateInventoryCounting(docEntry, request)
                else return error

            } else return error
        }
    }

    override suspend fun insertInventoryCounting(request: InventoryCountingsForPost): Any? {
        val response =
            retryIO { inventoryService.insertInventoryCounting(body = request) }
        Log.d("inventory insert", response.body().toString())
        Log.d("inventory insert", Gson().toJson(request))
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertInventoryCounting(request)
                else return error

            } else return error
        }
    }

    override suspend fun checkIfDocumentIsInserted(mobileAppId: String): Any? {
        val filterString = "U_mobileAppId eq '${mobileAppId}'"

        val response = retryIO { inventoryService.checkIfDocumentIsInserted(filter = filterString) }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) checkIfDocumentIsInserted(mobileAppId)
                else return error

            } else return error
        }
    }

    override suspend fun closeInventoryCounting(docEntry: Long): Any? {
        val response = retryIO { inventoryService.closeInventoryCounting(docEntry) }
        return if (response.isSuccessful) {
            Log.d("CLOSE", response.body().toString())
            response.body()
        } else {
            Log.d("CLOSE", "${response.errorBody()?.string()}")
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) closeInventoryCounting(docEntry)
                else return error

            } else return error
        }
    }

    override suspend fun cancelInventoryCounting(docEntry: Long): Any? {
        val response = retryIO { inventoryService.cancelInventoryCounting(docEntry) }
        return if (response.isSuccessful) {
            Log.d("CLOSE", response.body().toString())
            response.body()
        } else {
            Log.d("CLOSE", "${response.errorBody()?.string()}")
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelInventoryCounting(docEntry)
                else return error

            } else return error
        }
    }

    override suspend fun getAllItemsToBasket(whsCode: String): Any? {
        val filterStringBuilder = "WarehouseCode eq '$whsCode'"


        val response = retryIO {
            inventoryService.getItemsForBasketList(
                filter = filterStringBuilder
            )
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getAllItemsToBasket(
                    whsCode
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