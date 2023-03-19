package com.example.yecwms.data.entity.masterdatas

import com.example.yecwms.util.Utils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Warehouse master data
 */
data class WarehousesVal(
    @SerializedName("value")
    @Expose
    var value: List<Warehouses>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class Warehouses(
    @SerializedName("WarehouseCode")
    @Expose
    var WarehouseCode: String? = null,

    @SerializedName("WarehouseName")
    @Expose
    var WarehouseName: String? = null,

    @SerializedName("EnableBinLocations")
    @Expose
    var EnableBinLocations: String? = null,

    var BinLocationsActivated: Boolean = false,

    @SerializedName("DefaultBin")
    @Expose
    var DefaultBinAbsEntry: Int? = null
)

/**
 * Bin Locations master data
 */
data class BinLocationsVal(
    @SerializedName("value")
    val value: List<BinLocation>
)

data class BinLocation(
    @SerializedName("AbsEntry")
    val absEntry: Int,
    @SerializedName("BarCode")
    val barCode: String?,
    @SerializedName("BinCode")
    val binCode: String,
    @SerializedName("Warehouse")
    val warehouse: String,
    var defaultBinEntry: Int = 860
)


/**
 * Item Groups master data
 */

data class ItemsGroupVal(
    @SerializedName("value")
    @Expose
    var value: List<ItemsGroup>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)


data class ItemsGroup(
    @SerializedName("Number")
    @Expose
    var GroupCode: Int = -1,

    @SerializedName("GroupName")
    @Expose
    var GroupName: String? = null
)

/**
 * Unit of measurement master data
 */

data class UnitOfMeasurementGroupsVal(
    @SerializedName("value")
    @Expose
    var value: List<UnitOfMeasurementGroups>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class UnitOfMeasurementGroups(
    @SerializedName("AbsEntry")
    @Expose
    var GroupCode: Int = -1,

    @SerializedName("Name")
    @Expose
    var GroupName: String? = null,

    @SerializedName("BaseUoM")
    @Expose
    var BaseUoM: String? = null,

    @SerializedName("UoMGroupDefinitionCollection")
    @Expose
    var UoMGroupDefinitionCollection: List<UoMGroupDefinitionCollection> = listOf()

)

data class UoMGroupDefinitionCollection(
    @SerializedName("AlternateUoM")
    @Expose
    var AlternateUoM: Int = -1,

    @SerializedName("AlternateQuantity")
    @Expose
    var AlternateQuantity: Int = 0,

    @SerializedName("BaseQuantity")
    @Expose
    var BaseQuantity: Int = 0,

    @SerializedName("Active")
    @Expose
    var Active: String? = null
)


data class UnitOfMeasurementVal(
    @SerializedName("value")
    @Expose
    var value: List<UnitOfMeasurement>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class UnitOfMeasurement(
    @SerializedName("AbsEntry")
    @Expose
    var UomCode: Int = -1,

    @SerializedName("Name")
    @Expose
    var UomName: String? = null
)


/**
 * Business pertners group codes
 */

data class BusinessPartnerGroupsVal(
    @SerializedName("value")
    @Expose
    var value: List<BusinessPartnerGroups>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class BusinessPartnerGroups(
    @SerializedName("Code")
    @Expose
    var Code: Int = -1,

    @SerializedName("Name")
    @Expose
    var Name: String? = null,

    @SerializedName("Type")
    @Expose
    var Type: String? = null
)


/**
 * Business pertners group codes
 */

data class PriceListsVal(
    @SerializedName("value")
    @Expose
    var value: List<PriceLists>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class PriceLists(
    @SerializedName("Active")
    var active: String = "",

    @SerializedName("BasePriceList")
    var basePriceList: Int = 0,

    @SerializedName("DefaultAdditionalCurrency1")
    var defaultAdditionalCurrency1: String = "",

    @SerializedName("DefaultAdditionalCurrency2")
    var defaultAdditionalCurrency2: String = "",

    @SerializedName("DefaultPrimeCurrency")
    var defaultPrimeCurrency: String = "",

    @SerializedName("Factor")
    var factor: Double = 0.0,

    @SerializedName("FixedAmount")
    var fixedAmount: Double = 0.0,

    @SerializedName("GroupNum")
    var groupNum: String = "",

    @SerializedName("IsGrossPrice")
    var isGrossPrice: String = "",

    @SerializedName("PriceListName")
    var priceListName: String = "",

    @SerializedName("PriceListNo")
    var priceListNo: Int = 0,

    @SerializedName("RoundingMethod")
    var roundingMethod: String = "",

    @SerializedName("RoundingRule")
    var roundingRule: String = "",

    @SerializedName("ValidFrom")
    var validFrom: String = "",

    @SerializedName("ValidTo")
    var validTo: String = ""
)

/***
BarCodes
 */


data class BarCodesVal(
    @SerializedName("value")
    @Expose
    var value: List<BarCodes>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class BarCodes(
    @SerializedName("AbsEntry")
    @Expose
    var AbsEntry: String? = null,

    @SerializedName("UoMEntry")
    @Expose
    var UoMEntry: String? = null,

    @SerializedName("Barcode")
    @Expose
    var Barcode: String? = null,

    @SerializedName("ItemNo")
    @Expose
    var ItemCode: String? = null,

    @SerializedName("FreeText")
    @Expose
    var FreeText: String? = null
)

/**
 * EXCHANGE RATES
 */

data class ExchangeRates(
    @SerializedName("Currency")
    var currency: String,
    @SerializedName("Date")
    var date: String = Utils.getCurrentDateinUSAFormat()
)


data class CurrenciesVal(
    @SerializedName("value")
    var value: List<Currencies>,
    @SerializedName("odata.nextLink")
    var nextlink: String
)

data class Currencies(
    @SerializedName("Code")
    var code: String,
    @SerializedName("Name")
    var name: String,
    @SerializedName("Rate")
    var rate: Double? = null
)
