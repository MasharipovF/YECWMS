package com.example.yecwms.data.entity.items


import com.google.gson.annotations.SerializedName

data class ItemsCrossJoinVal(
    @SerializedName("odata.nextLink")
    var odataNextLink: String = "",
    @SerializedName("value")
    var value: List<ItemsCrossJoin> = listOf()
)

data class ItemsCrossJoin(
    @SerializedName("Items")
    var items: Items = Items(),
    @SerializedName("Items/ItemWarehouseInfoCollection")
    var itemsItemWarehouseInfoCollection: ItemWarehouseInfo = ItemWarehouseInfo()
)