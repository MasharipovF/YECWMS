package com.example.yecwms.data.entity.inventory



import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InventoryOperationsVal(
    @SerializedName("value")
    @Expose
    var value: List<InventoryOperations>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class InventoryOperations(
    @SerializedName("DocEntry")
    var docEntry: Long? = null,
    @SerializedName("DocObjectCode")
    var DocObjectCode: String? = null,
    @SerializedName("DocDate")
    var docDate: String? = null,
    @SerializedName("DueDate")
    var dueDate: String? = null,
    @SerializedName("Comments")
    var comments: Any? = null,
    @SerializedName("FromWarehouse")
    var fromWarehouse: String? = null,
    @SerializedName("ToWarehouse")
    var toWarehouse: String? = null,
    @SerializedName("UpdateDate")
    var updateDate: String? = null,
    @SerializedName("DocNum")
    var docNum: Int? = null,
    @SerializedName("DocumentStatus")
    var documentStatus: String? = null,
    @SerializedName("StockTransferLines")
    var inventoryOperationsLines: List<InventoryOperationsLines>,
    @SerializedName("BaseDocEntry")
    var baseDocumentEntry: String? = null,
    @SerializedName("BaseDocNum")
    var baseDocumentNumber: String? = null,
    @SerializedName("BasePickRemark")
    var baseDocumentRemark: String? = null,
    @SerializedName("DocumentReferences")
    var documentReferences: List<DocumentReferences>,
    @SerializedName("U_whsRemark")
    var warehouseRemark: String? = null,
    var isChecked: Boolean = false
)


data class InventoryOperationsLines(
    @SerializedName("LineNum")
    var lineNum: Int? = null,
    @SerializedName("DocEntry")
    var docEntry: Int? = null,
    @SerializedName("ItemCode")
    var itemCode: String? = null,
    @SerializedName("ItemDescription")
    var itemDescription: String? = null,
    @SerializedName("Quantity")
    var quantity: BigDecimal? = null,

    var Barcode: String = "",

    var UserQuantity: BigDecimal = BigDecimal.valueOf(0.0),

    var MaxQuantity: BigDecimal = BigDecimal.valueOf(0.0),

    var InitialQuantity: BigDecimal = BigDecimal.valueOf(0.0),

    var OnHand: BigDecimal? = null,

    var Committed: BigDecimal? = null,

    var managedBy:String? = null,

    @SerializedName("WarehouseCode")
    var toWarehouse: String? = null,
    @SerializedName("FromWarehouseCode")
    var fromWarehouse: String? = null,
    @SerializedName("BaseType")
    var baseType: String? = null,
    @SerializedName("BaseLine")
    var baseLine: Any? = null,
    @SerializedName("BaseEntry")
    var baseEntry: Any? = null,
    @SerializedName("UoMEntry")
    var uoMEntry: Int? = null,
    @SerializedName("UoMCode")
    var uoMCode: String? = null,
    @SerializedName("InventoryQuantity")
    var inventoryQuantity: BigDecimal? = null,
    @SerializedName("RemainingOpenQuantity")
    var remainingOpenQuantity: BigDecimal? = null,
    @SerializedName("RemainingOpenInventoryQuantity")
    var remainingOpenInventoryQuantity: BigDecimal? = null,
    @SerializedName("LineStatus")
    var lineStatus: String? = null,

    @SerializedName("BatchNumbers")
    @Expose
    var BatchNumbers: ArrayList<BatchNumbersForPost> = arrayListOf(),

    var TempBatchNumbers: ArrayList<BatchNumbersVal.BatchNumbers> = arrayListOf(),
)

data class DocumentReferences(
    @SerializedName("DocEntry")
    val docEntry: Int? = null,
    @SerializedName("LineNumber")
    val lineNumber: Int? = null,
    @SerializedName("RefDocEntr")
    val refDocEntr: Int? = null,
    @SerializedName("RefDocNum")
    val refDocNum: Int? = null,
    @SerializedName("RefObjType")
    val refObjType: String? = null,
    @SerializedName("Remark")
    val remark: Any? = null
)
