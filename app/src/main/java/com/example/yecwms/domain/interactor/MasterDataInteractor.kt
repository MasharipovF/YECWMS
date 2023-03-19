package com.example.yecwms.domain.interactor

import android.util.Log
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.masterdatas.*
import com.example.yecwms.data.entity.series.Series
import com.example.yecwms.data.entity.series.SeriesForPost
import com.example.yecwms.data.entity.series.SeriesVal
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse
import com.example.yecwms.domain.mappers.Mappers

interface MasterDataInteractor {
    suspend fun getWarehouses(onlyWithBinLocations: Boolean? = null): List<Warehouses>?
    suspend fun getBinLocations(binCode: String): List<BinLocation>?
    suspend fun getItemsGroups(): List<ItemsGroup>?
    suspend fun getUomGroups(): List<UnitOfMeasurementGroups>?
    suspend fun getUomsOfUomGroup(uomGroup: UnitOfMeasurementGroups?): List<UnitOfMeasurement>?
    suspend fun getPriceLists(): List<PriceLists>?
    suspend fun getBpGroups(bpGroupType: String?): List<BusinessPartnerGroups>?
    suspend fun getLastBarCode(): String?
    suspend fun getExchangeRate(currencyCode: String): Double?
    suspend fun getCurrencies(date: String): List<Currencies>?
    suspend fun getSalesManagers(): List<SalesManagers>?
    suspend fun getSalesManager(managerCode: Long): SalesManagers?
    suspend fun getSeries(params: SeriesForPost): List<Series>?
    suspend fun getDefaultSeries(params: SeriesForPost): Series?
    suspend fun getCompanyInfo(): CompanyInfo?
    var errorMessage: String?
}

class MasterDataInteractorImpl : MasterDataInteractor {

    override var errorMessage: String? = null

    private val repository: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override suspend fun getWarehouses(onlyWithBinLocations: Boolean?): List<Warehouses>? {
        val response = repository.getWarehouses(onlyWithBinLocations)
        return if (response is WarehousesVal) {
            response.value.forEachIndexed { index, warehouse ->
                warehouse.BinLocationsActivated =
                    warehouse.EnableBinLocations.toString() == GeneralConsts.T_YES
            }
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }


    }

    override suspend fun getBinLocations(binCode: String): List<BinLocation>? {
        val response= repository.getBinLocations(binCode)
        return if (response is BinLocationsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }


    }

    override suspend fun getItemsGroups(): List<ItemsGroup>? {
        val response =  repository.getItemsGroups()
        return if (response is ItemsGroupVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getUomGroups(): List<UnitOfMeasurementGroups>? {
        val response= repository.getUomGroups()
        return if (response is UnitOfMeasurementGroupsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getUomsOfUomGroup(uomGroup: UnitOfMeasurementGroups?): List<UnitOfMeasurement>? {
        val response = repository.getUoms()

        return if (response is UnitOfMeasurementVal) {
            if (uomGroup?.GroupCode == -1) {
                response.value
            } else {
                Mappers.mapAllUomCodesToNames(uomGroup?.UoMGroupDefinitionCollection, response.value)
            }
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }


    }

    override suspend fun getPriceLists(): List<PriceLists>? {
        val response =  repository.getPriceLists()
        return if (response is PriceListsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }


    }

    override suspend fun getBpGroups(bpGroupType: String?): List<BusinessPartnerGroups>? {
        val response =  repository.getBpGroups(bpGroupType)
        return if (response is BusinessPartnerGroupsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }

    }

    override suspend fun getLastBarCode(): String? {
        val response = repository.getLastBarCode()
        return if (response is BarCodesVal) {
            if (response.value.isEmpty()) ""
            else response.value[0].Barcode
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getExchangeRate(currencyCode: String): Double? {
        val response = repository.getExchangeRate(currencyCode)
        return if (response is Double) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getCurrencies(date: String): List<Currencies>? {
        val response = repository.getCurrencies(date)
        return if (response is CurrenciesVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getSalesManagers(): List<SalesManagers>? {
        val response = repository.getSalesManagers()
        return if (response is SalesManagersVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getSalesManager(managerCode: Long): SalesManagers? {
        val response = repository.getSalesManager(managerCode)
        return if (response is SalesManagers) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getSeries(params: SeriesForPost): List<Series>? {
        Log.d("SERIES0", params.toString())
        val response = repository.getSeries(params)
        return if (response is SeriesVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getDefaultSeries(params: SeriesForPost): Series? {
        val response = repository.getDefaultSeries(params)
        return if (response is Series) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getCompanyInfo(): CompanyInfo? {
        val response = repository.getCompanyInfo()
        return if (response is CompanyInfo) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

}