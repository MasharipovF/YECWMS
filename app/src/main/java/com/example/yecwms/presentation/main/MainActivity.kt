package com.example.yecwms.presentation.main

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.data.Preferences
import com.example.yecwms.databinding.ActivityMainBinding
import com.example.yecwms.presentation.inventorytransfer.insertpage.InventoryTransferInsertActivity
import com.example.yecwms.presentation.items.ItemsActivity
import com.example.yecwms.presentation.purchasedeliveries.openpurchaseorderslist.OpenPurchaseOrdersListActivity
import com.example.yecwms.presentation.invoice.InvoiceInsertActivity
import com.example.yecwms.presentation.settings.Settings
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.Utils


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mViewModel: MainActivityViewModel

    override fun init(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)


        mViewModel.loading.observe(this) {
            if (it > 0) {
                showLoader(this)
            }
        }



        if (Preferences.currencyDate == null || Preferences.currencyDate != Utils.getCurrentDateinUSAFormat()) {
            mViewModel.getCurrencies()
            showLoader(this)
        }
    }

    override fun onResume() {
        super.onResume()
        //TODO checkActiveWidgetTypes()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_user_settings -> {
                val intent: Intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }
            /*R.id.menu_exchange -> {
                mViewModel.getExchangeRate()
                showLoader(this)
                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showLoader(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_loader)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnReload = dialog.findViewById<Button>(R.id.btnReload)

        dialog.setOnCancelListener {
            finish()
        }
        mViewModel.loading.observe(this) {
            if (it > 0) {
                loader.visibility = View.VISIBLE
                btnReload.visibility = View.GONE
            } else {
                loader.visibility = View.GONE
                btnReload.visibility = View.VISIBLE

            }
        }

        mViewModel.currencies.observe(this) {
            Log.wtf("CURRENCIES", it.toString())
            if (it != null && mViewModel.loading.value == 0)
                dialog.dismiss()
        }

        mViewModel.companyInfo.observe(this) {
            if (it != null && mViewModel.loading.value == 0)
                dialog.dismiss()
        }

        mViewModel.errorItem.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        btnReload.setOnClickListener {
            mViewModel.getCompanyInfo()
            mViewModel.getCurrencies()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, this, 70, 30)
    }


    fun onClick(view: View) {
        val intent: Intent = when (view.id) {
            R.id.btnItems -> Intent(this, ItemsActivity::class.java)
            R.id.btnPurchaseDeliveries -> Intent(this, OpenPurchaseOrdersListActivity::class.java)
            R.id.btnSalesOrders -> Intent(this, InvoiceInsertActivity::class.java)
            R.id.btnInventoryTransfers -> Intent(this, InventoryTransferInsertActivity::class.java)
            else -> return
        }
        startActivity(intent)
    }

}
