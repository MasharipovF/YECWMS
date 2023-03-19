package com.example.yecwms.presentation.items.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.util.LoadMore

class BatchesListAdapter(val listener: ItemsListClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
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
            1 -> ItemsListVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.dialog_cfl_list_batches_row, parent, false)
            )
            2 -> LoaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.bottom_loader_recycler, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is ItemsListVH && item is Items) {
            with(holder) {
                tvItemName.text = item.BatchNumber
                tvOnHandQuantity.text = item.QuantityOnStockByBatch.toString()
            }
        } else if (holder is LoaderViewHolder && item is LoadMore) {
            listener.loadmore(position)
        }

    }

    override fun getItemCount(): Int = list.size


    inner class ItemsListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvItemName: TextView
        var tvOnHandQuantity: TextView
        var btnPrint: ImageView

        init {
            tvItemName = itemView.findViewById<TextView>(R.id.tvItemName)
            tvOnHandQuantity = itemView.findViewById<TextView>(R.id.tvOnHandQuantity)
            btnPrint = itemView.findViewById<ImageView>(R.id.btnPrint)
            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as Items)
            }
            btnPrint.setOnClickListener {
                listener.onPrintClicked(list[absoluteAdapterPosition] as Items)
            }
        }
    }

    inner class LoaderViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    interface ItemsListClickListener {
        fun onClick(item: Any)
        fun loadmore(lastItemIndex: Int)
        fun onPrintClicked(item: Items)
    }


}