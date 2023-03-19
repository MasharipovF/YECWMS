package com.example.yecwms.presentation.businesspartners.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.util.LoadMore
import com.example.yecwms.util.Utils


class BpListAdapter(var listener: BpClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isFirstPage = true

    fun loadMoreBP(loadedItems: MutableList<Any>) {
        if (list[list.size - 1] is LoadMore) list.removeAt(list.size - 1)
        list.addAll(loadedItems)
        notifyDataSetChanged()
    }

    fun removeLastLoadMore() {
        if (list.isNotEmpty()) {
            if (list[list.size - 1] is LoadMore) list.removeAt(list.size - 1)
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is BusinessPartners -> 1
            is LoadMore -> 2
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> BPViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_bp, parent, false)
            )
            2 -> LoaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.bottom_loader_recycler, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is BPViewHolder && item is BusinessPartners) {
            with(holder) {
                tvCardName?.text = item.CardName
                tvAddress?.text = item.Address
                tvPhone?.text = item.Phone1
                tvBalanceUSD?.text = Utils.getNumberWithThousandSeparator(item.Balance!!) + " ${Preferences.localCurrency}"
            }
        } else if (holder is LoaderViewHolder && item is LoadMore) {
            listener.loadMore(position)
        }
    }


    inner class BPViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var tvCardName: TextView? = null
        var tvAddress: TextView? = null
        var tvPhone: TextView? = null
        var tvBalanceUSD: TextView? = null
        var tvBalanceUZS: TextView? = null

        init {
            tvCardName = itemView.findViewById<TextView>(R.id.tvBpName)
            tvAddress = itemView.findViewById<TextView>(R.id.tvAddress)
            tvPhone = itemView.findViewById<TextView>(R.id.tvPhone)
            tvBalanceUSD = itemView.findViewById<TextView>(R.id.tvBalanceUSD)
            tvBalanceUZS = itemView.findViewById<TextView>(R.id.tvBalanceUZS)
            itemView.setOnClickListener {
                val item = list[adapterPosition] as BusinessPartners
                listener.onClick(item)
            }
        }
    }

    inner class LoaderViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    interface BpClickListener {
        fun onClick(bp: BusinessPartners)
        fun loadMore(lastItemIndex: Int)
    }
}