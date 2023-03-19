package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.example.yecwms.data.repository.BatchNumbersRepository
import com.example.yecwms.data.repository.BatchNumbersRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse


interface BatchNumbersInteractor {

    suspend fun getBatchNumbers(
        ItemCode: String,
        WhsCode: String
    ): List<BatchNumbersVal.BatchNumbers>?


    var errorMessage: String?

}

class BatchNumbersInteractorImpl : BatchNumbersInteractor {


    override var errorMessage: String? = null
    private val repository: BatchNumbersRepository by lazy { BatchNumbersRepositoryImpl() }

    override suspend fun getBatchNumbers(
        ItemCode: String,
        WhsCode: String
    ): List<BatchNumbersVal.BatchNumbers>? {
        val response = repository.getBatchNumbers(ItemCode, WhsCode)
        return if (response is BatchNumbersVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


}