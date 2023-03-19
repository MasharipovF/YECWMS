package com.example.yecwms.data.entity.businesspartners

import com.example.yecwms.core.GeneralConsts
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class BusinessPartnersForPost(

    @SerializedName("CardName")
    @Expose
    var CardName: String? = null,

    @SerializedName("CardType")
    @Expose
    var CardType: String? = GeneralConsts.BP_TYPE_CUSTOMER,

    @SerializedName("GroupCode")
    @Expose
    var GroupCode: Int? = null,

    @SerializedName("Phone1")
    @Expose
    var Phone1: String? = null,

    @SerializedName("CreditLimit")
    @Expose
    var CreditLimit: Double? = null,

    @SerializedName("PriceListNum")
    @Expose
    var DefaultPriceListCode: Int? = null,

    @SerializedName("Series")
    @Expose
    var Series: Int? = null,

    @SerializedName("BPAddresses")
    @Expose
    var BPAddresses: List<BPAddresses>? = null,

    @SerializedName("Currency")
    @Expose
    var Currency: String? = null,
)