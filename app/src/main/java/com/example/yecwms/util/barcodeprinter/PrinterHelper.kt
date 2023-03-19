package com.example.yecwms.util.barcodeprinter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.yecwms.R
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.documents.DocumentLines
import com.godex.Godex

object PrinterHelper {

    enum class PrinterTextSize(
        val value: Godex.InternalFontID,
        val dotSize: Int,
        val newLineSpace: Int,
        val ttfFontSize: Int,
    ) {
        TINY(Godex.InternalFontID.A, 6, 8, 10),
        SMALL(Godex.InternalFontID.B, 8, 10, 15),
        NORMAL(Godex.InternalFontID.C, 10, 16, 25),
        BIG(Godex.InternalFontID.D, 12, 20, 30),
        HUGE(Godex.InternalFontID.F, 18, 28, 35),
        VERY_HUGE(Godex.InternalFontID.G, 24, 26, 40);
    }

    private val marginSizeInMM = 4

    private var dotsPerMM = 8

    private val newLineSizeInMM = 2

    private val barcodeHeightInMM = 11
    private val barcodeBottomTextSizeInMM = 4

    private val imageSizeInMM = 16

    fun convertPointSizeToMM(points: Int): Double {
        return points * 0.215
    }

    fun convertMMtoDots(mm: Int): Int {
        return mm * dotsPerMM
    }

    fun convertDotsToMM(dots: Int): Int {
        return dots / dotsPerMM
    }

    private var isUnsuccessful = false

    fun printReceipt(
        applicationContext: Context,
        document: List<DocumentLines>,
        paperWidth: Int = 60,
        paperHeight: Int = 60,
    ): Boolean {

        document.forEach { documentLines ->
            documentLines.BatchNumbers.forEach { batchNumbers ->
                if (batchNumbers.Quantity == 0.0) return false
                for (i in 1..batchNumbers.Quantity.toInt()) {

                    if (isUnsuccessful) return false
                    val wifi =
                        applicationContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

                    if (!wifi.isWifiEnabled)
                        wifi.isWifiEnabled = true
                    Godex.getMainContext(applicationContext)

                    val itemCodeSize = PrinterTextSize.HUGE
                    val itemNameSize = PrinterTextSize.NORMAL

                    var currentYpos = convertMMtoDots(marginSizeInMM)
                    val currentXpos = convertMMtoDots(marginSizeInMM)


                    val isConnected = Godex.openport(Preferences.printerIp, 1)
                    if (!isConnected) {
                        Toast.makeText(
                            applicationContext,
                            "Не удалось подключиться к принтеру!",
                            Toast.LENGTH_SHORT
                        ).show()
                        isUnsuccessful = true
                        return false
                    } else {
                        Godex.setup(paperHeight.toString(), "8", "2", "0", "3", "0")
                        Godex.sendCommand("^W$paperWidth") // WIDTH OF PAPER
                        Godex.sendCommand("^L")

                        // -----------------------------LOGO AND QR CODE ---------------------------------
                        val image =
                            BitmapFactory.decodeResource(
                                applicationContext.resources,
                                R.drawable.company_logo_bw
                            )
                        val resizedImage = resizeBitmapHeight(image, convertMMtoDots(imageSizeInMM))
                        Godex.putImage(currentXpos, currentYpos, resizedImage)

                        Godex.Bar_QRCode(
                            convertMMtoDots(35),
                            currentYpos,
                            2,
                            2,
                            "H",
                            8,
                            3,
                            0,
                            "https://yec.uz/"
                        )
                        currentYpos += convertMMtoDots(imageSizeInMM)

                        // -----------------------------ITEM CODE ---------------------------------
                        val xPosForItemCode =
                            (paperWidth - (documentLines.ItemCode.toString().length * convertPointSizeToMM(
                                itemCodeSize.dotSize
                            ))).toInt()

                        Log.wtf("XPOSFORITEMCODE: ", "$xPosForItemCode")

                        Godex.InternalFont_TextOut(
                            itemCodeSize.value,
                            convertMMtoDots(xPosForItemCode / 2),
                            currentYpos,
                            0,
                            1,
                            0,
                            "0B",
                            documentLines.ItemCode.toString()
                        )

                        currentYpos += itemCodeSize.dotSize + itemCodeSize.newLineSpace + +itemCodeSize.newLineSpace / 2


                        // -----------------------------ITEM NAME ---------------------------------
                        val charsPerLine: Int =
                            ((paperWidth - ((marginSizeInMM) * 2)) / convertPointSizeToMM(
                                itemNameSize.dotSize
                            )).toInt()

                        val multiLineText = documentLines.ItemName.toString().chunked(charsPerLine)
                        multiLineText.forEachIndexed { i, data ->
                            if (i > 1) return@forEachIndexed
                            val mData = if (data[0] == ' ') data.substring(1, data.length) else data

                            currentYpos =
                                if (i == 0) {
                                    currentYpos
                                } else {
                                    currentYpos + itemNameSize.dotSize + itemNameSize.newLineSpace
                                }

                            Godex.TrueTypeFont_TextOut(
                                Godex.DownloadFontID.A,
                                currentXpos,
                                currentYpos,
                                itemNameSize.ttfFontSize,
                                itemNameSize.ttfFontSize,
                                0,
                                "0",
                                mData
                            )
                        }

                        if (multiLineText.size == 1) {
                            currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace
                        }


                        // -----------------------------BARCODE ---------------------------------
                        currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace * 2
                        Godex.Bar_1D(
                            Godex.BarCodeType.Code128_Auto,
                            currentXpos,
                            currentYpos,
                            3,
                            2,
                            convertMMtoDots(barcodeHeightInMM),
                            0,
                            Godex.Readable.Bottom_Centered,
                            batchNumbers.BatchNumber
                        )



                        Godex.sendCommand("E")
                        isUnsuccessful = false
                    }

                    Godex.close()
                }

            }

        }



        return true
    }

    fun printItemBatch(
        applicationContext: Context,
        itemCode: String,
        itemName: String,
        foreignName: String,
        batchNumber: String,
        paperWidth: Int = 60,
        paperHeight: Int = 60,
    ): Boolean {


        Toast.makeText(applicationContext, "ДА1", Toast.LENGTH_SHORT)
            .show()


        if (isUnsuccessful) return false
        val wifi =
            applicationContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (!wifi.isWifiEnabled)
            wifi.isWifiEnabled = true
        Godex.getMainContext(applicationContext)

        val itemCodeSize = PrinterTextSize.HUGE
        val itemNameSize = PrinterTextSize.NORMAL

        var currentYpos = convertMMtoDots(marginSizeInMM)
        val currentXpos = convertMMtoDots(marginSizeInMM)


        val isConnected = Godex.openport(Preferences.printerIp, 1)
        if (!isConnected) {
            Toast.makeText(
                applicationContext,
                "Не удалось подключиться к принтеру!",
                Toast.LENGTH_SHORT
            ).show()
            isUnsuccessful = true
            return false
        } else {
            Godex.setup(paperHeight.toString(), "8", "2", "0", "3", "0")
            Godex.sendCommand("^W$paperWidth") // WIDTH OF PAPER
            Godex.sendCommand("^L")

            // -----------------------------LOGO AND QR CODE ---------------------------------
            val image =
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.company_logo_bw
                )
            val resizedImage = resizeBitmapHeight(image, convertMMtoDots(imageSizeInMM))
            Godex.putImage(currentXpos, currentYpos, resizedImage)

            Godex.Bar_QRCode(
                convertMMtoDots(35),
                currentYpos,
                2,
                2,
                "H",
                8,
                4,
                0,
                "https://yec.uz//"
            )
            currentYpos += convertMMtoDots(imageSizeInMM)

            // -----------------------------ITEM CODE ---------------------------------
            val xPosForItemCode =
                (paperWidth - (itemCode.length * convertPointSizeToMM(
                    itemCodeSize.dotSize
                ))).toInt()

            Log.wtf("XPOSFORITEMCODE: ", "$xPosForItemCode")

            Godex.InternalFont_TextOut(
                itemCodeSize.value,
                convertMMtoDots(xPosForItemCode / 2),
                currentYpos,
                0,
                1,
                0,
                "0B",
                itemCode
            )

            currentYpos += itemCodeSize.dotSize + itemCodeSize.newLineSpace + +itemCodeSize.newLineSpace / 2


            // -----------------------------ITEM NAME ---------------------------------
            val charsPerLine: Int =
                ((paperWidth - ((marginSizeInMM) * 2)) / convertPointSizeToMM(
                    itemNameSize.dotSize
                )).toInt()

            val multiLineText = itemName.chunked(charsPerLine)
            multiLineText.forEachIndexed { i, data ->
                if (i > 1) return@forEachIndexed
                val mData = if (data[0] == ' ') data.substring(1, data.length) else data

                currentYpos =
                    if (i == 0) {
                        currentYpos
                    } else {
                        currentYpos + itemNameSize.dotSize + itemNameSize.newLineSpace
                    }

                Godex.TrueTypeFont_TextOut(
                    Godex.DownloadFontID.A,
                    currentXpos,
                    currentYpos,
                    itemNameSize.ttfFontSize,
                    itemNameSize.ttfFontSize,
                    0,
                    "0",
                    mData
                )
            }

            if (multiLineText.size == 1) {
                currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace
            }

            val multiLineForeignText =
                foreignName.chunked(charsPerLine)
            multiLineForeignText.forEachIndexed { i, data ->
                if (i > 1) return@forEachIndexed
                val mData = if (data[0] == ' ') data.substring(1, data.length) else data

                currentYpos =
                    if (i == 0) {
                        currentYpos + itemNameSize.dotSize + itemNameSize.newLineSpace + convertMMtoDots(
                            1
                        )
                    } else {
                        currentYpos + itemNameSize.dotSize + itemNameSize.newLineSpace
                    }

                Godex.TrueTypeFont_TextOut(
                    Godex.DownloadFontID.A,
                    currentXpos,
                    currentYpos,
                    itemNameSize.ttfFontSize,
                    itemNameSize.ttfFontSize,
                    0,
                    "0",
                    mData
                )
            }

            if (multiLineForeignText.size == 1) {
                currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace
            }


            // -----------------------------BARCODE ---------------------------------
            currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace * 2
            Godex.Bar_1D(
                Godex.BarCodeType.Code128_Auto,
                currentXpos,
                currentYpos,
                3,
                2,
                convertMMtoDots(barcodeHeightInMM),
                0,
                Godex.Readable.Bottom_Centered,
                batchNumber
            )



            Godex.sendCommand("E")
            isUnsuccessful = false
        }

        Godex.close()



        return true
    }

    fun printItemBatchCopies(
        applicationContext: Context,
        itemCode: String,
        itemName: String,
        foreignName: String,
        batchNumber: String,
        copies: Int,
        paperWidth: Int = 60,
        paperHeight: Int = 60,
    ): Boolean {


        for (i in 1..copies) {
            if (isUnsuccessful) return false
            val wifi =
                applicationContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            if (!wifi.isWifiEnabled)
                wifi.isWifiEnabled = true
            Godex.getMainContext(applicationContext)

            val itemCodeSize = PrinterTextSize.HUGE
            val itemNameSize = PrinterTextSize.NORMAL

            var currentYpos = convertMMtoDots(marginSizeInMM)
            val currentXpos = convertMMtoDots(marginSizeInMM)


            val isConnected = Godex.openport(Preferences.printerIp, 1)
            if (!isConnected) {
                Toast.makeText(
                    applicationContext,
                    "Не удалось подключиться к принтеру!",
                    Toast.LENGTH_SHORT
                ).show()
                isUnsuccessful = true
                return false
            } else {
                Godex.setup(paperHeight.toString(), "8", "2", "0", "3", "0")
                Godex.sendCommand("^W$paperWidth") // WIDTH OF PAPER
                Godex.sendCommand("^L")

                // -----------------------------LOGO AND QR CODE ---------------------------------
                val image =
                    BitmapFactory.decodeResource(
                        applicationContext.resources,
                        R.drawable.company_logo_bw
                    )
                val resizedImage = resizeBitmapHeight(image, convertMMtoDots(imageSizeInMM))
                Godex.putImage(currentXpos, currentYpos, resizedImage)

                Godex.Bar_QRCode(
                    convertMMtoDots(35),
                    currentYpos,
                    2,
                    2,
                    "H",
                    8,
                    4,
                    0,
                    "https://yec.uz//"
                )
                currentYpos += convertMMtoDots(imageSizeInMM)

                // -----------------------------ITEM CODE ---------------------------------
                val xPosForItemCode =
                    (paperWidth - (itemCode.length * convertPointSizeToMM(
                        itemCodeSize.dotSize
                    ))).toInt()

                Log.wtf("XPOSFORITEMCODE: ", "$xPosForItemCode")

                Godex.InternalFont_TextOut(
                    itemCodeSize.value,
                    convertMMtoDots(xPosForItemCode / 2),
                    currentYpos,
                    0,
                    1,
                    0,
                    "0B",
                    itemCode
                )

                currentYpos += itemCodeSize.dotSize + itemCodeSize.newLineSpace + +itemCodeSize.newLineSpace / 2


                // -----------------------------ITEM NAME ---------------------------------
                val charsPerLine: Int =
                    ((paperWidth - ((marginSizeInMM) * 2)) / convertPointSizeToMM(
                        itemNameSize.dotSize
                    )).toInt()

                val multiLineText = itemName.chunked(charsPerLine)
                multiLineText.forEachIndexed { i, data ->
                    if (i > 1) return@forEachIndexed
                    val mData = if (data[0] == ' ') data.substring(1, data.length) else data

                    currentYpos =
                        if (i == 0) {
                            currentYpos
                        } else {
                            currentYpos + itemNameSize.dotSize + itemNameSize.newLineSpace
                        }

                    Godex.TrueTypeFont_TextOut(
                        Godex.DownloadFontID.A,
                        currentXpos,
                        currentYpos,
                        itemNameSize.ttfFontSize,
                        itemNameSize.ttfFontSize,
                        0,
                        "0",
                        mData
                    )
                }

                if (multiLineText.size == 1) {
                    currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace
                }

                val multiLineForeignText =
                    foreignName.chunked(charsPerLine)
                multiLineForeignText.forEachIndexed { i, data ->
                    if (i > 1) return@forEachIndexed
                    val mData = if (data[0] == ' ') data.substring(1, data.length) else data

                    currentYpos =
                        if (i == 0) {
                            currentYpos + itemNameSize.dotSize + itemNameSize.newLineSpace + convertMMtoDots(
                                1
                            )
                        } else {
                            currentYpos + itemNameSize.dotSize + itemNameSize.newLineSpace
                        }

                    Godex.TrueTypeFont_TextOut(
                        Godex.DownloadFontID.A,
                        currentXpos,
                        currentYpos,
                        itemNameSize.ttfFontSize,
                        itemNameSize.ttfFontSize,
                        0,
                        "0",
                        mData
                    )
                }

                if (multiLineForeignText.size == 1) {
                    currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace
                }


                // -----------------------------BARCODE ---------------------------------
                currentYpos += itemNameSize.dotSize + itemNameSize.newLineSpace * 2
                Godex.Bar_1D(
                    Godex.BarCodeType.Code128_Auto,
                    currentXpos,
                    currentYpos,
                    3,
                    2,
                    convertMMtoDots(barcodeHeightInMM),
                    0,
                    Godex.Readable.Bottom_Centered,
                    batchNumber
                )



                Godex.sendCommand("E")
                isUnsuccessful = false
            }

            Godex.close()
        }




        return true
    }


    fun resizeBitmap(source: Bitmap, changeBy: Float): Bitmap {
        val width = 200
        val ratio = width.toDouble() / source.width.toDouble()
        val finalWidth = width * changeBy
        val finalHeight = source.height * ratio * changeBy
        return Bitmap.createScaledBitmap(source, finalWidth.toInt(), finalHeight.toInt(), false)
    }

    fun resizeBitmapWidth(source: Bitmap, width: Int): Bitmap {
        val ratio = width.toDouble() / source.width.toDouble()
        val finalWidth = width
        val finalHeight = source.height * ratio
        return Bitmap.createScaledBitmap(source, finalWidth, finalHeight.toInt(), false)
    }

    fun resizeBitmapHeight(source: Bitmap, height: Int): Bitmap {
        val ratio = height.toDouble() / source.height.toDouble()
        val finalWidth = source.width * ratio
        val finalHeight = height
        return Bitmap.createScaledBitmap(source, finalWidth.toInt(), finalHeight, false)
    }


    fun printTest(
        printer: EscPosPrinter,
        applicationContext: Context,
    ): Boolean {

        try {

            // receipt with headers
            printer
                .printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                        printer, applicationContext.resources.getDrawableForDensity(
                            R.drawable.printerlogo, DisplayMetrics.DENSITY_MEDIUM
                        )
                    ) + "</img>\n" +
                            "[C]<b>ТЕСТОВАЯ СТРАНИЦА</b>\n" +
                            getDividerString(Preferences.printerMaxChar) +
                            "[L]\n" +
                            "[L]<b>ПОЛЬЗОВАТЕЛЬ: ${Preferences.userName}</b>\n" +
                            "[L]\n" +
                            "[L]МАГАЗИН: ${Preferences.defaultWhs}\n" +
                            "[L]\n" +
                            getDividerString(Preferences.printerMaxChar) +
                            "[L]\n" + "[L]\n" + "[L]\n" + "[L]\n" +
                            "[L]\n", 100
                )

            printer.disconnectPrinter()

            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    fun getDividerString(numberOfChars: Int): String {
        var resultString = "[L]"
        for (i in 1..numberOfChars) {
            resultString += "="
        }
        return resultString + "\n"
    }

    fun getOneLineDividerString(numberOfChars: Int): String {
        var resultString = "[L]"
        for (i in 1..numberOfChars) {
            resultString += "-"
        }
        return resultString
    }


}