package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.productionorders.ProductionOrders
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.ProductionOrdersService
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO
import com.example.yecwms.domain.dto.login.LoginRequestDto

interface ProductionOrdersRepository {
    suspend fun getProductionOrderList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String = ""
    ): Any? //DocumentsVal?

    suspend fun getEventsList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String = ""
    ): Any? //DocumentsVal?

    suspend fun getProductionOrder(docEntry: Long): Any? //Document?
    suspend fun insertProductionOrder(ProductionOrder: ProductionOrders): Any? //Document?
    suspend fun updatetProductionOrder(
        docEntry: Long,
        ProductionOrder: ProductionOrders
    ): Any? //Document?

    suspend fun cancelProductionOrder(docEntry: Long): Any? //Boolean?
}

class ProductionOrdersRepositoryImpl(
    private val productionOrdersService: ProductionOrdersService = ProductionOrdersService.get(),
    private val loginService: LoginService = LoginService.get()

) :
    ProductionOrdersRepository {


    override suspend fun getProductionOrderList(
        filter: String,
        skipValue: Int,
        docStatus: String
    ): Any? {
        val filterStringBuilder =
            "(contains(ProductDescription, '$filter') or contains(DocumentNumber, '$filter')) and ProductionOrderStatus ne 'boposClosed'"
        Log.d(
            "BPDEFAULTS",
            filterStringBuilder
        )
        val response = retryIO {
            productionOrdersService.getProductionOrdersList(
                filter = filterStringBuilder,
                skipValue = skipValue
            )
        }

        Log.d("BPDEFAULTS", response.body().toString())

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getProductionOrderList(filter,skipValue,docStatus)
                else return error

            } else return error
        }
    }

    override suspend fun getEventsList(
        filter: String,
        skipValue: Int,
        docStatus: String
    ): Any? {
        val filterStringBuilder =
            "(contains(ProductDescription, '$filter') or contains(DocumentNumber, '$filter')) and ProductionOrderStatus ne 'L' and ProductionOrderStatus ne 'C'"
        Log.d(
            "BPDEFAULTS",
            filterStringBuilder
        )
        val response = retryIO {
            productionOrdersService.getEventsList(
                filter = filterStringBuilder,
                skipValue = skipValue
            )
        }

        Log.d("BPDEFAULTS", response.body().toString())

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getEventsList(filter,skipValue,docStatus)
                else return error

            } else return error
        }
    }



    override suspend fun getProductionOrder(docEntry: Long): Any? {
        val response = retryIO { productionOrdersService.getProductionOrder(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getProductionOrder(docEntry)
                else return error

            } else return error
        }
    }

    override suspend fun insertProductionOrder(ProductionOrder: ProductionOrders): Any? {
        val response = retryIO { productionOrdersService.addProductionOrder(ProductionOrder) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertProductionOrder(ProductionOrder)
                else return error

            } else return error
        }
    }

    override suspend fun updatetProductionOrder(
        docEntry: Long,
        ProductionOrder: ProductionOrders
    ): Any? {
        val response = retryIO {
            productionOrdersService.updateProductionOrder(
                docEntry = docEntry,
                body = ProductionOrder
            )
        }
        Log.d("UPDATE ITEM", ProductionOrder.toString())
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) updatetProductionOrder(docEntry,ProductionOrder)
                else return error

            } else return error
        }
    }

    override suspend fun cancelProductionOrder(docEntry: Long): Any? {
        val response = retryIO { productionOrdersService.cancelProductionOrder(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelProductionOrder(docEntry)
                else return error

            } else return error
        }
    }

    private suspend fun reLogin():Boolean{
        val response = retryIO {
            loginService.requestLogin(LoginRequestDto(Preferences.companyDB, Preferences.userPassword, Preferences.userName))
        }
        return if (response.isSuccessful){
            Preferences.sessionID = response.body()?.SessionId
            true
        }
        else {
            false
        }    }

}