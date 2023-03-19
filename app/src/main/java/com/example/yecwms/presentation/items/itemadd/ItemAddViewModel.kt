package com.example.yecwms.presentation.items.itemadd

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.core.ValidationStatusEnum
import com.example.yecwms.data.entity.masterdatas.ItemsGroup
import com.example.yecwms.data.entity.masterdatas.UnitOfMeasurement
import com.example.yecwms.data.entity.masterdatas.UnitOfMeasurementGroups
import com.example.yecwms.data.entity.items.ItemsForPost
import com.example.yecwms.domain.interactor.ItemsInteractor
import com.example.yecwms.domain.interactor.ItemsInteractorImpl
import com.example.yecwms.domain.interactor.MasterDataInteractor
import com.example.yecwms.domain.interactor.MasterDataInteractorImpl
import kotlinx.coroutines.launch
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.util.Utils

class ItemAddViewModel : BaseViewModel() {

    private val interactor: ItemsInteractor by lazy { ItemsInteractorImpl() }
    private val masterDataInteractor: MasterDataInteractor by lazy { MasterDataInteractorImpl() }
    var newItem: ItemsForPost = ItemsForPost(Series = GeneralConsts.SERIES_ITEM_ADD)
    var listItemGroup: MutableLiveData<ArrayList<ItemsGroup>> = MutableLiveData()
    var listUomGroup: MutableLiveData<ArrayList<UnitOfMeasurementGroups>> = MutableLiveData()
    var listUom: MutableLiveData<ArrayList<UnitOfMeasurement>> = MutableLiveData()

    var barcode: MutableLiveData<String> = MutableLiveData()

    var loadingPage: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoadingPage: MutableLiveData<Boolean> = MutableLiveData()

    var errorAddNewItem: MutableLiveData<Boolean> = MutableLiveData()

    var loadingBarcodeCheck: MutableLiveData<Boolean> = MutableLiveData()
    var barcodeCheckLoadingStatus: MutableLiveData<ValidationStatusEnum> = MutableLiveData()

    var loadingBarcodeGet: MutableLiveData<Boolean> = MutableLiveData()
    var barcodeGetLoadingStatus: MutableLiveData<ValidationStatusEnum> = MutableLiveData()


    init {
        loadPage()
    }

    private fun addNewItem() {
        vmScope.launch {
            loadingPage.postValue(true)
            val item = interactor.addNewItem(newItem)

            if (item != null) {
                insertItem.postValue(item.ItemCode)
            } else errorAddNewItem.postValue(true)
            loadingPage.postValue(false)
        }
    }

    fun setNewItemData(
        itemName: String,
        itemGroupPosition: Int,
        barCode: String,
        uomGroupPosition: Int,
        invUomPosition: Int,
        salesUomPosition: Int,
        purchaseUomPosition: Int
    ) {

        newItem.ItemName = itemName
        newItem.ItemsGroupCode = listItemGroup.value?.get(itemGroupPosition)?.GroupCode
        newItem.BarCode = barCode
        newItem.UoMGroupEntry = listUomGroup.value?.get(uomGroupPosition)?.GroupCode
        newItem.InventoryUoMEntry = listUom.value?.get(invUomPosition)?.UomCode
        newItem.SalesUoMEntry = listUom.value?.get(salesUomPosition)?.UomCode
        newItem.PurchasingUoMEntry = listUom.value?.get(purchaseUomPosition)?.UomCode

        Log.d("NEWITEM", newItem.toString())

        addNewItem()

    }

    fun checkIfBarcodeExists(barcode: String) {
        vmScope.launch {
            loadingBarcodeCheck.postValue(true)
            val barCodeExists = interactor.checkIfBarCodeExists(barcode)
            if (barCodeExists != null) {
                if (!barCodeExists)
                    barcodeCheckLoadingStatus.postValue(ValidationStatusEnum.VALIDATION_SUCCESS)
                else {
                    barcodeCheckLoadingStatus.postValue(ValidationStatusEnum.VALIDATION_FAIL)
                }
            } else {
                barcodeCheckLoadingStatus.postValue(ValidationStatusEnum.ERROR_FROM_API)
            }
            loadingBarcodeCheck.postValue(false)
        }
    }


    fun getItemGroups() {
        vmScope.launch {
            loadingPage.postValue(true)
            val item = masterDataInteractor.getItemsGroups()
            if (item != null) {
                listItemGroup.postValue(item as ArrayList<ItemsGroup>)
            } else errorLoadingPage.postValue(true)
            loadingPage.postValue(false)


        }

    }

    fun getUomGroups() {
        vmScope.launch {
            loadingPage.postValue(true)
            val item = masterDataInteractor.getUomGroups()
            if (item != null) {
                listUomGroup.postValue(item as ArrayList<UnitOfMeasurementGroups>)
            } else errorLoadingPage.postValue(true)
            loadingPage.postValue(false)


        }

    }

    fun getUoms(position: Int) {
        Log.d("uomsss", position.toString())
        vmScope.launch {
            val uomGroup = listUomGroup.value?.get(position)
            loadingPage.postValue(true)
            val item = masterDataInteractor.getUomsOfUomGroup(uomGroup)
            if (item != null) {
                listUom.postValue(item as ArrayList<UnitOfMeasurement>)
            } else errorLoadingPage.postValue(true)
            loadingPage.postValue(false)


        }
    }


    fun loadPage() {
        vmScope.launch {
            loadingPage.postValue(true)
            Log.d("LOADPAGE", "loading")
            val itemgroups = masterDataInteractor.getItemsGroups()
            val uomgroups = masterDataInteractor.getUomGroups()

            if (itemgroups != null && uomgroups != null) {
                listItemGroup.postValue(itemgroups as ArrayList<ItemsGroup>)
                listUomGroup.postValue(uomgroups as ArrayList<UnitOfMeasurementGroups>)
                errorLoadingPage.postValue(false)
            } else {
                errorLoadingPage.postValue(true)
            }
            loadingPage.postValue(false)
        }
    }

    fun generateBarCode() {
        vmScope.launch {
            loadingBarcodeGet.postValue(true)
            val lastBarcode = masterDataInteractor.getLastBarCode()
            if (lastBarcode != null) {

                val newBarCode = Utils.generateEan13Barcode(lastBarcode)

                barcode.postValue(newBarCode)

            } else {
                barcodeGetLoadingStatus.postValue(ValidationStatusEnum.ERROR_FROM_API)
            }
            loadingBarcodeGet.postValue(false)
        }
    }


}