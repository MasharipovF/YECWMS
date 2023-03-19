package com.example.yecwms.presentation.invoice.adapter

import android.graphics.Bitmap
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yecwms.R
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.util.CalculatorBottomSheet
import com.example.yecwms.util.LoadMore
import com.example.yecwms.util.Utils
import com.google.android.material.imageview.ShapeableImageView

class InvoiceBasketAdapter(
    val listener: InvBasketItemClickListener,
    val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<DocumentLines> = arrayListOf<DocumentLines>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].Type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> InvBasketVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_basket, parent, false)
            )
            1 -> InvBasketYarnVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_basket_yarn, parent, false)
            )
            2 -> InvBasketCarpetVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_basket_carpet, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is InvBasketVH -> {
                with(holder) {
                    if (list[position].DiscountPercent != 0.0) {
                        discountLayout.visibility = View.VISIBLE
                        discountPercent.text =
                            "Скидка ${Utils.getIntOrDoubleNumberString(item.DiscountPercent)}%!"
                    } else {
                        discountLayout.visibility = View.GONE
                    }


                    val itemNameText =
                        "<font color=#47824a>${absoluteAdapterPosition + 1}) </font> <font color=#000000>${item.ItemName ?: ""} (${item.ItemCode})</font>"
                    name.text = Html.fromHtml(itemNameText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    quantity.text = item.UserQuantity.toString()
                    price.text = item.UserPriceAfterVAT.toString()
                    linetotal.text =
                        Utils.getNumberWithThousandSeparator((item.UserQuantity!! * item.UserPriceAfterVAT!!))


                    tvChooseBatches.isVisible = item.ManageBatchNumbers == GeneralConsts.T_YES
                    val batchesSelectedColor =
                        when (item.UserQuantity) {
                            item.TempBatchNumbers.sumOf { it.selectedQuantity } -> R.color.secondaryDarkColor
                            else -> R.color.red
                        }

                    tvChooseBatches.setTextColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            batchesSelectedColor,
                            null
                        )
                    )
                }
            }

            is InvBasketCarpetVH -> {
                with(holder) {
                    if (list[position].DiscountPercent != 0.0) {
                        discountLayout.visibility = View.VISIBLE
                        discountPercent.text =
                            "Скидка ${Utils.getIntOrDoubleNumberString(item.DiscountPercent)}%!"
                    } else {
                        discountLayout.visibility = View.GONE
                    }


                    quality.text = item.Quality
                    quality.text = "${item.Height} X ${item.Width}"
                    quantity.text = item.UserQuantity.toString()
                    price.text = item.UserPriceAfterVAT.toString()
                    linetotal.text =
                        Utils.getNumberWithThousandSeparator((item.UserQuantity!! * item.UserPriceAfterVAT!!))


                    tvChooseBatches.isVisible = item.ManageBatchNumbers == GeneralConsts.T_YES
                    val batchesSelectedColor =
                        when (item.UserQuantity) {
                            item.TempBatchNumbers.sumOf { it.selectedQuantity } -> R.color.secondaryDarkColor
                            else -> R.color.red
                        }

                    tvChooseBatches.setTextColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            batchesSelectedColor,
                            null
                        )
                    )
                }
            }


            is InvBasketYarnVH -> {
                with(holder) {
                    if (list[position].DiscountPercent != 0.0) {
                        discountLayout.visibility = View.VISIBLE
                        discountPercent.text =
                            "Скидка ${Utils.getIntOrDoubleNumberString(item.DiscountPercent)}%!"
                    } else {
                        discountLayout.visibility = View.GONE
                    }


                    code.text = item.ItemCode
                    name.text = item.ItemName
                    netto.text = Utils.getIntOrDoubleNumberString(item.UserQuantity?:0.0)
                    brutto.text = Utils.getIntOrDoubleNumberString(item.Brutto?:0.0)
                    tare.text = Utils.getIntOrDoubleNumberString(item.Tare?:0.0)
                    bobbin.text = Utils.getIntOrDoubleNumberString(item.Bobbin?:0.0)

                    price.text = item.UserPriceAfterVAT.toString()
                    linetotal.text =
                        Utils.getNumberWithThousandSeparator((item.UserQuantity!! * item.UserPriceAfterVAT!!))


                    tvChooseBatches.isVisible = item.ManageBatchNumbers == GeneralConsts.T_YES
                    val batchesSelectedColor =
                        when (item.UserQuantity) {
                            item.TempBatchNumbers.sumOf { it.selectedQuantity } -> R.color.secondaryDarkColor
                            else -> R.color.red
                        }

                    Log.d("BATCHESCOUNT", item.TempBatchNumbers.sumOf { it.selectedQuantity }.toString())
                    Log.d("BATCHESCOUNT", item.UserQuantity.toString())

                    tvChooseBatches.setTextColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            batchesSelectedColor,
                            null
                        )
                    )
                }
            }

        }

    }

    override fun getItemCount(): Int = list.size


    inner class InvBasketVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var quantity: TextView
        var price: TextView
        var linetotal: TextView
        var btnPlus: ImageButton
        var btnMinus: ImageButton
        var discountLayout: LinearLayout
        var discountPercent: TextView
        var tvChooseBatches: TextView


        init {
            name = itemView.findViewById(R.id.tvItemName)
            quantity = itemView.findViewById(R.id.etvQuantity)
            price = itemView.findViewById(R.id.etvPrice)
            linetotal = itemView.findViewById(R.id.tvLineTotal)
            btnPlus = itemView.findViewById(R.id.btnPlus)
            btnMinus = itemView.findViewById(R.id.btnMinus)
            discountLayout = itemView.findViewById(R.id.layoutDiscount)
            discountPercent = itemView.findViewById(R.id.tvDiscountPercent)
            tvChooseBatches = itemView.findViewById(R.id.tvChooseBatches)



            tvChooseBatches.setOnClickListener {
                listener.onBatchItemClick(absoluteAdapterPosition, list[absoluteAdapterPosition])
            }

            btnPlus.setOnClickListener {
                listener.changeQuantity(
                    absoluteAdapterPosition,
                    list[absoluteAdapterPosition].Quantity!! + 1.0
                )
            }
            btnMinus.setOnClickListener {
                if (list[absoluteAdapterPosition].Quantity!! > 1)
                    listener.changeQuantity(
                        absoluteAdapterPosition,
                        list[absoluteAdapterPosition].Quantity!! - 1.0
                    )
            }

            quantity.setOnClickListener {
                val calculator =
                    CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                        val currentPosition = absoluteAdapterPosition
                        override fun onEdit(number: String) {
                            list[currentPosition].UserQuantity =
                                if (number.isEmpty()) 0.0 else number.toDouble()
                            notifyDataSetChanged()
                        }

                        override fun onSubmit(number: Double?) {
                            val currentQuantity: Double = number?.toDouble() ?: 0.0
                            val canAdd =
                                listener.changeQuantity(
                                    position = currentPosition,
                                    quantity = currentQuantity
                                )

                            if (!canAdd) {
                                list[currentPosition].UserQuantity =
                                    list[currentPosition].Quantity
                            }
                            notifyDataSetChanged()


                        }
                    })
                calculator.show(fragmentManager, "bottomsheet_calculator")
            }

            price.setOnClickListener {

                val calculator =
                    CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                        val currentPosition = absoluteAdapterPosition
                        val prevPrice = list[currentPosition].UserPriceAfterVAT
                        override fun onEdit(number: String) {
                            list[currentPosition].UserPriceAfterVAT =
                                if (number.isEmpty()) 0.0 else number.toDouble()
                            notifyDataSetChanged()
                        }

                        override fun onSubmit(number: Double?) {
                            if (number != null) {
                                val currentPrice = number.toDouble()
                                val canAdd = listener.changePrice(
                                    position = currentPosition,
                                    price = currentPrice
                                )
                                if (!canAdd) {
                                    list[currentPosition].UserPriceAfterVAT =
                                        prevPrice
                                }
                                notifyDataSetChanged()
                            }
                        }
                    })
                calculator.show(fragmentManager, "bottomsheet_calculator")
            }
        }
    }

    inner class InvBasketCarpetVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var quality: TextView
        var dimensions: TextView
        var quantity: TextView
        var price: TextView
        var linetotal: TextView
        var discountLayout: LinearLayout
        var discountPercent: TextView
        var tvChooseBatches: TextView


        init {
            quality = itemView.findViewById(R.id.tvQuality)
            dimensions = itemView.findViewById(R.id.tvDimensions)
            quantity = itemView.findViewById(R.id.tvQuantity)
            price = itemView.findViewById(R.id.etvPrice)
            linetotal = itemView.findViewById(R.id.tvLineTotal)
            discountLayout = itemView.findViewById(R.id.layoutDiscount)
            discountPercent = itemView.findViewById(R.id.tvDiscountPercent)
            tvChooseBatches = itemView.findViewById(R.id.tvChooseBatches)


            tvChooseBatches.setOnClickListener {
                listener.onBatchItemClick(absoluteAdapterPosition, list[absoluteAdapterPosition])
            }

            price.setOnClickListener {

                val calculator =
                    CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                        val currentPosition = absoluteAdapterPosition
                        val prevPrice = list[currentPosition].UserPriceAfterVAT
                        override fun onEdit(number: String) {
                            list[currentPosition].UserPriceAfterVAT =
                                if (number.isEmpty()) 0.0 else number.toDouble()
                            notifyDataSetChanged()
                        }

                        override fun onSubmit(number: Double?) {
                            if (number != null) {
                                val currentPrice = number.toDouble()
                                val canAdd = listener.changePrice(
                                    position = currentPosition,
                                    price = currentPrice
                                )
                                if (!canAdd) {
                                    list[currentPosition].UserPriceAfterVAT =
                                        prevPrice
                                }
                                notifyDataSetChanged()
                            }
                        }
                    })
                calculator.show(fragmentManager, "bottomsheet_calculator")
            }
        }
    }

    inner class InvBasketYarnVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var code: TextView
        var brutto: TextView
        var tare: TextView
        var netto: TextView
        var bobbin: TextView
        var price: TextView
        var linetotal: TextView
        var discountLayout: LinearLayout
        var discountPercent: TextView
        var tvChooseBatches: TextView


        init {
            name = itemView.findViewById(R.id.tvItemName)
            code = itemView.findViewById(R.id.tvItemCode)
            brutto = itemView.findViewById(R.id.tvBrutto)
            tare = itemView.findViewById(R.id.tvTare)
            netto = itemView.findViewById(R.id.tvNetto)
            bobbin = itemView.findViewById(R.id.tvBobbin)
            price = itemView.findViewById(R.id.etvPrice)
            linetotal = itemView.findViewById(R.id.tvLineTotal)
            discountLayout = itemView.findViewById(R.id.layoutDiscount)
            discountPercent = itemView.findViewById(R.id.tvDiscountPercent)
            tvChooseBatches = itemView.findViewById(R.id.tvChooseBatches)



            tvChooseBatches.setOnClickListener {
                listener.onBatchItemClick(absoluteAdapterPosition, list[absoluteAdapterPosition])
            }

            price.setOnClickListener {

                val calculator =
                    CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                        val currentPosition = absoluteAdapterPosition
                        val prevPrice = list[currentPosition].UserPriceAfterVAT
                        override fun onEdit(number: String) {
                            list[currentPosition].UserPriceAfterVAT =
                                if (number.isEmpty()) 0.0 else number.toDouble()
                            notifyDataSetChanged()
                        }

                        override fun onSubmit(number: Double?) {
                            if (number != null) {
                                val currentPrice = number.toDouble()
                                val canAdd = listener.changePrice(
                                    position = currentPosition,
                                    price = currentPrice
                                )
                                if (!canAdd) {
                                    list[currentPosition].UserPriceAfterVAT =
                                        prevPrice
                                }
                                notifyDataSetChanged()
                            }
                        }
                    })
                calculator.show(fragmentManager, "bottomsheet_calculator")
            }
        }
    }


    interface InvBasketItemClickListener {
        fun changeQuantity(position: Int, quantity: Double): Boolean
        fun changePrice(position: Int, price: Double): Boolean
        fun onBatchItemClick(position: Int, item: DocumentLines)
        fun onImageClick(image: Bitmap?)

    }
}