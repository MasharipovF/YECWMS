package com.example.yecwms.presentation.inventorytransfer.insertpage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.yecwms.R
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.masterdatas.Warehouses
import com.example.yecwms.databinding.FragmentInventoryTransferSelectBinding
import com.example.yecwms.presentation.inventorytransfer.insertpage.adapter.InventoryTransferItemsListAdapter
import com.example.yecwms.presentation.inventorytransfer.insertpage.adapter.InventoryTransferWarehousesListAdapter
import com.example.yecwms.util.CalculatorBottomSheet
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.Utils
import com.example.yecwms.util.customviews.SearchEditText
import java.math.BigDecimal
import java.util.*

class InventoryTransferSelectFragment : Fragment() {

    private lateinit var binding: FragmentInventoryTransferSelectBinding
    private lateinit var itemsAdapter: InventoryTransferItemsListAdapter
    private lateinit var mViewModel: InventoryTransferInsertViewModel


    companion object {
        fun newInstance() = InventoryTransferSelectFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory_transfer_select, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInventoryTransferSelectBinding.bind(view)
        mViewModel =
            ViewModelProvider(requireActivity()).get(InventoryTransferInsertViewModel::class.java)

        itemsAdapter = InventoryTransferItemsListAdapter(object :
            InventoryTransferItemsListAdapter.InvItemListClickListener {
            override fun onClick(item: Items) {

                val isKeyboardVisible = Utils.isSoftKeyboardVisible(binding.rootLayout)
                if (isKeyboardVisible) {
                    Utils.hideSoftKeyboard(requireActivity())
                    collapseItemsSection()
                }

                val canAdd = mViewModel.addToBasket(item, BigDecimal.valueOf(1.0))
                if (!canAdd) Toast.makeText(
                    requireContext(),
                    "Количество сокращается до отрицательного значения!",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun loadmore(position: Int) {
                mViewModel.getMoreItemsList(position)
            }

        })
        binding.recyclerViewDocRows.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDocRows.adapter = itemsAdapter


        mViewModel.isViewMode.observe(viewLifecycleOwner, {
            enableViewMode(it)
        })

        // VIEWMODELS RELATED TO ITEM LIST
        /*
        mViewModel.loadingDocument.observe(viewLifecycleOwner, {
            if (it) {
                binding.documentLoader.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
            } else {
                binding.documentLoader.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        })
        */



        mViewModel.itemsLoading.observe(viewLifecycleOwner, {
            if (it) {
                binding.itemsLoader.visibility = View.VISIBLE
            } else {
                binding.itemsLoader.visibility = View.GONE
            }
        })

        mViewModel.itemsFilterString.observe(viewLifecycleOwner, {
            if (it != null) {
                mViewModel.getItemsList()
                binding.recyclerViewDocRows.scrollToPosition(0)
            } else {
                Log.d("FILTERSTRING", "lalalalalalalalalalal")
                binding.etvSearch.text?.clear()
            }
        })

        mViewModel.itemsList.observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d("INVENTORYLIST", it.toString())
                itemsAdapter.list = it
                binding.etvSearch.isEnabled = true
            } else {
                itemsAdapter.clearList()
                binding.etvSearch.isEnabled = false
            }
        })

        mViewModel.errorItemsLoading.observe(viewLifecycleOwner, {
            Toast.makeText(
                requireContext(),
                "Error Loading",
                Toast.LENGTH_SHORT
            ).show()
        })

        mViewModel.currentQuantityInPackage.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.tvQuantity.isEnabled = true
                if (!binding.tvQuantity.isFocused) binding.tvQuantity.setText(
                    it.toString()
                )
            } else {
                binding.tvQuantity.text = ""
                binding.tvQuantity.isEnabled = false
            }
        })

        mViewModel.currentChosenItem.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.tvItemName.text = it.ItemName
            } else {
                binding.tvItemName.setText(R.string.iteminfo_item_name)
            }
        })


        //VIEWMODELS RELATED TO WAREHOUSES AND DATES
        mViewModel.fromWarehouse.observe(viewLifecycleOwner, {
            binding.tvFromWarehouse.text = it
            if (mViewModel.toWarehouse.value != null)
                mViewModel.getItemsList()
        })

        mViewModel.toWarehouse.observe(viewLifecycleOwner, {
            binding.tvToWarehouse.text = it
            if (mViewModel.fromWarehouse.value != null)
                mViewModel.getItemsList()
        })

        mViewModel.docDueDate.observe(viewLifecycleOwner, {
            binding.tvDocDueDate.text = Utils.convertUSAdatetoNormal(it)
        })


        // HERE STARTS LISTENERS FOR  BUTTON CLICK / EDITTEXT AND SO ON
        binding.tvFromWarehouse.setOnClickListener {
            showWarehousesListDialog(requireActivity(), true)
        }

        binding.tvToWarehouse.setOnClickListener {
            showWarehousesListDialog(requireActivity(), false)
        }

        binding.tvDocDueDate.setOnClickListener {
            showDatePicker(requireContext())
        }

        binding.tvQuantity.setOnClickListener {
            val calculator =
                CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                    override fun onEdit(number: String) {
                        binding.tvQuantity.text = number
                    }

                    override fun onSubmit(number: Double?) {
                        if (number != null) {
                            val prevQuantity = mViewModel.currentQuantityInPackage.value
                            val currentQuantity = BigDecimal.valueOf(number.toDouble())
                            if (prevQuantity != currentQuantity) {
                                val canAdd =
                                    mViewModel.addToBasket(quantityToAdd = currentQuantity - prevQuantity!!)
                                if (!canAdd) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Количество сокращается до отрицательного значения!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mViewModel.currentQuantityInPackage.value =
                                        mViewModel.currentQuantityInPackage.value
                                }
                            }
                        }
                    }


                })
            calculator.show(parentFragmentManager, "bottomsheet_calculator")
        }


        binding.etvSearch.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) expandItemsSection()
            else collapseItemsSection()

        }

        binding.etvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val prevSearchString = mViewModel.itemsFilterString.value
                if (prevSearchString != null) {
                    val string = binding.etvSearch.text.toString()
                    if (prevSearchString != string) mViewModel.itemsFilterString.value = string
                }
            }

        })

        binding.etvSearch.setKeyImeChangeListener(object : SearchEditText.KeyImeChange {
            override fun onKeyIme(keyCode: Int, event: KeyEvent?) {
                val isKeyboardVisible = Utils.isSoftKeyboardVisible(binding.rootLayout)
                if (event?.keyCode == KeyEvent.KEYCODE_BACK && isKeyboardVisible) {
                    collapseItemsSection()
                }
            }

        })

        binding.etvSearch.setOnTouchListener { editText, motionEvent ->
            if (mViewModel.isViewMode.value != null && mViewModel.isViewMode.value!!)
                return@setOnTouchListener false

            editText.performClick()
            val isKeyboardVisible = Utils.isSoftKeyboardVisible(binding.rootLayout)
            if (!isKeyboardVisible) {
                expandItemsSection()
            }
            false
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
                val canAdd = mViewModel.addToBasket(null, BigDecimal.valueOf(1.0))
                if (!canAdd) Toast.makeText(
                    requireContext(),
                    "Количество сокращается до отрицательного значения!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnMinus.setOnClickListener {
            if (mViewModel.currentChosenItem.value != null) {

                if (mViewModel.currentQuantityInPackage.value!! > BigDecimal.valueOf(1.0)) {
                    val canAdd = mViewModel.addToBasket(null, BigDecimal.valueOf(-1.0))
                    if (!canAdd) Toast.makeText(
                        requireContext(),
                        "Количество сокращается до отрицательного значения!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        binding.imgBtnClearSearch.setOnClickListener {
            binding.etvSearch.text?.clear()
        }
    }

    private fun showDatePicker(context: Context) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            { _, Year, monthOfYear, dayOfMonth ->
                mViewModel.docDueDate.value = "$Year-${monthOfYear + 1}-$dayOfMonth"
            },
            year,
            month,
            day
        )

        dpd.show()
    }


    private fun showWarehousesListDialog(activity: Activity, isFromWarehouse: Boolean) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val btnAddNewBp = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)
        btnAddNewBp.visibility = View.GONE
        val etvSearch = dialog.findViewById<EditText>(R.id.dialog_etvSearch)
        etvSearch.visibility = View.GONE
        val labelDialog = dialog.findViewById<TextView>(R.id.labelDialog)
        labelDialog.text = "Склады"
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)

        val adapter = InventoryTransferWarehousesListAdapter(object :
            InventoryTransferWarehousesListAdapter.InventoryRequestWarehousesListClickListener {
            override fun onClick(item: Any) {
                if (isFromWarehouse) {
                    mViewModel.fromWarehouse.value = (item as Warehouses).WarehouseCode
                    mViewModel.changeFromWhsOfBasketList()
                } else {
                    mViewModel.toWarehouse.value = (item as Warehouses).WarehouseCode
                    mViewModel.changeToWhsOfBasketList()
                }
                dialog.dismiss()
            }
        })

        mViewModel.getWarehouseList()
        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter

        mViewModel.warehousesLoading.observe(viewLifecycleOwner, {
            if (it) {
                loader.visibility = View.VISIBLE
                rvBps.visibility = View.GONE
            } else {
                loader.visibility = View.GONE
                rvBps.visibility = View.VISIBLE
            }
        })

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        /*
        mViewModel.bpFilterString.value = ""
        etvSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mViewModel.bpFilterString.value = etvSearch.text.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        mViewModel.bpFilterString.observe(viewLifecycleOwner, {
            mViewModel.getBpList()
            rvBps.scrollToPosition(0)
        })*/

        mViewModel.warehousesList.observe(viewLifecycleOwner, {
            adapter.list = it
        })


        mViewModel.errorWarehousesLoading.observe(viewLifecycleOwner, {
            Toast.makeText(
                requireContext(),
                "Error Loading BP",
                Toast.LENGTH_SHORT
            ).show()
        })

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 70)
    }


    private fun enableViewMode(enable: Boolean) {
        binding.tvFromWarehouse.isClickable = !enable
        binding.tvToWarehouse.isClickable = !enable
        binding.recyclerViewDocRows.visibility = if (enable) View.GONE else View.VISIBLE
        binding.recyclerViewDocRows.visibility = if (enable) View.GONE else View.VISIBLE
        binding.etvSearch.isClickable = !enable
        binding.etvSearch.isFocusable = !enable
        binding.imgBtnClearSearch.isClickable = !enable
        binding.imgBtnClearSearch.isEnabled = !enable
    }


    fun expandItemsSection() {
        binding.layoutFromWarehouse.visibility = View.GONE
        binding.layoutToWarehouse.visibility = View.GONE
        binding.layoutDocDueDate.visibility = View.GONE
        binding.tvItemName.visibility = View.GONE
        binding.layoutItemQuantity.visibility = View.GONE
        binding.dividerItem.visibility = View.GONE
        binding.dividerDocDate.visibility = View.GONE
    }

    fun collapseItemsSection() {
        binding.layoutFromWarehouse.visibility = View.VISIBLE
        binding.layoutToWarehouse.visibility = View.VISIBLE
        binding.layoutDocDueDate.visibility = View.VISIBLE
        binding.tvItemName.visibility = View.VISIBLE
        binding.layoutItemQuantity.visibility = View.VISIBLE
        binding.dividerItem.visibility = View.VISIBLE
        binding.dividerDocDate.visibility = View.VISIBLE
    }

}