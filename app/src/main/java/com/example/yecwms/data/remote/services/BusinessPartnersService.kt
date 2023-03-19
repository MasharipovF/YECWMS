package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.businesspartners.*
import retrofit2.Response
import retrofit2.http.*

interface BusinessPartnersService {

    companion object {

        fun get(): BusinessPartnersService =
            ServiceBuilder.createService(BusinessPartnersService::class.java)
    }


    @GET("BusinessPartners")
    suspend fun getFilteredBps(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Query("\$select") fields: String = "CardCode,CardName,CardType,Currency,GroupCode,CurrentAccountBalance,Phone1,Phone2,ShipToDefault,CreditLimit,MaxCommitment,PriceListNum,Valid,Frozen,Series",
        @Query("\$filter") filter: String = "",
        @Query("\$orderby") order: String = "CardName asc",
        @Query("\$skip") skipValue: Int = 0
    ): Response<BusinessPartnersVal>


    @GET("BusinessPartners")
    suspend fun checkIfPhoneExists(
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean = true,
        @Header("Prefer") maxPage: String = "odata.maxpagesize=1",
        @Query("\$select") fields: String = "CardCode,CardName,Phone1",
        @Query("\$filter") filter: String = ""
    ): Response<BusinessPartnersVal>


    @GET("BusinessPartners('{bpCode}')")
    suspend fun getBpInfo(
        @Path("bpCode") bpCode: String,
        @Query("\$select") fields: String = "CardCode,CardName,CardType,Currency,GroupCode,CurrentAccountBalance,OpenDeliveryNotesBalance,OpenOrdersBalance,FederalTaxID,Phone1,Phone2,ShipToDefault,CreditLimit,MaxCommitment,PriceListNum,Valid,Frozen,Series,BPAddresses",
    ): Response<BusinessPartners>

    @POST("BusinessPartners")
    suspend fun insertNewBp(
        @Body bp: BusinessPartnersForPost
    ): Response<BusinessPartners>

    @PATCH("BusinessPartners('{cardCode}')")
    suspend fun updateBusinessPartner(
        @Path("cardCode") cardCode: String,
        @Body body: BusinessPartnersForPost
    ): Response<Any>


}