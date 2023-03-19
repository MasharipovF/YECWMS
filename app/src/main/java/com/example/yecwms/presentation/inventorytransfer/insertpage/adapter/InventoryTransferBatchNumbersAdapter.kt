package com.example.yecwms.presentation.inventorytransfer.insertpage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.google.android.material.card.MaterialCardView


class InventoryTransferBatchNumbersAdapter(
    val listener: GoodsDeliveryBatchNumbersClickListener,
    val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<InventoryTransferBatchNumbersAdapter.InventoryTransferBatchNumbersVH>() {

    var list: ArrayList<BatchNumbersVal.BatchNumbers> = arrayListOf<BatchNumbersVal.BatchNumbers>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun sortList() {
        val newList =
            list.sortedWith(compareByDescending<BatchNumbersVal.BatchNumbers> { it.isChecked }
                .thenByDescending { it.absEntry })
        list = ArrayList(newList)
    }

    fun calculateTotalQuantity(): Double {
        var quantity: Double = 0.0
        for (item in list) {
            if (item.isChecked)
                quantity += item.selectedQuantity!!
        }
        return quantity
    }

    fun autoSelect(totalQuantity: Double) {
        //DESELECT ALL
        list.forEach {
            it.isChecked = false
            it.selectedQuantity = 0.0
        }
        sortList()

        var newList = arrayListOf<BatchNumbersVal.BatchNumbers>()
        var selectedQuantity = 0.0
        for (item in list) {
            if (selectedQuantity < totalQuantity) {
                item.isChecked = true
                item.selectedQuantity =
                    if (totalQuantity - selectedQuantity <= item.quantity!!) totalQuantity - selectedQuantity else item.quantity
                selectedQuantity += item.selectedQuantity!!
            }
            newList.add(item)
        }
        list = newList

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryTransferBatchNumbersVH {
        return InventoryTransferBatchNumbersVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_list_batch_item_select, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InventoryTransferBatchNumbersVH, position: Int) {
        val item = list[position]
        with(holder) {
            batchNumber.setText(item.batchNumber)

            quantity.setText(item.quantity.toString())
            quantityToChoose.setText(item.selectedQuantity.toString())

            if (item.selectedQuantity!! > item.quantity!!) {
                quantity.setTextColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.red,
                        null
                    )
                )
            } else {
                quantity.setTextColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.black,
                        null
                    )
                )
            }

            if (item.isChecked) {
                cardView.setCardBackgroundColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.green_opacity_20,
                        null
                    )
                )
                btnCancel.visibility = View.VISIBLE

            } else {
                cardView.setCardBackgroundColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.white,
                        null
                    )
                )
                btnCancel.visibility = View.GONE
            }

        }
    }

    override fun getItemCount(): Int = list.size

    inner class InventoryTransferBatchNumbersVH(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var batchNumber: EditText

        var quantity: EditText
        var quantityToChoose: EditText
        var btnCancel: Button
        val cardView: MaterialCardView

        init {
            batchNumber = itemView.findViewById<EditText>(R.id.etvBatchNumber)
            quantity = itemView.findViewById(R.id.etvQuantity)
            quantityToChoose = itemView.findViewById(R.id.etvQuantityToChoose)
            btnCancel = itemView.findViewById(R.id.btnCancel)
            cardView = itemView.findViewById(R.id.cardView)

            itemView.setOnClickListener {
                listener.onClick(
                    list[absoluteAdapterPosition] as BatchNumbersVal.BatchNumbers,
                    absoluteAdapterPosition
                )
            }

            quantityToChoose.addTextChangedListener {
                list[absoluteAdapterPosition].selectedQuantity =
                    if (quantityToChoose.text.isNullOrEmpty()) 0.0 else
                        quantityToChoose.text.toString().toDouble()

                listener.onQuantityChange(calculateTotalQuantity())
            }

            cardView.setOnClickListener {
                list[absoluteAdapterPosition].isChecked = true
                sortList()
            }

            btnCancel.setOnClickListener {
                list[absoluteAdapterPosition].isChecked = false
                list[absoluteAdapterPosition].selectedQuantity = 0.0
                sortList()
            }
        }
    }


    inner class LoadMoreVH(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    interface GoodsDeliveryBatchNumbersClickListener {
        fun onClick(item: BatchNumbersVal.BatchNumbers, position: Int)
        fun onQuantityChange(selectedQuantity: Double)
    }


}