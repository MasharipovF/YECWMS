package com.example.yecwms.util.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.masterdatas.SalesManagers

class SalesManagersListAdapter(val listener: SalesManagersListClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var list: MutableList<SalesManagers> = arrayListOf<SalesManagers>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is SalesManagers -> 1
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> SalesManagersListVH(
                LayoutInflater.from(parent.context).inflate(R.layout.dialog_cfl_list_row, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is SalesManagersListVH && item is SalesManagers) {
            with(holder) {
                bpName.text = item.salesEmployeeName
                bpAddress.visibility = View.GONE
                bpPhone.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int = list.size


    inner class SalesManagersListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bpName: TextView
        var bpAddress: TextView
        var bpPhone: TextView

        init {
            bpName = itemView.findViewById<TextView>(R.id.tvName)
            bpAddress = itemView.findViewById<TextView>(R.id.tvAddress)
            bpPhone = itemView.findViewById<TextView>(R.id.tvPhone)
            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as SalesManagers)
            }
        }
    }



    interface SalesManagersListClickListener {
        fun onClick(item: SalesManagers)
        //fun loadmore(position: Int)
    }


}