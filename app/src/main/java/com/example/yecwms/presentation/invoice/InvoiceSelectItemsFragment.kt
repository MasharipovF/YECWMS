package com.example.yecwms.presentation.invoice

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.yecwms.R
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.masterdatas.Currencies
import com.example.yecwms.data.entity.masterdatas.SalesManagers
import com.example.yecwms.data.entity.masterdatas.Warehouses
import com.example.yecwms.databinding.FragmentInvoiceBasketBinding
import com.example.yecwms.databinding.FragmentInvoiceSelectBinding
import com.example.yecwms.presentation.businesspartners.adapter.BpListAdapter
import com.example.yecwms.presentation.businesspartners.bpadd.BpAddActivity
import com.example.yecwms.util.CalculatorBottomSheet
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.Utils
import com.example.yecwms.util.adapters.CurrenciesListAdapter
import com.example.yecwms.util.adapters.ItemsListAdapter
import com.example.yecwms.util.adapters.SalesManagersListAdapter
import com.example.yecwms.util.adapters.WarehousesListAdapter
import com.example.yecwms.util.customviews.SearchEditText

class InvoiceSelectItemsFragment : Fragment() {

    private val getNewBpFromActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bp =
                    result.data?.getParcelableExtra<BusinessPartners>(GeneralConsts.PASSED_BP)
                mViewModel.currentChosenBp.value = bp
            }

        }
    private lateinit var binding: FragmentInvoiceSelectBinding
    private lateinit var itemsAdapter: ItemsListAdapter
    private lateinit var mViewModel: InvoiceInsertViewModel


    companion object {
        fun newInstance() = InvoiceSelectItemsFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invoice_select, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInvoiceSelectBinding.bind(view)
        mViewModel =
            ViewModelProvider(requireActivity()).get(InvoiceInsertViewModel::class.java)

        mViewModel.isCopiedFrom.observe(viewLifecycleOwner) {
            if (!it) {
                if (mViewModel.currentWhsCode.value == null) {
                    showWarehousesListDialog(requireActivity())
                }
            }
        }



        itemsAdapter = ItemsListAdapter(object :
            ItemsListAdapter.InvItemListClickListener {
            override fun onClick(item: Items) {
                val isKeyboardVisible = Utils.isSoftKeyboardVisible(binding.rootLayout)
                if (isKeyboardVisible) {
                    Utils.hideSoftKeyboard(requireActivity())
                    collapseItemsSection()
                }

                val canAdd = mViewModel.addToBasket(item, if (item.ManageBatchNumbers==GeneralConsts.T_YES) item.QuantityOnStockByBatch else 1.0, item.DiscountedPrice)
                if (!canAdd) Toast.makeText(
                    requireContext(),
                    "Количество должно быть больше 0!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun loadMore(lastItemIndex: Int) {
                mViewModel.getMoreItemsList(lastItemIndex)
            }

            override fun onImageClick(image: Bitmap?) {
                DialogUtils.showEnlargedImage(requireContext(), image)
            }

        })
        binding.recyclerViewDocRows.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDocRows.adapter = itemsAdapter


        mViewModel.isCopiedFrom.observe(viewLifecycleOwner) {
            disableBodySection(it)
        }


        // VIEWMODELS RELATED TO ITEM LIST
        mViewModel.itemsLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.itemsLoader.visibility = View.VISIBLE
            } else {
                binding.itemsLoader.visibility = View.GONE
            }
        }


        mViewModel.itemsFilterString.observe(viewLifecycleOwner) {
            if (it != null) {
                if ((activity as InvoiceInsertActivity).canLoadItems()) {
                    mViewModel.getItemsList()
                }

                binding.recyclerViewDocRows.scrollToPosition(0)
            }
        }

        /*   mViewModel.barcodeFilterString.observe(viewLifecycleOwner) {
               if (!it.isNullOrEmpty()) {
                   mViewModel.getItemByBarCode()
                   binding.recyclerViewDocRows.scrollToPosition(0)
               }
           }*/

        mViewModel.itemsList.observe(viewLifecycleOwner) {

            if (it != null) {
                Log.d("ITEMSLIST", "ITEMSLIST" + it.toString())
                if (mViewModel.isBarcodeSwitchChecked.value == true) {
                    if (it.size == 1) {
                        val item = it[0] as Items
                        val canAdd = mViewModel.addToBasket(item, 1.0, item.DiscountedPrice!!)
                        if (!canAdd)
                            Toast.makeText(
                                requireContext(),
                                "Количество сокращается до отрицательного значения!",
                                Toast.LENGTH_SHORT
                            ).show()
                        mViewModel.barcodeFilterString.value = ""
                    }
                } else {
                    itemsAdapter.list = it
                }

                binding.etvSearch.isEnabled = true
                binding.switchBarcode.isEnabled = true
            } else {
                itemsAdapter.clearList()
                binding.etvSearch.isEnabled = false
                binding.switchBarcode.isEnabled = false
            }
        }

        mViewModel.errorItemsLoading.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()
        }

        mViewModel.currentQuantity.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvQuantity.isEnabled = true
                if (!binding.tvQuantity.isFocused) binding.tvQuantity.setText(
                    it.toInt().toString()
                )
            } else {
                binding.tvQuantity.text = ""
                binding.tvQuantity.isEnabled = false
            }
        }
        mViewModel.currentPrice.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvPrice.isEnabled = true
                if (!binding.tvPrice.isFocused) binding.tvPrice.setText(it.toString())
            } else {
                binding.tvPrice.text = ""
                binding.tvPrice.isEnabled = false
            }
        }

        mViewModel.currentChosenItem.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvItemName.text = it.ItemName
            } else {
                binding.tvItemName.setText(R.string.iteminfo_item_name)
            }
        }

        mViewModel.isBarcodeSwitchChecked.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.switchBarcode.isChecked = it
            } else {
                binding.switchBarcode.isChecked = false
            }
        }

        mViewModel.currentWhsCode.observe(viewLifecycleOwner) {
            if (it != null) {
                if ((activity as InvoiceInsertActivity).canLoadItems()) {
                    mViewModel.getItemsList()
                }
                binding.tvWarehouse.text = it
            }
        }

        binding.tvWarehouse.setOnClickListener {
            showWarehousesListDialog(requireActivity())
        }

        mViewModel.currentCurrency.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvCurrency.text = it.code
                if (it.rate==null || it.rate==0.0) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.no_rate_set),
                        Toast.LENGTH_LONG
                    ).show()
                }

                itemsAdapter.setCurrencyAndRate(it.code, it.rate?:0.0)
            } else {
                binding.tvCurrency.text = ""
            }
        }

        mViewModel.currenciesList.observe(viewLifecycleOwner) {
            if (it != null && mViewModel.currentChosenBp.value != null) {
                mViewModel.currentCurrency.value =
                    it.find { currency -> currency.code == mViewModel.currentChosenBp.value!!.Currency }
            }
        }

        binding.tvCurrency.setOnClickListener {
            if (mViewModel.currentChosenBp.value?.Currency == GeneralConsts.ALL_CURRENCY){
                showCurrenciesListDialog(requireActivity())
            }
        }

        binding.tvManager.setOnClickListener {
            showManagersListDialog(requireActivity())
        }

        mViewModel.currentChosenManager.observe(viewLifecycleOwner) {
            if (it != null)
                binding.tvManager.text = it.salesEmployeeName
            else
                binding.tvManager.text = ""
        }

        //VIEWMODELS RELATED TO BUSINESS PARTNER
        mViewModel.currentChosenBp.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvBpName.text = it.CardName

                if (it.Currency == GeneralConsts.ALL_CURRENCY) {
                    mViewModel.currentCurrency.value = Currencies(
                        code = Preferences.localCurrency!!,
                        name = Preferences.localCurrency!!,
                        rate = 1.0
                    )
                } else {
                    if (mViewModel.currenciesList.value != null) {
                        mViewModel.currentCurrency.value =
                            mViewModel.currenciesList.value!!.find { currency -> currency.code == it.Currency }
                    } else {
                        mViewModel.getCurrencies()
                    }
                }

                if (!binding.switchBarcode.isChecked) mViewModel.itemsFilterString.value = ""
            } else {
                mViewModel.clearItemsSection()
                binding.tvBpName.text = ""
            }
        }

        // HERE STARTS LISTENERS FOR  BUTTON CLICK / EDITTEXT AND SO ON
        binding.tvQuantity.setOnClickListener {
            val calculator =
                CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                    override fun onEdit(number: String) {
                        binding.tvQuantity.setText(number)
                    }

                    override fun onSubmit(number: Double?) {
                        if (number != null) {
                            val prevQuantity = mViewModel.currentQuantity.value
                            val currentQuantity = number.toDouble()
                            if (currentQuantity == 0.0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Количество должно быть больше 0!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mViewModel.currentQuantity.value =
                                    mViewModel.currentQuantity.value
                                return
                            }


                            if (prevQuantity != currentQuantity) {
                                val canAdd =
                                    mViewModel.addToBasket(quantityToAdd = currentQuantity - prevQuantity!!)
                                if (!canAdd) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Количество должно быть больше 0!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mViewModel.currentQuantity.value =
                                        mViewModel.currentQuantity.value
                                }
                            }
                        }
                    }
                })
            calculator.show(parentFragmentManager, "bottomsheet_calculator")
        }

        binding.tvPrice.setOnClickListener {
            val calculator =
                CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                    override fun onEdit(number: String) {
                        binding.tvPrice.text = number
                    }

                    override fun onSubmit(number: Double?) {
                        if (number != null) {
                            val prevPrice = mViewModel.currentPrice.value
                            val currentPrice = number.toDouble()
                            if (prevPrice != currentPrice) {
                                val canAdd =
                                    mViewModel.changePrice(price = currentPrice)
                                if (!canAdd) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Нельзя указывать цену ниже чем в прайс листе!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mViewModel.currentPrice.value =
                                        mViewModel.currentPrice.value
                                }
                            }
                        }
                    }
                })
            calculator.show(parentFragmentManager, "bottomsheet_calculator")
        }


        binding.switchBarcode.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                binding.etvSearch.inputType = InputType.TYPE_NULL
                binding.etvSearch.requestFocus()
                itemsAdapter.clearList()
                Utils.hideSoftKeyboard(requireActivity())
                collapseItemsSection()
            } else {
                mViewModel.itemsFilterString.value = ""
                binding.etvSearch.inputType = InputType.TYPE_CLASS_TEXT
            }

            mViewModel.isBarcodeSwitchChecked.value = isChecked
            binding.etvSearch.text?.clear()
        }

        /*
        binding.etvSearch.setOnFocusChangeListener { view, hasFocus ->
            if (binding.switchBarcode.isChecked) {
                Utils.hideSoftKeyboard(requireActivity())
            } else {
                if (hasFocus) expandItemsSection()
                else collapseItemsSection()
            }
        }
        */


        var timer: CountDownTimer? = null
        binding.etvSearch.addTextChangedListener {
            timer?.cancel()
            timer = object :
                CountDownTimer(GeneralConsts.TIMER_MS_IN_FUTURE, GeneralConsts.TIMER_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    val prevSearchString = mViewModel.itemsFilterString.value
                    if (prevSearchString != null) {
                        val string = binding.etvSearch.text.toString()
                        if (prevSearchString != string) mViewModel.itemsFilterString.value = string
                    }
                }
            }.start()

        }
/*
        binding.etvSearch.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {

                if (binding.switchBarcode.isChecked && !editable.isNullOrEmpty()) {
                    timer?.cancel()
                    timer = object : CountDownTimer(300, 1500) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            mViewModel.barcodeFilterString.value = binding.etvSearch.text.toString()
                            binding.etvSearch.setText("")
                            binding.etvSearch.requestFocus()
                            Log.d("TIME", "TIME HAS PASSED FOR THE FIRST TIME")
                        }
                    }.start()
                } else {
                    val prevSearchString = mViewModel.itemsFilterString.value
                    if (prevSearchString != null) {
                        val string = binding.etvSearch.text.toString()
                        if (prevSearchString != string) mViewModel.itemsFilterString.value = string
                    }

                }
            }

        })*/

        binding.etvSearch.setKeyImeChangeListener(object : SearchEditText.KeyImeChange {
            override fun onKeyIme(keyCode: Int, event: KeyEvent?) {
                val isKeyboardVisible = Utils.isSoftKeyboardVisible(binding.rootLayout)
                if (event?.keyCode == KeyEvent.KEYCODE_BACK && isKeyboardVisible) {
                    collapseItemsSection()
                }
            }

        })

        binding.etvSearch.setOnTouchListener { editText, motionEvent ->
            editText.performClick()
            val isKeyboardVisible = Utils.isSoftKeyboardVisible(binding.rootLayout)
            if (!isKeyboardVisible && !binding.switchBarcode.isChecked) {
                expandItemsSection()
            }
            false
        }


        binding.tvBpName.setOnClickListener {
            showBpListDialog(requireActivity())
        }


        binding.imgBtnDeleteItem.setOnClickListener {

            if (mViewModel.currentChosenItem.value != null) {

                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle(R.string.invoice_delete_from_basket)
                    .setMessage(mViewModel.currentChosenItem.value?.ItemName)
                    .setNegativeButton("Нет") { _, _ ->
                    }
                    .setPositiveButton("Да") { _, _ ->
                        mViewModel.removeItemFromBasket(mViewModel.currentChosenItem.value!!)
                    }
                    .create()
                    .show()
            }
        }

        binding.btnPlus.setOnClickListener {
            if (mViewModel.currentChosenItem.value != null) {
                val canAdd =
                    mViewModel.addToBasket(null, 1.0, null)
                if (!canAdd) Toast.makeText(
                    requireContext(),
                    "Количество должно быть больше 0!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnMinus.setOnClickListener {
            if (mViewModel.currentChosenItem.value != null) {

                if (mViewModel.currentQuantity.value!! > 1.0) {
                    val canAdd =
                        mViewModel.addToBasket(null, -1.0, null)
                    if (!canAdd) Toast.makeText(
                        requireContext(),
                        "Количество должно быть больше 0!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.imgBtnClearSearch.setOnClickListener {
            binding.etvSearch.text?.clear()
            collapseItemsSection()
        }
    }

    private fun getBarCode(editable: Editable?) {
        if (editable.toString().length == 13) {
            mViewModel.itemsFilterString.value = editable.toString()
        }
        if (editable.toString().contains("\n")) {
            Toast.makeText(
                requireContext(),
                "CARRIAGE RETURN INDEX ${editable.toString().indexOf("\n")}",
                Toast.LENGTH_SHORT
            ).show()
            binding.etvSearch.setText(
                StringBuilder(editable.toString()).deleteCharAt(
                    editable.toString().indexOf("\n")
                )
            )
        }
    }

    private fun disableBodySection(disable: Boolean) {
        if (disable) {
            binding.tvBpName.isEnabled = false
            binding.layoutBpName.isEnabled = false
            binding.tvWarehouse.isEnabled = false
            binding.tvCurrency.isEnabled = false
            binding.recyclerViewDocRows.isEnabled = false
            binding.switchBarcode.isEnabled = false
            binding.etvSearch.isEnabled = false
            binding.imgBtnClearSearch.isEnabled = false
        } else {
            binding.tvBpName.isEnabled = true
            binding.layoutBpName.isEnabled = true
            binding.tvWarehouse.isEnabled = true
            binding.tvCurrency.isEnabled = true
            binding.recyclerViewDocRows.isEnabled = true
            binding.switchBarcode.isEnabled = true
            binding.etvSearch.isEnabled = true
            binding.imgBtnClearSearch.isEnabled = true
        }
    }

    fun expandItemsSection() {
        binding.layoutBpName.visibility = View.GONE
        binding.layoutWhsAndCurrency.visibility = View.GONE
        binding.tvItemName.visibility = View.GONE
        binding.layoutItemQuantity.visibility = View.GONE
        binding.layoutItemPrice.visibility = View.GONE
        binding.layoutManager.visibility = View.GONE
        binding.dividerItem.visibility = View.GONE
        binding.dividerBpName.visibility = View.GONE
    }

    fun collapseItemsSection() {
        binding.layoutBpName.visibility = View.VISIBLE
        binding.layoutWhsAndCurrency.visibility = View.VISIBLE
        binding.tvItemName.visibility = View.VISIBLE
        binding.layoutItemQuantity.visibility = View.VISIBLE
        binding.layoutItemPrice.visibility = View.VISIBLE
        binding.layoutManager.visibility = View.VISIBLE
        binding.dividerItem.visibility = View.VISIBLE
        binding.dividerBpName.visibility = View.VISIBLE
    }


    private fun showBpListDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val btnAddNewBp = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)
        val etvSearch = dialog.findViewById<EditText>(R.id.dialog_etvSearch)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)

        val adapter = BpListAdapter(object : BpListAdapter.BpClickListener {
            override fun onClick(bp: BusinessPartners) {
                mViewModel.currentChosenBp.value = bp
                dialog.dismiss()
            }

            override fun loadMore(lastItemIndex: Int) {
                mViewModel.getMoreBpList(lastItemIndex)
            }
        })

        mViewModel.bpFilterString.value = ""
        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAddNewBp.isVisible = true
        btnAddNewBp.setOnClickListener {
            getNewBpFromActivity.launch(Intent(requireActivity(), BpAddActivity::class.java))
            dialog.dismiss()
        }

        etvSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mViewModel.bpFilterString.value = etvSearch.text.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        mViewModel.bpFilterString.observe(viewLifecycleOwner) {
            mViewModel.getBpList()
            rvBps.scrollToPosition(0)
        }

        mViewModel.bpLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.visibility = View.VISIBLE
                rvBps.visibility = View.INVISIBLE
            } else {
                loader.visibility = View.GONE
                rvBps.visibility = View.VISIBLE
            }
        }

        mViewModel.bpList.observe(viewLifecycleOwner, {
            adapter.list = it
        })

        mViewModel.errorBpLoading.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                "Ошибка: $it",
                Toast.LENGTH_SHORT
            ).show()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 90, 70)
    }

    private fun showManagersListDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl)
        val rvManagers = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val btnAddNewBp = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)
        btnAddNewBp.visibility = View.GONE
        val etvSearch = dialog.findViewById<EditText>(R.id.dialog_etvSearch)
        etvSearch.visibility = View.GONE
        val labelDialog = dialog.findViewById<TextView>(R.id.labelDialog)
        labelDialog.text = getString(R.string.sales_managers)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)


        val adapter = SalesManagersListAdapter(object :
            SalesManagersListAdapter.SalesManagersListClickListener {
            override fun onClick(item: SalesManagers) {
                mViewModel.currentChosenManager.value = item
                dialog.dismiss()
            }
        })

        mViewModel.getSalesManagersList()
        rvManagers.layoutManager = LinearLayoutManager(activity)
        rvManagers.adapter = adapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        mViewModel.managersList.observe(viewLifecycleOwner) {
            adapter.list = it
        }

        mViewModel.managersLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.visibility = View.VISIBLE
                rvManagers.visibility = View.GONE
            } else {
                loader.visibility = View.GONE
                rvManagers.visibility = View.VISIBLE
            }
        }


        mViewModel.errorManagersLoading.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 70)
    }


    private fun showWarehousesListDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_cfl)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnAddNewBp = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)
        btnAddNewBp.visibility = View.GONE
        val etvSearch = dialog.findViewById<EditText>(R.id.dialog_etvSearch)
        etvSearch.visibility = View.GONE
        val dialogLabel = dialog.findViewById<TextView>(R.id.labelDialog)
        dialogLabel.text = getString(R.string.warehouses)

        dialog.setOnCancelListener {
            activity.finish()
        }

        val adapter = WarehousesListAdapter(object :
            WarehousesListAdapter.WarehousesListClickListener {
            override fun onClick(item: Any) {
                mViewModel.currentWhsCode.value = (item as Warehouses).WarehouseCode
                dialog.dismiss()
            }
        })

        mViewModel.getWarehouseList()
        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        mViewModel.warehousesList.observe(viewLifecycleOwner) {
            adapter.list = it
        }

        mViewModel.warehousesLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.visibility = View.VISIBLE
                rvBps.visibility = View.INVISIBLE
            } else {
                loader.visibility = View.GONE
                rvBps.visibility = View.VISIBLE
            }
        }

        mViewModel.errorWarehousesLoading.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                "Ошибка: $it",
                Toast.LENGTH_SHORT
            ).show()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 70)
    }


    private fun showCurrenciesListDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_cfl)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnAddNewBp = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)
        btnAddNewBp.visibility = View.GONE
        val etvSearch = dialog.findViewById<EditText>(R.id.dialog_etvSearch)
        etvSearch.visibility = View.GONE
        val dialogLabel = dialog.findViewById<TextView>(R.id.labelDialog)
        dialogLabel.text = getString(R.string.currency)

        val previousCurrency = mViewModel.currentCurrency.value

        dialog.setOnCancelListener {
            activity.finish()
        }

        val adapter = CurrenciesListAdapter(object :
            CurrenciesListAdapter.CurrenciesListClickListener {
            override fun onClick(item: Currencies) {
                if (item.rate == null || item.rate == 0.0) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.no_rate_set),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                mViewModel.currentCurrency.value = item
                dialog.dismiss()
            }
        })

        mViewModel.getCurrencies()
        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter

        btnCancel.setOnClickListener {
            mViewModel.currentCurrency.value = previousCurrency
            dialog.dismiss()
        }

        mViewModel.currenciesList.observe(viewLifecycleOwner) {
            adapter.list = it as MutableList<Currencies>
        }

        mViewModel.currenciesLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.visibility = View.VISIBLE
                rvBps.visibility = View.INVISIBLE
            } else {
                loader.visibility = View.GONE
                rvBps.visibility = View.VISIBLE
            }
        }

        mViewModel.errorCurrenciesLoading.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                "Ошибка: $it",
                Toast.LENGTH_SHORT
            ).show()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 70)
    }


}