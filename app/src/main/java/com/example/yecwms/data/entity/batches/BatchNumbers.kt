package com.example.yecwms.data.entity.batches


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BatchNumbersVal(
    @SerializedName("value")
    val value: List<BatchNumbers>? = listOf()
) {
    data class BatchNumbers(
        var ItemCode: String? = null,
        var WhsCode: String? = null,
        @SerializedName("AbsEntry")
        val absEntry: Int? = null,
        @SerializedName("DistNumber")
        val batchNumber: String? = null,
        @SerializedName("LotNumber")
        val lotNumber: String? = null,
        @SerializedName("MnfSerial")
        val mnfSerial: String? = null,
        @SerializedName("Quantity")
        val quantity: Double? = null,
        var selectedQuantity: Double = 0.0,
        var isChecked: Boolean = false,

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
    )
}