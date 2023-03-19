package com.example.yecwms.presentation.items.itemslist

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.masterdatas.Warehouses
import com.example.yecwms.domain.interactor.ItemsInteractor
import com.example.yecwms.domain.interactor.ItemsInteractorImpl
import com.example.yecwms.domain.interactor.MasterDataInteractor
import com.example.yecwms.domain.interactor.MasterDataInteractorImpl
import com.example.yecwms.util.LoadMore
import com.example.yecwms.util.Utils
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ItemsListViewModel : BaseViewModel() {

    private val interactor: ItemsInteractor by lazy { ItemsInteractorImpl() }
    private val masterDataInteractor: MasterDataInteractor by lazy {MasterDataInteractorImpl() }
    var listToDraw: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    val itemsListForImages: MutableLiveData<List<Items>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoading: MutableLiveData<String> = MutableLiveData()
    var filterString: MutableLiveData<String> = MutableLiveData()
    var itemSearchJob: Job? = null

    var currentWarehouse: MutableLiveData<Warehouses> = MutableLiveData()
    var warehousesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorWarehousesLoading: MutableLiveData<String> = MutableLiveData()
    var warehousesList: MutableLiveData<ArrayList<Warehouses>> = MutableLiveData()

    private val allWhs = Warehouses(WarehouseCode = "##", WarehouseName = "Все склады")

    var printerLoading: MutableLiveData<Boolean> = MutableLiveData()
    var printerError: MutableLiveData<String> = MutableLiveData()
    var printerSuccess: MutableLiveData<Boolean> = MutableLiveData()


    init {
        filterString.value = ""
        currentWarehouse.value = allWhs
        getWarehouseList()
        getItemsList()
    }

    fun setFilter(filter: String) {
        filterString.value = filter
    }

    fun getWarehouseList() {
        vmScope.launch {
            warehousesLoading.postValue(true)
            val warehouses = masterDataInteractor.getWarehouses()
            if (warehouses != null) {
                val newWarehousesList: ArrayList<Warehouses> =
                    warehouses.map { it.copy() } as ArrayList<Warehouses>
                newWarehousesList.add(0, allWhs)
                warehousesList.postValue(newWarehousesList as ArrayList<Warehouses>?)
            } else {
                errorWarehousesLoading.postValue(masterDataInteractor.errorMessage)
            }
            warehousesLoading.postValue(false)
        }
    }


    /*fun getItems() {
        if (itemSearchJob?.isActive == true) {
            itemSearchJob?.cancel()
        }

        listToDraw.value = arrayListOf()
        itemSearchJob = vmScope.launch {
            loading.postValue(true)
            val listResult = ArrayList<Any>()
            val items = interactor.getAllItems(
                filter = filterString.value!!,
                whsCode = Preferences.defaultWhs,
                priceListCode = GeneralConsts.DEF_PRICELIST_CODE
            )
            if (items != null) {

                listResult.addAll(items)
                itemsListForImages.postValue(makeItemsListForImages(items))

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
            } else {
                errorLoading.postValue(true)
            }
            loading.postValue(false)
            listToDraw.postValue(listResult)
        }
    }

    fun getMoreItems(skipValue: Int) {
        vmScope.launch {
            val listResult =
                ArrayList<Any>(listToDraw.value?.filter { it !is LoadMore } ?: listOf())

            val items =
                interactor.getMoreItems(
                    filter = filterString.value!!,
                    whsCode = Preferences.defaultWhs,
                    skipValue = skipValue,
                    priceListCode = GeneralConsts.DEF_PRICELIST_CODE
                )

            if (items != null) {
                listResult.addAll(items)
                itemsListForImages.postValue(makeItemsListForImages(items))

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorLoading.postValue(true)
            }
            listToDraw.postValue(listResult)
        }
    }*/

    fun getItemsList() {
        if (filterString.value == null) return

        if (itemSearchJob?.isActive == true) {
            itemSearchJob?.cancel()
        }

        listToDraw.value = arrayListOf()
        itemSearchJob = vmScope.launch {
            Log.wtf("FILTERSTRINGMORE", filterString.value.toString())

            loading.postValue(true)
            val listResult = ArrayList<Any>()
            val items = interactor.getItemsViaSML(
                cardCode = GeneralConsts.DEF_CARD_CODE,
                whsCode = if (currentWarehouse.value == allWhs) "" else currentWarehouse.value?.WarehouseCode.toString(),
                date = Utils.getCurrentDateinUSAFormat(),
                priceListCode = GeneralConsts.DEF_PRICELIST_CODE,
                filter = filterString.value!!
            )

            if (items != null) {
                listResult.addAll(items)
                itemsListForImages.postValue(makeItemsListForImages(items))
                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
            } else {
                errorLoading.postValue("Ошибка при загрузке товаров: ${interactor.errorMessage}")
            }
            loading.postValue(false)
            listToDraw.postValue(listResult)
        }

    }

    fun getMoreItemsList(skipValue: Int) {
        vmScope.launch {
            Log.wtf("FILTERSTRINGMORE", filterString.value.toString())
            val listResult =
                ArrayList<Any>(listToDraw.value?.filter { it !is LoadMore } ?: listOf())
            val items =
                interactor.getItemsViaSML(
                    cardCode = GeneralConsts.DEF_CARD_CODE,
                    whsCode = if (currentWarehouse.value == allWhs) "" else currentWarehouse.value?.WarehouseCode.toString(),
                    date = Utils.getCurrentDateinUSAFormat(),
                    priceListCode = GeneralConsts.DEF_PRICELIST_CODE,
                    filter = filterString.value!!,
                    skipValue = skipValue
                )

            if (items != null) {
                listResult.addAll(items)
                itemsListForImages.postValue(makeItemsListForImages(items))

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorLoading.postValue("Ошибка при загрузке товаров: ${interactor.errorMessage}")
            }
            listToDraw.postValue(listResult)
        }
    }

    private fun makeItemsListForImages(items: List<Items>): List<Items> {
        return items.distinctBy { it.ItemCode }.toList() ?: listOf()
    }

    fun getItemImage(itemcode: String) {
        Log.wtf("IMAGE", "ITEMIMAGE IS GETTING")
        vmScope.launch {
            val image = interactor.getItemImage(itemcode)
            Log.wtf("ITEMINFO", "Passed string FOR GETTING IMAGE $itemcode \n ${image.toString()}")
            if (image != null) {
                setItemImage(image, itemcode)
                Log.wtf("ITEM IMAGE IN VIEWMODEL", image.toString())
            }
        }

    }

    private suspend fun setItemImage(image: Bitmap, itemCode: String) {
        Log.wtf("IMAGE", "ITEMIMAGE IS SET")
        listToDraw.value?.forEach {
            if (it is Items) {
                if (it.ItemCode == itemCode) {
                   // it.ItemImage = image
                }
            }
        }

        listToDraw.postValue(listToDraw.value)
    }


}