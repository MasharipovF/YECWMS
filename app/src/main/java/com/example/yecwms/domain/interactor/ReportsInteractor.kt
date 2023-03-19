package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.reports.StockTransactionReport
import com.example.yecwms.data.entity.reports.StockTransactionReportVal
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.data.repository.ReportsRepository
import com.example.yecwms.data.repository.ReportsRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse


interface ReportsInteractor {

    suspend fun getStockTransactionReport(
        itemCode: String? = null,
        fromDate: String? = null,
        toDate: String? = null,
        whsCode: String? = null,
        skipValue: Int = 0
    ): List<StockTransactionReport>? //StockTransactionReportVal?

    suspend fun getStockOnDate(
        itemCode: String? = null,
        whsCode: String? = null,
        date: String,
        isBeginningDate: Boolean
    ): List<StockTransactionReport>?


    val errorMessage: String?

}

class ReportsInteractorImpl : ReportsInteractor {

    private val repository: ReportsRepository by lazy { ReportsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }
    override var errorMessage: String? = null

    override suspend fun getStockTransactionReport(
        itemCode: String?,
        fromDate: String?,
        toDate: String?,
        whsCode: String?,
        skipValue: Int
    ): List<StockTransactionReport>? {
        val response =
            repository.getStockTransactionReport(
                itemCode, fromDate, toDate, whsCode, skipValue
            )

        return if (response is StockTransactionReportVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getStockOnDate(
        itemCode: String?,
        whsCode: String?,
        date: String,
        isBeginningDate: Boolean
    ): List<StockTransactionReport>? {
        val response =
            repository.getStockOnDate(
                itemCode,
                whsCode,
                date,
                isBeginningDate
            )

        return if (response is StockTransactionReportVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


}