package com.example.yecwms.data.entity.series


import com.google.gson.annotations.SerializedName

data class SeriesForPost(
    @SerializedName("DocumentTypeParams")
    val documentTypeParams: DocumentTypeParams? = null
) {
    data class DocumentTypeParams(
        @SerializedName("Document")
        val document: String? = null,
        @SerializedName("DocumentSubType")
        val documentSubType: String? = null
    )
}