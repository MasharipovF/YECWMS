package com.example.yecwms.presentation.purchasedeliveries.insert

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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.masterdatas.Currencies
import com.example.yecwms.databinding.ActivityPurchaseDeliveryInsertBinding
import com.example.yecwms.domain.mappers.Mappers
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.barcodeprinter.PrinterHelper
import com.google.zxing.integration.android.IntentIntegrator


class PurchaseDeliveryInsertActivity : BaseActivity() {

    private lateinit var binding: ActivityPurchaseDeliveryInsertBinding
    private lateinit var mViewModel: PurchaseDeliveryInsertViewModel


    override fun init(savedInstanceState: Bundle?) {
        binding = ActivityPurchaseDeliveryInsertBinding.inflate(layoutInflater)
        mViewModel = ViewModelProvider(this).get(PurchaseDeliveryInsertViewModel::class.java)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentPurchaseDelivery)
        binding.bottomNavView.setupWithNavController(navController)


        val bundle: Bundle? = intent.extras
        mViewModel.docEntry =
            bundle?.getLong(GeneralConsts.PASSED_PURCHASEORDER_DOCENTRY)
        if (mViewModel.docEntry != null) {
            mViewModel.isCopiedFrom.value = true
            mViewModel.loadPurchaseOrder(mViewModel.docEntry!!)
        }


        mViewModel.loadingDocument.observe(this) {
            if (it) {
                showLoader(this)
            }
        }


        mViewModel.document.observe(this) { it ->
            mViewModel.getBp(it.CardCode!!)
            mViewModel.getManager(it.SalesManagerCode!!)
            mViewModel.currentWhsCode.postValue(it.DocumentLines[0].WarehouseCode)
            mViewModel.discount.postValue(it.DiscountPercent!!.toInt())

            val listForBasket = Mappers.getDocLinesWithBaseDoc(it, false)
            mViewModel.basketList.postValue(listForBasket)

            listForBasket.distinctBy { basketItem -> basketItem.ItemCode }.toList()
                .forEach { line ->
                    mViewModel.getItemImage(line.ItemCode!!)
                }

            if (mViewModel.isCopiedFrom.value == true){
                setCurrency(it.DocCurrency.toString())
            }
        }

        mViewModel.basketList.observe(this) {
            mViewModel.calculateDocTotal()
        }

        mViewModel.itemWithDiscountByQuantityCollection.observe(this) {
            if (!it.isNullOrEmpty()) {
                mViewModel.basketList.value!!.forEachIndexed { index, documentLines ->
                    if (documentLines.ItemCode == it[0].itemCode) {
                        documentLines.DiscountByQuantityCollection = it
                        documentLines.DiscountByQuantityLoading = false
                        mViewModel.applyDiscountByQuantity(index, documentLines.Quantity!!)
                    }
                }
            }
        }

        mViewModel.currentChosenBp.observe(this) {
            Log.d("BP", "CURRENT BP: ${it} / PREVIOUS BP: ${mViewModel.previousChosenBp.value}")

            if (it != null) {
                val phone =
                    if (mViewModel.isCopiedFrom.value == true) mViewModel.document.value?.U_phone else it.Phone1

                if (mViewModel.previousChosenBp.value != it) {
                    mViewModel.previousChosenBp.value = it
                    mViewModel.currentBpPhone.value =
                        if (it.CardCode == Preferences.defaultCustomer) "" else phone
                }
            } else {
                mViewModel.previousChosenBp.value = null
                mViewModel.currentBpPhone.value = ""
            }
        }


        mViewModel.insertItem.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.purchaseDeliverySelect)

        }


        mViewModel.insertedDocument.observe(this) {
            if (it != null) {

                if (!mViewModel.basketList.value.isNullOrEmpty())
                    printConfirmationDialog(this, mViewModel.basketList.value!!)

                mViewModel.clearAll()

            }


        }

        mViewModel.itemsListForImages.observe(this) {
            if (it != null) {
                it.forEach { listItem ->
                    mViewModel.getItemImage(listItem.ItemCode!!)
                }
            }
        }

        mViewModel.errorItem.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        mViewModel.connectionError.observe(this) {
            Toast.makeText(this, "Connection error: ${mViewModel.errorString}", Toast.LENGTH_SHORT)
                .show()

            if (mViewModel.itemsLoading.value == true) {
                mViewModel.itemsLoading.value = false
            }

            if (mViewModel.loadingInsert.value == true) {
                mViewModel.loadingInsert.value = false
            }

            if (mViewModel.bpLoading.value == true) {
                mViewModel.bpLoading.value = false
            }

            if (mViewModel.managersLoading.value == true) {
                mViewModel.managersLoading.value = false
            }

            if (mViewModel.warehousesLoading.value == true) {
                mViewModel.warehousesLoading.value = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_purchase_delivery, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_open_scanner -> {

                /*val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setBeepEnabled(true)
                intentIntegrator.setOrientationLocked(false)
                intentIntegrator.setCameraId(0)
                intentIntegrator.setPrompt("SCAN")
                intentIntegrator.setBarcodeImageEnabled(false)
                intentIntegrator.initiateScan()

                 */

                if (!mViewModel.basketList.value.isNullOrEmpty())
                    printConfirmationDialog(this, mViewModel.basketList.value!!)


                true
            }
            R.id.menu_generate_batches -> {
                mViewModel.generateBatchesToAllItems()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "ACTIVITY Nothing found", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "FOUND BARCODE: ${result.contents}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "ACTIVITY RESULT IS NULL", Toast.LENGTH_SHORT).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun printConfirmationDialog(
        activity: Activity,
        documentLines: List<DocumentLines>
    ) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_print_confirm)
        dialog.setCancelable(false)

        val layoutDialogProgress = dialog.findViewById<LinearLayout>(R.id.layoutDialogProgress)
        val layoutError = dialog.findViewById<LinearLayout>(R.id.layoutError)

        val printerIpAddress = dialog.findViewById<EditText>(R.id.etvPrinterIpAddress)
        val printerPort = dialog.findViewById<EditText>(R.id.etvPrinterPort)
        val btnYes = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnNo = dialog.findViewById<Button>(R.id.btnCancel)

        layoutDialogProgress.visibility = View.GONE
        layoutError.visibility = View.GONE

        var printingResult = false


        printerIpAddress.setText(Preferences.printerIp)
        printerPort.setText(Preferences.printerPort.toString())


        btnYes.setOnClickListener {
            val isSuccessful = PrinterHelper.printReceipt(this, documentLines)
            dialog.dismiss()

            /* mViewModel.connectAndPrint(
                 activity.applicationContext,
                 documentLines,
                 printerIp = if (mViewModel.printerError.value != null) printerIpAddress.text.toString() else null,
                 printerPort = if (mViewModel.printerError.value != null) printerPort.text.toString()
                     .toInt() else null,
             )*/
        }

        btnNo.setOnClickListener {
            mViewModel.clearAll()
            dialog.dismiss()
        }

        mViewModel.printerLoading.observe(this) {
            if (it) {
                layoutDialogProgress.visibility = View.VISIBLE
                layoutError.visibility = View.GONE
            }
        }

        mViewModel.printerError.observe(this) {
            if (it != null) {
                layoutDialogProgress.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
            }
        }

        mViewModel.printerSuccess.observe(this) {
            if (it) {
                mViewModel.printerLoading.postValue(false)
                mViewModel.printerSuccess.postValue(false)
                mViewModel.printerError.postValue(null)
                mViewModel.clearAll()
                dialog.dismiss()
            }
        }



        dialog.show()
        DialogUtils.resizeDialogWidth(dialog, this, 50)
    }

    private fun showLoader(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_loader)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnReload = dialog.findViewById<Button>(R.id.btnReload)

        dialog.setOnCancelListener {
            activity.finish()
        }
        mViewModel.loadingDocument.observe(this) {
            if (it) {
                loader.visibility = View.VISIBLE
                btnReload.visibility = View.GONE
            } else {
                dialog.dismiss()
            }
        }


        mViewModel.errorLoadingDocument.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            loader.visibility = View.GONE
            btnReload.visibility = View.VISIBLE
        }

        btnReload.setOnClickListener {
            if (mViewModel.document.value == null)
                mViewModel.loadPurchaseOrder(mViewModel.docEntry!!)
            else {
                mViewModel.getWarehouseList()
            }
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, activity, 70, 30)
    }


    fun setCurrency(currencyCode: String) {
        if (currencyCode == GeneralConsts.ALL_CURRENCY) {
            mViewModel.currentCurrency.value = Currencies(
                code = Preferences.localCurrency!!,
                name = Preferences.localCurrency!!,
                rate = 1.0
            )
        } else {
            if (mViewModel.currenciesList.value != null) {
                mViewModel.currentCurrency.value =
                    mViewModel.currenciesList.value!!.find { currency -> currency.code == currencyCode }
            } else {
                mViewModel.getCurrencies()
            }
        }

    }

    fun hasChanges(): Boolean {
        if (mViewModel.currentChosenBp.value != null) return true
        if (!mViewModel.basketList.value.isNullOrEmpty()) return true

        return false
    }

    fun canLoadItems(): Boolean {
        return !(mViewModel.currentChosenBp.value == null || mViewModel.currentWhsCode.value == null)
    }


}