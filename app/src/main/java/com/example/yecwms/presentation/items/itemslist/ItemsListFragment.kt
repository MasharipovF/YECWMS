package com.example.yecwms.presentation.items.itemslist

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.*
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
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.masterdatas.Warehouses
import com.example.yecwms.databinding.FragmentItemsListBinding
import com.example.yecwms.presentation.items.ItemsActivity
import com.example.yecwms.presentation.items.adapter.ItemsListAdapter
import com.example.yecwms.presentation.items.itemadd.ItemAddActivity
import com.example.yecwms.presentation.items.iteminfo.ItemInfoFragment
import com.example.yecwms.util.DialogUtils
import com.example.yecwms.util.adapters.WarehousesListAdapter
import com.example.yecwms.util.barcodeprinter.PrinterHelper
import com.example.yecwms.util.barcodereader.CustomScannerActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class ItemsListFragment : Fragment() {

    private lateinit var mViewModel: ItemsListViewModel
    private lateinit var binding: FragmentItemsListBinding
    private lateinit var adapter: ItemsListAdapter

    companion object {
        val TAG = ItemsListFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentItemsListBinding.bind(view)
        mViewModel = ViewModelProvider(this).get(ItemsListViewModel::class.java)

        adapter = ItemsListAdapter(object :
            ItemsListAdapter.InvItemListClickListener {
            override fun onClick(item: Items) {
                (activity as ItemsActivity).replaceFragment(
                    R.id.itemsFragmentContainer,
                    ItemInfoFragment.newInstance(item.ItemCode!!),
                    ItemInfoFragment.TAG,
                    backStack = true
                )
            }

            override fun loadMore(lastItemIndex: Int) {
                mViewModel.getMoreItemsList(lastItemIndex)
            }

            override fun onImageClick(image: Bitmap?) {
                DialogUtils.showEnlargedImage(requireContext(), image)
            }

            override fun onPrintClicked(item: Items) {
                printConfirmationDialog(requireActivity(), item)
            }

        })

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = adapter

        mViewModel.itemsListForImages.observe(viewLifecycleOwner) {
            it?.forEach { listItem ->
                mViewModel.getItemImage(listItem.ItemCode!!)
            }
        }

        binding.tvWarehouse.setOnClickListener {
            showWarehousesListDialog(requireActivity())
        }

        mViewModel.currentWarehouse.observe(viewLifecycleOwner) {
            binding.tvWarehouse.text = it.WarehouseName
            mViewModel.getItemsList()
        }

        mViewModel.filterString.observe(viewLifecycleOwner) {
            mViewModel.getItemsList()
        }

        mViewModel.listToDraw.observe(viewLifecycleOwner) {
            adapter.list = it
        }

        mViewModel.errorLoading.observe(viewLifecycleOwner) {
            if (it != null) Toast.makeText(
                requireContext(),
                "Ошибка при загрузке: $it",
                Toast.LENGTH_SHORT
            ).show()
        }

        mViewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.loader.visibility = View.VISIBLE
                binding.rvItems.visibility = View.GONE
            } else {
                binding.loader.visibility = View.GONE
                binding.rvItems.visibility = View.VISIBLE
            }
        }

        var timer: CountDownTimer? = null
        binding.etvSearch.addTextChangedListener {
            timer?.cancel()
            timer = object :
                CountDownTimer(GeneralConsts.TIMER_MS_IN_FUTURE, GeneralConsts.TIMER_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    val string = binding.etvSearch.text.toString()
                    if (string != mViewModel.filterString.value) {
                        mViewModel.setFilter(string)
                    }
                }
            }.start()

        }

        binding.imgBtnClearSearch.setOnClickListener { binding.etvSearch.text?.clear() }

        binding.fabAddItem.setOnClickListener {
            val intent = Intent(requireContext(), ItemAddActivity::class.java)
            startActivity(intent)
        }
    }

    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
         menuInflater.inflate(R.menu.menu_sales_order, menu)
         return super.onCreateOptionsMenu(menu)
     }*/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sales_order, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_open_scanner -> {

                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
                options.setPrompt("Отсканируйте штрихкод")
                options.setCameraId(0) // Use a specific camera of the device
                options.setBeepEnabled(false)
                options.setBarcodeImageEnabled(true)
                options.setOrientationLocked(false)
                options.captureActivity = CustomScannerActivity::class.java
                barcodeLauncher.launch(options)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(requireContext(), "Ничего не найдено!", Toast.LENGTH_LONG).show()
        } else {
            binding.etvSearch.setText(result.contents)
        }
    }


    private fun showWarehousesListDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_cfl)
        val rvBps = dialog.findViewById<RecyclerView>(R.id.dialog_rv)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_btnCancel)
        val btnAddNewBp = dialog.findViewById<ImageButton>(R.id.dialog_btnAddNew)
        val loader = dialog.findViewById<LottieAnimationView>(R.id.loader)
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
                mViewModel.currentWarehouse.value = item as Warehouses
                dialog.dismiss()
            }
        })

        mViewModel.getWarehouseList()
        rvBps.layoutManager = LinearLayoutManager(activity)
        rvBps.adapter = adapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        mViewModel.warehousesLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.visibility = View.VISIBLE
                rvBps.visibility = View.GONE
            } else {
                loader.visibility = View.GONE
                rvBps.visibility = View.VISIBLE

            }
        }


        mViewModel.warehousesList.observe(viewLifecycleOwner) {
            adapter.list = it
        }


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