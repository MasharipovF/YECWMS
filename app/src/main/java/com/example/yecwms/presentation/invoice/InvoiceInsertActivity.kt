package com.example.yecwms.presentation.invoice

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.databinding.ActivityInvoiceInsertBinding
import com.example.yecwms.domain.mappers.Mappers
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.adapters.ItemsListAdapter
import com.example.yecwms.util.barcodereader.CustomScannerActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class InvoiceInsertActivity : BaseActivity() {

    private lateinit var binding: ActivityInvoiceInsertBinding
    private lateinit var mViewModel: InvoiceInsertViewModel


    override fun init(savedInstanceState: Bundle?) {
        binding = ActivityInvoiceInsertBinding.inflate(layoutInflater)
        mViewModel = ViewModelProvider(this).get(InvoiceInsertViewModel::class.java)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentSalesOrder)
        binding.bottomNavView.setupWithNavController(navController)


        /*val bundle: Bundle? = intent.extras
        mViewModel.docEntry =
            bundle?.getLong(GeneralConsts.PASSED_PURCHASEORDER_DOCENTRY)
        if (mViewModel.docEntry != null) {
            mViewModel.isCopiedFrom.value = true
            mViewModel.loadPurchaseOrder(mViewModel.docEntry!!)
        }*/


        mViewModel.loadingDocument.observe(this) {
            if (it) {
                showLoader(this)
            }
        }


        mViewModel.document.observe(this) {
            mViewModel.getBp(it.CardCode!!)
            mViewModel.getManager(it.SalesManagerCode!!)
            mViewModel.currentWhsCode.postValue(it.DocumentLines[0].WarehouseCode)
            mViewModel.basketList.postValue(Mappers.getDocLinesWithBaseDoc(it, false))
            mViewModel.discount.postValue(it.DiscountPercent!!.toInt())
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

        mViewModel.itemsListForImages.observe(this) {
            if (it != null) {
                it.forEach { listItem ->
                    mViewModel.getItemImage(listItem.ItemCode!!)
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
            navController.navigate(R.id.salesOrderSelect)
        }


        mViewModel.insertedDocument.observe(this) {
            if (it != null) {
                mViewModel.clearAll()
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
        menuInflater.inflate(R.menu.menu_sales_order, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_open_scanner -> {
                if (!canLoadItems()) {
                    Toast.makeText(this, "Выберите клиента и склад!", Toast.LENGTH_SHORT).show()
                    return false
                }
                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
                options.setPrompt("Отсканируйте штрихкод")
                options.setCameraId(0) // Use a specific camera of the device
                options.setBeepEnabled(false)
                options.setBarcodeImageEnabled(true)
                options.setOrientationLocked(false)
                options.captureActivity = CustomScannerActivity::class.java
                barcodeLauncher.launch(options)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Ничего не найдено!", Toast.LENGTH_LONG).show()
        } else {
            showBarcodeResultsDialog(result.contents)
        }
    }

    private fun showBarcodeResultsDialog(itemCode: String) {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl_onhand_by_whs)
        val rv = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
        val dialogTitle = dialog.findViewById<TextView>(R.id.labelDialog)

        dialogTitle.text = "Штрихкод: $itemCode"

        val itemsAdapter = ItemsListAdapter(object :
            ItemsListAdapter.InvItemListClickListener {
            override fun onClick(item: Items) {
                val canAdd = mViewModel.addToBasket(
                    chosenItem = item,
                    priceUZS = item.DiscountedPrice,
                    quantityToAdd = if (item.ManageBatchNumbers==GeneralConsts.T_YES) item.QuantityOnStockByBatch else 1.0
                )

                if (!canAdd) {
                    Toast.makeText(
                        this@InvoiceInsertActivity,
                        "Количество сокращается до отрицательного значения! Товар: ${item.ItemName}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mViewModel.barCodeItemsList.value = null
                    dialog.dismiss()
                }
            }

            override fun loadMore(lastItemIndex: Int) {
                mViewModel.getMoreItemsListByBarCode(lastItemIndex)
            }

            override fun onImageClick(image: Bitmap?) {
                DialogUtils.showEnlargedImage(this@InvoiceInsertActivity, image)
            }

        })


        mViewModel.barcodeFilterString.value = itemCode
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = itemsAdapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        mViewModel.barCodeItemsLoading.observe(this) {
            if (it) {
                rv.visibility = View.GONE
                loader.visibility = View.VISIBLE
            } else {
                rv.visibility = View.VISIBLE
                loader.visibility = View.GONE
            }
        }

        mViewModel.barcodeFilterString.observe(this) {
            if (it != null) {
                mViewModel.getItemsListByBarCode()
                rv.scrollToPosition(0)
            }
        }


        mViewModel.barCodeItemFound.observe(this) {
            if (it != null) {

                val canAdd = mViewModel.addToBasket(
                    chosenItem = it,
                    priceUZS = it.DiscountedPrice,
                    quantityToAdd = if (it.ManageBatchNumbers==GeneralConsts.T_YES) it.QuantityOnStockByBatch else 1.0
                )
                if (!canAdd) {
                    Toast.makeText(
                        this,
                        "Количество сокращается до отрицательного значения! Товар: ${it.ItemName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                mViewModel.barCodeItemFound.value = null
            }
            dialog.dismiss()
        }

        mViewModel.barCodeItemNotFound.observe(this) {
            if (it == true) {
                Toast.makeText(
                    this,
                    "Ничего не найдено!",
                    Toast.LENGTH_SHORT
                ).show()
                mViewModel.barCodeItemNotFound.value = false
            }
            dialog.dismiss()
        }

        mViewModel.barCodeItemsList.observe(this) {
            if (it != null) {
                itemsAdapter.list = it
            } else {
                itemsAdapter.clearList()
            }
        }

        mViewModel.errorBarCodeItemsLoading.observe(this) {
            Toast.makeText(
                this,
                "Ошибка при загрузке товаров: ${it.toString()}",
                Toast.LENGTH_SHORT
            ).show()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, this, 100, 70)
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
            /*if (mViewModel.document.value == null)
                mViewModel.loadPurchaseOrder(mViewModel.docEntry!!)
            else {
                mViewModel.getWarehouseList()
            }*/
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, activity, 70, 30)
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