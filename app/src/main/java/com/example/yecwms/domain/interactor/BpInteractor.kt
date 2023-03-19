package com.example.yecwms.domain.interactor

import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.data.entity.businesspartners.BusinessPartnersForPost
import com.example.yecwms.data.entity.businesspartners.BusinessPartnersVal
import com.example.yecwms.data.entity.masterdatas.BusinessPartnerGroupsVal
import com.example.yecwms.data.entity.masterdatas.PriceListsVal
import com.example.yecwms.data.repository.BpRepository
import com.example.yecwms.data.repository.BpRepositoryImpl
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse
import com.example.yecwms.domain.mappers.Mappers

interface BpInteractor {

    suspend fun getAllBp(
        filter: String = "",
        bpType: String? = null,
        onlyWithDebts: Boolean
    ): List<BusinessPartners>?

    suspend fun getMoreBps(
        filter: String = "",
        skipValue: Int,
        bpType: String? = null,
        onlyWithDebts: Boolean
    ): List<BusinessPartners>?

    suspend fun getBpInfo(bpCode: String, isForDocument: Boolean = false): BusinessPartners?
    suspend fun addNewBp(bp: BusinessPartnersForPost): BusinessPartners?
    suspend fun updateBp(boCode: String, bpToUpdate: BusinessPartnersForPost): Boolean
    suspend fun checkIfPhoneExists(phone: String?, bpType: String?): String?
    val errorMessage: String?
}

class BpInteractorImpl : BpInteractor {

    private val repository: BpRepository by lazy { BpRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getAllBp(
        filter: String,
        bpType: String?,
        onlyWithDebts: Boolean
    ): List<BusinessPartners>? {
        val response =
            repository.getBps(
                filter = filter,
                bpType = bpType,
                onlyWithDebts = onlyWithDebts
            )

        return if (response is BusinessPartnersVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getMoreBps(
        filter: String,
        skipValue: Int,
        bpType: String?,
        onlyWithDebts: Boolean
    ): List<BusinessPartners>? {
        val response = repository.getBps(
            filter = filter,
            skipValue = skipValue,
            bpType = bpType,
            onlyWithDebts = onlyWithDebts
        )

        return if (response is BusinessPartnersVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getBpInfo(bpCode: String, isForDocument: Boolean): BusinessPartners? {
        val response = repository.getBpInfo(bpCode)

        if (response is BusinessPartners) {
            if (!isForDocument) {
                val priceLists = masterDataRepo.getPriceLists()
                if (priceLists is PriceListsVal) {
                    response.PriceListName =
                        Mappers.mapPriceListCodeToName(priceLists.value, response.PriceListCode)
                } else {
                    errorMessage = (priceLists as ErrorResponse).error.message.value
                    return null
                }


                val bpGroups = masterDataRepo.getBpGroups(null)
                if (bpGroups is BusinessPartnerGroupsVal) {
                    response.GroupName = Mappers.mapBpGroupCodeToName(
                        bpGroups.value,
                        response.GroupCode
                    )
                } else {
                    errorMessage = (bpGroups as ErrorResponse).error.message.value
                    return null
                }


            }

            return response

        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            return null
        }

    }


    override suspend fun addNewBp(bp: BusinessPartnersForPost): BusinessPartners? {
        val response = repository.insertNewBp(bp)
        return if (response is BusinessPartners) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun updateBp(bpCode: String, bpToUpdate: BusinessPartnersForPost): Boolean {
        val response = repository.updateBp(bpCode, bpToUpdate)
        return if (response is ErrorResponse) {
            errorMessage = (response).error.message.value
            false
        } else {
            true
        }
    }

    override suspend fun checkIfPhoneExists(phone: String?, bpType: String?): String? {
        val response = repository.checkIfPhoneExists(phone, bpType)


        return if (response is BusinessPartnersVal) {
            when {
                response.value.isEmpty() -> {
                    ""
                }
                else -> "${response.value[0].CardCode} - ${response.value[0].CardName}"
            }
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

}