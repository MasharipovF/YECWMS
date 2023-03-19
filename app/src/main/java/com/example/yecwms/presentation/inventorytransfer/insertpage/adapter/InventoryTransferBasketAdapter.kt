package com.example.yecwms.presentation.inventorytransfer.insertpage.adapter

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yecwms.R
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.inventory.InventoryOperationsLines
import com.google.android.material.card.MaterialCardView
import java.math.BigDecimal

class InventoryTransferBasketAdapter(
    val listener: InvBasketItemClickListener,
    val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<InventoryTransferBasketAdapter.InventoryRequestBasketVH>() {

    var list: MutableList<InventoryOperationsLines> = arrayListOf<InventoryOperationsLines>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isViewModeEnabled: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryRequestBasketVH {
        return InventoryRequestBasketVH(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_basket, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InventoryRequestBasketVH, position: Int) {
        with(holder) {

            val item = list[position]

            val itemNameText =
                "<font color=#47824a>${position + 1})</font> <font color=#000000>${list[position].itemDescription} </font> <font color=#47824a></font>"
            name.text = Html.fromHtml(itemNameText, HtmlCompat.FROM_HTML_MODE_LEGACY)

            val userQuantity = if (item.UserQuantity == null) 0.0 else item.UserQuantity.toDouble()
            val initialQuantity = item.InitialQuantity.toDouble()


            quantityFact.text = userQuantity.toString()

            layoutItemPrice.visibility = View.GONE
            layoutLinetotal.visibility = View.GONE

            if (item.managedBy != GeneralConsts.MANAGED_BY_BATCH || isViewModeEnabled) tvChoose.visibility =
                View.GONE else View.VISIBLE

            Log.d("COLOR", initialQuantity.toString())
            val cardColor: Int = if (initialQuantity == 0.0) {
                R.color.white
            } else {
                when {
                    userQuantity == 0.0 -> R.color.white
                    userQuantity < initialQuantity -> R.color.yellow_opacity_20
                    userQuantity == initialQuantity -> R.color.green_opacity_20
                    userQuantity > initialQuantity -> R.color.red_opacity_50
                    else -> R.color.white
                }
            }


            cardView.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    itemView.resources,
                    cardColor,
                    null
                )
            )
        }
    }

    override fun getItemCount(): Int = list.size

    inner class InventoryRequestBasketVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var quantityFact: TextView
        var price: TextView
        var linetotal: TextView
        var btnPlus: ImageButton
        var btnMinus: ImageButton
        var layoutItemPrice: LinearLayout
        var layoutLinetotal: LinearLayout
        var tvChoose: TextView
        var cardView: MaterialCardView

        init {
            name = itemView.findViewById(R.id.tvItemName)
            quantityFact = itemView.findViewById(R.id.etvQuantity)
            price = itemView.findViewById(R.id.etvPrice)
            linetotal = itemView.findViewById(R.id.tvLineTotal)
            btnPlus = itemView.findViewById(R.id.btnPlus)
            btnMinus = itemView.findViewById(R.id.btnMinus)
            layoutItemPrice = itemView.findViewById(R.id.layoutItemPrice)
            layoutLinetotal = itemView.findViewById(R.id.layoutLineTotal)
            tvChoose = itemView.findViewById(R.id.tvChooseBatches)
            cardView = itemView.findViewById(R.id.cardView)

            tvChoose.setOnClickListener {
                listener.onBatchItemClick(absoluteAdapterPosition, list[absoluteAdapterPosition])
            }
            quantityFact.setOnClickListener {
                listener.onQuantityViewClicked(
                    absoluteAdapterPosition,
                    list[absoluteAdapterPosition]
                )
            }



        }
    }

    interface InvBasketItemClickListener {
        fun changeQuantity(position: Int, quantity: BigDecimal): Boolean
        fun onBatchItemClick(position: Int, item: InventoryOperationsLines)
        fun onQuantityViewClicked(
            position: Int,
            item: InventoryOperationsLines,
        )
    }
}