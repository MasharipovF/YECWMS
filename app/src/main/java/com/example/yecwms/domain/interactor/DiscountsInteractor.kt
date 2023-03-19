package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.discount.DiscountByDocTotalVal
import com.example.yecwms.data.repository.DiscountsRepository
import com.example.yecwms.data.repository.DiscountsRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse

interface DiscountsInteractor {
    suspend fun getDiscountByDocTotal(): DiscountByDocTotalVal?
    var errorMessage: String?

}

class DiscountsInteractorImpl : DiscountsInteractor {

    override var errorMessage: String? = null

    private val repository: DiscountsRepository by lazy { DiscountsRepositoryImpl() }


    override suspend fun getDiscountByDocTotal(): DiscountByDocTotalVal? {

        val response = repository.getDiscountByDocTotal()
        return if (response is DiscountByDocTotalVal) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


}