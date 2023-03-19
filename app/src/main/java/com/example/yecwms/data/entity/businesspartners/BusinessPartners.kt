package com.example.yecwms.data.entity.businesspartners

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class BusinessPartnersVal(
    @SerializedName("value")
    @Expose
    var value: List<BusinessPartners>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String

)

@Parcelize
data class BusinessPartners(
    @SerializedName("CardCode")
    @Expose
    var CardCode: String? = null,

    @SerializedName("CardName")
    @Expose
    var CardName: String? = null,

    @SerializedName("CardType")
    @Expose
    var CardType: String? = null,

    @SerializedName("Currency")
    @Expose
    var Currency: String? = null,

    @SerializedName("GroupCode")
    @Expose
    var GroupCode: Int? = 0,

    var GroupName: String? = null,

    @SerializedName("CurrentAccountBalance")
    @Expose
    var Balance: Double? = 0.0,

    @SerializedName("CurrentAccountBalanceByShop")
    @Expose
    var BalanceByShop: Double? = 0.0,

    @SerializedName("OpenOrdersBalance")
    @Expose
    var OpenOrdersBalance: Double? = null,

    @SerializedName("Phone1")
    @Expose
    var Phone1: String? = null,

    @SerializedName("ShipToDefault")
    @Expose
    var Address: String? = null,

    @SerializedName("CreditLimit")
    @Expose
    var CreditLimit: Double? = 0.0,

    @SerializedName("MaxCommitment")
    @Expose
    var MaxCommitment: Double? = 0.0,

    @SerializedName("PriceListNum")
    @Expose
    var PriceListCode: Int? = -1,

    var PriceListName: String? = null,

    @SerializedName("Valid")
    @Expose
    var Valid: String? = "tYES",

    @SerializedName("Frozen")
    @Expose
    var Frozen: String? = "tNO",

    @SerializedName("Series")
    @Expose
    var Series: Int? = null,

    @SerializedName("BPAddresses")
    @Expose
    var BPAddresses: List<BPAddresses>? = listOf()
) : Parcelable


@Parcelize
data class BPAddresses(
    @SerializedName("AddressName")
    var addressName: String = "",

    @SerializedName("AddressName2")
    var addressName2: String? = null,

    @SerializedName("AddressName3")
    var addressName3: String? = null,

    @SerializedName("AddressType")
    var addressType: String = "",

    @SerializedName("BPCode")
    var bPCode: String = "",

    @SerializedName("Block")
    var block: String? = null,

    @SerializedName("BuildingFloorRoom")
    var buildingFloorRoom: String? = null,

    @SerializedName("City")
    var city: String? = null,

    @SerializedName("Country")
    var country: String = "",

    @SerializedName("County")
    var county: String? = null,

    @SerializedName("FederalTaxID")
    var federalTaxID: String? = null,

    @SerializedName("RowNum")
    var rowNum: Int = 0,

    @SerializedName("State")
    var state: String? = null,

    @SerializedName("Street")
    var street: String? = null,

    @SerializedName("StreetNo")
    var streetNo: String? = null,

    @SerializedName("TaxCode")
    var taxCode: String? = null,

    @SerializedName("TypeOfAddress")
    var typeOfAddress: String? = null
) : Parcelable

data class ContactPersons(
    @SerializedName("Active")
    var active: String = "",

    @SerializedName("Address")
    var address: Any? = null,

    @SerializedName("CardCode")
    var cardCode: String = "",

    @SerializedName("E_Mail")
    var eMail: Any? = null,

    @SerializedName("FirstName")
    var firstName: String = "",

    @SerializedName("InternalCode")
    var internalCode: Int = 0,

    @SerializedName("LastName")
    var lastName: Any? = null,

    @SerializedName("MiddleName")
    var middleName: Any? = null,

    @SerializedName("MobilePhone")
    var mobilePhone: Any? = null,

    @SerializedName("Name")
    var name: String = "",

    @SerializedName("Phone1")
    var phone1: String = "",

    @SerializedName("Phone2")
    var phone2: Any? = null,

    @SerializedName("Position")
    var position: Any? = null
)
