package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.inventory.InventoryOperationsForPost
import com.example.yecwms.data.remote.services.InventoryTransferRequestService
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface InventoryTransferRequestRepository {
    suspend fun getInventoryTransferRequestsList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String? = null
    ): Any? //DocumentsVal?

    suspend fun getInventoryTransferRequestsCount(
        docStatus: String? = null,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String? = null
    ): Any? //DocumentsVal?

    suspend fun getInventoryTransferRequest(docEntry: Long): Any? //Document?
    suspend fun updatetInventoryRequest(
        docEntry: Long,
        request: InventoryOperationsForPost
    ): Any? //Document?

    suspend fun insertInventoryTransferRequest(request: InventoryOperationsForPost): Any?// Document?
    suspend fun closeInventoryTransferRequest(docEntry: Long): Any? //Boolean?
}

class InventoryTransferRequestRepositoryImpl(
    private val inventoryService: InventoryTransferRequestService = InventoryTransferRequestService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    InventoryTransferRequestRepository {


    override suspend fun getInventoryTransferRequestsList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String?
    ): Any? {
        var filterStringBuilder = "contains(DocNum, '$filter')"

        if (getProceeded!=null){
            filterStringBuilder =
                "$filterStringBuilder and U_proceeded eq '$getProceeded'"
        }

        if (docStatus != null) filterStringBuilder =
            "$filterStringBuilder and DocumentStatus eq '$docStatus'"

        filterStringBuilder =
            if (getMyRequests) "$filterStringBuilder and ToWarehouse eq '${whsCode}'"
            else "$filterStringBuilder and FromWarehouse eq '${whsCode}'"

        val response = retryIO {
            inventoryService.getInventoryTransferRequestsList(
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
                if (isLoggedIn) getInventoryTransferRequestsList(
                    filter,
                    skipValue,
                    docStatus,
                    whsCode,
                    getMyRequests,
                    getProceeded
                )
                else return error

            } else return error
        }
    }

    override suspend fun getInventoryTransferRequestsCount(
        docStatus: String?,
        whsCode: String,
        getMyRequests: Boolean,
        getProceeded: String?
    ): Any? {
        var filterStringBuilder = ""

        if (getProceeded!=null){
            filterStringBuilder =
                "$filterStringBuilder and U_proceeded eq '$getProceeded'"
        }

        if (docStatus != null) filterStringBuilder =
            "$filterStringBuilder and DocumentStatus eq '$docStatus'"

        filterStringBuilder =
            if (getMyRequests) "$filterStringBuilder and ToWarehouse eq '${whsCode}'"
            else "$filterStringBuilder and FromWarehouse eq '${whsCode}'"

        val response = retryIO {
            inventoryService.getInventoryTransferRequestsCount(
                filter = filterStringBuilder
            )
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getInventoryTransferRequestsCount(
                    docStatus,
                    whsCode,
                    getMyRequests,
                    getProceeded
                )
                else return error

            } else return error
        }
    }


    override suspend fun getInventoryTransferRequest(docEntry: Long): Any? {
        val response = retryIO { inventoryService.getInventoryTransferRequest(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getInventoryTransferRequest(docEntry)
                else return error

            } else return error
        }
    }


    override suspend fun updatetInventoryRequest(
        docEntry: Long,
        request: InventoryOperationsForPost
    ): Any? {
        val response =
            retryIO { inventoryService.updateInventoryRequest(docEntry = docEntry, body = request) }
        Log.d("UPDATE ITEM", request.toString())
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) updatetInventoryRequest(docEntry, request)
                else return error

            } else return error
        }
    }

    override suspend fun insertInventoryTransferRequest(request: InventoryOperationsForPost): Any? {
        val response =
            retryIO { inventoryService.addInventoryTransferRequest(request) }
        Log.d("inventory insert", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertInventoryTransferRequest(request)
                else return error

            } else return error
        }
    }

    override suspend fun closeInventoryTransferRequest(docEntry: Long): Any? {
        val response = retryIO { inventoryService.closeInventoryTransferRequest(docEntry) }
        return if (response.isSuccessful) {
            Log.d("CLOSE", response.body().toString())
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) closeInventoryTransferRequest(docEntry)
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