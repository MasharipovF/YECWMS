package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.inventory.InventoryOperationsForPost
import com.example.yecwms.data.remote.services.InventoryTransferService
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO
import com.google.gson.Gson

interface InventoryTransferRepository {
    suspend fun getInventoryTransfersList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        whsCode: String,
        getMyRequests: Boolean
    ): Any? //DocumentsVal?

    suspend fun getInventoryTransfer(docEntry: Long): Any? //Document?
    suspend fun insertInventoryTransfer(request: InventoryOperationsForPost): Any?// Document?
  }

class InventoryTransferRepositoryImpl(
    private val inventoryService: InventoryTransferService = InventoryTransferService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    InventoryTransferRepository {


    override suspend fun getInventoryTransfersList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        whsCode: String,
        getMyRequests: Boolean
    ): Any? {
        var filterStringBuilder = "contains(DocNum, '$filter')"

        if (docStatus != null) filterStringBuilder =
            "$filterStringBuilder and DocumentStatus eq '$docStatus'"

        filterStringBuilder =
            if (getMyRequests) "$filterStringBuilder and ToWarehouse eq '${whsCode}'"
            else "$filterStringBuilder and FromWarehouse eq '${whsCode}'"

        val response = retryIO {
            inventoryService.getInventoryTransfersList(
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
                if (isLoggedIn) getInventoryTransfersList(
                    filter,
                    skipValue,
                    docStatus,
                    whsCode,
                    getMyRequests
                )
                else return error

            } else return error
        }
    }




    override suspend fun getInventoryTransfer(docEntry: Long): Any? {
        val response = retryIO { inventoryService.getInventoryTransfer(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getInventoryTransfer(docEntry)
                else return error

            } else return error
        }
    }



    override suspend fun insertInventoryTransfer(request: InventoryOperationsForPost): Any? {
        val response =
            retryIO { inventoryService.addInventoryTransfer(body=request) }
        Log.d("inventory insert", response.body().toString())
        Log.d("inventory insert", Gson().toJson(request))
        return if (response.isSuccessful) {
            response.body()
        } else {

            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertInventoryTransfer(request)
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