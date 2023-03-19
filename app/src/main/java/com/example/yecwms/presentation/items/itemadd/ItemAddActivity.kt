package com.example.yecwms.presentation.items.itemadd

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.core.ValidationStatusEnum
import com.example.yecwms.databinding.ActivityItemAddBinding
import com.example.yecwms.presentation.items.adapter.SpinnerAdapter


class ItemAddActivity : BaseActivity() {

    private lateinit var mViewModel: ItemAddViewModel
    private lateinit var binding: ActivityItemAddBinding
    private lateinit var itemGroupAdapter: SpinnerAdapter
    private lateinit var uomGroupAdapter: SpinnerAdapter
    private lateinit var uomAdapter: SpinnerAdapter
        

    override fun init(savedInstanceState: Bundle?) {

        binding = ActivityItemAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel = ViewModelProvider(this).get(ItemAddViewModel::class.java)


        itemGroupAdapter = SpinnerAdapter(this)
        binding.spinItemGroup.adapter = itemGroupAdapter

        uomAdapter = SpinnerAdapter(this)
        binding.spinInvenrotyUom.adapter = uomAdapter
        binding.spinPurchaseUom.adapter = uomAdapter
        binding.spinSalesUom.adapter = uomAdapter

        uomGroupAdapter = SpinnerAdapter(this)
        binding.spinUomGroup.adapter = uomGroupAdapter



        mViewModel.connectionError.observe(this, {
            Toast.makeText(this, mViewModel.errorString, Toast.LENGTH_SHORT).show()

            if (mViewModel.loadingPage.value != null && mViewModel.loadingPage.value!!) {
                mViewModel.errorLoadingPage.value = true
                mViewModel.loadingPage.value = false
            }

            if (mViewModel.loadingBarcodeCheck.value != null && mViewModel.loadingBarcodeCheck.value!!) {
                mViewModel.loadingBarcodeCheck.value = false
                mViewModel.barcodeCheckLoadingStatus.value = ValidationStatusEnum.CONNECTION_ERROR
            }

            if (mViewModel.loadingBarcodeGet.value != null && mViewModel.loadingBarcodeGet.value!!) {
                mViewModel.loadingBarcodeGet.value = false
                mViewModel.barcodeGetLoadingStatus.value = ValidationStatusEnum.CONNECTION_ERROR
            }
        })

        mViewModel.loadingPage.observe(this, {
            if (it) {
                binding.textView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.btnAdd.isClickable = false
                binding.btnAdd.isFocusable = false
            } else {
                binding.textView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.btnAdd.isClickable = true
                binding.btnAdd.isFocusable = true
            }
        })

        mViewModel.errorLoadingPage.observe(this, {
            Log.d("ERROR", "errorloadigpage $it")
            if (it) {
                binding.textView.text = getString(R.string.btn_reload)
            } else {
                binding.textView.text = getString(R.string.btn_add)
            }
        })


        mViewModel.listItemGroup.observe(this, {
            itemGroupAdapter.list = it as MutableList<Any>
        })

        mViewModel.listUomGroup.observe(this, {
            uomGroupAdapter.list = it as MutableList<Any>

        })

        mViewModel.listUom.observe(this, {
            uomAdapter.list = it as MutableList<Any>

        })

        mViewModel.insertItem.observe(this, {
            Toast.makeText(this, "Added item code: $it", Toast.LENGTH_SHORT).show()
            finish()
        })

        mViewModel.errorAddNewItem.observe(this, {
            Toast.makeText(this, "Error while adding new item", Toast.LENGTH_SHORT)
                .show()

        })

        binding.spinUomGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                mViewModel.getUoms(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        var prevBarcodeValue = ""
        binding.etvBarCode.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(GeneralConsts.BARCODE_LENGTH))
        binding.etvBarCode.setOnFocusChangeListener { view, hasFocus ->
            val barcode = binding.etvBarCode.text.toString()
            if (!hasFocus) {
                if (barcode.length < GeneralConsts.BARCODE_LENGTH && barcode.isNotEmpty()) {
                    setBarcodeCheckerError("Штрих код должен состоять из 13 цифр")
                    return@setOnFocusChangeListener
                }

                //TODO BARCODE CHECKER

                if (barcode.isNotEmpty() && prevBarcodeValue != barcode) {
                    mViewModel.checkIfBarcodeExists(barcode)
                } else if (barcode.isEmpty()) {
                    resetBarcodeChecker()
                }
            }
            prevBarcodeValue = barcode
        }

        binding.etvItemName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                binding.tilItemName.error = null
            val itemname = binding.etvItemName.text.toString()
            if (!hasFocus && itemname.isNotEmpty())
                binding.tilItemName.error = null
        }

        mViewModel.loadingBarcodeCheck.observe(this, {
            if (it) {
                resetBarcodeChecker()
                binding.barcodeCheckLoader.visibility = View.VISIBLE
            } else binding.barcodeCheckLoader.visibility = View.GONE
        })

        mViewModel.barcodeCheckLoadingStatus.observe(this, {
            when (it) {
                ValidationStatusEnum.VALIDATION_FAIL -> {
                    setBarcodeCheckerError("Такой штрих код существует в системе")
                }
                ValidationStatusEnum.VALIDATION_SUCCESS -> {
                    setBarcodeCheckerSucces()
                }
                ValidationStatusEnum.ERROR_FROM_API -> {
                    setBarcodeCheckerFailedToLoad()
                }
                ValidationStatusEnum.CONNECTION_ERROR -> {
                    setBarcodeCheckerFailedToLoad()
                }
                else -> {
                }
            }
        })

        mViewModel.barcode.observe(this, {
            binding.etvBarCode.setText(it)
            binding.etvBarCode.clearFocus()
            mViewModel.checkIfBarcodeExists(it)
            binding.imgBarCode.setImageResource(R.drawable.ic_baseline_qr_code)
        })

        mViewModel.loadingBarcodeGet.observe(this, {
            if (it) {
                binding.getBarCodeLoader.visibility = View.VISIBLE
                binding.imgBarCode.visibility = View.GONE
            } else {
                binding.getBarCodeLoader.visibility = View.GONE
                binding.imgBarCode.visibility = View.VISIBLE
            }
        })

        mViewModel.barcodeGetLoadingStatus.observe(this, {
            if (it == ValidationStatusEnum.ERROR_FROM_API || it == ValidationStatusEnum.CONNECTION_ERROR)
                binding.imgBarCode.setImageResource(R.drawable.ic_outline_loading)
        })

        binding.btnGetBarCode.setOnClickListener {
            mViewModel.generateBarCode()
        }

        binding.btnAdd.setOnClickListener {

            val itemName: String = binding.etvItemName.text.toString()
            if (itemName.isEmpty()) {
                binding.tilItemName.error = "Заполните поле"
                return@setOnClickListener
            } else binding.tilItemName.error = null


            val itemGroupPosition: Int = binding.spinItemGroup.selectedItemPosition
            val barCode: String = binding.etvBarCode.text.toString()
            val uomGroupPosition: Int = binding.spinUomGroup.selectedItemPosition
            val invUomPosition: Int = binding.spinInvenrotyUom.selectedItemPosition
            val salesUomPosition: Int = binding.spinSalesUom.selectedItemPosition
            val purchaseUomPosition: Int = binding.spinPurchaseUom.selectedItemPosition

            Log.d("SPINNER", itemGroupPosition.toString())

            mViewModel.setNewItemData(
                itemName,
                itemGroupPosition,
                barCode,
                uomGroupPosition,
                invUomPosition,
                salesUomPosition,
                purchaseUomPosition
            )
        }

    }

    fun hasChanges(): Boolean {
        val itemName: String = binding.etvItemName.text.toString()
        if (itemName.isNotEmpty()) return true

        val barCode: String = binding.etvBarCode.text.toString()
        if (barCode.isNotEmpty()) return true

        return false
    }

    private fun setBarcodeCheckerSucces() {
        binding.tilBarcode.error = null
        binding.imgBarcodeCheck.visibility = View.VISIBLE
        binding.imgBarcodeCheck.setImageResource(R.drawable.ic_baseline_check_circle)
        binding.imgBarcodeCheck.isClickable = false
    }

    private fun setBarcodeCheckerError(errorString: String) {
        binding.imgBarcodeCheck.visibility = View.GONE
        binding.tilBarcode.error = errorString
        binding.imgBarcodeCheck.isClickable = false
    }

    private fun setBarcodeCheckerFailedToLoad() {
        binding.tilBarcode.error = null
        binding.imgBarcodeCheck.setImageResource(R.drawable.ic_outline_loading)
        binding.imgBarcodeCheck.visibility = View.VISIBLE
        binding.imgBarcodeCheck.isClickable = true
    }

    private fun resetBarcodeChecker() {
        binding.tilBarcode.error = null
        binding.imgBarcodeCheck.isClickable = false
        binding.imgBarcodeCheck.visibility = View.GONE
    }
    

}