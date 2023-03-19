package com.example.yecwms.data

import android.content.Context
import android.content.SharedPreferences
import com.example.yecwms.data.entity.masterdatas.Currencies
import com.example.yecwms.data.entity.masterdatas.SalesManagers
import com.example.yecwms.data.entity.userdefaults.UserDefaultsBranches
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object Preferences {
    const val CREDENTIALS = "CREDENTIALS"


    fun init(context: Context) {
        preferences = context.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE)
    }

    fun setPreference(preferences: SharedPreferences) {
        Preferences.preferences = preferences
    }

    lateinit var preferences: SharedPreferences


    var companyDB: String?
        get() = preferences.getString(Preferences::companyDB.name, null)
        set(value) {
            preferences.edit().putString(Preferences::companyDB.name, value).apply()
        }

    var ipAddress: String?
        get() = preferences.getString(Preferences::ipAddress.name, null)
        set(value) {
            preferences.edit().putString(Preferences::ipAddress.name, value).apply()
        }


    var portNumber: String?
        get() = preferences.getString(Preferences::portNumber.name, null)
        set(value) {
            preferences.edit().putString(Preferences::portNumber.name, value).apply()
        }

    var sessionID: String?
        get() = preferences.getString(Preferences::sessionID.name, null)
        set(value) {
            preferences.edit().putString(Preferences::sessionID.name, value).apply()
        }

    var firstLogin: Boolean
        get() = preferences.getBoolean(Preferences::firstLogin.name, true)
        set(value) {
            preferences.edit().putBoolean(Preferences::firstLogin.name, value).apply()
        }


    var userName: String?
        get() = preferences.getString(Preferences::userName.name, null)
        set(value) {
            preferences.edit().putString(Preferences::userName.name, value).apply()
        }

    var userPassword: String?
        get() = preferences.getString(Preferences::userPassword.name, null)
        set(value) {
            preferences.edit().putString(Preferences::userPassword.name, value).apply()
        }

    var defaultWhs: String?
        get() = preferences.getString(Preferences::defaultWhs.name, null)
        set(value) {
            preferences.edit().putString(Preferences::defaultWhs.name, value).apply()
        }

    var batchPrefix: String?
        get() = preferences.getString(Preferences::batchPrefix.name, null)
        set(value) {
            preferences.edit().putString(Preferences::batchPrefix.name, value).apply()
        }


    var defaultWhsName: String?
        get() = preferences.getString(Preferences::defaultWhsName.name, null)
        set(value) {
            preferences.edit().putString(Preferences::defaultWhsName.name, value).apply()
        }


    var defaultCustomer: String?
        get() = preferences.getString(Preferences::defaultCustomer.name, null)
        set(value) {
            preferences.edit().putString(Preferences::defaultCustomer.name, value).apply()
        }

    var branches: String?
        get() = preferences.getString(Preferences::branches.name, null)
        set(value) {
            preferences.edit().putString(Preferences::branches.name, value).apply()
        }

    var salesPerson: String?
        get() = preferences.getString(Preferences::salesPerson.name, null)
        set(value) {
            preferences.edit().putString(Preferences::salesPerson.name, value).apply()
        }

    var defaultAccount: String?
        get() = preferences.getString(Preferences::defaultAccount.name, null)
        set(value) {
            preferences.edit().putString(Preferences::defaultAccount.name, value).apply()
        }

    var localCurrency: String?
        get() = preferences.getString(Preferences::localCurrency.name, null)
        set(value) {
            preferences.edit().putString(Preferences::localCurrency.name, value).apply()
        }

    var systemCurrency: String?
        get() = preferences.getString(Preferences::systemCurrency.name, null)
        set(value) {
            preferences.edit().putString(Preferences::systemCurrency.name, value).apply()
        }

    var isDirectRateCalculation: Boolean
        get() = preferences.getBoolean(Preferences::isDirectRateCalculation.name, false)
        set(value) {
            preferences.edit().putBoolean(Preferences::isDirectRateCalculation.name, value).apply()
        }

    var currencies: String?
        get() = preferences.getString(Preferences::currencies.name, null)
        set(value) {
            preferences.edit().putString(Preferences::currencies.name, value).apply()
        }

    var currencyDate: String?
        get() = preferences.getString(Preferences::currencyDate.name, null)
        set(value) {
            preferences.edit().putString(Preferences::currencyDate.name, value).apply()
        }

    var totalsAccuracy: Int
        get() = preferences.getInt(Preferences::totalsAccuracy.name, 6)
        set(value) {
            preferences.edit().putInt(Preferences::totalsAccuracy.name, value).apply()
        }

    var pricesAccuracy: Int
        get() = preferences.getInt(Preferences::pricesAccuracy.name, 6)
        set(value) {
            preferences.edit().putInt(Preferences::pricesAccuracy.name, value).apply()
        }


    //PRINTER SETTINGS

    var printerIp: String?
        get() = preferences.getString(Preferences::printerIp.name, null)
        set(value) {
            preferences.edit().putString(Preferences::printerIp.name, value).apply()
        }

    var printerPort: Int
        get() = preferences.getInt(Preferences::printerPort.name, 0)
        set(value) {
            preferences.edit().putInt(Preferences::printerPort.name, value).apply()
        }


    var printerMaxChar: Int
        get() = preferences.getInt(Preferences::printerMaxChar.name, 0)
        set(value) {
            preferences.edit().putInt(Preferences::printerMaxChar.name, value).apply()
        }


    var widgets: String?
        get() = preferences.getString(Preferences::widgets.name, null)
        set(value) {
            preferences.edit().putString(Preferences::widgets.name, value).apply()
        }

    fun putBranchesIntoPref(branches: List<UserDefaultsBranches>?) {
        Preferences.branches = Gson().toJson(branches)
    }

    fun getBranchesFromPref(): List<UserDefaultsBranches>? {
        val type: Type = object : TypeToken<List<UserDefaultsBranches?>?>() {}.type
        return Gson().fromJson<List<UserDefaultsBranches>>(Preferences.branches, type)
    }

    fun putSalesPersonIntoPref(slpCode: Int, slpName: String) {
        Preferences.salesPerson =
            Gson().toJson(SalesManagers(salesEmployeeCode = slpCode, salesEmployeeName = slpName))
    }

    fun getSalesPersonFromPref(): SalesManagers? {
        return if (Preferences.salesPerson!=null) {
            Gson().fromJson(Preferences.salesPerson, SalesManagers::class.java)
        } else null
    }

    fun putCurrenciesIntoPref(currencies: List<Currencies>?, currencyDate: String) {
        this.currencies = Gson().toJson(currencies)
        this.currencyDate = currencyDate
    }

    fun getCurrenciesFromPref(): List<Currencies>? {
        val type: Type = object : TypeToken<List<Currencies?>?>() {}.type
        return Gson().fromJson<List<Currencies>>(this.currencies, type)
    }

}