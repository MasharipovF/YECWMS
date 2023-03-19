package com.example.yecwms.data.repository

import android.util.Log
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.remote.services.InvoicesService
import com.example.yecwms.util.ErrorUtils
import com.example.yecwms.util.retryIO

interface InvoicesRepository {
    suspend fun getInvoiceList(
        filter: String = "",
        skipValue: Int = 0,
        docStatus: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Any? //DocumentsVal?

    suspend fun getInvoice(docEntry: Long): Any? //Document?
    suspend fun insertInvoice(invoice: DocumentForPost): Any?// Document?
    suspend fun cancelInvoice(docEntry: Long): Any? //Boolean?
}

class InvoicesRepositoryImpl(
    private val invoiceService: InvoicesService = InvoicesService.get()
) :
    InvoicesRepository {


    override suspend fun getInvoiceList(
        filter: String,
        skipValue: Int,
        docStatus: String?,
        dateFrom: String?,
        dateTo: String?
    ): Any? {

        var filterStringBuilder =
            if (docStatus != null) "(contains(CardName, '$filter') or contains(DocNum, '$filter')) and DocumentStatus eq '$docStatus'"
            else "(contains(CardName, '$filter') or contains(DocNum, '$filter'))"

        if (Preferences.defaultWhs != null) filterStringBuilder += " and U_whs eq '${Preferences.defaultWhs}'"

        if (dateFrom != null)
            filterStringBuilder += " and DocDate ge '$dateFrom'"

        if (dateTo != null)
            filterStringBuilder += " and DocDate le '$dateTo'"

        val response = retryIO {
            invoiceService.getInvoicesList(filter = filterStringBuilder, skipValue = skipValue)
        }

        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
    }


    override suspend fun getInvoice(docEntry: Long): Any? {
        val response = retryIO { invoiceService.getInvoice(docEntry) }
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
        /*
        return if (response.isSuccessful) {
            Log.d(
                "BPDEFAULTS",
                response.body().toString()
            )
            response.body()
        } else {
            Log.d(
                "BPDEFAULTS",
                response.errorBody()!!.string()
            )
            null
        }*/
    }

    override suspend fun insertInvoice(invoice: DocumentForPost): Any? {
        val response = invoiceService.addInvoice(invoice)
        Log.d("INVOICEINSERT", response.body().toString())
        return if (response.isSuccessful) {
            response.body()
        } else {
            return ErrorUtils.errorProcess(response)
        }
        /*if (response.isSuccessful) {
            Log.d("BPINSERT", response.body().toString())
        } else Log.d("BPINSERTERROR", response.errorBody()!!.string())
        return response.body()*/
    }

    override suspend fun cancelInvoice(docEntry: Long): Any? {
        val response = retryIO { invoiceService.cancelInvoice(docEntry) }
        return if (response.isSuccessful) {
            true
        } else {
            return ErrorUtils.errorProcess(response)
        }
        /*
        return if (response.isSuccessful) {
            Log.d("BPINSERT", response.body().toString())
            true
        } else {
            Log.d("BPINSERTERROR", response.errorBody()!!.string())
            false
        }*/
    }

}