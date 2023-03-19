package com.example.yecwms.util

import android.util.Log
import com.example.yecwms.domain.dto.error.Error
import com.example.yecwms.domain.dto.error.ErrorResponse
import com.example.yecwms.domain.dto.error.ErrorResponseV2
import com.example.yecwms.domain.dto.error.Message
import com.google.gson.Gson
import retrofit2.Response

abstract class ErrorUtils {
    companion object {
        fun <T> errorProcess(responseBody: Response<T>): ErrorResponse {
            val body = responseBody.errorBody()!!.string()
            var error:ErrorResponse? = null
            var errorV2: ErrorResponseV2? = null
            var errorMessage = "Неизвестная ошибка"
            var errorCode = -1

            try {
                error= Gson().fromJson(body, ErrorResponse::class.java)
                errorMessage = error.error.message.value.toString()
                errorCode = error.error.code
            } catch (e: Exception){
                errorV2 = Gson().fromJson(body, ErrorResponseV2::class.java)
                errorMessage = errorV2?.error?.message.toString()
                errorCode = errorV2?.error?.code?.toInt()!!
                Log.wtf("ERROR_BODY", errorMessage)
            }

            Log.wtf("ERROR_BODY", errorMessage)
            error = ErrorResponse(
                Error(
                    errorCode,
                    Message("ru", errorMessage)
                )
            )
            Log.wtf("ERROR_BODY", error.toString())

            return error
        }
    }
}