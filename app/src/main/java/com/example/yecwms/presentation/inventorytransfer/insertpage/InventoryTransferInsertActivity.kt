package com.example.yecwms.presentation.inventorytransfer.insertpage

import android.app.Activity
import android.app.AlertDialog
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.databinding.ActivityInventoryTransferInsertBinding
import com.example.yecwms.util.DialogUtils

class InventoryTransferInsertActivity : BaseActivity() {

    private lateinit var binding: ActivityInventoryTransferInsertBinding
    private lateinit var mViewModel: InventoryTransferInsertViewModel

   /* private val getInventoryRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val docEntries =
                    result.data?.getLongArrayExtra(GeneralConsts.PASSED_INVENTORYREQUEST_DOCENTRY)
                if (docEntries != null) {
                    mViewModel.clearAll()
                    mViewModel.documentsToLoadCount.value = docEntries.size
                    mViewModel.documentsToLoad = convertLongArrayToList(docEntries)
                    showLoader(this)
                    mViewModel.loadManyInventoryRequest(convertLongArrayToList(docEntries))
                    mViewModel.isCopiedFromInventoryRequest.value = true
                } else {
                    mViewModel.isCopiedFromInventoryRequest.value = false
                }

            }

        }*/


    override fun init(savedInstanceState: Bundle?) {
        binding = ActivityInventoryTransferInsertBinding.inflate(layoutInflater)
        mViewModel = ViewModelProvider(this).get(InventoryTransferInsertViewModel::class.java)
        setContentView(binding.root)

        mViewModel.isCopiedFromInventoryRequest.value = false

      /*  val bundle: Bundle? = intent.extras
        if (bundle!=null) {
            val invTransferDocEntry =
                bundle?.getLong(GeneralConsts.PASSED_INVENTORYTRANSFER_DOCENTRY)
            if (invTransferDocEntry != 0L) {
                mViewModel.invTransferDocEntry = invTransferDocEntry!!
                mViewModel.loadInventoryTransfer(invTransferDocEntry)
                mViewModel.isViewMode.value = true
                showInvTransferLoader(this)
            } else {
                mViewModel.isViewMode.value = false
            }


            val invRequestDocEntry =
                bundle?.getLong(GeneralConsts.PASSED_INVENTORYREQUEST_DOCENTRY_SINGLE)
            if (invRequestDocEntry != 0L) {
                mViewModel.clearAll()
                mViewModel.documentsToLoadCount.value = 1
                mViewModel.documentsToLoad = arrayListOf(invRequestDocEntry)
                showLoader(this)
                mViewModel.loadManyInventoryRequest(listOf(invRequestDocEntry))
                mViewModel.isCopiedFromInventoryRequest.value = true
            } else {
                mViewModel.isCopiedFromInventoryRequest.value = false
            }
        }
*/

        mViewModel.baseDocumentsNumber.observe(this) {
            if (!it.isNullOrEmpty()) {
                supportActionBar?.title = "Основания №: $it"
            }
        }


        val navController = findNavController(R.id.fragmentInventoryTransferInsert)
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.invoiceInsert,
                R.id.invoiceBasket,
                R.id.invoicePaymentFragment
            )
        )*/
        //setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)

        mViewModel.insertItem.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.inventoryTransferSelectFragment)
        }

        mViewModel.errorItem.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        mViewModel.connectionError.observe(this) {
            Toast.makeText(this, "Connection error: ${mViewModel.errorString}", Toast.LENGTH_SHORT)
                .show()
        }
    }
/*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_list -> {
                if (mViewModel.fromWarehouse.value == null || mViewModel.toWarehouse.value == null) {
                    Toast.makeText(this, "Сначала выберите склады!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val intent = Intent(this, InventoryRequestListActivity::class.java)
                    intent.putExtra(
                        GeneralConsts.FROM_WHS_CODE,
                        mViewModel.fromWarehouse.value!!
                    )
                    intent.putExtra(
                        GeneralConsts.TO_WHS_CODE,
                        mViewModel.toWarehouse.value!!
                    )
                    getInventoryRequest.launch(intent)
                }


                /*
                if (mViewModel.isViewMode.value == null || !mViewModel.isViewMode.value!!) {

                    if (hasChanges())
                        showAlertDialog()
                    else {
                        mViewModel.clearAll()
                        getInventoryRequest.launch(Intent(this, InventoryRequestListActivity::class.java))
                    }

                }*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
*/
    lateinit var loaderDialog: Dialog
    private fun showLoader(activity: Activity) {

        Log.d("LOADEDDOCUMENTS", "dialog shown")
        loaderDialog = Dialog(activity)
        loaderDialog.setCanceledOnTouchOutside(false)
        loaderDialog.setCancelable(false)
        loaderDialog.setContentView(R.layout.dialog_loader)
        loaderDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val loader = loaderDialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnReload = loaderDialog.findViewById<Button>(R.id.btnReload)
        val btnCancel = loaderDialog.findViewById<Button>(R.id.btnCancel)


        mViewModel.loadingDocument.observe(this) {
            if (it) {
                loader.visibility = View.VISIBLE
                btnReload.visibility = View.GONE
                btnCancel.visibility = View.GONE
            } else {
                loader.visibility = View.GONE
                btnReload.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
            }
        }


        mViewModel.loadedDocumentsCount.observe(this) {
            if (it != 0 && it == mViewModel.documentsToLoadCount.value) {
                mViewModel.documentsToLoad = arrayListOf()
                mViewModel.documentsToLoadCount.value = 0
                mViewModel.loadedDocumentsCount.value = 0
                mViewModel.proceededDocumentsCount.value = 0
                loaderDialog.dismiss()
            }
        }

        /*
        mViewModel.proceededDocumentsCount.observe(this, {
            if (it == mViewModel.documentsToLoadCount.value) {
                mViewModel.loadingDocument.value = false
            }
        })

         */

        mViewModel.errorLoadingDocument.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        btnCancel.setOnClickListener {
            mViewModel.documentsToLoad = arrayListOf()
            mViewModel.documentsToLoadCount.value = 0
            mViewModel.loadedDocumentsCount.value = 0
            mViewModel.proceededDocumentsCount.value = 0
            mViewModel.clearAll()
            loaderDialog.dismiss()
        }

        btnReload.setOnClickListener {
            mViewModel.loadInventoryRequestJob?.cancel()
            Log.d("LOADEDDOCUMENTS", mViewModel.documentsToLoad.size.toString())
            mViewModel.loadManyInventoryRequest(mViewModel.documentsToLoad)
        }

        loaderDialog.show()
        DialogUtils.resizeDialog(loaderDialog, this, 70, 30)
    }

    lateinit var invTransferLoaderDialog: Dialog
    private fun showInvTransferLoader(activity: Activity) {

        Log.d("LOADEDDOCUMENTS", "dialog shown")
        invTransferLoaderDialog = Dialog(activity)
        invTransferLoaderDialog.setCanceledOnTouchOutside(false)
        invTransferLoaderDialog.setCancelable(false)
        invTransferLoaderDialog.setContentView(R.layout.dialog_loader)
        invTransferLoaderDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val loader = invTransferLoaderDialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnReload = invTransferLoaderDialog.findViewById<Button>(R.id.btnReload)
        val btnCancel = invTransferLoaderDialog.findViewById<Button>(R.id.btnCancel)


        mViewModel.loadingDocument.observe(this) {
            if (it) {
                loader.visibility = View.VISIBLE
                btnReload.visibility = View.GONE
                btnCancel.visibility = View.GONE
            } else {
                loader.visibility = View.GONE
                btnReload.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
            }
        }


        mViewModel.fromWarehouse.observe(this) {
            invTransferLoaderDialog.dismiss()
        }


        mViewModel.errorLoadingDocument.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            activity.finish()
        }

        btnReload.setOnClickListener {
            mViewModel.loadInventoryTransfer(mViewModel.invTransferDocEntry)
        }

        invTransferLoaderDialog.show()
        DialogUtils.resizeDialog(invTransferLoaderDialog, this, 70, 30)
    }


    fun hasChanges(): Boolean {
        if (mViewModel.isViewMode.value != null && mViewModel.isViewMode.value!!) return false
        if (mViewModel.fromWarehouse.value != null) return true
        if (mViewModel.toWarehouse.value != null) return true
        if (!mViewModel.basketList.value.isNullOrEmpty()) return true
        return false
    }

    /*
    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.doc_changed)
            .setMessage(R.string.doc_resetchanges)
            .setNegativeButton("Назад") { _, _ ->

            }
            .setPositiveButton("Сбросить") { _, _ ->
                mViewModel.clearAll()
                getInventoryRequest.launch(Intent(this, InventoryRequestListActivity::class.java))
            }
            .create()
            .show()
    }*/


}