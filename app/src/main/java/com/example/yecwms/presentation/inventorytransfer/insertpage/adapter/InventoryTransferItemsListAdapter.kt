package com.example.yecwms.presentation.inventorytransfer.insertpage.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.util.LoadMore
import java.math.BigDecimal

class InventoryTransferItemsListAdapter(val listener: InvItemListClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is Items -> 1
            is LoadMore -> 2
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> InvItemListVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_doclines, parent, false)
            )
            2 -> LoadMoreVH(
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

                /*
                if (item.OnHandCurrentWhs == BigDecimal.valueOf(0.0))
                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            R.color.red_opacity_50,
                            null
                        )
                    )
                else
                    cardView.setCardBackgroundColor(
                        ResourcesCompat.getColor(
                            itemView.resources,
                            R.color.white,
                            null
                        )
                    )
*/
                val salesUnit = if (item.SalesUnit==null) "" else item.SalesUnit

                val itemNameText = "<font color=#000000>${item.ItemName}, </font> <font color=#47824a>${salesUnit} </font>"
                itemName.text = Html.fromHtml(itemNameText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                itemQuantity.text = item.OnHandCurrentWhs.toString()

            }
        } else if (holder is LoadMoreVH && item is LoadMore) {
            listener.loadmore(position)
        }
    }

    override fun getItemCount(): Int = list.size


    inner class InvItemListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemPrice: TextView
        var itemQuantity: TextView
        var cardView: CardView

        init {
            itemName = itemView.findViewById<TextView>(R.id.tvItemName)
            itemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)
            itemQuantity = itemView.findViewById<TextView>(R.id.tvOnHandByWhs)
            cardView = itemView.findViewById(R.id.cardView)
            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as Items)
            }
        }
    }

    inner class LoadMoreVH(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    interface InvItemListClickListener {
        fun onClick(item: Items)
        fun loadmore(position: Int)
    }
}