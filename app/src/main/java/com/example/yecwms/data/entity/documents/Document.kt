package com.example.yecwms.data.entity.documents

import android.graphics.Bitmap
import android.os.Parcelable
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.example.yecwms.data.entity.discount.DiscountByQuantity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DocumentsVal(
    @SerializedName("value")
    @Expose
    var value: List<Document>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class Document(

    @SerializedName("DocEntry")
    @Expose
    var DocEntry: Long? = null,

    @SerializedName("DocNum")
    @Expose
    var DocNum: String? = null,

    @SerializedName("BPL_IDAssignedToInvoice")
    var BPL_ID: String? = null,

    @SerializedName("NumAtCard")
    @Expose
    var NumAtCard: String? = null,

    @SerializedName("DocDate")
    @Expose
    var DocDate: String? = null,

    @SerializedName("DocTime")
    @Expose
    var DocTime: String? = null,

    @SerializedName("DocDueDate")
    @Expose
    var DocDueDate: String? = null,

    @SerializedName("UpdateTime")
    @Expose
    var UpdateTime: String? = null,

    @SerializedName("UpdateDate")
    @Expose
    var UpdateDate: String? = null,

    @SerializedName("CardCode")
    @Expose
    var CardCode: String? = null,

    @SerializedName("CardName")
    @Expose
    var CardName: String? = null,

    @SerializedName("U_phone")
    @Expose
    var U_phone: String? = "",

    @SerializedName("DocumentLines")
    @Expose
    var DocumentLines: List<DocumentLines>,

    @SerializedName("DocTotal")
    @Expose
    var DocTotal: Double? = 0.0,


    @SerializedName("DocTotalSys")
    @Expose
    var DocTotalSys: Double? = 0.0,

    @SerializedName("PaidToDate")
    @Expose
    var PaidToDate: Double? = 0.0,

    @SerializedName("PaidToDateSys")
    @Expose
    var PaidToDateSys: Double? = 0.0,

    @SerializedName("DiscountPercent")
    @Expose
    var DiscountPercent: Double? = 0.0,

    @SerializedName("DocCurrency")
    @Expose
    var DocCurrency: String? = null,

    @SerializedName("Cancelled")
    @Expose
    var Cancelled: String? = null,

    @SerializedName("DocumentStatus")
    @Expose
    var DocumentStatus: String? = null,

    @SerializedName("DocObjectCode")
    var DocObjectCode: String? = null,

    @SerializedName("ShipToCode")
    var ShipToCode: String? = null,

    @SerializedName("Comments")
    var Comments: String? = null,

    @SerializedName("SalesPersonCode")
    var SalesManagerCode: Long? = null,

    var isChecked: Boolean = false

)

data class DocumentLines(
    @SerializedName("ItemCode")
    @Expose
    var ItemCode: String? = null,

    @SerializedName("ItemDescription")
    @Expose
    var ItemName: String? = null,

    @SerializedName("Type")
    @Expose
    var Type: Int = 0,

    @SerializedName("U_brutto")
    @Expose
    var Brutto: Double = 0.0,

    @SerializedName("U_tara")
    @Expose
    var Tare: Double = 0.0,

    @SerializedName("U_bobin")
    @Expose
    var Bobbin: Double = 0.0,

    @SerializedName("U_asft")
    @Expose
    var Quality: String? = null,

    @SerializedName("U_boyi")
    @Expose
    var Height: String? = null,

    @SerializedName("U_eni")
    @Expose
    var Width: String? = null,

    var OnHand: Double? = null,

    var Committed: Double? = null,

    var isOnHandValuesLoaded: Boolean = false,

    @SerializedName("LineStatus")
    @Expose
    var LineStatus: String? = null,

    @SerializedName("Quantity")
    @Expose
    var Quantity: Double? = 0.0,

    // FOR ITEMS LIST FOR EVENT
    var PlannedQty: Double = 0.0,
    var IssuedQty: Double = 0.0,
    var ReturnedQty: Double = 0.0,
    var NecessaryQty: Double = 0.0,

    var SupplierCode: String? = null,
    var SupplierName: String? = null,

    @SerializedName("RemainingOpenQuantity")
    @Expose
    var RemainingOpenQuantity: Double? = null,

    var UserQuantity: Double? = null,

    var MaxQuantity: Double? = null,

    var InitialQuantity: Double = 0.0,

    var TotalQuantity: Double = 0.0,

    var BaseQuantity: Double? = 0.0,

    var BarCode: String = "",

    var Uom: String = "",

    @SerializedName("WarehouseCode")
    @Expose
    var WarehouseCode: String = "",

    var BinLocationsActivated: Boolean = false,

    var DefaultBinLocationEntry: Int? = null,

    @SerializedName("UnitPrice")
    @Expose
    var Price: Double? = null,

    @SerializedName("PriceAfterVAT")
    @Expose
    var PriceAfterVAT: Double? = 0.0,

    var UserPriceAfterVAT: Double? = null,

    var InitialPrice: Double? = 0.0,

    @SerializedName("DiscountPercent")
    @Expose
    var DiscountPercent: Double = 0.0,

    var DiscountType: String? = null,

    var DiscountMain: Double = 0.0,

    var DiscountByQuantity: Double = 0.0,

    var DiscountByQuantityCollection: List<DiscountByQuantity?>? = emptyList(),

    var DiscountByQuantityLoading: Boolean = false,

    @SerializedName("LineNum")
    @Expose
    var LineNum: Int? = null,

    @SerializedName("BaseEntry")
    @Expose
    var BaseEntry: Long? = null,

    @SerializedName("BaseType")
    @Expose
    var BaseType: String? = null,

    @SerializedName("BaseLine")
    @Expose
    var BaseLine: Int? = null,

    var ManageBatchNumbers: String? = null,

    @SerializedName("BatchNumbers")
    @Expose
    var BatchNumbers: ArrayList<BatchNumbersForPost> = arrayListOf(),

    var TempBatchNumbers: ArrayList<BatchNumbersVal.BatchNumbers> = arrayListOf(),

    @SerializedName("DocumentLinesBinAllocations")
    var binLocations: ArrayList<DocumentLinesBinAllocations> = arrayListOf()
)


@Parcelize
data class DocumentLinesBinAllocations(
    @SerializedName("AllowNegativeQuantity")
    val allowNegativeQuantity: String? = null,
    @SerializedName("BaseLineNumber")
    val baseLineNumber: Int? = null,
    @SerializedName("BinAbsEntry")
    var binAbsEntry: Int? = null,
    var binCode: String? = null,
    @SerializedName("BinActionType")
    var quantity: Double? = null,
    var userQuantity: Double? = 0.0,
    var onHandQty: Double? = 0.0,
    @SerializedName("SerialAndBatchNumbersBaseLine")
    val serialAndBatchNumbersBaseLine: Int? = null
) : Parcelable