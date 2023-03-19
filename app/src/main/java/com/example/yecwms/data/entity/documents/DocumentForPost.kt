package com.example.yecwms.data.entity.documents

import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DocumentsForPostVal(
    @SerializedName("value")
    @Expose
    var value: List<Document>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class DocumentForPost(
    @SerializedName("DocObjectCode")
    var docObjectCode: String? = null,

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

    @SerializedName("DocDueDate")
    @Expose
    var DocDueDate: String? = null,

    @SerializedName("CardCode")
    @Expose
    var CardCode: String? = null,

    @SerializedName("CardName")
    @Expose
    var CardName: String? = null,

    @SerializedName("DocumentLines")
    @Expose
    var DocumentLines: List<DocumentLinesForPost>,

    @SerializedName("DocTotal")
    @Expose
    var DocTotal: Double? = null,

    @SerializedName("DiscountPercent")
    @Expose
    var DiscountPercent: Double? = null,

    @SerializedName("DocCurrency")
    @Expose
    var DocCurrency: String? = null,

    @SerializedName("Cancelled")
    @Expose
    var Cancelled: String? = null,

    @SerializedName("ShipToCode")
    var ShipToCode: String? = null,


    @SerializedName("Comments")
    var Comments: String? = null,

    @SerializedName("SalesPersonCode")
    var SalesManagerCode: Int? = null
)

data class DocumentLinesForPost(
    @SerializedName("ItemCode")
    @Expose
    var ItemCode: String? = null,

    @SerializedName("ItemDescription")
    @Expose
    var ItemName: String? = null,

    @SerializedName("Quantity")
    @Expose
    var Quantity: Double? = null,

    @SerializedName("WarehouseCode")
    @Expose
    var WarehouseCode: String = "",

    @SerializedName("PriceAfterVAT")
    @Expose
    var PriceAfterVAT: Double? = null,

    @SerializedName("Price")
    @Expose
    var Price: Double? = null,

    @SerializedName("UnitPrice")
    @Expose
    var UnitPrice: Double? = null,

    @SerializedName("DiscountPercent")
    @Expose
    var DiscountPercent: Double? = null,

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

    @SerializedName("BatchNumbers")
    @Expose
    var BatchNumbers: List<BatchNumbersForPost>? = null,

    @SerializedName("DocumentLinesBinAllocations")
    var binLocations: ArrayList<DocumentLinesBinAllocationsForPost> = arrayListOf()
)

data class DocumentLinesBinAllocationsForPost(
    @SerializedName("AllowNegativeQuantity")
    val allowNegativeQuantity: String? = null,
    @SerializedName("BaseLineNumber")
    val baseLineNumber: Int? = null,
    @SerializedName("BinAbsEntry")
    val binAbsEntry: Int? = null,
    @SerializedName("Quantity")
    val quantity: Double? = null,
    @SerializedName("SerialAndBatchNumbersBaseLine")
    val serialAndBatchNumbersBaseLine: Int? = null
)
