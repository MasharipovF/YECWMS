package com.example.yecwms.presentation.items.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.yecwms.R
import com.example.yecwms.data.entity.masterdatas.*
import com.example.yecwms.data.entity.series.Series

class SpinnerAdapter(val context: Context) : BaseAdapter() {

    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val inflater = LayoutInflater.from(context)


    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = convertView ?: inflater.inflate(R.layout.list_spinner, parent, false)

        val item = list[position]
        val tvName = view.findViewById<TextView>(R.id.tvName)
        tvName.text = when (item) {
            is ItemsGroup -> item.GroupName
            is UnitOfMeasurementGroups -> item.GroupName
            is UnitOfMeasurement -> item.UomName
            is PriceLists -> item.priceListName
            is BusinessPartnerGroups -> item.Name
            is Series -> item.name
            else -> ""
        }
        return view
    }


}