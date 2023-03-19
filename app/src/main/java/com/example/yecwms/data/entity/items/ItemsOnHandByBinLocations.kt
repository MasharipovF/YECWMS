package com.example.yecwms.data.entity.items


import com.google.gson.annotations.SerializedName

data class ItemsOnHandByBinLocationsVal(
    @SerializedName("@odata.context")
    val odataContext: String? = null,
    @SerializedName("value")
    val value: List<ItemsOnHandByBinLocations>
)

data class ItemsOnHandByBinLocations(
    @SerializedName("AbsEntry")
    val absEntry: Int? = null,
    @SerializedName("BinAbsEntry")
    val binAbsEntry: Int? = null,
    @SerializedName("BinCode")
    val binCode: String? = null,
    @SerializedName("id__")
    val id: Int? = null,
    @SerializedName("ItemCode")
    val itemCode: String? = null,
    @SerializedName("ItemName")
    val itemName: String? = null,
    @SerializedName("SalesUOM")
    val salesUOM: String? = null,
    @SerializedName("OnHandQty")
    val onHandQty: Double = 0.0,
    @SerializedName("WhsCode")
    val whsCode: String? = null,
    @SerializedName("BarCode")
    val barCodes: String = ""
)