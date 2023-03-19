package com.example.yecwms.presentation.purchasedeliveries.openpurchaseorderslist

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.databinding.ActivityOpenPurchaseOrdersListBinding
import com.example.yecwms.presentation.purchasedeliveries.insert.PurchaseDeliveryInsertActivity
import com.example.yecwms.presentation.purchasedeliveries.openpurchaseorderslist.adapter.OpenPurchaseOrdersListAdapter
import com.example.yecwms.util.Utils
import java.util.*

class OpenPurchaseOrdersListActivity : BaseActivity() {
    var isActivityForResult: Boolean = false

    private lateinit var binding: ActivityOpenPurchaseOrdersListBinding
    private lateinit var mViewModel: OpenPurchaseOrdersListViewModel
    private lateinit var adapter: OpenPurchaseOrdersListAdapter

    override fun init(savedInstanceState: Bundle?) {
        binding = ActivityOpenPurchaseOrdersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (callingActivity != null) isActivityForResult = true

        mViewModel = ViewModelProvider(this).get(OpenPurchaseOrdersListViewModel::class.java)

        adapter = OpenPurchaseOrdersListAdapter(object :
            OpenPurchaseOrdersListAdapter.OnAdapterItemClickListener {

            override fun onClick(doc: Document) {
                val intent = Intent(
                    this@OpenPurchaseOrdersListActivity,
                    PurchaseDeliveryInsertActivity::class.java
                )

                intent.putExtra(GeneralConsts.PASSED_PURCHASEORDER_DOCENTRY, doc.DocEntry)
                startActivity(intent)
                /*
                val activity = activity as InventoryTransferRequestListActivity
                if (activity.isActivityForResult) {
                    val intent = Intent()
                    intent.putExtra(GeneralConsts.PASSED_Order_DOCENTRY, request.docEntry)
                    activity.setResult(RESULT_OK, intent)
                    activity.finish()
                } else {
                    val intent = Intent(this, OrderInfoActivity::class.java)
                    intent.putExtra(GeneralConsts.PASSED_Order_DOCENTRY, request.docEntry)
                    startActivity(intent)
                }

                 */
            }

            override fun loadMore(lastItemIndex: Int) {
                mViewModel.getMorePurchaseOrdersList(lastItemIndex)
            }

        }, this)

        binding.rvDocuments.layoutManager = LinearLayoutManager(this)
        binding.rvDocuments.adapter = adapter


        mViewModel.connectionError.observe(this, {
            Toast.makeText(this, mViewModel.errorString, Toast.LENGTH_SHORT).show()
        })

        mViewModel.dateFrom.observe(this, {
            if (it != null)
                binding.tvDateFrom.text = Utils.convertUSAdatetoNormal(it)
            else
                binding.tvDateFrom.text = ""

            mViewModel.getPurchaseOrdersList()
        })

        binding.tvDateFrom.setOnClickListener {
            showDatePicker(this, true)
        }

        mViewModel.dateTo.observe(this, {
            if (it != null)
                binding.tvDateTo.text = Utils.convertUSAdatetoNormal(it)
            else
                binding.tvDateTo.text = ""

            mViewModel.getPurchaseOrdersList()
        })

        binding.imgBtnClearDateFilter.setOnClickListener {
            mViewModel.dateTo.value = null
            mViewModel.dateFrom.value = null
        }

        binding.tvDateTo.setOnClickListener {
            showDatePicker(this, false)
        }

        mViewModel.listToDraw.observe(this, {
            adapter.list = it
            Log.d("OrderS", it.toString())
        })

        mViewModel.errorLoading.observe(this, {
            Toast.makeText(
                this, it, Toast.LENGTH_SHORT
            ).show()
        })

        mViewModel.loading.observe(this, {
            if (it)
                binding.loader.visibility = View.VISIBLE
            else binding.loader.visibility = View.GONE
        })

        mViewModel.filterString.observe(this, {
            if (it != null) {
                mViewModel.getPurchaseOrdersList()
                binding.rvDocuments.scrollToPosition(0)
            } else {
                binding.etvSearch.text?.clear()
            }
        })


        binding.etvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val prevSearchString = mViewModel.filterString.value
                if (prevSearchString != null) {
                    val string = binding.etvSearch.text.toString()
                    if (prevSearchString != string) mViewModel.filterString.value = string
                }
            }

        })

        binding.imgBtnClearSearch.setOnClickListener {
            binding.etvSearch.text?.clear()
        }

        binding.fabAddDocument.setOnClickListener {
            val intent = Intent(
                this,
                PurchaseDeliveryInsertActivity::class.java
            )
            startActivity(intent)
        }
    }

    private fun showDatePicker(context: Context, isDateFrom: Boolean) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            { _, Year, monthOfYear, dayOfMonth ->
                if (isDateFrom)
                    mViewModel.dateFrom.value = Utils.getDateInUSAFormat(
                        Year.toString(),
                        (monthOfYear + 1).toString(),
                        dayOfMonth.toString()
                    )
                else
                    mViewModel.dateTo.value = Utils.getDateInUSAFormat(
                        Year.toString(),
                        (monthOfYear + 1).toString(),
                        dayOfMonth.toString()
                    )
            },
            year,
            month,
            day
        )
        dpd.show()
    }


    override fun onResume() {
        super.onResume()
        mViewModel.getPurchaseOrdersList()
    }


}