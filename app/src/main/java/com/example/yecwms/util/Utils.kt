package com.example.yecwms.util

import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.yecwms.core.GeneralConsts.DOC_STATUS_CLOSED
import com.example.yecwms.core.GeneralConsts.DOC_STATUS_CLOSED_NAME
import com.example.yecwms.core.GeneralConsts.DOC_STATUS_OPEN
import com.example.yecwms.core.GeneralConsts.DOC_STATUS_OPEN_NAME
import com.example.yecwms.data.Preferences
import java.lang.Double.parseDouble
import java.util.*
import kotlin.math.floor


object Utils {

    fun hideSoftKeyboard(activity: Activity) {
        try {
            val inputMethodManager: InputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0
            )
        } catch (e: Exception) {
        }
    }

    fun isSoftKeyboardVisible(view: View): Boolean {
        val heightDiff = view.rootView.height - view.height
        return heightDiff > 200.px
    }

    fun convertUSAdatetoNormal(date: String): String {
        var year: String = date.substringBefore("-")
        var day = date.substringAfterLast("-")
        if (day.length == 1) day = "0$day"
        var month = date.substringAfter("-").substringBefore("-")
        if (month.length == 1) month = "0$month"
        return "$day.$month.$year"
    }

    fun getDocStatus(statusCode: String): String {
        return when (statusCode) {
            DOC_STATUS_OPEN -> DOC_STATUS_OPEN_NAME
            DOC_STATUS_CLOSED -> DOC_STATUS_CLOSED_NAME
            else -> "???"
        }
    }

    fun getObjectCode(objectName: String): String? {
        return when (objectName) {
            "oOrders" -> "17"
            "1250000001" -> "InventoryTransferRequest"
            "oPurchaseInvoices" -> "18"
            "oGoodsReturnRequest" -> "234000032"
            "oReturnRequest" -> "234000031"
            "oPurchaseOrders" -> "22"
            else -> null
        }
    }

    fun getObjectNameFromCode(objectCode: String): String {
        return when (objectCode) {
            "13" -> "Продажа"
            "14" -> "Отм продаж"
            "15" -> "Отгрузка"
            "16" -> "Возврат"
            "18" -> "Закупка"
            "19" -> "Отм закупки"
            "20" -> "Поступление"
            "21" -> "Возврат пост"
            "59" -> "Поступление"
            "60" -> "Списание"
            "67" -> "Перемещение"
            "310000001" -> "Нач сальдо"
            "10000071" -> "Переучет"
            else -> objectCode
        }
    }

    fun generateBatchNumber(itemCode: String): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val hourString = if (hour < 10) "0${hour}" else hour
        val minuteString = if (minute < 10) "0${minute}" else minute
        val yearString = year.toString().takeLast(2)
        val monthString = if (month < 9) "0${month + 1}" else month + 1
        val dayString = if (day < 10) "0${day}" else day


        return "${Preferences.batchPrefix}$itemCode$dayString$monthString$yearString$hourString$minuteString"

    }


    fun getDateInUSAFormat(year: String, sourceMonth: String, sourceDay: String): String {
        val day = if (sourceDay.length == 1) "0$sourceDay" else sourceDay
        val month = if (sourceMonth.length == 1) "0$sourceMonth" else sourceMonth
        return "$year-$month-$day"
    }


    fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val monthString = if (month < 9) "0${month + 1}" else month + 1
        val dayString = if (day < 10) "0${day}" else day

        return "$year-${monthString}-$dayString"
    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val hourString = if (hour < 10) "0${hour}" else hour
        val minuteString = if (minute < 10) "0${minute}" else minute

        return "$hourString:${minuteString}"
    }


    fun getCurrentDateinUSAFormat(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val monthString = if (month < 9) "0${month + 1}" else month + 1
        val dayString = if (day < 10) "0${day}" else day

        return "$year-${monthString}-$dayString"
    }

    fun getFirstDayOFCurrentMONTHinUSAFormat(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)

        val monthString = if (month < 9) "0${month + 1}" else month + 1


        return "$year-${monthString}-01"
    }

    fun getFirstDayOFCurrentYEARinUSAFormat(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        return "$year-01-01"
    }


    fun getNumberWithThousandSeparator(number: Double): String {
        return if ((number == floor(number))) {
            number.toInt().toString().replace(",", " ")
        } else {
            String.format("%,2d", number.toInt()).replace(",", " ")
        }

        /* return if ((number == floor(number))) {                    //Check if value is integer
         } else {
             String.format("%,.2f", number).replace(",", " ")
         }*/

    }

    fun getNumberString(number: Double): String {
        return String.format("%,d", number.toInt()).replace(",", "")

        /* return if ((number == floor(number))) {                    //Check if value is integer
         } else {
             String.format("%,.2f", number).replace(",", " ")
         }*/

    }

    fun isNumber(value: String): Boolean {
        var numeric = true
        try {
            val num = parseDouble(value)
        } catch (e: NumberFormatException) {
            numeric = false
        }
        return numeric
    }

    fun getIntOrDoubleNumberString(value: Double): String {
        return if ((value == floor(value))) {                    //Check if value is integer
            value.toInt().toString()
        } else {
            value.toString()
        }
    }

    fun generateEan13Barcode(lastBarcode: String): String {
        var result = ""
        var checkDigit = 0
        val defaultBarcode = "2000000000008"
        var evenSum = 0;
        var oddSum = 0;

        if (lastBarcode.isEmpty()) return defaultBarcode

        result = lastBarcode.substring(
            0,
            lastBarcode.length - 2
        ) + (lastBarcode[lastBarcode.length - 2] + 1)

        result.forEachIndexed { index: Int, char: Char ->
            //if (index % 2 == 0) oddSum += char.toInt()
            //else evenSum += char.toInt() * 3
        }


        for (i in 0..11) {

            val currentValue = Character.getNumericValue(result.get(i))
            if (i % 2 == 0) oddSum += currentValue
            else evenSum += currentValue * 3

        }

        checkDigit = Character.getNumericValue((evenSum + oddSum).toString().last())

        result = if (checkDigit == 0) "${result}0"
        else "${result}${10 - checkDigit}"

        return result

    }

    fun applyDiscount(sum: Double, dateInUSAFormat: String): Double {
        return 0.0
    }


    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()


}