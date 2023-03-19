package com.example.yecwms.domain.interactor

import android.graphics.Bitmap
import android.util.Log
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.discount.DiscountByQuantity
import com.example.yecwms.data.entity.discount.DiscountByQuantityVal
import com.example.yecwms.data.entity.items.*
import com.example.yecwms.data.entity.masterdatas.BarCodesVal
import com.example.yecwms.data.entity.masterdatas.ItemsGroupVal
import com.example.yecwms.data.entity.masterdatas.UnitOfMeasurementGroupsVal
import com.example.yecwms.data.entity.masterdatas.WarehousesVal
import com.example.yecwms.data.repository.ItemsRepository
import com.example.yecwms.data.repository.ItemsRepositoryImpl
import com.example.yecwms.data.repository.MasterDataRepository
import com.example.yecwms.data.repository.MasterDataRepositoryImpl
import com.example.yecwms.domain.dto.error.ErrorResponse
import com.example.yecwms.domain.mappers.Mappers

interface ItemsInteractor {

    suspend fun getAllItems(
        filter: String = "",
        priceListCode: Int? = null,
        whsCode: String?,
        onlyValid: Boolean = false
    ): List<Items>?

    suspend fun getMoreItems(
        filter: String = "",
        skipValue: Int,
        priceListCode: Int? = null,
        whsCode: String?,
        onlyValid: Boolean = false
    ): List<Items>?

    suspend fun getItemsViaSML(
        cardCode: String,
        whsCode: String,
        binLocation: String? = null,
        priceListCode: Int,
        date: String,
        filter: String = "",
        skipValue: Int = 0
    ): List<Items>?

    suspend fun getItemBatchesViaSML(
        whsCode: String?,
        itemCode: String,
        filter: String = "",
        skipValue: Int = 0,
        showAllBatches: Boolean = false
    ): List<Items>?

    suspend fun getItemWithDiscountByQuantity(
        cardCode: String? = null,
        itemCode: String,
        lineNum: Int
    ): List<DiscountByQuantity?>?

    suspend fun getItemByBarCode(
        barcode: String,
        priceListCode: Int
    ): List<Items>?


    suspend fun getItemOnHandByWarehouses(
        itemCode: String,
    ): List<ItemsOnHandByWhs>?


    suspend fun getItemOnHandByBinLocations(
        itemCode: String,
        whsCode: String? = null,
        binLocationsFilter: String? = null,
        onlyPositiveStock: Boolean = true,
        skipValue: Int = 0
    ): List<ItemsOnHandByBinLocations>?

    suspend fun getItemsListOnHandByBinLocation(
        itemFilter: String? = null,
        whsCode: String,
        binLocationEntry: Int,
        onlyPositiveStock: Boolean = true,
        skipValue: Int = 0
    ): List<ItemsOnHandByBinLocations>?

    suspend fun getItemsListOnHandByWarehouse(
        whsCode: String?,
        filter: String?
    ): List<ItemsOnHandByWhs>?

    suspend fun getItemInfo(itemcode: String): Items?
    suspend fun getItemImage(itemcode: String): Bitmap?
    suspend fun addNewItem(item: ItemsForPost): Items?
    suspend fun checkIfBarCodeExists(barcode: String): Boolean?
    var errorMessage: String?

}

class ItemsInteractorImpl : ItemsInteractor {

    private val repository: ItemsRepository by lazy { ItemsRepositoryImpl() }
    private val masterDataRepo: MasterDataRepository by lazy { MasterDataRepositoryImpl() }

    override var errorMessage: String? = null

    override suspend fun getAllItems(
        filter: String,
        priceListCode: Int?,
        whsCode: String?,
        onlyValid: Boolean
    ): List<Items>? {

        val response = if (priceListCode != null) {
            repository.getItems(filter = filter, withPrices = true, onlyValid = onlyValid)
        } else {
            repository.getItems(filter = filter, onlyValid = onlyValid)
        }

        return if (response is ItemsVal) {
            var items = response.items

            if (whsCode != null) {
                items = Mappers.setOnHandByCurrentWarehouse(items, whsCode)!!
                if (priceListCode != null) {
                    items = Mappers.setPriceFromChosenPricelist(items, priceListCode)
                }
                Log.d("DEFAULTWHS", whsCode)
            }
            items
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }

    }

    override suspend fun getMoreItems(
        filter: String,
        skipValue: Int,
        priceListCode: Int?,
        whsCode: String?,
        onlyValid: Boolean
    ): List<Items>? {
        val response = if (priceListCode != null) {
            repository.getItems(
                filter = filter,
                skipValue = skipValue,
                withPrices = true,
                onlyValid = onlyValid
            )
        } else {
            repository.getItems(
                filter = filter,
                skipValue = skipValue,
                onlyValid = onlyValid
            )
        }


        return if (response is ItemsVal) {
            var items = response.items

            if (whsCode != null) {
                items = Mappers.setOnHandByCurrentWarehouse(items, whsCode)!!
                if (priceListCode != null) {
                    items = Mappers.setPriceFromChosenPricelist(items, priceListCode)
                }
                Log.d("DEFAULTWHS", whsCode)
            }
            items
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }

    }

    override suspend fun getItemsViaSML(
        cardCode: String,
        whsCode: String,
        binLocation: String?,
        priceListCode: Int,
        date: String,
        filter: String,
        skipValue: Int
    ): List<Items>? {
        val response =
            repository.getItemsViaSML(
                cardCode = cardCode,
                whsCode = whsCode,
                binLocation = binLocation,
                date = date,
                priceListCode = priceListCode,
                filter = filter,
                skipValue = skipValue
            )

        return if (response is ItemsVal) {
            val items = response.items

            items.forEach {
                it.DiscountedPrice =
                    (it.Price * (100 - it.DiscountApplied)) / 100
                it.Price = it.Price
            }

            items
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getItemBatchesViaSML(
        whsCode: String?,
        itemCode: String,
        filter: String,
        skipValue: Int,
        showAllBatches: Boolean
    ): List<Items>? {
        val response =
            repository.getItemBatchesViaSML(
                whsCode = whsCode,
                itemCode = itemCode,
                filter = filter,
                skipValue = skipValue,
                showAllBatches = showAllBatches
            )

        return if (response is ItemsVal) {
            val items = response.items

            items.forEach {
                it.DiscountedPrice =
                    (it.Price * (100 - it.DiscountApplied)) / 100
                it.Price = it.Price
            }

            items
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getItemWithDiscountByQuantity(
        cardCode: String?,
        itemCode: String,
        lineNum: Int
    ): List<DiscountByQuantity?>? {
        val response =
            repository.getItemWithDiscountByQuantity(
                cardCode = cardCode,
                itemCode = itemCode,
                lineNum = lineNum
            )

        return if (response is DiscountByQuantityVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


    override suspend fun getItemOnHandByWarehouses(itemCode: String): List<ItemsOnHandByWhs>? {
        val response =
            repository.getItemOnHandByWarehouses(
                itemCode = itemCode,
            )
        return if (response is ItemsOnHandByWhsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


    override suspend fun getItemsListOnHandByBinLocation(
        itemFilter: String?,
        whsCode: String,
        binLocationEntry: Int,
        onlyPositiveStock: Boolean,
        skipValue: Int
    ): List<ItemsOnHandByBinLocations>? {
        val response =
            repository.getItemsListOnHandByBinLocation(
                itemFilter = itemFilter,
                whsCode = whsCode,
                binLocationEntry = binLocationEntry,
                onlyPositiveStock = onlyPositiveStock,
                skipValue = skipValue
            )

        Log.d("RESPONSEINTERACTOR", response.toString())
        return if (response is ItemsOnHandByBinLocationsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getItemOnHandByBinLocations(
        itemCode: String,
        whsCode: String?,
        binLocationsFilter: String?,
        onlyPositiveStock: Boolean,
        skipValue: Int
    ): List<ItemsOnHandByBinLocations>? {
        val response =
            repository.getItemOnHandByBinLocations(
                itemCode = itemCode,
                whsCode = whsCode,
                binLocationFilter = binLocationsFilter,
                onlyPositiveStock = onlyPositiveStock,
                skipValue = skipValue
            )

        Log.d("RESPONSEINTERACTOR", response.toString())
        return if (response is ItemsOnHandByBinLocationsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getItemsListOnHandByWarehouse(
        whsCode: String?,
        filter: String?
    ): List<ItemsOnHandByWhs>? {
        val response =
            repository.getItemsListOnHandByWarehouse(
                whsCode = whsCode,
                filter = filter
            )
        return if (response is ItemsOnHandByWhsVal) {
            response.value
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun getItemByBarCode(barcode: String, priceListCode: Int): List<Items>? {
        val response = repository.getItemByBarCode(barcode)

        return if (response is BarCodesVal) {
            var barcodeResponse = response.value
            if (barcodeResponse.isNotEmpty()) {
                Log.d("BARCODE", "FOUND BARCODE ${barcodeResponse[0].ItemCode!!}")
                return mapBarcodeWithItemInfo(barcodeResponse[0].ItemCode!!, priceListCode)
            } else {
                errorMessage = "По запросу ничего не найдено"
                null
            }
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }

    }


    private suspend fun mapBarcodeWithItemInfo(itemcode: String, priceListCode: Int): List<Items>? {
        val resultList = arrayListOf<Items>()
        val response = repository.getItemsWithPricesSQLQuery(
            itemCode = itemcode,
            whsCode = Preferences.defaultWhs!!,
            priceListCode = priceListCode
        )

        Log.d("BARCODE", response.toString())

        return if (response is ItemsSQLQueryVal) {
            response.value!!.forEach {
                val item = Items(
                    ItemName = it!!.itemName,
                    TotalOnHand = it.totalOnHand!!,
                    OnHandCurrentWhs = it.onHand!!,
                    SalesUnit = it.salesUnitMeasure,
                    Price = it.price!!
                )
                resultList.add(item)
            }
            resultList
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }


    override suspend fun getItemInfo(itemcode: String): Items? {
        val result = repository.getItemInfo(itemcode)
        val whs = masterDataRepo.getWarehouses()
        if (whs is WarehousesVal) {
            result?.ItemWarehouseInfoCollection =
                Mappers.mapWhsCodeToWhsName(whs.value, result?.ItemWarehouseInfoCollection)
        } else {
            errorMessage = (whs as ErrorResponse).error.message.value
            return null
        }

        val itemsGroups = masterDataRepo.getItemsGroups()
        if (itemsGroups is ItemsGroupVal) {
            result?.ItemsGroupName =
                Mappers.mapItemGroupCodeToName(itemsGroups.value, result?.ItemsGroupCode)
        } else {
            errorMessage = (itemsGroups as ErrorResponse).error.message.value
            return null
        }

        val uomGroups = masterDataRepo.getUomGroups()
        if (uomGroups is UnitOfMeasurementGroupsVal) {
            result?.UoMGroupName =
                Mappers.mapUomGroupCodeToName(uomGroups.value, result?.UoMGroupEntry)
        } else {
            errorMessage = (uomGroups as ErrorResponse).error.message.value
            return null
        }

        return result
    }

    override suspend fun getItemImage(itemcode: String): Bitmap? {
        val response = repository.getItemImage(itemcode)
        return if (response is Bitmap) {
            response
        } else {
            errorMessage = (response as ErrorResponse).error.message.value
            null
        }
    }

    override suspend fun addNewItem(item: ItemsForPost): Items? {
        return repository.addNewItem(item)
    }

    override suspend fun checkIfBarCodeExists(barcode: String): Boolean? {
        val response = repository.checkIfBarCodeExists(barcode)?.value
        return when {
            response == null -> {
                null
            }
            response.isEmpty() -> {
                false
            }
            else -> true
        }
    }

}