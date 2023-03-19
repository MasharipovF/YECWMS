package com.example.yecwms.presentation.invoice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.businesspartners.BPAddresses
import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.util.LoadMore

class InvoiceBpListAdapter(val listener: InvBpListClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is BusinessPartners -> 1
            is BPAddresses -> 2
            is LoadMore -> 3
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> CreditNoteBpListVH(
                LayoutInflater.from(parent.context).inflate(R.layout.dialog_cfl_list_row, parent, false)
            )
            2 -> CreditNoteBpAddressVH(
                LayoutInflater.from(parent.context).inflate(R.layout.dialog_cfl_list_row, parent, false)
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
        if (holder is CreditNoteBpListVH && item is BusinessPartners) {
            with(holder) {
                bpName.text = item.CardName
                bpAddress.text = item.Address
                bpPhone.text = item.Phone1
            }
        } else if (holder is CreditNoteBpAddressVH && item is BPAddresses) {
            with(holder) {
                address.text = item.addressName + " - " + addressMaker(
                    item.city,
                    item.county,
                    item.street,
                    item.block
                )
                addressToBeHidden.visibility = View.GONE
                phoneToBeHidden.visibility = View.GONE
            }
        } else if (holder is LoadMoreVH && item is LoadMore) {
            listener.loadmore(position)
        }
    }

    override fun getItemCount(): Int = list.size


    inner class CreditNoteBpListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bpName: TextView
        var bpAddress: TextView
        var bpPhone: TextView

        init {
            bpName = itemView.findViewById<TextView>(R.id.tvName)
            bpAddress = itemView.findViewById<TextView>(R.id.tvAddress)
            bpPhone = itemView.findViewById<TextView>(R.id.tvPhone)
            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as BusinessPartners)
            }
        }
    }

    inner class CreditNoteBpAddressVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var address: TextView
        var addressToBeHidden: TextView
        var phoneToBeHidden: TextView

        init {
            address = itemView.findViewById<TextView>(R.id.tvName)
            addressToBeHidden = itemView.findViewById<TextView>(R.id.tvAddress)
            phoneToBeHidden = itemView.findViewById<TextView>(R.id.tvPhone)
            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as BPAddresses)
            }
        }
    }


    inner class LoadMoreVH(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    interface InvBpListClickListener {
        fun onClick(item: Any)
        fun loadmore(position: Int)
    }

    private fun addressMaker(
        city: String?,
        county: String?,
        street: String?,
        block: String?
    ): String {
        var result = ""
        if (!city.isNullOrEmpty()) result = city
        if (!county.isNullOrEmpty()) result += ", $county"
        if (!street.isNullOrEmpty()) result += ", $street"
        if (!block.isNullOrEmpty()) result += ", $block"
        return result
    }

}