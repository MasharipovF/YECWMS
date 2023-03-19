package com.example.yecwms.domain.mappers

import android.util.Log
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.documents.DocumentLinesBinAllocationsForPost
import com.example.yecwms.data.entity.documents.DocumentLinesForPost
import com.example.yecwms.data.entity.inventory.InventoryOperations
import com.example.yecwms.data.entity.inventory.InventoryOperationsLines
import com.example.yecwms.data.entity.inventory.InventoryOperationsLinesForPost
import com.example.yecwms.data.entity.inventorycounting.InventoryCountingLine
import com.example.yecwms.data.entity.inventorycounting.InventoryCountingLineForPost
import com.example.yecwms.data.entity.inventorycounting.InventoryCountings
import com.example.yecwms.data.entity.items.ItemPrices
import com.example.yecwms.data.entity.items.ItemWarehouseInfo
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.items.ItemsCrossJoin
import com.example.yecwms.data.entity.masterdatas.*
import com.example.yecwms.util.Utils
import java.math.BigDecimal


object Mappers {


    fun getDocLinesWithBaseDoc(baseDoc: Document, isPlanFact: Boolean): ArrayList<DocumentLines> {
        val resultList = ArrayList<DocumentLines>()
        for (line in baseDoc.DocumentLines) {
            line.BaseEntry = baseDoc.DocEntry
            line.BaseType = Utils.getObjectCode(baseDoc.DocObjectCode!!)
            line.BaseLine = line.LineNum

            if (line.LineStatus == GeneralConsts.DOC_STATUS_CLOSED || line.RemainingOpenQuantity!! <= 0.0) {
                continue
            } else {

                if (isPlanFact) {
                    line.UserQuantity = 0.0
                    line.Quantity = 0.0
                } else {
                    line.UserQuantity = line.RemainingOpenQuantity
                    line.Quantity = line.RemainingOpenQuantity
                }

                line.MaxQuantity = line.RemainingOpenQuantity
                line.InitialQuantity = line.RemainingOpenQuantity!!
            }

            line.BaseQuantity = line.Quantity
            line.DiscountMain = line.DiscountPercent
            line.InitialPrice = line.Price
            line.UserPriceAfterVAT = line.PriceAfterVAT
            resultList.add(line)
        }
        Log.wtf("MAPPEDLIST", resultList.toString())
        return resultList
    }

    fun getParentDocLines(baseDocLines: List<DocumentLines>): ArrayList<DocumentLines> {
        val resultList = ArrayList<DocumentLines>()

        for (line in baseDocLines) {

            var isRepeatedItem = false
            resultList.forEachIndexed { index, resultLines ->
                if (resultLines.ItemCode == line.ItemCode) {
                    resultLines.InitialQuantity += line.InitialQuantity
                    isRepeatedItem = true
                }
            }

            if (isRepeatedItem) continue

            resultList.add(line)
        }
        return resultList
    }


    fun mapDocLinesToDocLinesForPost(
        docLines: List<DocumentLines>,
        isCopyFrom: Boolean,
        areBatchesIssued: Boolean
    ): ArrayList<DocumentLinesForPost> {
        Log.d("INSERTINVOICE", docLines.toString())
        val resultList = ArrayList<DocumentLinesForPost>()
        for (line in docLines) {


            val updateLine = DocumentLinesForPost(
                LineNum = line.LineNum,
                ItemCode = line.ItemCode,
                ItemName = line.ItemName,
                Quantity = line.Quantity,
                BatchNumbers = line.BatchNumbers,
                PriceAfterVAT = line.UserPriceAfterVAT,
                Price = line.UserPriceAfterVAT,
                UnitPrice = line.UserPriceAfterVAT,
                DiscountPercent = line.DiscountPercent,
                WarehouseCode = line.WarehouseCode
            )

            line.binLocations.forEach {
                val binLine = DocumentLinesBinAllocationsForPost(
                    binAbsEntry = it.binAbsEntry,
                    quantity = it.quantity,
                )
                updateLine.binLocations.add(binLine)
            }


            if (isCopyFrom) {
                updateLine.BaseLine = line.BaseLine
                updateLine.BaseType = line.BaseType
                updateLine.BaseEntry = line.BaseEntry
            } else {
                updateLine.BaseLine = null
                updateLine.BaseType = "-1"
                updateLine.BaseEntry = null
            }

            if (areBatchesIssued) {
                val tempBatchList = line.TempBatchNumbers
                val batchNumbersForPost = arrayListOf<BatchNumbersForPost>()
                for (tempBatchItem in tempBatchList) {
                    val batchForPostItem = BatchNumbersForPost(
                        tempBatchItem.batchNumber,
                        tempBatchItem.selectedQuantity,
                        tempBatchItem.mnfSerial,
                        tempBatchItem.lotNumber
                    )
                    batchNumbersForPost.add(batchForPostItem)
                }
                updateLine.BatchNumbers = batchNumbersForPost
            }

            resultList.add(updateLine)
        }
        Log.d("INSERTINVOICE", resultList.toString())

        return resultList
    }

    fun getParentInventoryDocLines(
        baseDocLines: List<InventoryOperationsLines>,
        fromWarehouse: String? = null,
        toWarehouse: String? = null
    ): ArrayList<InventoryOperationsLines> {
        val resultList = ArrayList<InventoryOperationsLines>()

        for (line in baseDocLines) {

            var isRepeatedItem = false
            resultList.forEachIndexed { index, resultLines ->
                if (resultLines.itemCode == line.itemCode) {
                    resultLines.InitialQuantity += line.InitialQuantity
                    resultLines.fromWarehouse = fromWarehouse ?: resultLines.fromWarehouse
                    resultLines.toWarehouse = toWarehouse ?: resultLines.toWarehouse
                    isRepeatedItem = true
                }
            }

            if (isRepeatedItem) continue

            resultList.add(line)
        }
        return resultList
    }


    fun getInventoryDocLinesWithBaseDoc(
        baseDoc: InventoryOperations,
        isPlanFact: Boolean = false,
        fromWarehouse: String? = null,
        toWarehouse: String? = null
    ): ArrayList<InventoryOperationsLines> {
        Log.d("BASEDOC", baseDoc.toString())

        val resultList = ArrayList<InventoryOperationsLines>()

        for (line in baseDoc.inventoryOperationsLines) {
            line.baseEntry = baseDoc.docEntry
            line.baseType = Utils.getObjectCode(baseDoc.DocObjectCode!!)
            line.baseLine = line.lineNum
            line.MaxQuantity = line.quantity!!
            line.fromWarehouse = fromWarehouse ?: line.fromWarehouse
            line.toWarehouse = toWarehouse ?: line.toWarehouse
            line.InitialQuantity = line.quantity!!
            if (isPlanFact) {
                line.UserQuantity = BigDecimal.ZERO
                line.quantity = BigDecimal.ZERO
            } else {
                line.UserQuantity = line.quantity!!
            }
            resultList.add(line)
        }

        Log.d("BASEDOC", resultList.toString())

        return resultList
    }


    fun mapInventoryDocLinesToInventoryDocLinesForPost(
        docLines: List<InventoryOperationsLines>,
        fromWarehouse: String? = null,
        toWarehouse: String? = null,
        isCopyFrom: Boolean
    ): List<InventoryOperationsLinesForPost> {
        val resultList = ArrayList<InventoryOperationsLinesForPost>()
        for (line in docLines) {
            val updateLine = InventoryOperationsLinesForPost(
                lineNum = line.lineNum,
                itemCode = line.itemCode,
                itemDescription = line.itemDescription,
                quantity = line.UserQuantity,
                toWarehouse = toWarehouse ?: line.toWarehouse,
                fromWarehouse = fromWarehouse ?: line.fromWarehouse
            )



            if (isCopyFrom) {
                updateLine.docEntry = line.docEntry
                updateLine.baseLine = line.baseLine
                updateLine.baseType = line.baseType
                updateLine.baseEntry = line.baseEntry
            } else {
                updateLine.docEntry = null
                updateLine.baseLine = null
                updateLine.baseType = "Default"
                updateLine.baseEntry = null
            }
            resultList.add(updateLine)
        }
        return resultList
    }

    fun getInventoryCountingLinesWithBaseDoc(
        baseDoc: InventoryCountings,
    ): ArrayList<InventoryCountingLine> {
        Log.d("BASEDOC", baseDoc.toString())

        val resultList = ArrayList<InventoryCountingLine>()

        for (line in baseDoc.inventoryCountingLines) {
            line.userCountedQuantity = line.countedQuantity!!
            line.initialCountedQuantity = line.countedQuantity!!
            resultList.add(line)
        }

        Log.d("BASEDOC", resultList.toString())

        return resultList
    }


    fun mapInventoryCountingLinesToInventoryCountingLinesForPost(
        docLines: List<InventoryCountingLine>
    ): List<InventoryCountingLineForPost> {
        val resultList = ArrayList<InventoryCountingLineForPost>()
        for (line in docLines) {

            if (line.inventoryCountingBatchNumbers.isNullOrEmpty()) {
                val updateLine = InventoryCountingLineForPost(
                    itemCode = line.itemCode,
                    itemName = line.itemName,
                    countedQuantity = line.countedQuantity.toInt(),
                )
                resultList.add(updateLine)
            } else {
                line.inventoryCountingBatchNumbers.forEach { batches ->
                    val updateLine = InventoryCountingLineForPost(
                        itemCode = line.itemCode,
                        itemName = line.itemName,
                        countedQuantity = batches.Quantity,
                        batchNumber = batches.BatchNumber
                    )
                    resultList.add(updateLine)
                }
            }

        }
        Log.wtf("BATCHES", resultList.toString())

        return resultList
    }


    fun mapItemsCrossJoinToItems(sourceList: List<ItemsCrossJoin>?): List<Items> {
        val resultList: ArrayList<Items> = arrayListOf()
        if (sourceList != null) {
            for (value in sourceList) {
                val tempItem = value.items
                tempItem.OnHandCurrentWhs = value.itemsItemWarehouseInfoCollection.InStock
                resultList.add(tempItem)
            }
        }
        return resultList
    }

    fun setOnHandByCurrentWarehouse(sourceList: List<Items>?, searchFor: String?): List<Items>? {
        val resultList: ArrayList<Items>? = arrayListOf()
        if (sourceList != null) {

            for (sourceItem in sourceList) {

                val itemWhsInfoList = sourceItem.ItemWarehouseInfoCollection
                for (itemWhsInfo in itemWhsInfoList) {
                    if (itemWhsInfo.WarehouseCode == searchFor) {
                        sourceItem.OnHandCurrentWhs = itemWhsInfo.InStock
                        sourceItem.CommittedCurrentWhs = itemWhsInfo.Committed
                        break
                    }
                }

                resultList?.add(sourceItem)

            }

        }

        return resultList
    }

    fun setPriceFromChosenPricelist(
        sourceList: List<Items>?,
        priceListForSearch: Int?,
        currencyForSearch: String? = Preferences.localCurrency
    ): List<Items> {
        val resultList: ArrayList<Items> = arrayListOf()
        if (sourceList != null) {

            for (sourceItem in sourceList) {

                val itemPricesList = sourceItem.ItemPrices
                for (itemPrice in itemPricesList) {
                    if (itemPrice.priceList == priceListForSearch) {

                        when (currencyForSearch) {
                            itemPrice.currency -> {
                                sourceItem.Price =
                                    itemPrice.price
                                sourceItem.DiscountedPrice =
                                    itemPrice.price
                            }

                            itemPrice.additionalCurrency1 -> {
                                sourceItem.Price =
                                    itemPrice.additionalPrice1
                                sourceItem.DiscountedPrice =
                                    itemPrice.additionalPrice1
                            }

                            itemPrice.additionalCurrency2 -> {
                                sourceItem.Price =
                                    itemPrice.additionalPrice2
                                sourceItem.DiscountedPrice =
                                    itemPrice.additionalPrice2
                            }

                            else -> sourceItem.Price = 0.0
                        }
                        break
                    }
                }
                resultList.add(sourceItem)
            }

        }
        return resultList
    }


    fun mapWhsCodeToWhsName(
        sourceList: List<Warehouses>?,
        resultList: List<ItemWarehouseInfo>?
    ): List<ItemWarehouseInfo> {

        if (sourceList != null && resultList != null) {

            for (itemWhsInfo in resultList) {
                for (whs in sourceList) {
                    Log.d(
                        "WAREHOUSES",
                        "whsResult ${itemWhsInfo.WarehouseCode} \t\twhsMaster: ${whs.WarehouseCode} / ${whs.WarehouseName}"
                    )
                    if (itemWhsInfo.WarehouseCode == whs.WarehouseCode) {
                        itemWhsInfo.WarehouseName = whs.WarehouseName
                        break
                    }

                }

            }

        }
        return resultList as ArrayList<ItemWarehouseInfo>
    }

    fun mapItemGroupCodeToName(source: List<ItemsGroup>?, searchFor: Int?): String {
        var result = ""
        if (source != null) {
            for (value in source) {
                if (searchFor == value.GroupCode) {
                    result = value.GroupName.toString()
                    break
                }
            }
        }
        return result
    }

    fun mapUomGroupCodeToName(source: List<UnitOfMeasurementGroups>?, searchFor: Int?): String {
        var result = ""
        if (source != null) {
            for (value in source) {
                if (searchFor == value.GroupCode) {
                    result = value.GroupName.toString()
                    break
                }
            }
        }
        return result
    }

    fun mapAllUomCodesToNames(
        sourceList: List<UoMGroupDefinitionCollection>?,
        lookFrom: List<UnitOfMeasurement>?
    ): List<UnitOfMeasurement> {
        val resultList = ArrayList<UnitOfMeasurement>()
        if (sourceList != null && lookFrom != null) {

            for (uomInCollection in sourceList) {

                for (uom in lookFrom) {

                    if (uomInCollection.AlternateUoM == uom.UomCode) {
                        resultList.add(UnitOfMeasurement(uom.UomCode, uom.UomName))
                        break
                    }

                }

            }

        }
        return resultList
    }

    fun mapBpGroupCodeToName(source: List<BusinessPartnerGroups>?, searchFor: Int?): String {
        var result = ""
        if (source != null) {
            for (value in source) {
                if (searchFor == value.Code) {
                    result = value.Name.toString()
                    break
                }
            }
        }
        return result
    }

    fun mapPriceListCodeToName(source: List<PriceLists>?, searchFor: Int?): String {
        var result = ""
        if (source != null) {
            for (value in source) {
                if (searchFor == value.priceListNo) {
                    result = value.priceListName
                    break
                }
            }
        }
        return result
    }

    fun mapPricelistToPrice(sourceList: List<ItemPrices>?, searchFor: Int?): Double {
        var result = 0.0
        if (sourceList != null) {
            for (value in sourceList) {
                if (searchFor == value.priceList) {
                    result = value.price
                    break
                }
            }
        }
        return result

    }


    /*
    fun mapBpDebtByShop(sourceList: List<BusinessPartnersDebtForPost>): List<BusinessPartnersDebtByShop> {
        val resultList = ArrayList<BusinessPartnersDebtByShop>()
        resultList.add(
            BusinessPartnersDebtByShop(
                sourceList[0].whsCode,
                sourceList[0].docTotal!! - sourceList[0].paidToDate!!
            )
        )

        for (sourceItem in sourceList) {
            val whs = sourceItem.whsCode
            val debt = sourceItem.multiplyBy!! * (sourceItem.docTotal!! - sourceItem.paidToDate!!)

            if (resultList.isEmpty()) {
                resultList.add(BusinessPartnersDebtByShop(whs, debt))
                continue
            }

            for (resultItem in resultList) {
                if (resultItem.whsCode == whs) {
                    resultItem.debtByShop = resultItem.debtByShop?.plus(debt)
                } else {
                    resultList.add(BusinessPartnersDebtByShop(whs, debt))
                    break
                }
            }
        }
        return resultList
    }

    fun mapBpDebtByShop(
        sourceList: List<BusinessPartnersDebtForPost>,
        whsCode: String
    ): List<BusinessPartnersDebtByShop> {
        var result: BusinessPartnersDebtByShop? = null

        for (sourceItem in sourceList) {
            val whs = sourceItem.whsCode
            val debt = sourceItem.multiplyBy!! * (sourceItem.docTotal!! - sourceItem.paidToDate!!)

            if (whsCode == whs) {
                if (result == null) {
                    result = BusinessPartnersDebtByShop(whs, debt)
                    continue
                }
                result.debtByShop = result.debtByShop?.plus(debt)
            }
        }

        return when (result) {
            null -> emptyList<BusinessPartnersDebtByShop>()
            else -> listOf(result)
        }
    }
    */

}