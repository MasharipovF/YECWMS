package com.example.yecwms.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.example.yecwms.BuildConfig
import com.example.yecwms.data.entity.items.Items
import org.apache.commons.compress.utils.IOUtils
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.*


class ExportToExcel {

    private val excelColumns = arrayOf("Наименование товара", "Остаток (Основная ЕИ)", "Остаток (Доп ЕИ)")

    @Throws(IOException::class)
    fun writeToExcel(itemArray: Array<Items>, context: Context) {
        val excelWorkBook = XSSFWorkbook()
        val createHelper = excelWorkBook.creationHelper
        val sheet = excelWorkBook.createSheet("Items")

        val headerFont = excelWorkBook.createFont()
        headerFont.color = IndexedColors.BLACK.getIndex()
        headerFont.fontHeight = 250
        headerFont.bold = true

        val headerCellStyle = excelWorkBook.createCellStyle()
        headerCellStyle.setFont(headerFont)

        val headerRow = sheet.createRow(0) //initialize 1st row

        //create 1st row
        for (col in excelColumns.indices) {
            val cell = headerRow.createCell(col)
            cell.setCellValue(excelColumns[col])
            cell.cellStyle = headerCellStyle
        }


        //insert picture's media into workbook
        val inputStream: InputStream = FileInputStream("./logotest.png")
        val imageBytes: ByteArray = IOUtils.toByteArray(inputStream)
        val pictureIdx: Int = excelWorkBook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG)
        inputStream.close()

        //insert picture anchored over the cells of the sheet
        val helper: CreationHelper = excelWorkBook.creationHelper
        val drawing: Drawing<*> = sheet.createDrawingPatriarch()
        val anchor = helper.createClientAnchor()
        anchor.setCol1(0) //col A
        anchor.row1 = 0 //row 1

        val pict: Picture = drawing.createPicture(anchor, pictureIdx)
        pict.resize() //now picture is anchored at A1 and sized to it's original size


        //cell style for age
       /* val ageCellStyle = excelWorkBook.createCellStyle()
        ageCellStyle.dataFormat = createHelper.createDataFormat().getFormat("#")*/

        var rowIndex = 1
        for (item in itemArray) {
            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue(item.ItemName)
            row.createCell(1).setCellValue(item.TotalOnHand.toString() + " ${item.InventoryUOM}")

        /*
            val ageCell = row.createCell(3)
            item.TotalOnHand?.toDouble()?.let { ageCell.setCellValue(it) }
            ageCell.cellStyle = ageCellStyle*/
        }

        val generatedExcelFile =
            FileOutputStream("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/SAPtovarlar.xlsx")
        excelWorkBook.write(generatedExcelFile)
        excelWorkBook.close()
        openExcel(context)
    }


    fun openExcel(context: Context){
        val pdfFile = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/SAPtovarlar.xlsx")
        val openPdf = Intent(Intent.ACTION_VIEW)
        openPdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", pdfFile)
        openPdf.setDataAndType(fileUri, "application/pdf")
        openPdf.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(context, Intent.createChooser(openPdf, "Выберите приложение"),null)
    }
}