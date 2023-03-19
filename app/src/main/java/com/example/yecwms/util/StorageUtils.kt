package com.example.yecwms.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.yecwms.data.entity.inventorycounting.InventoryCountings
import com.google.gson.Gson
import java.io.*
import java.util.*

object StorageUtils {

    fun saveToExternalStorage(fileToSave: String, context: Context): Boolean {

        val fullPath: String =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/SAP_SKLAD";
        try {
            val dir: File = File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            var fOut: OutputStream? = null;

            val calendar = Calendar.getInstance()

            val dateTimeForFileName =
                "${calendar.get(Calendar.YEAR)}-" +
                        "${calendar.get(Calendar.MONTH)+1}-" +
                        "${calendar.get(Calendar.DAY_OF_MONTH)}"

            val file: File = File(fullPath, "Inventarizacia(${dateTimeForFileName}).txt");
            if (file.exists())
                file.delete();
            file.createNewFile();
            fOut = FileOutputStream(file);
            fOut.write(fileToSave.toByteArray())
            fOut.close();
            Log.e("saveToExternalStorage()", "file has been written in folder ${dir.absolutePath}");
            Log.e("saveToExternalStorage()", fileToSave);
            return true
        } catch (e: Exception) {
            Log.e("saveToExternalStorage()", e.message.toString());
            return false
        }

    }


    fun readFileFromExternalStorage(
        fileName: String,
        context: Context
    ): Any {

        var docForLoad = InventoryCountings()

        val fullPath: String =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/SAP_SKLAD";
        val myExternalFile = File(File(fullPath), fileName)

        try {
            if (fileName.toString() != null && fileName.toString().trim() != "") {
                var fileInputStream = FileInputStream(myExternalFile)
                var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder: StringBuilder = StringBuilder()
                var text: String? = null
                while (run {
                        text = bufferedReader.readLine()
                        text
                    } != null) {
                    stringBuilder.append(text)
                }
                fileInputStream.close()
                Log.e("readFromExternalStorage()", stringBuilder.toString());

                docForLoad =
                    Gson().fromJson(stringBuilder.toString(), InventoryCountings::class.java)
            }
        } catch (e: Exception) {
            Log.e("readFromExternalStorage()", e.message.toString());
            return e.toString()
        }

        return docForLoad
    }
}