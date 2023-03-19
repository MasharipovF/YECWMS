package com.example.yecwms.data.remote.services

import com.example.yecwms.core.ServiceBuilder
import com.example.yecwms.data.entity.series.Series
import com.example.yecwms.data.entity.series.SeriesForPost
import com.example.yecwms.data.entity.series.SeriesVal
import com.example.yecwms.data.entity.masterdatas.*
import retrofit2.Response
import retrofit2.http.*

interface MasterDataServices {

    companion object {
        fun get(): MasterDataServices = ServiceBuilder.createService(MasterDataServices::class.java)
    }

    @GET("Warehouses")
    suspend fun getWarehouses(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Query("\$select") fields: String = "WarehouseCode,WarehouseName,EnableBinLocations,DefaultBin",
        @Query("\$filter") filter: String? = null,
        @Query("\$orderby") ordderby: String? = "WarehouseName asc"
    ): Response<WarehousesVal>


    @GET("BinLocations")
    suspend fun getBinLocations(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Query("\$select") fields: String = "AbsEntry,BarCode,BinCode,Warehouse",
        @Query("\$filter") filter: String? = null,
        @Query("\$orderby") ordderby: String? = "BinCode asc"
    ): Response<BinLocationsVal>


    @GET("UnitOfMeasurementGroups")
    suspend fun getUnitOfMeasureGroups(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Query("\$select") fields: String = "AbsEntry,Name,BaseUoM,UoMGroupDefinitionCollection"
    ): Response<UnitOfMeasurementGroupsVal>

    @GET("ItemGroups")
    suspend fun getItemsGroup(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Query("\$select") fields: String = "Number,GroupName"
    ): Response<ItemsGroupVal>

    @GET("UnitOfMeasurements")
    suspend fun getUnitOfMeasures(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Query("\$select") fields: String = "AbsEntry,Name"
    ): Response<UnitOfMeasurementVal>

    @GET("PriceLists")
    suspend fun getPriceLists(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
    ): Response<PriceListsVal>

    @GET("BusinessPartnerGroups")
    suspend fun getBpGroups(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Query("\$filter") filter: String = "Type eq 'bbpgt_VendorGroup' or Type eq 'bbpgt_CustomerGroup'"
    ): Response<BusinessPartnerGroupsVal>

    @GET("SalesPersons")
    suspend fun getSalesManagers(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=5000",
        @Query("\$select") fields: String = "SalesEmployeeCode,SalesEmployeeName",
        @Query("\$filter") filter: String
    ): Response<SalesManagersVal>


    @GET("SalesPersons({managerCode})")
    suspend fun getSalesManager(
        @Path("managerCode") managerCode: Long,
        @Query("\$select") fields: String = "SalesEmployeeCode,SalesEmployeeName"
    ): Response<SalesManagers>

    @GET("BarCodes")
    suspend fun getLastBarCode(
        @Query("\$filter") filter: String = "startswith(Barcode, '2')",
        @Query("\$orderby") orderby: String = "Barcode desc",
        @Query("\$top") top: String = "1"
    ): Response<BarCodesVal>


    @POST("SBOBobService_GetCurrencyRate")
    suspend fun getExchangeRate(
        @Body body: ExchangeRates
    ): Response<Double>

    @GET("sml.svc/CURRENCIESParameters(P_Date='{date}')/CURRENCIES")
    suspend fun getCurrencies(
        @Header("Prefer") maxPage: String = "odata.maxpagesize=10000",
        @Path("date") date: String,
    ): Response<CurrenciesVal>

    @POST("SeriesService_GetDocumentSeries")
    suspend fun getSeries(
        @Body body: SeriesForPost
    ): Response<SeriesVal>

    @POST("SeriesService_GetDefaultSeries")
    suspend fun getDefaultSeries(
        @Body body: SeriesForPost
    ): Response<Series>

    @POST("CompanyService_GetAdminInfo")
    suspend fun getCompanyInfo(): Response<CompanyInfo>


}