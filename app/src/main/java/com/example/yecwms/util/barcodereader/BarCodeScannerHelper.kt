package com.example.yecwms.util.barcodereader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.yecwms.util.barcodereader.BarCodeScannerConsts.SCAN_EXTRA_DATA_STRING
import com.example.yecwms.util.barcodereader.BarCodeScannerConsts.SCAN_EXTRA_LABEL_TYPE
import com.example.yecwms.util.barcodereader.BarCodeScannerConsts.SCAN_EXTRA_SOURCE


object BarCodeScannerConsts {

    const val ACTION = "android.intent.ACTION_DECODE_DATA"
    const val SCAN_EXTRA_DATA_STRING = "barcode_string"
    const val SCAN_EXTRA_SOURCE = "com.symbol.datawedge.source"
    const val SCAN_EXTRA_LABEL_TYPE = "com.symbol.datawedge.label_type"

}

class BarCodeScannerHelper {

    var actionListener: BarCodeScannerActionListener? = null

    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            val b: Bundle? = intent?.extras

            Log.d("WEDGESCANNERACTION", intent.toString())

            if (action.equals(BarCodeScannerConsts.ACTION)) {
                displayScanResult(intent, "via Broadcast")
            }

        }
    }

    //
    // The section below assumes that a UI exists in which to place the data. A production
    // application would be driving much of the behavior following a scan.
    //
    fun displayScanResult(initiatingIntent: Intent?, howDataReceived: String) {
        val decodedSource =
            initiatingIntent?.getStringExtra(SCAN_EXTRA_SOURCE)
        val decodedData =
            initiatingIntent?.getStringExtra(SCAN_EXTRA_DATA_STRING)
        val decodedLabelType =
            initiatingIntent?.getStringExtra(SCAN_EXTRA_LABEL_TYPE)
        Log.d("ZEBRA", "Data: $decodedData   /  label-type: $decodedLabelType ${initiatingIntent?.extras?.keySet()}")

        val bundle = initiatingIntent?.extras
        val bundleKeySet = bundle!!.keySet() // string key set

        for (key in bundleKeySet) { // traverse and print pairs
            Log.i(key, " : " + bundle!![key])
        }

        actionListener?.onScanned(decodedData, decodedLabelType, decodedSource)

    }



    
    interface BarCodeScannerActionListener{
        fun onScanned(barcode: String?, labelType: String?, source: String?)
    }
}