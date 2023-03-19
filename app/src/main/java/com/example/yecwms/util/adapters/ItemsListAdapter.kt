package com.example.yecwms.util.adapters

import android.graphics.Bitmap
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yecwms.R
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.util.LoadMore
import com.example.yecwms.util.Utils
import com.google.android.material.imageview.ShapeableImageView

class ItemsListAdapter(val listener: InvItemListClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var rate: Double = 1.0
    var currency: String? = null

    fun setCurrencyAndRate(currency: String, rate: Double) {
        this.currency = currency
        this.rate = rate
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is Items -> {
                (list[position] as Items).Type
            }
            is LoadMore -> 3
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> InvItemListVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_doclines, parent, false)
            )
            1 -> InvItemListYarnVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_doclines_yarn, parent, false)
            )
            2 -> InvItemListCarpetVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_doclines_carpet, parent, false)
            )
            3 -> LoadMoreVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.bottom_loader_recycler, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is InvItemListVH && item is Items) {
            with(holder) {

                if (item.ManageBatchNumbers == GeneralConsts.T_YES && item.QuantityOnStockByBatch <= 0.0) {
                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            R.color.red_opacity_50,
                            null
                        )
                    )
                } else if (item.OnHandCurrentWhs <= 0.0) {
                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            R.color.red_opacity_50,
                            null
                        )
                    )
                } else {

                    val color = when (item.DiscountType) {
                        "S" -> R.color.discount_special_price
                        "V" -> R.color.discount_volume_period
                        "G" -> R.color.discount_group
                        else -> R.color.white
                    }

                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            color,
                            null
                        )
                    )

                }


                if (item.DiscountApplied != 0.0) {
                    discountLayout.visibility = View.VISIBLE
                    discountDivider.visibility = View.VISIBLE
                    discountPercent.text =
                        "Скидка ${Utils.getIntOrDoubleNumberString(item.DiscountApplied)}%!"
                } else {
                    discountLayout.visibility = View.GONE
                    discountDivider.visibility = View.GONE
                }

                if (item.ManageBatchNumbers == GeneralConsts.T_YES) {
                    batchLayout.isVisible = true
                    batchQuantity.text =
                        Utils.getIntOrDoubleNumberString(item.QuantityOnStockByBatch)
                    batchNumber.text =
                        item.BatchNumber ?: ""
                } else {
                    batchLayout.isVisible = false
                }

                val itemNameText =
                    "<font color=#000000>${item.ItemName} (${item.ItemCode}), </font> <font color=#47824a>${item.SalesUnit}</font>"
                itemName.text = Html.fromHtml(itemNameText, HtmlCompat.FROM_HTML_MODE_LEGACY)


                val price = when {
                    rate == 0.0 -> {
                        "Курс не определен!"
                    }
                    item.Price == 0.0 -> {
                        "Нет цены!"
                    }
                    else -> {
                        "${
                            Utils.getNumberWithThousandSeparator(item.DiscountedPrice * rate)
                        } ${currency ?: item.Currency}"
                    }
                }

                itemPrice.text = price

                itemQuantity.text = item.OnHandCurrentWhs.toInt().toString()
            }
        } else if (holder is InvItemListYarnVH && item is Items) {
            with(holder) {

                if (item.QuantityOnStockByBatch <= 0.0) {
                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            R.color.red_opacity_50,
                            null
                        )
                    )
                } else {

                    val color = when (item.DiscountType) {
                        "S" -> R.color.discount_special_price
                        "V" -> R.color.discount_volume_period
                        "G" -> R.color.discount_group
                        else -> R.color.white
                    }

                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            color,
                            null
                        )
                    )

                }


                if (item.DiscountApplied != 0.0) {
                    discountLayout.visibility = View.VISIBLE
                    discountDivider.visibility = View.VISIBLE
                    discountPercent.text =
                        "Скидка ${Utils.getIntOrDoubleNumberString(item.DiscountApplied)}%!"
                } else {
                    discountLayout.visibility = View.GONE
                    discountDivider.visibility = View.GONE
                }

                batchLayout.isVisible = item.ManageBatchNumbers == GeneralConsts.T_YES

                batchNumber.text =
                    item.BatchNumber ?: ""
                netto.text = Utils.getIntOrDoubleNumberString(item.QuantityOnStockByBatch)
                brutto.text = Utils.getIntOrDoubleNumberString(item.Brutto)
                tare.text = Utils.getIntOrDoubleNumberString(item.Tare)
                bobbin.text = Utils.getIntOrDoubleNumberString(item.Bobbin)

                val itemNameText =
                    "<font color=#000000>${item.ItemName} (${item.ItemCode}), </font> <font color=#47824a>${item.SalesUnit}</font>"
                itemName.text = Html.fromHtml(itemNameText, HtmlCompat.FROM_HTML_MODE_LEGACY)


                val price = when {
                    rate == 0.0 -> {
                        "Курс не определен!"
                    }
                    item.Price == 0.0 -> {
                        "Нет цены!"
                    }
                    else -> {
                        "${
                            Utils.getNumberWithThousandSeparator(item.DiscountedPrice * rate)
                        } ${currency ?: item.Currency}"
                    }
                }

                itemPrice.text = price

                itemQuantity.text = item.OnHandCurrentWhs.toInt().toString()
            }
        } else if (holder is InvItemListCarpetVH && item is Items) {
            with(holder) {

                if (item.QuantityOnStockByBatch <= 0.0) {
                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            R.color.red_opacity_50,
                            null
                        )
                    )
                } else {

                    val color = when (item.DiscountType) {
                        "S" -> R.color.discount_special_price
                        "V" -> R.color.discount_volume_period
                        "G" -> R.color.discount_group
                        else -> R.color.white
                    }

                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            color,
                            null
                        )
                    )

                }


                if (item.DiscountApplied != 0.0) {
                    discountLayout.visibility = View.VISIBLE
                    discountDivider.visibility = View.VISIBLE
                    discountPercent.text =
                        "Скидка ${Utils.getIntOrDoubleNumberString(item.DiscountApplied)}%!"
                } else {
                    discountLayout.visibility = View.GONE
                    discountDivider.visibility = View.GONE
                }

                batchLayout.isVisible = item.ManageBatchNumbers == GeneralConsts.T_YES

                batchNumber.text =
                    item.BatchNumber ?: ""
                batchQuantity.text = Utils.getIntOrDoubleNumberString(item.QuantityOnStockByBatch)


                quality.text = item.Quality ?: ""
                dimensions.text = "${item.Height} X ${item.Width}"


                val price = when {
                    rate == 0.0 -> {
                        "Курс не определен!"
                    }
                    item.Price == 0.0 -> {
                        "Нет цены!"
                    }
                    else -> {
                        "${
                            Utils.getNumberWithThousandSeparator(item.DiscountedPrice * rate)
                        } ${currency ?: item.Currency}"
                    }
                }

                itemPrice.text = price

                itemQuantity.text = item.OnHandCurrentWhs.toInt().toString()
            }
        } else if (holder is LoadMoreVH && item is LoadMore) {
            listener.loadMore(position)
        }
    }

    override fun getItemCount(): Int = list.size


    inner class InvItemListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemPrice: TextView
        var itemQuantity: TextView
        var cardView: CardView
        var discountLayout: LinearLayout
        var discountDivider: View
        var discountPercent: TextView
        var batchLayout: LinearLayout
        var batchNumber: TextView
        var batchQuantity: TextView

        init {
            itemName = itemView.findViewById<TextView>(R.id.tvItemName)
            itemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)
            itemQuantity = itemView.findViewById<TextView>(R.id.tvOnHandByWhs)
            cardView = itemView.findViewById(R.id.cardView)
            discountLayout = itemView.findViewById(R.id.layoutDiscount)
            discountDivider = itemView.findViewById(R.id.discountDivider)
            discountPercent = itemView.findViewById(R.id.tvDiscountPercent)
            batchLayout = itemView.findViewById(R.id.layoutBatchInfo)
            batchNumber = itemView.findViewById(R.id.tvBatchNumber)
            batchQuantity = itemView.findViewById(R.id.tvOnHandByBatch)



            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as Items)
            }


        }
    }

    inner class LoadMoreVH(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    inner class InvItemListCarpetVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var quality: TextView
        var dimensions: TextView
        var itemPrice: TextView
        var itemQuantity: TextView
        var cardView: CardView
        var discountLayout: LinearLayout
        var discountDivider: View
        var discountPercent: TextView
        var batchLayout: LinearLayout
        var batchNumber: TextView
        var batchQuantity: TextView


        init {
            quality = itemView.findViewById<TextView>(R.id.tvQuality)
            dimensions = itemView.findViewById<TextView>(R.id.tvDimensions)
            itemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)
            itemQuantity = itemView.findViewById<TextView>(R.id.tvOnHandByWhs)
            cardView = itemView.findViewById(R.id.cardView)
            discountLayout = itemView.findViewById(R.id.layoutDiscount)
            discountDivider = itemView.findViewById(R.id.discountDivider)
            discountPercent = itemView.findViewById(R.id.tvDiscountPercent)
            batchLayout = itemView.findViewById(R.id.layoutBatchInfo)
            batchNumber = itemView.findViewById(R.id.tvBatchNumber)
            batchQuantity = itemView.findViewById(R.id.tvQuantity)



            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as Items)
            }


        }
    }


    inner class InvItemListYarnVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemPrice: TextView
        var itemQuantity: TextView
        var cardView: CardView
        var discountLayout: LinearLayout
        var discountDivider: View
        var discountPercent: TextView
        var batchLayout: LinearLayout
        var batchNumber: TextView
        var brutto: TextView
        var tare: TextView
        var netto: TextView
        var bobbin: TextView

        init {
            itemName = itemView.findViewById<TextView>(R.id.tvItemName)
            itemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)
            itemQuantity = itemView.findViewById<TextView>(R.id.tvOnHandByWhs)
            cardView = itemView.findViewById(R.id.cardView)
            discountLayout = itemView.findViewById(R.id.layoutDiscount)
            discountDivider = itemView.findViewById(R.id.discountDivider)
            discountPercent = itemView.findViewById(R.id.tvDiscountPercent)
            batchLayout = itemView.findViewById(R.id.layoutBatchInfo)
            batchNumber = itemView.findViewById(R.id.tvBatchNumber)
            brutto = itemView.findViewById(R.id.tvBrutto)
            tare = itemView.findViewById(R.id.tvTare)
            netto = itemView.findViewById(R.id.tvNetto)
            bobbin = itemView.findViewById(R.id.tvBobbin)



            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as Items)
            }


        }
    }


    interface InvItemListClickListener {
        fun onClick(item: Items)
        fun loadMore(lastItemIndex: Int)
        fun onImageClick(image: Bitmap?)
    }
}