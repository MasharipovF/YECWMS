package com.example.yecwms.presentation.invoice.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.example.yecwms.util.Utils
import com.google.android.material.card.MaterialCardView


class InvoiceSelectedBatchNumbersAdapter(
    val listener: InvoiceSelectedBatchesClickListener,
    val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        Log.d("SELECTBATCHES", "adapter list ${list.toString()}")

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

    override fun getItemViewType(position: Int): Int {
        return list[position].Type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> InvoiceSelectedBatchesYarnVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.dialog_list_batch_item_select_yarn, parent, false)
            )
            else -> InvoiceSelectedBatchesVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.dialog_list_batch_item_select, parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when(holder){
            is InvoiceSelectedBatchesVH ->{
                with(holder) {
                    batchNumber.setText(item.batchNumber)
                    quantity.setText(Utils.getNumberWithThousandSeparator(item.quantity ?: 0.0))
                    quantityToChoose.setText(Utils.getNumberWithThousandSeparator(item.selectedQuantity.toDouble()))


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
                                R.color.green_opacity_50,
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

            is InvoiceSelectedBatchesYarnVH ->{
                with(holder) {
                    batchNumber.setText(item.batchNumber)
                    quantity.setText(Utils.getIntOrDoubleNumberString(item.quantity ?: 0.0))
                    quantityToChoose.setText(Utils.getIntOrDoubleNumberString(item.selectedQuantity.toDouble()))
                    brutto.setText(Utils.getIntOrDoubleNumberString(item.Brutto.toDouble()))
                    tare.setText(Utils.getIntOrDoubleNumberString(item.Tare.toDouble()))
                    bobbin.setText(Utils.getIntOrDoubleNumberString(item.Bobbin.toDouble()))


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
                                R.color.green_opacity_50,
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
        }
    }

    override fun getItemCount(): Int = list.size

    inner class InvoiceSelectedBatchesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                val quantity: Double =
                    if (quantityToChoose.text.isNullOrEmpty()) 0.0 else quantityToChoose.text.toString().toDouble()

                if (quantity > 0) list[absoluteAdapterPosition].isChecked = true
                list[absoluteAdapterPosition].selectedQuantity = quantity
                listener.onQuantityChange(calculateTotalQuantity())
            }

            quantityToChoose.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (list[absoluteAdapterPosition].selectedQuantity > 0) list[absoluteAdapterPosition].isChecked =
                        true
                    sortList()
                    return@setOnEditorActionListener true;
                }
                // Return true if you have consumed the action, else false.
                return@setOnEditorActionListener false;
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

    inner class InvoiceSelectedBatchesYarnVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var batchNumber: EditText
        var quantity: EditText
        var quantityToChoose: EditText
        var brutto: EditText
        var tare: EditText
        var bobbin: EditText
        var btnCancel: Button
        val cardView: MaterialCardView

        init {
            batchNumber = itemView.findViewById<EditText>(R.id.etvBatchNumber)
            quantity = itemView.findViewById(R.id.etvNetto)
            quantityToChoose = itemView.findViewById(R.id.etvNettoToChoose)
            brutto = itemView.findViewById(R.id.etvBrutto)
            tare = itemView.findViewById(R.id.etvTare)
            bobbin = itemView.findViewById(R.id.etvBobbin)
            btnCancel = itemView.findViewById(R.id.btnCancel)
            cardView = itemView.findViewById(R.id.cardView)

            itemView.setOnClickListener {
                listener.onClick(
                    list[absoluteAdapterPosition] as BatchNumbersVal.BatchNumbers,
                    absoluteAdapterPosition
                )
            }

            quantityToChoose.addTextChangedListener {
                val quantity =
                    if (quantityToChoose.text.isNullOrEmpty()) 0.0 else quantityToChoose.text.toString().toDouble()

                if (quantity > 0) list[absoluteAdapterPosition].isChecked = true
                list[absoluteAdapterPosition].selectedQuantity = quantity
                listener.onQuantityChange(calculateTotalQuantity())
            }

            quantityToChoose.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (list[absoluteAdapterPosition].selectedQuantity > 0) list[absoluteAdapterPosition].isChecked =
                        true
                    sortList()
                    return@setOnEditorActionListener true;
                }
                // Return true if you have consumed the action, else false.
                return@setOnEditorActionListener false;
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

    interface InvoiceSelectedBatchesClickListener {
        fun onClick(item: BatchNumbersVal.BatchNumbers, position: Int)
        fun onQuantityChange(selectedQuantity: Double)
    }


}