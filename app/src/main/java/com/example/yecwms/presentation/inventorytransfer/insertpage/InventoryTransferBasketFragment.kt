package com.example.yecwms.presentation.inventorytransfer.insertpage

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.example.yecwms.data.entity.inventory.InventoryOperationsLines
import com.example.yecwms.databinding.FragmentInventoryTransferBasketBinding
import com.example.yecwms.presentation.inventorytransfer.insertpage.adapter.InventoryTransferBasketAdapter
import com.example.yecwms.presentation.invoice.adapter.InvoiceSelectedBatchNumbersAdapter
import com.example.yecwms.util.CalculatorBottomSheet
import com.example.yecwms.util.DialogUtils
import java.math.BigDecimal

class InventoryTransferBasketFragment : Fragment() {

    private lateinit var binding: FragmentInventoryTransferBasketBinding
    private lateinit var docLinesAdapter: InventoryTransferBasketAdapter
    private lateinit var mViewModel: InventoryTransferInsertViewModel
    private var btnText: String = ""


    companion object {
        fun newInstance() = InventoryTransferBasketFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory_transfer_basket, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInventoryTransferBasketBinding.bind(view)
        mViewModel =
            ViewModelProvider(requireActivity()).get(InventoryTransferInsertViewModel::class.java)

        var calculator: CalculatorBottomSheet

        var constraintLayoutHeight: Int = 0
        binding.constraintLayout.doOnLayout {
            constraintLayoutHeight = binding.constraintLayout.measuredHeight;
        }

        docLinesAdapter = InventoryTransferBasketAdapter(object :
            InventoryTransferBasketAdapter.InvBasketItemClickListener {
            override fun changeQuantity(position: Int, quantity: BigDecimal): Boolean {
                val canAdd = mViewModel.changeQuantity(position, quantity)
                if (!canAdd) {
                    Toast.makeText(
                        requireContext(),
                        "Количество сокращается до отрицательного значения!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                return true
            }

            override fun onBatchItemClick(position: Int, item: InventoryOperationsLines) {
                Log.d("CHOSENBATCH", item.toString())
                showBatchNumbersDialog(requireActivity(), position)
            }

            override fun onQuantityViewClicked(
                position: Int,
                item: InventoryOperationsLines,
            ) {
                val quantity = BigDecimal.ONE
                calculator =
                    CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                        override fun onEdit(number: String) {

                            docLinesAdapter.list[position].UserQuantity =
                                quantity * (if (number.isEmpty()) BigDecimal.valueOf(1.0) else BigDecimal.valueOf(
                                    number.toDouble()
                                ))
                            docLinesAdapter.notifyDataSetChanged()
                        }

                        override fun onSubmit(number: Double?) {
                            val layoutParams = binding.constraintLayout.layoutParams
                            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                            layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                            binding.constraintLayout.layoutParams = layoutParams

                            if (number != null) {
                                val currentQuantity =
                                    quantity * BigDecimal.valueOf(number.toDouble())

                                val canAdd = mViewModel.changeQuantity(
                                    position = position,
                                    quantity = currentQuantity
                                )
                                if (!canAdd) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Количество сокращается до отрицательного значения!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                                if (!canAdd) {
                                    docLinesAdapter.list[position].UserQuantity =
                                        docLinesAdapter.list[position].quantity!!
                                }
                                docLinesAdapter.notifyDataSetChanged()
                            }

                        }

                       /* override fun onCalculatorSizeCalculated(height: Int, width: Int) {
                            Log.d("CALCULATOR", "  view height: $constraintLayoutHeight")
                            Log.d("CALCULATOR", "  calcualtor height ${height}")
                            val layoutParams = binding.constraintLayout.layoutParams
                            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                            layoutParams.height = constraintLayoutHeight - height - 50
                            binding.constraintLayout.layoutParams = layoutParams
                            binding.rvBasket.smoothScrollToPosition(position)
                        }*/
                    })
                calculator.show(parentFragmentManager, "bottomsheet_calculator")


            }

        }, parentFragmentManager)

        docLinesAdapter.isViewModeEnabled = mViewModel.isViewMode.value!!

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle(R.string.invoice_delete_from_basket)
                    .setMessage(docLinesAdapter.list[viewHolder.absoluteAdapterPosition].itemDescription)
                    .setNegativeButton("Нет") { _, _ ->
                        docLinesAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                    }
                    .setPositiveButton("Да") { _, _ ->
                        docLinesAdapter.removeAt(viewHolder.absoluteAdapterPosition) //TODO i am deleting only from list inside of adapter, but android deletes item from viewmodel too
                    }
                    .create()
                    .show()
            }

        }

        binding.rvBasket.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBasket.adapter = docLinesAdapter
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvBasket)

        mViewModel.isViewMode.observe(viewLifecycleOwner) {
            enableViewMode(it)
            docLinesAdapter.isViewModeEnabled = it
            if (it == null || !it)
                ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvBasket)
        }


        mViewModel.basketList.observe(viewLifecycleOwner) {
            if (it != null) {
                docLinesAdapter.list = it
            } else docLinesAdapter.clearList()
            Log.d("list changed", "lalala")
        }


        mViewModel.loadingInsert.observe(viewLifecycleOwner) {
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

        mViewModel.errorLoadingInsert.observe(viewLifecycleOwner, {
            if (it) {
                binding.textView.text = getString(R.string.btn_reload)
            } else {
                binding.textView.text = getString(R.string.btn_add)
            }
        })

        mViewModel.barcodeScannedString.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val position = mViewModel.findItemPositionByBarCode(it)
                if (position != null) {
                    val quantity =
                        mViewModel.basketList.value?.get(position)?.UserQuantity!!
                    val currentQuantity = mViewModel.basketList.value?.get(position)?.UserQuantity!!

                    val updateQuantity =
                        quantity + currentQuantity

                    Log.d(
                        "QUANTITIES",
                        "$quantity      $currentQuantity       $updateQuantity"
                    )
                    Log.d("QUANTITIES", "${mViewModel.basketList.value?.get(position)}")

                    val canAdd = mViewModel.changeQuantity(
                        position = position,
                        quantity = updateQuantity
                    )

                    if (!canAdd) {
                        Toast.makeText(
                            requireContext(),
                            "Количество сокращается до отрицательного значения!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                    if (!canAdd) {
                        docLinesAdapter.list[position].UserQuantity =
                            docLinesAdapter.list[position].quantity!!
                    }
                    docLinesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Ничего не найдено!", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.etvBarcodeScanner.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(300, 1500) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        if (editable.toString().contains("\n")) {
                            binding.etvBarcodeScanner.setText(
                                StringBuilder(editable.toString()).deleteCharAt(
                                    editable.toString().indexOf("\n")
                                )
                            )
                            return
                        }

                        mViewModel.barcodeScannedString.value =
                            binding.etvBarcodeScanner.text.toString()
                        binding.etvBarcodeScanner.setText("")
                        binding.etvBarcodeScanner.requestFocus()
                        Log.d("TIME", "TIME HAS PASSED FOR THE FIRST TIME")
                    }
                }.start()
            }

        })



        binding.btnAdd.setOnClickListener {


            if (mViewModel.fromWarehouse.value != null && mViewModel.toWarehouse.value != null && mViewModel.basketList.value?.isNotEmpty()!!) {


                val message = resources.getString(R.string.adddoc_message)

                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle(R.string.adddoc_title)
                    .setMessage(message)
                    .setNegativeButton("Нет") { _, _ -> }
                    .setPositiveButton("Да") { _, _ ->
                        mViewModel.insertInventoryTransfer()
                    }
                    .create()
                    .show()
            } else Toast.makeText(
                requireContext(),
                "Заполните все поля и попробуйте еще раз",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun areAllItemsCollected(): Boolean {
        var flag = true
        mViewModel.basketList.value?.forEach {
            if (it.InitialQuantity != BigDecimal.ZERO) {
                if (it.InitialQuantity > it.UserQuantity) {
                    flag = false
                }
            }
        }

        return flag
    }

    private fun enableViewMode(enable: Boolean) {
        binding.btnAdd.isVisible = !enable
        binding.cardBarcodeScanner.visibility = if (enable) View.GONE else View.VISIBLE
    }

    private fun showBatchNumbersDialog(activity: Activity, position: Int) {
        val item = mViewModel.basketList.value?.get(position)!!
        Log.d("BATCHNUMBERS", item.toString())
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl_batches_select)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val tvTotalQuantity = dialog.findViewById<TextView>(R.id.tvTotalQuantity)
        val tvSelectedQuantity = dialog.findViewById<TextView>(R.id.tvSelectedQuantity)
        val btnAutoSelect = dialog.findViewById<ImageButton>(R.id.btnAutoSelect)


        val adapter = InvoiceSelectedBatchNumbersAdapter(object :
            InvoiceSelectedBatchNumbersAdapter.InvoiceSelectedBatchesClickListener {
            override fun onClick(item: BatchNumbersVal.BatchNumbers, position: Int) {

            }

            override fun onQuantityChange(selectedQuantity: Double) {
                tvSelectedQuantity.text = selectedQuantity.toString()
            }


        }, parentFragmentManager)

        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter
        mViewModel.getBatchNumbersList(item.itemCode!!, mViewModel.fromWarehouse.value!!)

        Log.d("CHOSENBATCHESINSIDEDIALOG", item.TempBatchNumbers.toString())
        mViewModel.itemBatchNumbers.observe(viewLifecycleOwner) {
            if (it.isEmpty()) Toast.makeText(
                requireContext(),
                "Остатков по данному товару на складе ${mViewModel.fromWarehouse.value!!} не обнаружено!",
                Toast.LENGTH_LONG
            ).show()
            val resultList = arrayListOf<BatchNumbersVal.BatchNumbers>()
            val checkList = item.TempBatchNumbers
            for (batch in it) {
                var resultBatch = batch
                for (checkBatch in checkList) {
                    if (batch.absEntry == checkBatch.absEntry) {
                        resultBatch = checkBatch
                    }
                }
                resultList.add(resultBatch)
            }
            adapter.list = resultList
            adapter.sortList()
        }

        mViewModel.batchesLoading.observe(viewLifecycleOwner) {
            if (it) {
                rvBps.visibility = View.INVISIBLE
            } else {
                rvBps.visibility = View.VISIBLE
            }
        }

        tvTotalQuantity.text = item.UserQuantity.toString()

        btnAutoSelect.setOnClickListener {
            adapter.autoSelect(item.UserQuantity!!.toDouble())
        }


        btnSubmit.setOnClickListener {
            val list = adapter.list
            for (listItem in list) {
                if (listItem.selectedQuantity!! > listItem.quantity!! && listItem.isChecked) {
                    Toast.makeText(
                        requireContext(),
                        "Выбранные количества в партиях больше чем !",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    adapter.notifyDataSetChanged()
                    return@setOnClickListener
                }
            }


            mViewModel.setTempBatchNumbersToItem(
                position,
                ArrayList(adapter.list.filter { it.isChecked })
            )
            dialog.dismiss()
        }


        /*
        etvSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mViewModel.bpFilterString.value = etvSearch.text.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        */

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 100)
    }


    private fun updateInventoryRequest() {

    }

    private fun insertInventoryRequest() {

    }
}