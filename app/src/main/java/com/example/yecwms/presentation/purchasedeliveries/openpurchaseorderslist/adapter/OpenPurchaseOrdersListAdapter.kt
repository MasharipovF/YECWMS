package com.example.yecwms.presentation.purchasedeliveries.openpurchaseorderslist.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.util.LoadMore
import com.example.yecwms.util.Utils
import kotlinx.android.synthetic.main.list_documents.view.*

class OpenPurchaseOrdersListAdapter(var listener: OnAdapterItemClickListener?, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.d("INVOICES", "INSIDE ADAPTER" +  value.toString())

        }

    var isFirstPage = true

    fun loadMoreDoc(loadedItems: MutableList<Any>) {
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
            is Document -> 1
            is LoadMore -> 2
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> DocViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_documents, parent, false)
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
        Log.d("INVOICES", "ON BIND VIEW HOLDER")

        val item = list[position]
        if (holder is DocViewHolder && item is Document) {
            holder.itemView.tvDocNumAndBpName.text = "${item.DocNum} - ${item.CardName}"
            holder.itemView.tvDocDate.text =
                "${Utils.convertUSAdatetoNormal(item.DocDate!!)}"

            if (item.DocumentStatus == GeneralConsts.DOC_STATUS_CLOSED) {
                holder.itemView.tvDocStatus.text = GeneralConsts.DOC_STATUS_CLOSED_NAME
                holder.itemView.tvDocStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            } else if (item.DocumentStatus == GeneralConsts.DOC_STATUS_OPEN) {
                holder.itemView.tvDocStatus.text = GeneralConsts.DOC_STATUS_OPEN_NAME
                holder.itemView.tvDocStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.secondaryColor
                    )
                )
            }

        } else if (holder is LoaderViewHolder && item is LoadMore) {
            listener?.loadMore(position)
        }
    }


    inner class DocViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        init {
            Log.d("INVOICES", "ON VIEW HOLDER")
            itemView.setOnClickListener {
                val item = list[adapterPosition] as Document
                listener?.onClick(item)
            }

            itemview.tvDocTotal.visibility = View.GONE
        }
    }

    inner class LoaderViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }


    interface OnAdapterItemClickListener {
        fun onClick(doc: Document)
        fun loadMore(lastItemIndex: Int)
    }
}