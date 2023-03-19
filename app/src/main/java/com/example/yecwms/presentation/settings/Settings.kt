package com.example.yecwms.presentation.settings

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.yecwms.R
import com.example.yecwms.core.BaseActivity
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.databinding.ActivitySettingsBinding
import com.example.yecwms.util.barcodeprinter.PrinterHelper
import com.google.android.material.snackbar.Snackbar

class Settings : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding


    override fun init(savedInstanceState: Bundle?) {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.etvUsername.setText(Preferences.userName.toString())
        binding.etvPrinterIpAddress.setText(Preferences.printerIp)
        binding.etvPrinterPort.setText(Preferences.printerPort.toString())





        binding.btnSubmit.setOnClickListener {
            Preferences.printerIp = binding.etvPrinterIpAddress.text.toString()
            Preferences.printerPort = binding.etvPrinterPort.text.toString().toInt()
            finish()
        }

        binding.btnPrintTest.setOnClickListener {
            Preferences.printerIp = binding.etvPrinterIpAddress.text.toString()
            Preferences.printerPort = binding.etvPrinterPort.text.toString().toInt()

            if (checkForFilling())
                connectAndPrint(this)
            else
                Toast.makeText(this, "Заполните IP и порт принтера!", Toast.LENGTH_SHORT).show()

        }
    }


    fun checkForFilling(): Boolean {
        return !(Preferences.printerIp.isNullOrEmpty() || Preferences.portNumber.isNullOrEmpty())
    }


    private fun showSnackbar(showstring: String) {
        Snackbar.make(binding.layoutMainSettings, showstring, Snackbar.LENGTH_LONG)
            .setActionTextColor(resources.getColor(R.color.button_unable)).show()
    }

    fun connectAndPrint(
        context: Context
    ) {

        val docLines = arrayListOf<DocumentLines>(
            DocumentLines(
                ItemCode = "02000123",
                ItemName = "РУССКОЕ КАКОЕ НАЗВАНИЕ ЦВЕТКА РУССКОЕ КАКОЕ НАЗВАНИЕ ЦВЕТКА",
                BarCode = "020012342206221113",
                BatchNumbers = arrayListOf<BatchNumbersForPost>( BatchNumbersForPost(BatchNumber = "020001232406221031", Quantity = 2.0))
            ),
            DocumentLines(
                ItemCode = "02000124",
                ItemName = "РУССКОЕ КАКОЕ НАЗВАНИЕ ЦВЕТКА",
                BarCode = "020066472206221113",
                BatchNumbers = arrayListOf<BatchNumbersForPost>( BatchNumbersForPost(BatchNumber = "020001242406221031", Quantity = 3.0)) as ArrayList<BatchNumbersForPost>

            )
        )


        val isSuccessful = PrinterHelper.printReceipt(context, docLines)



    }





}