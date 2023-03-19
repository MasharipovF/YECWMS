package com.example.yecwms.util.barcodegenerator

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter


object BarCodeGenerator {

    fun generateCODE128(data: String, width: Int, height: Int): Bitmap{
        val writer = MultiFormatWriter()
        val finaldata: String = Uri.encode(data, "utf-8")

        val bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, height)
        val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (i in 0 until width) { //width
            for (j in 0 until height) { //height
                imageBitmap.setPixel(i, j, if (bm[i, j]) Color.BLACK else Color.WHITE)
            }
        }

        return imageBitmap
    }


}