package com.example.yecwms.presentation.invoice

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.yecwms.R
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.databinding.FragmentInvoiceBasketBinding
import com.example.yecwms.presentation.invoice.adapter.InvoiceBasketAdapter
import com.example.yecwms.presentation.invoice.adapter.InvoiceSelectedBatchNumbersAdapter
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.Utils


class InvoiceBasketFragment : Fragment() {

    private lateinit var binding: FragmentInvoiceBasketBinding
    private lateinit var docLinesAdapter: InvoiceBasketAdapter
    private lateinit var mViewModel: InvoiceInsertViewModel

    companion object {
        fun newInstance() = InvoiceBasketFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invoice_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInvoiceBasketBinding.bind(view)
        mViewModel =
            ViewModelProvider(requireActivity()).get(InvoiceInsertViewModel::class.java)



        docLinesAdapter = InvoiceBasketAdapter(object :
            InvoiceBasketAdapter.InvBasketItemClickListener {
            override fun changeQuantity(position: Int, quantity: Double): Boolean {
                if (quantity == 0.0) {
                    Toast.makeText(
                        requireContext(),
                        "Количество должно быть больше 0!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }


                val canAdd = mViewModel.changeQuantity(position, quantity)
                if (!canAdd) {
                    Toast.makeText(
                        requireContext(),
                        "Нельзя превышать количество, больше чем на чеке!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                return true
            }

            override fun changePrice(position: Int, price: Double): Boolean {
                val canAdd = mViewModel.changePrice(position, price)
                if (!canAdd) {
                    Toast.makeText(
                        requireContext(),
                        "Нельзя указывать цену ниже чем в прайс листе!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                return true
            }

            override fun onBatchItemClick(position: Int, item: DocumentLines) {
                showBatchNumbersDialog(requireActivity(), position)
            }

            override fun onImageClick(image: Bitmap?) {
                DialogUtils.showEnlargedImage(requireContext(), image)
            }

        }, parentFragmentManager)

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
                    .setMessage(docLinesAdapter.list[viewHolder.absoluteAdapterPosition].ItemName)
                    .setNegativeButton("Нет") { _, _ ->
                        docLinesAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                    }
                    .setPositiveButton("Да") { _, _ ->
                        docLinesAdapter.removeAt(viewHolder.absoluteAdapterPosition) //TODO i am deleting only from list inside of adapter, but android deletes item from viewmodel too
                        mViewModel.calculateDocTotal()
                    }
                    .create()
                    .show()
            }

        }

        binding.rvBasket.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBasket.adapter = docLinesAdapter
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvBasket)


        mViewModel.basketList.observe(viewLifecycleOwner) {
            if (it != null) {
                docLinesAdapter.list = it
            } else docLinesAdapter.clearList()
            Log.d("list changed", "lalala")
        }

        mViewModel.discountedTotal.observe(viewLifecycleOwner) {
            binding.tvDocTotalUZS.text = it.toString()
        }

        mViewModel.currentCurrency.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvDocTotalLabel.text =
                    resources.getString(R.string.invoice_doctotal, it.code)
            }
        }

        mViewModel.discount.observe(viewLifecycleOwner) {
            binding.tvDiscount.text = it.toString()
        }

        mViewModel.loadingInsert.observe(viewLifecycleOwner) {
            if (it) {
                binding.insertBtnLabel.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.btnInsert.isClickable = false
                binding.btnInsert.isFocusable = false
            } else {
                binding.insertBtnLabel.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.btnInsert.isClickable = true
                binding.btnInsert.isFocusable = true
            }
        }

        mViewModel.errorLoadingInsert.observe(viewLifecycleOwner) {
            if (it) {
                binding.insertBtnLabel.text = getString(R.string.btn_reload)
                mViewModel.loadingInsert.value = false
            } else {
                binding.insertBtnLabel.text = mViewModel.addBtnText.value
            }
        }

        mViewModel.addBtnText.observe(viewLifecycleOwner) {
            binding.insertBtnLabel.text = it
        }

        binding.btnInsert.setOnClickListener {


            if (mViewModel.currentWhsCode.value != null && mViewModel.basketList.value?.isNotEmpty()!!) {
                val alertDialogBuilder = AlertDialog.Builder(requireActivity())
                alertDialogBuilder.setTitle(R.string.adddoc_title)
                    .setMessage(R.string.adddoc_message)
                    .setNegativeButton("Нет") { _, _ -> }
                    .setPositiveButton("Да") { _, _ ->
                        mViewModel.insertSalesOrder()
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

    private fun showBatchNumbersDialog(activity: Activity, position: Int) {
        val item = mViewModel.basketList.value?.get(position)!!
        Log.d("BATCHNUMBERS", item.toString())
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl_batches_select)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val tvTotalQuantity = dialog.findViewById<TextView>(R.id.tvTotalQuantity)
        val tvSelectedQuantity = dialog.findViewById<TextView>(R.id.tvSelectedQuantity)
        val btnAutoSelect = dialog.findViewById<ImageButton>(R.id.btnAutoSelect)


        val adapter = InvoiceSelectedBatchNumbersAdapter(object :
            InvoiceSelectedBatchNumbersAdapter.InvoiceSelectedBatchesClickListener {
            override fun onClick(item: BatchNumbersVal.BatchNumbers, position: Int) {

            }

            override fun onQuantityChange(selectedQuantity: Double) {
                tvSelectedQuantity.text =
                    Utils.getNumberWithThousandSeparator(selectedQuantity.toDouble())
            }


        }, parentFragmentManager)

        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter

        mViewModel.getItemBatches(item.ItemCode!!)


        mViewModel.batchesListToDraw.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            if (it.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Остатков по данному товару на складе ${mViewModel.currentWhsCode.value!!} не обнаружено!",
                    Toast.LENGTH_LONG
                ).show()
                return@observe
            }

            val resultList = arrayListOf<BatchNumbersVal.BatchNumbers>()
            val selectedBatches = item.TempBatchNumbers
            for (batch in it) {
                var resultBatch: BatchNumbersVal.BatchNumbers = BatchNumbersVal.BatchNumbers(
                    ItemCode = item.ItemCode,
                    batchNumber = batch.BatchNumber,
                    quantity = batch.QuantityOnStockByBatch,
                )
                for (selectedBatch in selectedBatches) {
                    if (batch.BatchNumber == selectedBatch.batchNumber) {
                        resultBatch = selectedBatch
                    }
                }

                resultBatch.Type = item.Type
                resultList.add(resultBatch)
            }
            Log.d("SELECTBATCHES", "viewmodel lalala resultlist ${resultList.toString()}")

            adapter.list = resultList
            adapter.sortList()
        }

        mViewModel.batchesLoading.observe(viewLifecycleOwner) {
            rvBps.isVisible = !it
            loader.isVisible = it
        }

        tvTotalQuantity.text = Utils.getNumberWithThousandSeparator(item.UserQuantity ?: 0.0)

        btnAutoSelect.setOnClickListener {
            adapter.autoSelect(item.UserQuantity!!)
        }


        btnSubmit.setOnClickListener {
            val list = adapter.list
            for (listItem in list) {
                if (listItem.selectedQuantity!! > listItem.quantity!! && listItem.isChecked) {
                    Toast.makeText(
                        requireContext(),
                        "Выбранные количества больше чем остатки!",
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
            mViewModel.batchesListToDraw.value = null
            dialog.dismiss()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 100)
    }

/*
    private fun showBatchNumbersDialog(activity: Activity, position: Int, item: DocumentLines) {
        Log.d("BATCHNUMBERS", item.toString())
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl_batches)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val btnAddNewBatch = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)

        val adapter = SalesOrderBatchNumbersAdapter(object :
            SalesOrderBatchNumbersAdapter.SalesOrderBatchNumbersClickListener {
            override fun onClick(item: Any) {
            }

            override fun loadmore(position: Int) {
            }

        }, parentFragmentManager, item.ItemCode.toString())


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
                    .setMessage(adapter.list[viewHolder.absoluteAdapterPosition].BatchNumber)
                    .setNegativeButton("Нет") { _, _ ->
                        adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                    }
                    .setPositiveButton("Да") { _, _ ->
                        adapter.removeAt(viewHolder.absoluteAdapterPosition) //TODO i am deleting only from list inside of adapter, but android deletes item from viewmodel too
                    }
                    .create()
                    .show()
            }

        }

        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter
        adapter.list = item.BatchNumbers
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvBps)


        btnSubmit.setOnClickListener {
            val list = adapter.list
            for (listItem in list) {
                if (listItem.BatchNumber.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Укажите код партии!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }

            mViewModel.setBatchNumbersToItem(position, adapter.list)
            dialog.dismiss()
        }

        btnAddNewBatch.setOnClickListener {
            adapter.addNewBatchNumber(item.UserQuantity!!)
        }



        dialog.show()
        DialogSizeUtils.resizeDialog(dialog, requireContext(), 100, 80)
    }*/


}