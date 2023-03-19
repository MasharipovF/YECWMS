package com.example.yecwms.data.entity.incomingpayments

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class IncomingPaymentsVal(
    @SerializedName("odata.nextLink")
    @Expose
    var nextLink: String,
    @SerializedName("value")
    @Expose
    var value: List<IncomingPayments>
)

@Parcelize
data class IncomingPayments(
    @SerializedName("Cancelled")
    @Expose
    var cancelled: String? = null,
    @SerializedName("CardCode")
    @Expose
    var cardCode: String? = null,

    @SerializedName("BPLID")
    @Expose
    var BPL_ID: String? = null,

    @SerializedName("CardName")
    @Expose
    var cardName: String? = null,

    @SerializedName("DocRate")
    @Expose
    var docRate: Double? = null,

    @SerializedName("CashAccount")
    @Expose
    var cashAccount: String? = null,
    @SerializedName("CashSum")
    @Expose
    var cashSum: Double? = 0.0,

    @SerializedName("CashSumSys")
    @Expose
    var cashSumSys: Double? = 0.0,

    @SerializedName("CashSumFC")
    @Expose
    var cashSumFC: Double? = 0.0,

    @SerializedName("TransferAccount")
    @Expose
    var transferAccount: String? = null,
    @SerializedName("TransferSum")
    @Expose
    var transferSum: Double? = 0.0,
    @SerializedName("ControlAccount")
    @Expose
    var controlAccount: String? = null,

    @SerializedName("DocDate")
    @Expose
    var docDate: String? = null,
    @SerializedName("DocEntry")
    @Expose
    var docEntry: Long? = null,
    @SerializedName("DocNum")
    @Expose
    var docNum: Int? = null,
    @SerializedName("DocType")
    @Expose
    var docType: String? = null,
    @SerializedName("DueDate")
    @Expose
    var dueDate: String? = null,
    @SerializedName("PaymentInvoices")
    @Expose
    var paymentInvoices: List<PaymentInvoice>? = null,

    @SerializedName("DocCurrency")
    @Expose
    var DocCurrency: String? = null,

    @SerializedName("CounterReference")
    @Expose
    var counterReference: String? = null,


    @SerializedName("U_cash")
    var uCash: Double? = 0.0,

    @SerializedName("U_card")
    var uCard: Double? = 0.0,

    @SerializedName("U_ePayment")
    var uePayment: Double? = 0.0,

    @SerializedName("Remarks")
    var remarks: String? = ""

) : Parcelable

@Parcelize
data class PaymentInvoice(
    @SerializedName("AppliedFC")
    @Expose
    var appliedFC: Double? = null,
    @SerializedName("AppliedSys")
    @Expose
    var appliedSys: Double? = null,
    @SerializedName("DiscountPercent")
    @Expose
    var discountPercent: Double? = null,
    @SerializedName("DocEntry")
    @Expose
    var docEntry: Long? = null,
    @SerializedName("DocLine")
    @Expose
    var docLine: Int? = null,
    @SerializedName("DocRate")
    @Expose
    var docRate: Double? = null,
    @SerializedName("InstallmentId")
    @Expose
    var installmentId: Int? = null,
    @SerializedName("InvoiceType")
    @Expose
    var invoiceType: String? = null,
    @SerializedName("LineNum")
    @Expose
    var lineNum: Int? = null,
    @SerializedName("PaidSum")
    @Expose
    var paidSum: Double? = null,
    @SerializedName("SumApplied")
    @Expose
    var sumApplied: Double? = null
) : Parcelable