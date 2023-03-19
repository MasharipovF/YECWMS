package com.example.yecwms.presentation.businesspartners.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.businesspartners.BPAddresses

class BpAddressesAdapter : RecyclerView.Adapter<BpAddressesAdapter.BpAddressesViewHolder>() {

    var list = arrayListOf<BPAddresses>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BpAddressesViewHolder {
        return BpAddressesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_bp_address, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BpAddressesViewHolder, position: Int) {
        val item = list[position]
        with(holder) {
            name?.text = item.addressName
            address?.text = addressMaker(item.city, item.county, item.street, item.block)
        }
    }

    override fun getItemCount(): Int = list.size


    inner class BpAddressesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null
        var address: TextView? = null

        init {
            name = itemView.findViewById(R.id.tvAddresName)
            address = itemView.findViewById(R.id.tvFullAddress)
        }
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
