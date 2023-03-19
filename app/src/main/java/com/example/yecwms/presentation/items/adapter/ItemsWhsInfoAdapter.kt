package com.example.yecwms.presentation.items.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.items.ItemWarehouseInfo
import com.example.yecwms.util.Utils


class ItemsWhsInfoAdapter(val listener: OnItemClickListener) :
    RecyclerView.Adapter<ItemsWhsInfoAdapter.ItemsWhsInfoViewHolder>() {

    var list: MutableList<ItemWarehouseInfo> = arrayListOf<ItemWarehouseInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ItemsWhsInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvWhsCode: TextView
        var tvWhsQty: TextView
        var tvWhsName: TextView
        var ivSeeBatches: ImageView

        init {
            tvWhsCode = itemView.findViewById<TextView>(R.id.tvWhsCode)
            tvWhsQty = itemView.findViewById<TextView>(R.id.tvWhsQty)
            tvWhsName = itemView.findViewById<TextView>(R.id.tvWhsName)
            ivSeeBatches = itemView.findViewById<ImageView>(R.id.ivSeeBatches)

            ivSeeBatches.setOnClickListener { listener.onClick(list[absoluteAdapterPosition]) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsWhsInfoViewHolder {
        return ItemsWhsInfoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_onhand_by_whs, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemsWhsInfoViewHolder, position: Int) {
        val item = list[position]
        with(holder) {
            tvWhsCode.text = item.WarehouseCode
            tvWhsName.text = item.WarehouseName
            tvWhsQty.text = Utils.getNumberWithThousandSeparator(item.InStock)
        }
    }


    interface OnItemClickListener {
        fun onClick(item: ItemWarehouseInfo)
    }
}