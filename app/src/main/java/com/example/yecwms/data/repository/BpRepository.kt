package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.businesspartners.BusinessPartnersForPost
import com.example.yecwms.data.remote.services.BusinessPartnersService
import com.example.yecwms.data.remote.services.LoginService.Companion.reLogin
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface BpRepository {
    suspend fun getBps(
        filter: String = "",
        skipValue: Int = 0,
        bpType: String? = null,
        onlyWithDebts: Boolean
    ): Any? //BusinessPartnersVal?

    suspend fun getBpInfo(bpCode: String): Any? //BusinessPartners?
    suspend fun insertNewBp(bpCode: BusinessPartnersForPost): Any? //BusinessPartners?
    suspend fun updateBp(
        bpCode: String,
        bpForUpdate: BusinessPartnersForPost
    ): Any? //BusinessPartners?

    suspend fun checkIfPhoneExists(phone: String?, bpType: String?): Any? //BusinessPartnersVal?
}

class BpRepositoryImpl(
    private val bpService: BusinessPartnersService = BusinessPartnersService.get()
) :
    BpRepository {


    override suspend fun getBps(
        filter: String,
        skipValue: Int,
        bpType: String?,
        onlyWithDebts: Boolean
    ): Any? {

        // IF WE USE SEMANTIC LAYER, WE NEED TO RENAME THE FIELDS tYES to Y / cCustomer to C and so on

        val cardType: String? =
            if (bpType == GeneralConsts.BP_TYPE_CUSTOMER) "(CardType eq '${GeneralConsts.BP_TYPE_CUSTOMER}' or CardType eq '${GeneralConsts.BP_TYPE_LID}')"
            else "CardType eq '${GeneralConsts.BP_TYPE_SUPPLIER}'"


        val onlyDebts = " and CurrentAccountBalance ne 0"

        var filterStringBuilder =
            if (cardType == null) "(contains(CardName, '$filter') or contains(Phone1, '$filter')) and Valid eq 'tYES'"
            else "(contains(CardName, '$filter') or contains(Phone1, '$filter')) and Valid eq 'tYES' and $cardType"


        if (onlyWithDebts) {
            filterStringBuilder += onlyDebts
        }

        Log.d("SML", filterStringBuilder)
        val response = retryIO {
            bpService.getFilteredBps(filter = filterStringBuilder, skipValue = skipValue)
        }


        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }


    override suspend fun getBpInfo(bpCode: String): Any? {
        val response = retryIO { bpService.getBpInfo(bpCode = bpCode) }

        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }


    override suspend fun insertNewBp(bpCode: BusinessPartnersForPost): Any? {
        val response = retryIO { bpService.insertNewBp(bpCode) }

        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun updateBp(bpCode: String, bpForUpdate: BusinessPartnersForPost): Any? {
        val response =
            retryIO {
                bpService.updateBusinessPartner(
                    cardCode = bpCode,
                    body = bpForUpdate
                )
            }
        Log.d("UPDATE RESPONSE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) updateBp(bpCode, bpForUpdate)
                else return error

            } else return error
        }

    }


    override suspend fun checkIfPhoneExists(phone: String?, bpType: String?): Any? {
        val phoneNumber = if (bpType == null) {
            "Phone1 eq '$phone'"
        } else "Phone1 eq '$phone' and CardType eq '$bpType'"

        val response = retryIO { bpService.checkIfPhoneExists(filter = phoneNumber) }

        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

}