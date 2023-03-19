package com.example.yecwms.presentation.purchasedeliveries.insert

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.databinding.FragmentPurchasedeliveryBasketBinding
import com.example.yecwms.presentation.purchasedeliveries.insert.adapter.PurchaseDeliveryBasketAdapter
import com.example.yecwms.presentation.purchasedeliveries.insert.adapter.PurchaseDeliveryBatchNumbersAdapter
import com.example.yecwms.util.DialogUtils


class PurchaseDeliveryBasketFragment : Fragment() {

    private lateinit var binding: FragmentPurchasedeliveryBasketBinding
    private lateinit var docLinesAdapter: PurchaseDeliveryBasketAdapter
    private lateinit var mViewModel: PurchaseDeliveryInsertViewModel


    companion object {
        fun newInstance() = PurchaseDeliveryBasketFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchasedelivery_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPurchasedeliveryBasketBinding.bind(view)
        mViewModel =
            ViewModelProvider(requireActivity()).get(PurchaseDeliveryInsertViewModel::class.java)



        docLinesAdapter = PurchaseDeliveryBasketAdapter(object :
            PurchaseDeliveryBasketAdapter.InvBasketItemClickListener {
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
                showBatchNumbersDialog(requireActivity(), position, item)
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
                        mViewModel.insertPurchaseDelivery()
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

    private fun showBatchNumbersDialog(activity: Activity, position: Int, item: DocumentLines) {
        Log.d("BATCHNUMBERS", item.toString())
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cfl_batches)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val btnAddNewBatch = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)

        val adapter = PurchaseDeliveryBatchNumbersAdapter(object :
            PurchaseDeliveryBatchNumbersAdapter.PurchaseDeliveryBatchNumbersClickListener {
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
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 80)
    }


}