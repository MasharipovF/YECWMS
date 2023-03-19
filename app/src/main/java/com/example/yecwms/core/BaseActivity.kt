package com.example.yecwms.core

import android.app.AlertDialog
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.yecwms.R
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.Widgets
import com.example.yecwms.presentation.businesspartners.bpadd.BpAddActivity
import com.example.yecwms.presentation.items.itemadd.ItemAddActivity
import com.example.yecwms.presentation.main.MainActivity
import com.example.yecwms.presentation.purchasedeliveries.insert.PurchaseDeliveryInsertActivity
import com.example.yecwms.presentation.invoice.InvoiceInsertActivity
import com.example.yecwms.util.barcodereader.BarCodeScannerConsts
import com.example.yecwms.util.barcodereader.BarCodeScannerHelper
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    // THIS IS FOR ZEBRA LIKE DEVICES
    lateinit var barcodeScannerHelper: BarCodeScannerHelper

    abstract fun init(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)


        //showAlertDialogTrialTimeout()

        barcodeScannerHelper = BarCodeScannerHelper()

        val docType = when (this) {

            is MainActivity -> {
                resources.getString(R.string.doctype_main)
            }

            else -> ""
        }


        supportActionBar?.title =
            docType + "   | Склад: " + if (Preferences.defaultWhs != null) Preferences.defaultWhs else "?"

        init(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()

        val filter = IntentFilter()
        filter.addAction(BarCodeScannerConsts.ACTION)
        registerReceiver(barcodeScannerHelper.receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(barcodeScannerHelper.receiver)
    }


    fun replaceFragment(
        containerId: Int,
        fragment: Fragment,
        tag: String,
        backStack: Boolean = false,
        transition: Int = FragmentTransaction.TRANSIT_NONE
    ) {
        val foundFragment = supportFragmentManager.findFragmentByTag(tag)

        if (foundFragment != null && foundFragment.isAdded) return

        val transaction = supportFragmentManager.beginTransaction()
        if (transition != FragmentTransaction.TRANSIT_NONE)
            transaction.setTransition(transition)
        transaction.replace(containerId, fragment, tag)
        if (backStack)
            transaction.addToBackStack(tag)
        transaction.commit()
    }


    fun addFragment(
        containerId: Int,
        fragment: Fragment,
        tag: String,
        backStack: Boolean = false,
        transition: Int = FragmentTransaction.TRANSIT_NONE
    ) {
        val foundFragment = supportFragmentManager.findFragmentByTag(tag)

        if (foundFragment != null && foundFragment.isAdded) {
            if (foundFragment.isHidden) {
                supportFragmentManager.beginTransaction().show(foundFragment).commitNow()
            }
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        if (transition != FragmentTransaction.TRANSIT_NONE)
            transaction.setTransition(transition)
        transaction.add(containerId, fragment, tag)
        if (backStack)
            transaction.addToBackStack(tag)
        transaction.commit()
    }

    override fun onBackPressed() {

        var hasChanges: Boolean = false

        when (this) {
            is ItemAddActivity -> {
                hasChanges = this.hasChanges()
                if (hasChanges) showAlertDialog()
                else super.onBackPressed()
            }
            is BpAddActivity -> {
                hasChanges = this.hasChanges()
                if (hasChanges) showAlertDialog()
                else super.onBackPressed()
            }


            is PurchaseDeliveryInsertActivity -> {
                hasChanges = this.hasChanges()
                if (hasChanges) showAlertDialog()
                else super.onBackPressed()
            }

            is InvoiceInsertActivity -> {
                hasChanges = this.hasChanges()
                if (hasChanges) showAlertDialog()
                else super.onBackPressed()
            }
            else -> super.onBackPressed()
        }

        /*if (supportFragmentManager.backStackEntryCount > 1) {
            val index = supportFragmentManager.backStackEntryCount - 1
            val backEntry = supportFragmentManager.getBackStackEntryAt(index);
            val tag = backEntry.name;
            val fragment = supportFragmentManager.findFragmentByTag(tag);
            super.onBackPressed()

        } else {
            if (!hasChanges) finish()
        }*/

        if (supportFragmentManager.backStackEntryCount == 0) {
            if (!hasChanges) finish()
        }
    }

    private fun showAlertDialog(cancelChanges: Boolean = false) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.doc_changed)
            .setMessage(R.string.doc_resetchanges)
            .setNegativeButton("Назад") { _, _ ->

            }
            .setPositiveButton("Сбросить") { _, _ ->
                /*if (this is RequestsToMeTransferActivity && cancelChanges) {
                    this.reopenInventoryRequestAndClose()
                } else {
                    finish()
                }*/
                finish()
            }
            .create()
            .show()
    }

    private fun showAlertDialogTrialTimeout() {
        if (SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
                .toString() == "2022-05-30"
        ) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Период тестирования закончился")
                .setMessage("Обратитесь к администратору для продления пробного периода")
                .setCancelable(false)
                .setPositiveButton("ОК") { _, _ ->
                    finish()
                }
                .create()
                .show()
        }


    }

    fun createDefaultWidgets() {
        val widgetsList = arrayListOf<Widgets>()
        widgetsList.add(Widgets(WidgetsEnum.ITEMS))
        widgetsList.add(Widgets(WidgetsEnum.REQUESTS_TO_ME))
        widgetsList.add(Widgets(WidgetsEnum.NEW_TRANSFER))
        widgetsList.add(Widgets(WidgetsEnum.NON_RECEIVED_TRANSFERS))
        widgetsList.add(Widgets(WidgetsEnum.INVENTORY_COUNTING))
        widgetsList.add(Widgets(WidgetsEnum.BIN_ALLOCATION))
        widgetsList.add(Widgets(WidgetsEnum.PURCHASE_DELIVERY))
        widgetsList.add(Widgets(WidgetsEnum.DELIVERY))
        widgetsList.add(Widgets(WidgetsEnum.RETURN_FROM_CLIENT))
        widgetsList.add(Widgets(WidgetsEnum.RETURN_TO_SUPPLIER))
        Preferences.widgets = Gson().toJson(widgetsList)
        Log.wtf("WIDGETS", Preferences.widgets)
    }


}