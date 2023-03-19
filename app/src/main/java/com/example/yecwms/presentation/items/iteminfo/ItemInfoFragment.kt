package com.example.yecwms.presentation.items.iteminfo

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.yecwms.R
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.items.ItemWarehouseInfo
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.databinding.FragmentItemInfoBinding
import com.example.yecwms.presentation.items.adapter.BatchesListAdapter
import com.example.yecwms.presentation.items.adapter.ItemsWhsInfoAdapter
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.Utils
import com.example.yecwms.util.barcodeprinter.PrinterHelper


class ItemInfoFragment : Fragment() {
    private lateinit var itemcode: String
    private lateinit var mViewModel: ItemInfoViewModel
    private lateinit var binding: FragmentItemInfoBinding
    private lateinit var adapter: ItemsWhsInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemcode = it.getString(GeneralConsts.PASSED_ITEM_CODE).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_info, container, false)
    }

    companion object {

        val TAG = ItemInfoFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(itemcode: String) =
            ItemInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(GeneralConsts.PASSED_ITEM_CODE, itemcode)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentItemInfoBinding.bind(view)

        mViewModel = ViewModelProvider(this).get(ItemInfoViewModel::class.java)

        adapter = ItemsWhsInfoAdapter(object : ItemsWhsInfoAdapter.OnItemClickListener {
            override fun onClick(item: ItemWarehouseInfo) {
                showBatchesListDialog(requireActivity(), item.WarehouseCode.toString())
            }

        })

        binding.tvItemName.isSelected = true

        binding.recyclerViewOITW.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOITW.adapter = adapter




        mViewModel.getItemInfo(itemcode)

        mViewModel.itemInfo.observe(viewLifecycleOwner) {
            binding.tvItemCode.text = it.ItemCode
            binding.tvItemName.text = it.ItemName
            binding.tvOnHand.text = Utils.getNumberWithThousandSeparator(it.TotalOnHand)
            binding.tvItemGroupCode.text = it.ItemsGroupName
            binding.tvInvenrotyUom.text = it.InventoryUOM
            binding.tvSalesUom.text = it.SalesUnit
            binding.tvPurchaseUom.text = it.PurchaseUnit
            binding.tvUomGroup.text = it.UoMGroupName


            val price =
                if (!it.ItemPrices.isNullOrEmpty()) {
                    if (it.ItemPrices[0].price != 0.0) {
                        Utils.getNumberWithThousandSeparator(it.ItemPrices[0].price) + " " + it.ItemPrices[0].currency
                    } else {
                        "Нет цены!"
                    }
                } else "Нет цены!"
            binding.tvPrice.text = price

        }


        mViewModel.listToDraw.observe(viewLifecycleOwner) {
            adapter.list = it
        }

        mViewModel.errorLoading.observe(viewLifecycleOwner) {
            if (it != null) Toast.makeText(
                requireContext(),
                it.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }

        mViewModel.loading.observe(viewLifecycleOwner) {
            Log.wtf("LOADING", it.toString())
            if (it) {
                binding.loader.visibility = View.VISIBLE
                binding.layoutMain.visibility = View.GONE
            } else {
                binding.loader.visibility = View.GONE
                binding.layoutMain.visibility = View.VISIBLE
            }
        }

        mViewModel.connectionError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), mViewModel.errorString, Toast.LENGTH_SHORT)
                .show()
        }

    }


    private fun showBatchesListDialog(activity: Activity, selectedWhs: String) {
        val dialog = Dialog(activity)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_cfl)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
        val btnAddNewBp = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)
        btnAddNewBp.visibility = View.GONE
        val etvSearch = dialog.findViewById<EditText>(R.id.dialog_etvSearch)
        val dialogLabel = dialog.findViewById<TextView>(R.id.labelDialog)
        dialogLabel.text = getString(R.string.batches)

        dialog.setOnCancelListener {
            activity.finish()
        }

        etvSearch.addTextChangedListener {
            mViewModel.batchesFilterString.value = etvSearch.text.toString()
        }

        val adapter = BatchesListAdapter(object : BatchesListAdapter.ItemsListClickListener {
            override fun onClick(item: Any) {

            }

            override fun loadmore(lastItemIndex: Int) {
                mViewModel.getMoreItemBatches(selectedWhs, lastItemIndex)
            }

            override fun onPrintClicked(item: Items) {
                printConfirmationDialog(
                    requireActivity(),
                    item
                )

            }

        })

        mViewModel.batchesFilterString.value = ""
        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        mViewModel.batchesFilterString.observe(viewLifecycleOwner) {
            mViewModel.getItemBatches(selectedWhs)
        }
        mViewModel.batchesListToDraw.observe(viewLifecycleOwner) {
            adapter.list = it
        }

        mViewModel.batchesLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.visibility = View.VISIBLE
                rvBps.visibility = View.INVISIBLE
            } else {
                loader.visibility = View.GONE
                rvBps.visibility = View.VISIBLE
            }
        }

        mViewModel.errorBatchesLoading.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                "Ошибка: $it",
                Toast.LENGTH_SHORT
            ).show()
        }

        dialog.show()
        DialogUtils.resizeDialog(dialog, requireContext(), 100, 70)
    }


    private fun printConfirmationDialog(
        activity: Activity,
        item: Items
    ) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_print_confirm)
        dialog.setCancelable(false)

        val layoutDialogProgress = dialog.findViewById<LinearLayout>(R.id.layoutDialogProgress)
        val layoutError = dialog.findViewById<LinearLayout>(R.id.layoutError)
        val layoutNumberOfCopies = dialog.findViewById<LinearLayout>(R.id.layoutNumberOfCopies)
        layoutNumberOfCopies.isVisible = true
        val printerIpAddress = dialog.findViewById<EditText>(R.id.etvPrinterIpAddress)
        val printerPort = dialog.findViewById<EditText>(R.id.etvPrinterPort)
        val btnYes = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnNo = dialog.findViewById<Button>(R.id.btnCancel)

        val numberOfCopies: EditText = dialog.findViewById<EditText>(R.id.etvCopies)


        layoutDialogProgress.visibility = View.GONE
        layoutError.visibility = View.GONE

        var printingResult = false


        printerIpAddress.setText(Preferences.printerIp)
        printerPort.setText(Preferences.printerPort.toString())


        btnYes.setOnClickListener {
            val documentLines = listOf(
                DocumentLines(
                    ItemCode = item.ItemCode,
                    ItemName = item.ItemName,
                    BatchNumbers = arrayListOf(
                        BatchNumbersForPost(
                            BatchNumber = item.BatchNumber,
                            Quantity = 0.0
                        )
                    )
                )
            )

            val copies = numberOfCopies.text
            if (copies.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Укажите количество копий!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (copies.toString().toInt() == 0) {
                Toast.makeText(requireContext(), "Укажите количество копий!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            documentLines[0].BatchNumbers[0].Quantity = copies.toString().toDouble()

            Log.wtf("DOCUMENTLINES", documentLines.toString())
            if (documentLines[0].BatchNumbers[0].Quantity == 0.0) {
                Toast.makeText(requireContext(), "Укажите количество копий!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val isSuccessful = PrinterHelper.printReceipt(requireContext(), documentLines)
            dialog.dismiss()

        }


        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        mViewModel.printerLoading.observe(this) {
            if (it) {
                layoutDialogProgress.visibility = View.VISIBLE
                layoutError.visibility = View.GONE
            }
        }

        mViewModel.printerError.observe(this) {
            if (it != null) {
                layoutDialogProgress.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
            }
        }

        mViewModel.printerSuccess.observe(this) {
            if (it) {
                mViewModel.printerLoading.postValue(false)
                mViewModel.printerSuccess.postValue(false)
                mViewModel.printerError.postValue(null)
                dialog.dismiss()
            }
        }



        dialog.show()
        DialogUtils.resizeDialogWidth(dialog, requireContext(), 50)
    }


}