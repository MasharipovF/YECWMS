package com.example.yecwms.data.repository

import android.graphics.BitmapFactory
import android.util.Log
import com.example.yecwms.core.ErrorCodeEnums
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.CrossJoin
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.items.ItemsCrossJoinVal
import com.example.yecwms.data.entity.items.ItemsForPost
import com.example.yecwms.data.entity.items.ItemsVal
import com.example.yecwms.data.entity.masterdatas.BarCodesVal
import com.example.yecwms.data.remote.services.ItemsService
import com.example.yecwms.data.remote.services.LoginService
import com.example.yecwms.domain.dto.login.LoginRequestDto
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.Utils
import com.example.yecwms.util.retryIO


interface ItemsRepository {
    suspend fun getItems(
        filter: String = "",
        skipValue: Int = 0,
        withPrices: Boolean = false,
        onlyValid: Boolean = false
    ): ItemsVal?

    suspend fun getItemsViaSML(
        cardCode: String,
        whsCode: String,
        binLocation: String? = null,
        date: String,
        priceListCode: Int,
        filter: String = "",
        skipValue: Int = 0
    ): Any?

    suspend fun getItemBatchesViaSML(
        whsCode: String?,
        itemCode: String,
        filter: String = "",
        skipValue: Int = 0,
        showAllBatches: Boolean
    ): Any?

    suspend fun getItemWithDiscountByQuantity(
        cardCode: String? = null,
        itemCode: String,
        lineNum: Int
    ): Any?


    suspend fun getItemsCrossJoin(
        filters: List<String> = listOf(),
        skipValue: Int = 0,
        whsCode: String,
        onlyValid: Boolean = false
    ): ItemsCrossJoinVal?


    suspend fun getItemsWithPricesSQLQuery(
        itemCode: String,
        whsCode: String,
        priceListCode: Int
    ): Any?

    suspend fun getItemByBarCode(
        barcode: String
    ): Any?

    suspend fun getItemOnHandByWarehouses(
        itemCode: String
    ): Any?


    suspend fun getItemOnHandByBinLocations(
        itemCode: String,
        whsCode: String? = null,
        binLocationFilter: String? = null,
        onlyPositiveStock: Boolean = true,
        skipValue: Int = 0
    ): Any?

    suspend fun getItemsListOnHandByBinLocation(
        itemFilter: String? = null,
        whsCode: String,
        binLocationEntry: Int,
        onlyPositiveStock: Boolean = true,
        skipValue: Int = 0
    ): Any?


    suspend fun getItemsListOnHandByWarehouse(
        whsCode: String? = null,
        filter: String? = null
    ): Any?

    suspend fun getItemInfo(itemcode: String): Items?
    suspend fun getItemImage(itemcode: String): Any?
    suspend fun addNewItem(item: ItemsForPost): Items?
    suspend fun checkIfBarCodeExists(barcode: String): BarCodesVal?
}

class ItemsRepositoryImpl(
    private val itemsService: ItemsService = ItemsService.get(),
    private val loginService: LoginService = LoginService.get()

) :
    ItemsRepository {


    override suspend fun getItems(
        filter: String,
        skipValue: Int,
        withPrices: Boolean,
        onlyValid: Boolean
    ): ItemsVal? {

        val filterStringBuilder =
            if (onlyValid) "(contains(ItemCode, '$filter') or contains(ItemName, '$filter') or contains(ForeignName, '$filter') or contains(BarCode,'$filter')) and ItemType eq 'itItems' and Valid eq 'tYES'"
            else "(contains(ItemCode, '$filter') or contains(ItemName, '$filter') or contains(ForeignName, '$filter') or contains(BarCode,'$filter')) and ItemType eq 'itItems'"

        val response = retryIO {
            if (withPrices)
                itemsService.getFilteredItemsWithPrices(
                    filter = filterStringBuilder,
                    skipValue = skipValue
                )
            else
                itemsService.getFilteredItems(filter = filterStringBuilder, skipValue = skipValue)
        }

        Log.d("ITEMS", "response  " + response.toString())

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.d(
                "USERDEFAULTS",
                response.errorBody()!!.string()
            )
            null
        }

    }

    override suspend fun getItemBatchesViaSML(
        whsCode: String?,
        itemCode: String,
        filter: String,
        skipValue: Int,
        showAllBatches: Boolean
    ): Any? {

        Log.wtf("ITEMSVIASML", filter.toString())

        var filterStringBuilder = "ItemCode eq '$itemCode' and contains(BatchNumber, '$filter')"
        if (showAllBatches) filterStringBuilder+=" and QuantityOnStockByBatch gt 0"


        val response = retryIO {
            itemsService.getFilteredItemsViaSML(
                cardcode = "n",
                whscode = whsCode ?: Preferences.defaultWhs.toString(),
                pricelist = 1,
                date = Utils.getCurrentDateinUSAFormat(),
                filter = filterStringBuilder,
                skipValue = skipValue,
                maxPage = if (showAllBatches) "odata.maxpagesize=100000" else null
            )

        }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun getItemsViaSML(
        cardCode: String,
        whsCode: String,
        binLocation: String?,
        date: String,
        priceListCode: Int,
        filter: String,
        skipValue: Int
    ): Any? {

        //TODO ITEM TYPE IS ITEM

        var filterStringBuilder =
            "(contains(ItemCode, '$filter') or contains(ItemName, '$filter') or contains(ForeignName, '$filter') or contains(BatchNumber, '$filter') or contains(BarCode,'$filter'))"

        if (binLocation != null) {
            filterStringBuilder += " and BinCode eq '$binLocation'"
        }

        val response = retryIO {
            itemsService.getFilteredItemsViaSML(
                cardcode = cardCode,
                whscode = whsCode,
                pricelist = priceListCode,
                date = date,
                filter = filterStringBuilder,
                skipValue = skipValue
            )

        }

        Log.wtf("ITEMSVIASML", response.toString())

        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }


    override suspend fun getItemWithDiscountByQuantity(
        cardCode: String?,
        itemCode: String,
        lineNum: Int
    ): Any? {


        val response = retryIO {
            itemsService.getItemWithDiscountByQuantity(
                cardcode = cardCode ?: "*1",
                itemcode = itemCode,
                linenum = lineNum
            )
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }


    override suspend fun getItemsCrossJoin(
        filters: List<String>,
        skipValue: Int,
        whsCode: String,
        onlyValid: Boolean
    ): ItemsCrossJoinVal? {

        var itemsFilterString = ""
        var resultList = arrayListOf<DocumentLines>()

        filters.forEachIndexed { index, item ->
            itemsFilterString += "Items/ItemCode eq '${item}'"
            if (index < filters.size - 1 && itemsFilterString.isNotEmpty()) itemsFilterString += " or "
        }

        Log.d("SALESORDER", "filterstring: $itemsFilterString")

        val queryPath = "\$crossjoin(Items,Items/ItemWarehouseInfoCollection)"
        val expand =
            "\$expand=Items(\$select=ItemCode),Items/ItemWarehouseInfoCollection(\$select=WarehouseCode,InStock,Committed)"

        val filterString =
            if (onlyValid) "\$filter=Items/ItemCode eq Items/ItemWarehouseInfoCollection/ItemCode and Items/ItemWarehouseInfoCollection/WarehouseCode eq '$whsCode' and Items/ItemType eq 'itItems' and Items/Valid eq 'tYES' and ($itemsFilterString)"
            else "\$filter=Items/ItemCode eq Items/ItemWarehouseInfoCollection/ItemCode and Items/ItemWarehouseInfoCollection/WarehouseCode eq '$whsCode' and Items/ItemType eq 'itItems' and ($itemsFilterString)"

        val skip = "\$skip=$skipValue"
        val queryOption = "$expand&$filterString&$skip"

        val response = retryIO {
            itemsService.getFilteredItemsCrossJoin(
                body = CrossJoin(
                    queryOption = queryOption,
                    queryPath = queryPath
                )
            )
        }

        return if (response.isSuccessful) {
            response.body() as ItemsCrossJoinVal
        } else {
            Log.d(
                "USERDEFAULTS",
                response.errorBody()!!.string()
            )
            null
        }
    }

    /*override suspend fun getAllItemsByWhss(whsCode: String): Any? {

        val response = retryIO { itemsService.getAllItemsOnHandByWhs(filter = "WhsCode eq '$whsCode'") }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getAllItemsByWhss(whsCode)
                else return error

            } else return error
        }
    }*/

    override suspend fun getItemsWithPricesSQLQuery(
        itemCode: String,
        whsCode: String,
        priceListCode: Int
    ): Any? {
        Log.d("BARCODE", "ITEM: $itemCode, whs: $whsCode, price: $priceListCode")
        val response = retryIO {
            itemsService.getAllItemsOnHandByWhsAndPrice(
                itemCode = "'$itemCode'",
                whsCode = "'$whsCode'",
                priceListCode = priceListCode
            )
        }
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getItemsWithPricesSQLQuery(itemCode, whsCode, priceListCode)
                else return error

            } else return error
        }
    }

    override suspend fun getItemByBarCode(barcode: String): Any? {
        val filter = "Barcode eq '$barcode'"

        val response = retryIO {
            itemsService.getItemByBarCode(
                filter = filter
            )
        }
        Log.d("BARCODE", response.toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getItemByBarCode(barcode)
                else return error

            } else return error
        }
    }

    override suspend fun getItemOnHandByWarehouses(itemCode: String): Any? {
        val filterStringBuilder =
            "ItemCode eq '$itemCode' or contains(BarCode,'$itemCode')"

        val response =
            retryIO { itemsService.getItemOnHandByWarehouses(filter = filterStringBuilder) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.d("RESPONSE", response.errorBody()!!.string())
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getItemOnHandByWarehouses(itemCode)
                else return error

            } else return error
        }
    }


    override suspend fun getItemOnHandByBinLocations(
        itemCode: String,
        whsCode: String?,
        binLocationFilter: String?,
        onlyPositiveStock: Boolean,
        skipValue: Int
    ): Any? {


        var filterStringBuilder = "ItemCode eq '$itemCode'"

        filterStringBuilder += if (whsCode != null) "and WhsCode eq '$whsCode'" else ""


        filterStringBuilder += if (binLocationFilter != null) " and contains(BinCode, '$binLocationFilter')" else ""


        filterStringBuilder += if (onlyPositiveStock) " and OnHandQty gt 0" else ""


        Log.d("RESPONSE", filterStringBuilder)

        val response =
            retryIO {
                itemsService.getItemOnHandByBinLocations(
                    filter = filterStringBuilder,
                    skipValue = skipValue
                )
            }

        Log.d("RESPONSE", response.toString())

        return if (response.isSuccessful) {
            Log.d("RESPONSE", "SUCCESS" + response.body()!!.toString())

            response.body()
        } else {
            Log.d("RESPONSE", " ERROR" + response.errorBody()!!.string())
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getItemOnHandByBinLocations(
                    itemCode,
                    whsCode,
                    binLocationFilter,
                    onlyPositiveStock,
                    skipValue
                )
                else return error

            } else return error
        }
    }

    override suspend fun getItemsListOnHandByBinLocation(
        itemFilter: String?,
        whsCode: String,
        binLocationEntry: Int,
        onlyPositiveStock: Boolean,
        skipValue: Int
    ): Any? {


        var filterStringBuilder = "WhsCode eq '$whsCode' and BinAbsEntry eq $binLocationEntry"

        filterStringBuilder += if (itemFilter != null) " and (contains(ItemName, '$itemFilter') or contains(BarCode, '$itemFilter'))" else ""

        filterStringBuilder += if (onlyPositiveStock) " and OnHandQty gt 0" else ""


        Log.d("RESPONSE", filterStringBuilder)

        val response =
            retryIO {
                itemsService.getItemOnHandByBinLocations(
                    filter = filterStringBuilder,
                    skipValue = skipValue
                )
            }

        Log.d("RESPONSE", response.toString())

        return if (response.isSuccessful) {
            Log.d("RESPONSE", "SUCCESS" + response.body()!!.toString())
            response.body()
        } else {
            Log.d("RESPONSE", " ERROR" + response.errorBody()!!.string())
            val error = ErrorUtils.errorProcess(response)
            if (error?.error?.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getItemsListOnHandByBinLocation(
                    itemFilter,
                    whsCode,
                    binLocationEntry,
                    onlyPositiveStock,
                    skipValue
                )
                else return error

            } else return error
        }
    }


    override suspend fun getItemsListOnHandByWarehouse(
        whsCode: String?,
        filter: String?
    ): Any? {

        var filterStringBuilder: String? = if (whsCode == null && filter == null) null else ""

        if (whsCode != null)
            filterStringBuilder = "WhsCode eq '$whsCode'"

        if (whsCode != null && filter != null)
            filterStringBuilder += " and "

        if (filter != null)
            filterStringBuilder += "(contains(ItemName, '$filter') or contains(BarCode, '$filter'))"

        Log.wtf("ITEMSLIST", filterStringBuilder)
        val response =
            retryIO { itemsService.getItemOnHandByWarehouses(filter = filterStringBuilder) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.d("RESPONSE", response.errorBody()!!.string())
            val error = ErrorUtils.errorProcess(response)
            if (error.error.code == ErrorCodeEnums.SESSION_TIMEOUT.code) {

                val isLoggedIn = reLogin()
                if (isLoggedIn) getItemsListOnHandByWarehouse(whsCode, filter)
                else return error

            } else return error
        }
    }


    override suspend fun getItemInfo(itemcode: String): Items? {
        val response = retryIO {
            itemsService.getItemInfo(itemcode = itemcode)
        }

        return if (response.isSuccessful) {
            response.body()
        } else {

            Log.d(
                "USERDEFAULTS",
                response.errorBody()!!.string()
            )
            null
        }
    }

    override suspend fun getItemImage(itemcode: String): Any? {
        val response = retryIO {
            itemsService.getItemImage(itemcode = itemcode)
        }

        return if (response.isSuccessful) {
            Log.wtf("IMAGE", response.body().toString())
            Log.wtf("IMAGEBYTE", response.body()?.byteStream().toString())
            val bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
            bmp
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }

    override suspend fun addNewItem(item: ItemsForPost): Items? {
        val response = retryIO { itemsService.addNewItem(item) }
        if (response.isSuccessful) {
            Log.d("ITEMINSERT", response.body().toString())
        } else Log.d("ITEMINSERTERROR", response.errorBody()!!.string())
        return response.body()
    }

    override suspend fun checkIfBarCodeExists(barcode: String): BarCodesVal? {
        val barcode = "Barcode eq '$barcode'"
        val response = retryIO { itemsService.checkIfPhoneBarCodeExists(filter = barcode) }
        return if (response.isSuccessful) {
            Log.d(
                "BPDEFAULTS",
                response.body().toString()
            )
            response.body()

        } else {
            Log.d(
                "BPDEFAULTS",
                response.errorBody()!!.string()
            )
            null
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