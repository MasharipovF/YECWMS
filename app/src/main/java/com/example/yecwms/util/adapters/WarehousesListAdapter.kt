package com.example.yecwms.util.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.masterdatas.Warehouses

class WarehousesListAdapter(val listener: WarehousesListClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var list: MutableList<Warehouses> = arrayListOf<Warehouses>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is Warehouses -> 1
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> WarehousesListVH(
                LayoutInflater.from(parent.context).inflate(R.layout.dialog_cfl_list_row, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is WarehousesListVH && item is Warehouses) {
            with(holder) {
                bpName.text = item.WarehouseCode
                bpAddress.text = item.WarehouseName
                bpPhone.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int = list.size


    inner class WarehousesListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bpName: TextView
        var bpAddress: TextView
        var bpPhone: TextView

        init {
            bpName = itemView.findViewById<TextView>(R.id.tvName)
            bpAddress = itemView.findViewById<TextView>(R.id.tvAddress)
            bpPhone = itemView.findViewById<TextView>(R.id.tvPhone)
            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as Warehouses)
            }
        }
    }



    interface WarehousesListClickListener {
        fun onClick(item: Any)
        //fun loadmore(position: Int)
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