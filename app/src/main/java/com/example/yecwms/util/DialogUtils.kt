package com.example.yecwms.util

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.yecwms.R

object DialogUtils {

    fun resizeDialog(dialog: Dialog, context: Context, widthPercent: Int, heightPercent: Int) {

        val width = (context.resources.displayMetrics.widthPixels * widthPercent / 100)
        val height = (context.resources.displayMetrics.heightPixels * heightPercent / 100)

        val window: Window? = dialog.window
        window?.setLayout(
            width,
            height
        )
    }

    fun resizeDialogWidth(dialog: Dialog, context: Context, widthPercent: Int) {
        val width = (context.resources.displayMetrics.widthPixels * widthPercent / 100)
        val window: Window? = dialog.window
        window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun makeRectangleDialog(dialog: Dialog, context: Context, widthPercent: Int) {
        val width = (context.resources.displayMetrics.widthPixels * widthPercent / 100)
        val window: Window? = dialog.window
        window?.setLayout(
            width,
            width
        )
    }

    fun showEnlargedImage(context: Context, image: Bitmap?) {
        if (image == null) return

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_image_view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val imageView = dialog.findViewById<ImageView>(R.id.ivItemImage)
        Glide.with(context)
            .load(image)
            //.placeholder(R.drawable.no_photo_placeholder)
            .into(imageView)
        dialog.show()
        makeRectangleDialog(dialog, context, 80)
    }


}