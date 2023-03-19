package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.GoodsIssueService
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface GoodsIssueRepository {
    suspend fun getGoodsIssuesList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getGoodsIssue(docEntry: Long): Any? //Document?
    suspend fun insertGoodsIssue(GoodsIssue: DocumentForPost): Any?// Document?
    suspend fun cancelGoodsIssue(docEntry: Long): Any? //Boolean?
}

class GoodsIssueRepositoryImpl(
    private val goodsIssueservice: GoodsIssueService = GoodsIssueService.get(),
    private val loginService: LoginService = LoginService.get()
) :
    GoodsIssueRepository {


    override suspend fun getGoodsIssuesList(
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

        val response = retryIO {
            goodsIssueservice.getGoodsIssuesList(filter = filterStringBuilder, skipValue = skipValue)
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getGoodsIssuesList(filter, skipValue, docStatus, dateFrom, dateTo)
                else return error

            } else return error
        }
    }


    override suspend fun getGoodsIssue(docEntry: Long): Any? {
        val response = retryIO { goodsIssueservice.getGoodsIssue(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getGoodsIssue(docEntry)
                else return error

            } else return error
        }

    }


    override suspend fun insertGoodsIssue(GoodsIssue: DocumentForPost): Any? {
        val response = goodsIssueservice.insertGoodsIssue(GoodsIssue)
        Log.d("GoodsIssueINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) insertGoodsIssue(GoodsIssue)
                else return error

            } else return error
        }

    }

    override suspend fun cancelGoodsIssue(docEntry: Long): Any? {
        val response = retryIO { goodsIssueservice.cancelGoodsIssue(docEntry) }
        return if (response.isSuccessful) {
            true
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) cancelGoodsIssue(docEntry)
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