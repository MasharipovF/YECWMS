package com.example.yecwms.presentation.purchasedeliveries.insert.adapter

import android.graphics.Bitmap
import android.text.Html
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
import com.example.yecwms.util.CalculatorBottomSheet
import com.example.yecwms.util.Utils
import com.google.android.material.imageview.ShapeableImageView

class PurchaseDeliveryBasketAdapter(
    val listener: InvBasketItemClickListener,
    val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<PurchaseDeliveryBasketAdapter.InvBasketVH>() {

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvBasketVH {
        return InvBasketVH(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_basket, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InvBasketVH, position: Int) {
        val item = list[position]


        with(holder) {
            if (item.DiscountPercent != 0.0) {
                discountLayout.visibility = View.VISIBLE
                discountPercent.text =
                    "Скидка ${Utils.getIntOrDoubleNumberString(item.DiscountPercent)}%!"
            } else {
                discountLayout.visibility = View.GONE
            }

            val itemNameText =
                "<font color=#47824a>${absoluteAdapterPosition + 1}) </font> <font color=#000000>${item.ItemName?:""} (${item.ItemCode})</font>"
            name.text = Html.fromHtml(itemNameText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            quantity.text = item.UserQuantity?.toInt().toString()
            price.text = item.UserPriceAfterVAT.toString()
            linetotal.text =
                Utils.getNumberWithThousandSeparator((item.UserQuantity!! * item.UserPriceAfterVAT!!))



            tvChooseBatches.isVisible = item.ManageBatchNumbers == GeneralConsts.T_YES
            val batchesSelectedColor =
                when (item.UserQuantity?.toInt()) {
                    item.BatchNumbers.sumOf { it.Quantity.toInt() } -> R.color.secondaryDarkColor
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

    interface InvBasketItemClickListener {
        fun changeQuantity(position: Int, quantity: Double): Boolean
        fun changePrice(position: Int, price: Double): Boolean
        fun onBatchItemClick(position: Int, item: DocumentLines)
        fun onImageClick(image: Bitmap?)

    }
}