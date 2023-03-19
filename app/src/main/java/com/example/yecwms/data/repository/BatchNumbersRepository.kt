package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.remote.services.BatchNumberServices
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.LoginService.Companion.reLogin
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO


interface BatchNumbersRepository {
    suspend fun getBatchNumbers(ItemCode: String, WhsCode: String): Any?
}

class BatchNumbersRepositoryImpl(
    private val batchNumberService: BatchNumberServices = BatchNumberServices.get(),
    private val loginService: LoginService = LoginService.get()

) : BatchNumbersRepository {


    override suspend fun getBatchNumbers(ItemCode: String, WhsCode: String): Any? {
        val response =
            retryIO {
                batchNumberService.getBatchNumbers(
                    ItemCode = "'${ItemCode}'",
                    WhsCode = "'${WhsCode}'"
                )
            }
        Log.d("SELECTBATCHES", "bababa $ItemCode   $WhsCode   ")

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getBatchNumbers(ItemCode, WhsCode)
                else return error

            } else return error
        }
    }




}