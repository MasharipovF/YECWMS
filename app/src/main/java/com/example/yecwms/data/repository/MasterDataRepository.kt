package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.masterdatas.*
import com.example.yecwms.data.entity.series.SeriesForPost
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.data.remote.services.MasterDataServices
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface MasterDataRepository {
    suspend fun getWarehouses(onlyWithBinLocations: Boolean? = null): Any?
    suspend fun getBinLocations(binCode: String): Any?
    suspend fun getItemsGroups(): Any?
    suspend fun getUomGroups(): Any?
    suspend fun getUoms(): Any?
    suspend fun getPriceLists(): Any?
    suspend fun getBpGroups(bpGroupType: String?): Any?
    suspend fun getLastBarCode(): Any?
    suspend fun getExchangeRate(currencyCode: String): Any?
    suspend fun getCurrencies(date: String): Any?
    suspend fun getSalesManagers(): Any?
    suspend fun getSalesManager(managerCode: Long): Any?
    suspend fun getSeries(params: SeriesForPost): Any?
    suspend fun getDefaultSeries(params: SeriesForPost): Any?
    suspend fun getCompanyInfo(): Any?
}

class MasterDataRepositoryImpl(
    private val masterDataServices: MasterDataServices = MasterDataServices.get(),
    private val loginService: LoginService = LoginService.get()
) :
    MasterDataRepository {


    override suspend fun getWarehouses(onlyWithBinLocations: Boolean?): Any? {
        val filterString: String? = when (onlyWithBinLocations) {
            true -> "EnableBinLocations eq '${GeneralConsts.T_YES}'"
            false -> "EnableBinLocations eq '${GeneralConsts.T_NO}'"
            else -> null
        }

        val response = retryIO { masterDataServices.getWarehouses(filter = filterString) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }

    }

    override suspend fun getBinLocations(binCode: String): Any? {
        val response= retryIO {
            masterDataServices.getBinLocations(filter = "BinCode eq '$binCode'")
        }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }

    }

    override suspend fun getItemsGroups(): Any? {
        val response = retryIO { masterDataServices.getItemsGroup() }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }

    }

    override suspend fun getUomGroups(): Any? {
        val response = retryIO { masterDataServices.getUnitOfMeasureGroups() }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }

    }

    override suspend fun getUoms(): Any? {
        val response =  retryIO { masterDataServices.getUnitOfMeasures()}
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getPriceLists(): Any? {
        val response =  retryIO { masterDataServices.getPriceLists() }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getBpGroups(bpGroupType: String?): Any? {
        val response =  if (bpGroupType == null) {
            Log.d("BPGROUP", "NO TYPE")
            retryIO { masterDataServices.getBpGroups() }
        } else {
            Log.d("BPGROUP", bpGroupType.toString())
            retryIO { masterDataServices.getBpGroups(filter = "Type eq '$bpGroupType'") }
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getLastBarCode(): Any? {
        val response = retryIO { masterDataServices.getLastBarCode() }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }

    }

    override suspend fun getExchangeRate(currencyCode: String): Any? {
        val response = retryIO { masterDataServices.getExchangeRate(ExchangeRates(currencyCode)) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getCurrencies(date: String): Any? {
        val response= retryIO {
            masterDataServices.getCurrencies(date = date)
        }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }

    }


    override suspend fun getSalesManagers(): Any? {
        var filterString = "Active eq 'tYES'"

        val response = retryIO { masterDataServices.getSalesManagers(filter = filterString) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getSalesManager(managerCode: Long): Any? {
        val response = retryIO { masterDataServices.getSalesManager(managerCode) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getSeries(params: SeriesForPost): Any? {
        val response = retryIO { masterDataServices.getSeries(params) }
        Log.d("SERIES01", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getSeries(params)
                else return error

            } else return error
        }
    }

    override suspend fun getDefaultSeries(params: SeriesForPost): Any? {
        val response = retryIO { masterDataServices.getDefaultSeries(params) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getDefaultSeries(params)
                else return error

            } else return error
        }
    }

    override suspend fun getCompanyInfo(): Any? {
        val response = retryIO { masterDataServices.getCompanyInfo() }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getCompanyInfo()
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