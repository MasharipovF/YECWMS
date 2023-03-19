package com.example.yecwms.presentation.purchasedeliveries.insert.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.util.CalculatorBottomSheet
import com.example.yecwms.util.Utils

class PurchaseDeliveryBatchNumbersAdapter(
    val listener: PurchaseDeliveryBatchNumbersClickListener,
    val fragmentManager: FragmentManager,
    val itemCode: String
) :
    RecyclerView.Adapter<PurchaseDeliveryBatchNumbersAdapter.PurchaseDeliveryBatchNumbersVH>() {

    var list: ArrayList<BatchNumbersForPost> = arrayListOf<BatchNumbersForPost>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addNewBatchNumber(maxQuantity: Double) {
        var quantity = 0.0
        for (item in list) {
            quantity += item.Quantity!!
        }

        if (quantity < maxQuantity) {
            list.add(BatchNumbersForPost(BatchNumber = null, Quantity = maxQuantity.toDouble() - quantity.toDouble()))
            notifyDataSetChanged()
        }
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseDeliveryBatchNumbersVH {
        return PurchaseDeliveryBatchNumbersVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_list_batch_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PurchaseDeliveryBatchNumbersVH, position: Int) {
        val item = list[position]
        Log.d("BATCHBPNAMES", "$item")
        with(holder) {
            batchNumber.setText(if (item.BatchNumber == null) "" else item.BatchNumber)
            quantity.text = item.Quantity.toString()
        }
    }

    override fun getItemCount(): Int = list.size

    inner class PurchaseDeliveryBatchNumbersVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var batchNumber: EditText
        var quantity: TextView
        var btnBatchGen: ImageButton

        init {
            batchNumber = itemView.findViewById<EditText>(R.id.etvBatchNumber)
            quantity = itemView.findViewById<TextView>(R.id.tvQuantity)
            btnBatchGen = itemView.findViewById<ImageButton>(R.id.generateBatchNumber)

            itemView.setOnClickListener {
                listener.onClick(list[absoluteAdapterPosition] as BatchNumbersForPost)
            }

            btnBatchGen.setOnClickListener {
                val item = list[absoluteAdapterPosition]

                list[absoluteAdapterPosition].BatchNumber = Utils.generateBatchNumber(itemCode)
                notifyItemChanged(absoluteAdapterPosition)
            }

            batchNumber.addTextChangedListener {
                list[absoluteAdapterPosition].BatchNumber = batchNumber.text.toString()
            }

            quantity.setOnClickListener {
                val calculator =
                    CalculatorBottomSheet(object : CalculatorBottomSheet.CalculatorListener {
                        override fun onEdit(number: String) {
                            list[absoluteAdapterPosition].Quantity =
                                if (number.isEmpty()) 0.0 else number.toDouble()
                            notifyDataSetChanged()
                        }

                        override fun onSubmit(number: Double?) {
                            if (number != null) {
                                val currentQuantity = number.toDouble()
                                list[absoluteAdapterPosition].Quantity = currentQuantity.toDouble()
                                notifyDataSetChanged()
                            }

                        }
                    })
                calculator.show(fragmentManager, "bottomsheet_calculator")
            }

        }
    }


    inner class LoadMoreVH(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    interface PurchaseDeliveryBatchNumbersClickListener {
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