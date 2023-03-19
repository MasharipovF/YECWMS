package com.example.yecwms.presentation.businesspartners.bpadd

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.core.ValidationStatusEnum
import com.example.yecwms.data.Preferences
import com.example.yecwms.databinding.ActivityBpAddBinding
import com.example.yecwms.presentation.items.adapter.SpinnerAdapter
import com.example.yecwms.util.MaskWatcher


class BpAddActivity : BaseActivity() {

    private lateinit var mViewModel: BpAddViewModel
    private lateinit var binding: ActivityBpAddBinding
    private lateinit var priceListsAdapter: SpinnerAdapter
    private lateinit var bpGroupsAdapter: SpinnerAdapter
    private lateinit var seriesAdapter: SpinnerAdapter

    override fun init(savedInstanceState: Bundle?) {
        binding = ActivityBpAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel = ViewModelProvider(this).get(BpAddViewModel::class.java)

        priceListsAdapter = SpinnerAdapter(this)
        binding.spinPricelist.adapter = priceListsAdapter

        bpGroupsAdapter = SpinnerAdapter(this)
        binding.spinCardGroup.adapter = bpGroupsAdapter

        seriesAdapter = SpinnerAdapter(this)
        binding.spinSeries.adapter = seriesAdapter

        binding.etvCreditLimit.setText(GeneralConsts.BP_DEFAULT_LIMIT.toString())
        binding.etvLimitCurrency.setText(
            Preferences.localCurrency ?: ""
        )


        mViewModel.chosenBpGroupType.value = GeneralConsts.BP_TYPE_CUSTOMER

        mViewModel.chosenBpGroupType.observe(this) {
            mViewModel.loadPage()
        }

        mViewModel.connectionError.observe(this) {
            Toast.makeText(this, mViewModel.errorString, Toast.LENGTH_SHORT).show()

            if (mViewModel.loadingPage.value != null && mViewModel.loadingPage.value!!) {
                mViewModel.errorLoadingPage.value = true
                mViewModel.loadingPage.value = false
            }

            if (mViewModel.loadingPhoneCheck.value != null && mViewModel.loadingPhoneCheck.value!!) {
                mViewModel.loadingPhoneCheck.value = false
                mViewModel.phoneCheckLoadingStatus.value = ValidationStatusEnum.CONNECTION_ERROR
            }
        }

        mViewModel.loadingPage.observe(this) {
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
        }


        mViewModel.errorLoadingPage.observe(this) {
            Log.d("ERROR", "errorloadigpage $it")
            if (it) {
                binding.textView.text = getString(R.string.btn_reload)
            } else {
                binding.textView.text = getString(R.string.btn_add)
            }
        }

        mViewModel.listPricelists.observe(this) {
            priceListsAdapter.list = it as MutableList<Any>
        }

        mViewModel.listBpGroups.observe(this) {
            bpGroupsAdapter.list = it as MutableList<Any>
        }

        mViewModel.listSeries.observe(this) {
            seriesAdapter.list = it as MutableList<Any>
        }

        mViewModel.insertedBp.observe(this) {
            Toast.makeText(this, "Added card code: ${it.CardName}", Toast.LENGTH_SHORT).show()
            val intent = Intent()
            intent.putExtra(GeneralConsts.PASSED_BP, it)
            setResult(RESULT_OK, intent)
            finish()
        }

        mViewModel.errorAddNewBP.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT)
                .show()
        }

        var prevPhoneValue = ""

        binding.etvPhone.addTextChangedListener(MaskWatcher("#########"))
        binding.etvPhone.setOnFocusChangeListener { view, hasFocus ->
            val phone =  binding.etvPhone.text.toString()
            if (!hasFocus) {
                if (phone.length < GeneralConsts.PHONE_NUMBER_LENGTH && phone.isNotEmpty()) {
                    setPhoneCheckerError("Номер не указан полностью")
                    return@setOnFocusChangeListener
                }

                if (phone.isNotEmpty() && prevPhoneValue != phone) {
                    //mViewModel.checkIfPhoneExists(phone, GeneralConsts.BP_TYPE_CUSTOMER)
                } else if (phone.isEmpty()) {
                    resetPhoneChecker()
                }
            }
            prevPhoneValue = phone
        }

        binding.etvCardName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                binding.tilCardName.error = null
        }

        mViewModel.loadingPhoneCheck.observe(this) {
            if (it) {
                resetPhoneChecker()
                binding.phoneCheckLoader.visibility = View.VISIBLE
                binding.btnAdd.isEnabled = false
            } else {
                binding.phoneCheckLoader.visibility = View.GONE
            }
        }

        mViewModel.phoneCheckLoadingStatus.observe(this) {
            when (it) {
                ValidationStatusEnum.VALIDATION_FAIL -> {
                }
                ValidationStatusEnum.VALIDATION_SUCCESS -> {
                    setPhoneCheckerSucces()
                    binding.btnAdd.isEnabled = true
                }
                ValidationStatusEnum.ERROR_FROM_API -> {
                    setPhoneCheckerFailedToLoad()
                }
                ValidationStatusEnum.CONNECTION_ERROR -> {
                    setPhoneCheckerFailedToLoad()
                }
                else -> {
                }
            }
        }

        mViewModel.phoneNumberExists.observe(this, {
            setPhoneCheckerError("Клиент с таким номером уже есть в системе.\n$it")
        })

        binding.imgPhoneCheck.setOnClickListener {
            val phone = binding.etvPhone.text.toString()
            if (phone.isNotEmpty())
                mViewModel.checkIfPhoneExists(
                    phone,
                    GeneralConsts.BP_TYPE_CUSTOMER
                )
        }


        binding.btnAdd.setOnClickListener {
            val phone =  binding.etvPhone.text.toString()
            when {
                phone.isEmpty() -> {
                    binding.tilPhone.error = "Заполните поле"
                    return@setOnClickListener
                }
                else -> binding.tilPhone.error = null
            }

            val cardname = binding.etvCardName.text.toString()
            if (cardname.isEmpty()) {
                binding.tilCardName.error = "Заполните поле"
                return@setOnClickListener
            } else binding.tilCardName.error = null

            val address = binding.etvAddress.text.toString()
            val groupCodePosition = binding.spinCardGroup.selectedItemPosition
            val pricelistCodePosition = binding.spinPricelist.selectedItemPosition
            val series = binding.spinSeries.selectedItemPosition


            val limit: Double = if (binding.etvCreditLimit.text.toString().isNotEmpty())
                binding.etvCreditLimit.text.toString().toDouble() else 0.0

            mViewModel.setNewBpData(
                cardName = cardname,
                phone = phone,
                address = address,
                seriesCodePosition = series,
                groupCodePosition = groupCodePosition,
                priceListCodePosition = pricelistCodePosition,
                limit = limit
            )
        }
    }

    fun hasChanges(): Boolean {

        val cardname = binding.etvCardName.text.toString()
        if (cardname.isNotEmpty()) return true

        val address = binding.etvAddress.text.toString()
        if (address.isNotEmpty()) return true

        val phone = binding.etvPhone.text.toString()
        if (phone.isNotEmpty()) return true

        val limit = binding.etvCreditLimit.text.toString()
        if (limit.toInt() != GeneralConsts.BP_DEFAULT_LIMIT) return true

        return false
    }


    private fun setPhoneCheckerSucces() {
        binding.tilPhone.error = null
        binding.imgPhoneCheck.visibility = View.VISIBLE
        binding.imgPhoneCheck.setImageResource(R.drawable.ic_baseline_check_circle)
        binding.imgPhoneCheck.isClickable = false

    }

    private fun setPhoneCheckerError(errorString: String) {
        binding.imgPhoneCheck.visibility = View.GONE
        binding.tilPhone.error = errorString
        binding.imgPhoneCheck.isClickable = false

    }

    private fun setPhoneCheckerFailedToLoad() {
        binding.tilPhone.error = null
        binding.imgPhoneCheck.setImageResource(R.drawable.ic_outline_loading)
        binding.imgPhoneCheck.visibility = View.VISIBLE
        binding.imgPhoneCheck.isClickable = true
    }

    private fun resetPhoneChecker() {
        binding.tilPhone.error = null
        binding.imgPhoneCheck.isClickable = false
        binding.imgPhoneCheck.visibility = View.GONE
    }

}