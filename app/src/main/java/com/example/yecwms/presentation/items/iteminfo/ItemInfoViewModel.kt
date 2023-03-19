package com.example.yecwms.presentation.items.iteminfo

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.entity.items.ItemWarehouseInfo
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.domain.interactor.ItemsInteractor
import com.example.yecwms.domain.interactor.ItemsInteractorImpl
import com.example.yecwms.util.LoadMore
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ItemInfoViewModel : BaseViewModel() {

    private val interactor: ItemsInteractor by lazy { ItemsInteractorImpl() }
    var itemInfo: MutableLiveData<Items> = MutableLiveData()
    var listToDraw: MutableLiveData<ArrayList<ItemWarehouseInfo>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoading: MutableLiveData<String> = MutableLiveData()
    var image: MutableLiveData<Bitmap> = MutableLiveData()

    var batchesFilterString: MutableLiveData<String> = MutableLiveData()
    var batchesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorBatchesLoading: MutableLiveData<String> = MutableLiveData()
    var batchesSearchJob: Job? = null
    var batchesListToDraw: MutableLiveData<ArrayList<Any>> = MutableLiveData()

    var printerLoading: MutableLiveData<Boolean> = MutableLiveData()
    var printerError: MutableLiveData<String> = MutableLiveData()
    var printerSuccess: MutableLiveData<Boolean> = MutableLiveData()


    fun getItemInfo(itemcode: String) {
        vmScope.launch {
            loading.postValue(true)

            getItemImage(itemcode)

            val item = interactor.getItemInfo(itemcode)
            val stockByWhs = item?.ItemWarehouseInfoCollection
            Log.d("ITEMINFO", "Passed string $itemcode \n ${item.toString()}")
            if (item != null && stockByWhs != null) {
                itemInfo.postValue(item)
                listToDraw.postValue(stockByWhs as ArrayList<ItemWarehouseInfo>)
            } else errorLoading.postValue(interactor.errorMessage)
            loading.postValue(false)
        }
    }


    fun getItemImage(itemcode: String) {
        vmScope.launch {
            val item = interactor.getItemImage(itemcode)
            Log.wtf("ITEMINFO", "Passed string FOR GETTING IMAGE $itemcode \n ${item.toString()}")
            if (item != null) {
                image.postValue(item)
                Log.wtf("ITEM IMAGE IN VIEWMODEL", item.toString())
            } else errorLoading.postValue(interactor.errorMessage)
        }
    }

    fun getItemBatches(selectedWhs: String) {
        if (batchesFilterString.value == null) return

        if (batchesSearchJob?.isActive == true) {
            batchesSearchJob?.cancel()
        }

        batchesListToDraw.value = arrayListOf()
        batchesSearchJob = vmScope.launch {
            batchesLoading.postValue(true)
            val listResult = ArrayList<Any>()
            val items = interactor.getItemBatchesViaSML(
                filter = batchesFilterString.value!!,
                whsCode = selectedWhs,
                itemCode = itemInfo.value?.ItemCode.toString()
            )


            if (items != null) {
                listResult.addAll(items)
                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
            } else {
                errorBatchesLoading.postValue("Ошибка при загрузке товаров: ${interactor.errorMessage}")
            }
            batchesLoading.postValue(false)
            batchesListToDraw.postValue(listResult)
        }

    }

    fun getMoreItemBatches(selectedWhs: String, skipValue: Int) {
        vmScope.launch {
            val listResult =
                ArrayList<Any>(batchesListToDraw.value?.filter { it !is LoadMore } ?: listOf())
            val items =
                interactor.getItemBatchesViaSML(
                    filter = batchesFilterString.value!!,
                    whsCode = selectedWhs,
                    itemCode = itemInfo.value?.ItemCode.toString(),
                    skipValue = skipValue
                )

            if (items != null) {
                listResult.addAll(items)

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorBatchesLoading.postValue("Ошибка при загрузке товаров: ${interactor.errorMessage}")
            }
            batchesListToDraw.postValue(listResult)
        }
    }

}