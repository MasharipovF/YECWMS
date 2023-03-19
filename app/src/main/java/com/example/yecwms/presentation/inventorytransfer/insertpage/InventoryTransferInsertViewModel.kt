package com.example.yecwms.presentation.inventorytransfer.insertpage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.batches.BatchNumbersVal
import com.example.yecwms.data.entity.inventory.InventoryOperations
import com.example.yecwms.data.entity.inventory.InventoryOperationsForPost
import com.example.yecwms.data.entity.inventory.InventoryOperationsLines
import com.example.yecwms.data.entity.inventory.InventoryOperationsLinesForPost
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.masterdatas.Warehouses
import com.example.yecwms.domain.*
import com.example.yecwms.domain.interactor.*
import com.example.yecwms.domain.mappers.Mappers
import com.example.yecwms.util.LoadMore
import com.example.yecwms.util.Utils
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal

class InventoryTransferInsertViewModel : BaseViewModel() {

    private val itemsInteractor: ItemsInteractor by lazy { ItemsInteractorImpl() }
    private val inventoryTransferInteractor: InventoryTransferInteractor by lazy { InventoryTransferInteractorImpl() }
    private val inventoryRequestInteractor: InventoryTransferRequestInteractor by lazy { InventoryTransferRequestInteractorImpl() }
    private val masterDataInteractor: MasterDataInteractor by lazy { MasterDataInteractorImpl() }
    private val batchNumbersInteractor: BatchNumbersInteractor by lazy { BatchNumbersInteractorImpl() }

    var errorLoadingInsert: MutableLiveData<Boolean> = MutableLiveData()
    var loadingInsert: MutableLiveData<Boolean> = MutableLiveData()

    var isViewMode: MutableLiveData<Boolean> = MutableLiveData()
    var invTransferDocEntry: Long = -1

    var document: InventoryOperations? = null
    var loadingDocument: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoadingDocument: MutableLiveData<String> = MutableLiveData()
    var isCopiedFromInventoryRequest: MutableLiveData<Boolean> = MutableLiveData()
    var baseDocumentsNumber: MutableLiveData<String> = MutableLiveData()

    var itemsList: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    var itemsFilterString: MutableLiveData<String> = MutableLiveData()
    var itemsLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorItemsLoading: MutableLiveData<Boolean> = MutableLiveData()
    var currentChosenItem: MutableLiveData<Items> = MutableLiveData()
    var currentQuantityInPackage: MutableLiveData<BigDecimal> = MutableLiveData()

    var barcodeScannedString: MutableLiveData<String> = MutableLiveData()


    var warehousesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorWarehousesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var warehousesList: MutableLiveData<ArrayList<Warehouses>> = MutableLiveData()

    val docDueDate: MutableLiveData<String> = MutableLiveData()
    val docDate: MutableLiveData<String> = MutableLiveData()
    val fromWarehouse: MutableLiveData<String> = MutableLiveData()
    val toWarehouse: MutableLiveData<String> = MutableLiveData()

    var basketList: MutableLiveData<ArrayList<InventoryOperationsLines>> = MutableLiveData()

    var batchNumbersList: MutableLiveData<ArrayList<BatchNumbersVal.BatchNumbers>> =
        MutableLiveData()
    var itemBatchNumbers: MutableLiveData<ArrayList<BatchNumbersVal.BatchNumbers>> =
        MutableLiveData()
    var batchesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorBatchesLoading: MutableLiveData<Boolean> = MutableLiveData()

    var itemSearchJob: Job? = null

    var documentsToLoad: ArrayList<Long> = arrayListOf()
    var documentsToLoadCount: MutableLiveData<Int> = MutableLiveData()
    var loadedDocumentsCount: MutableLiveData<Int> = MutableLiveData()
    var proceededDocumentsCount: MutableLiveData<Int> = MutableLiveData()


    init {
        basketList.value = ArrayList<InventoryOperationsLines>()
        docDate.value = Utils.getCurrentDateinUSAFormat()
        docDueDate.value = Utils.getCurrentDateinUSAFormat()

        isViewMode.value = false
        documentsToLoadCount.value = 0
        loadedDocumentsCount.value = 0
        proceededDocumentsCount.value = 0
        baseDocumentsNumber.value = ""
    }


    fun findItemPositionByBarCode(barcode: String): Int? {
        Log.d("FIND", basketList.value.toString())
        var position: Int? = null
        basketList.value?.forEachIndexed { index, inventoryOperationsLines ->
            if (barcode == inventoryOperationsLines.Barcode){
                position = index
            }
        }
        return position
    }


    fun getBatchNumbersList(ItemCode: String, WhsCode: String) {
        vmScope.launch {
            batchesLoading.postValue(true)
            val batches = batchNumbersInteractor.getBatchNumbers(ItemCode, WhsCode)
            Log.d("SELECTBATCHES", "$ItemCode   $WhsCode   ${batches.toString()}")
            if (batches != null) {
                itemBatchNumbers.postValue(batches as ArrayList<BatchNumbersVal.BatchNumbers>?)
            } else {
                errorBatchesLoading.postValue(true)
                Log.d(
                    "SELECTBATCHESERROR",
                    "$ItemCode   $WhsCode   ${batchNumbersInteractor.errorMessage}"
                )

            }
            batchesLoading.postValue(false)
        }
    }

    fun getWarehouseList() {
        vmScope.launch {
            warehousesLoading.postValue(true)
            val warehouses = masterDataInteractor.getWarehouses()
            if (warehouses != null) {
                warehousesList.postValue(warehouses as ArrayList<Warehouses>?)
            } else {
                errorWarehousesLoading.postValue(true)
            }
            warehousesLoading.postValue(false)
        }
    }

    var loadInventoryRequestJob: Job? = null
    fun loadManyInventoryRequest(docEntries: List<Long>) {
        loadInventoryRequestJob = vmScope.launch {
            loadingDocument.postValue(true)

            docEntries.forEach { docEntry ->

                document = inventoryRequestInteractor.getInventoryTransferRequest(docEntry)
                if (document != null) {
                    if (loadedDocumentsCount.value == 0) {
                        fromWarehouse.postValue(document!!.fromWarehouse)
                        toWarehouse.postValue(document!!.toWarehouse)
                        docDueDate.postValue(document!!.dueDate)
                    }

                    if (!document!!.documentReferences.isNullOrEmpty()) {
                        val baseDocNumbers =
                            if (baseDocumentsNumber.value == "") document!!.documentReferences[0].refDocNum.toString()
                            else "${baseDocumentsNumber.value}, ${document!!.documentReferences[0].refDocNum}"

                        baseDocumentsNumber.postValue(baseDocNumbers)
                    }

                    var currentList: ArrayList<InventoryOperationsLines> = arrayListOf()
                    if (basketList.value != null) currentList = basketList.value!!
                    currentList.addAll(Mappers.getInventoryDocLinesWithBaseDoc(document!!, true))
                    basketList.postValue(currentList)
                    loadedDocumentsCount.postValue(loadedDocumentsCount.value!! + 1)
                    removeFromLoadingList(docEntry)

                } else {
                    errorLoadingDocument.postValue("Ошибка при загрузке запросов: ${inventoryRequestInteractor.errorMessage}")
                }

            }

            loadingDocument.postValue(false)

/*
            if (document != null) {
                fromWarehouse.postValue(document!!.fromWarehouse)
                toWarehouse.postValue(document!!.toWarehouse)
                docDueDate.postValue(document!!.dueDate)
                basketList.postValue(Mappers.getInventoryDocLinesWithBaseDoc(document!!, true))
            } else
                errorItem.postValue("Ошибка при загрузке: ${inventoryRequestInteractor.errorMessage}")
            loadingDocument.postValue(false)*/
        }
    }


    fun removeFromLoadingList(docEntry: Long) {
        documentsToLoad.filter { it != docEntry }
    }


    fun loadInventoryTransfer(docEntry: Long) {
        vmScope.launch {
            loadingDocument.postValue(true)

            document = inventoryTransferInteractor.getInventoryTransfer(docEntry)

            if (document != null) {
                Log.d("LOADEDINVOICE", document.toString())
                fromWarehouse.postValue(document!!.fromWarehouse)
                toWarehouse.postValue(document!!.toWarehouse)
                docDueDate.postValue(document!!.dueDate)
                basketList.postValue(Mappers.getInventoryDocLinesWithBaseDoc(document!!, true))

            } else
                errorItem.postValue("Ошибка при загрузке перемещения: ${inventoryTransferInteractor.errorMessage}")
            loadingDocument.postValue(false)

        }
    }


    fun changeQuantity(position: Int, quantity: BigDecimal): Boolean {
        val onHand = basketList.value?.get(position)?.OnHand!!
        val initialQuantity = basketList.value?.get(position)?.InitialQuantity!!

        if ( quantity < BigDecimal.valueOf(0.0)) {
            return false
        } // ERROR IF QUANTITY FALLS BEHIND ZERO

        /*if (isCopiedFromInventoryRequest.value!!) {
            if (quantity > basketList.value?.get(position)?.MaxQuantity!!) return false
        } // IN COPIED FROM INVOICE MODE, WE SHOULD NOT BE ABLE TO RETURN MORE QUANTITY THAN IN INVOICE
        */
        basketList.value?.get(position)?.quantity = quantity
        basketList.value?.get(position)?.UserQuantity = quantity

        basketList.value = basketList.value

        return true
    }

    fun removeItemFromBasket(item: Items) {
        val itemCode = item.ItemCode

        basketList.value?.forEachIndexed { index, row ->
            if (row.itemCode == itemCode) {
                basketList.value!!.removeAt(index)
                basketList.value = basketList.value
                clearItemsSection(true)
                return
            }
        }
    }


    fun clearItemsSection(withItemList: Boolean = false) {
        currentChosenItem.value = null
        currentQuantityInPackage.value = null
        if (!withItemList) {
            itemsFilterString.value = null
            itemsList.value = null
        }
    }

    fun clearAll() {
        itemsList.postValue(null)
        itemsFilterString.postValue(null)
        currentChosenItem.postValue(null)
        currentQuantityInPackage.postValue(null)

        isViewMode.postValue(false)


        basketList.postValue(ArrayList<InventoryOperationsLines>())

        docDate.postValue(Utils.getCurrentDateinUSAFormat())
        docDueDate.postValue(Utils.getCurrentDateinUSAFormat())

        baseDocumentsNumber.postValue("")

    }


    fun insertInventoryTransfer() {
        Log.d(
            "BASKETLIST",
            "SIZE ${basketList.value?.size},        is copied ${isCopiedFromInventoryRequest.value}"
        )

        val docLineForInsert: List<InventoryOperationsLinesForPost> =
            if (isCopiedFromInventoryRequest.value!!) {
                Mappers.mapInventoryDocLinesToInventoryDocLinesForPost(
                    basketList.value!!,
                    isCopyFrom = true,
                   // areBatchesIssued = true
                )
            } else Mappers.mapInventoryDocLinesToInventoryDocLinesForPost(
                basketList.value!!,
                isCopyFrom = false,
               // areBatchesIssued = true
            )


        val docForInsert = InventoryOperationsForPost(
            docDate = docDate.value,
            dueDate = docDueDate.value,
            inventoryOperationsLines = docLineForInsert,
            fromWarehouse = fromWarehouse.value,
            toWarehouse = toWarehouse.value
        )

        Log.d("COPIEDFROMORDER", Gson().toJson(docForInsert).toString())
        vmScope.launch {
            loadingInsert.postValue(true)
            val response = inventoryTransferInteractor.insertInventoryTransfer(docForInsert)
            if (response != null) {
                insertItem.postValue("Перемещение №${response.docNum} успешно добавлено!")
                isCopiedFromInventoryRequest.postValue(false)
            } else {
                errorLoadingInsert.postValue(true)
                loadingInsert.postValue(false)
                errorItem.postValue("Ошибка в перемещении: ${inventoryTransferInteractor.errorMessage}")
                return@launch
            }

            clearAll()
            loadingInsert.postValue(false)
        }

    }


    fun addToBasket(
        chosenItem: Items? = null,
        quantityToAdd: BigDecimal
    ): Boolean {
        val item: Items = chosenItem ?: currentChosenItem.value!!

        if (item.OnHandCurrentWhs == 0.0) return false  // IF QUANTITY OF CHOSEN ITEM IS 0, THEN MAKE TOAST AND DO NOT ADD ITEM TO BASKET

        Log.d("ADDTOBASKET", "${basketList.value?.size}")
        var itemFound = false
        val docLine = InventoryOperationsLines()

        for (row in basketList.value!!) {
            if (row.itemCode == item.ItemCode) {

                val updatedQuantity = row.quantity?.plus(quantityToAdd)!!
                if (item.OnHandCurrentWhs.toBigDecimal() < updatedQuantity - row.InitialQuantity || updatedQuantity < BigDecimal.valueOf(
                        0.0
                    )
                ) return false // IF QUANTITY OF CHOSEN ITEM IS LESS THAN IN QUANTITY IN BASKET, THEN MAKE TOAST AND DO NOT ADD ITEM TO BASKET

                row.quantity = updatedQuantity
                row.UserQuantity = updatedQuantity
                currentChosenItem.value = item
                itemFound = true
                break
            }
        }

        if (!itemFound) {
            docLine.itemCode = item.ItemCode
            docLine.itemDescription = item.ItemName
            docLine.quantity = quantityToAdd
            docLine.UserQuantity = quantityToAdd
            docLine.OnHand = item.OnHandCurrentWhs.toBigDecimal()
            docLine.Committed = item.CommittedCurrentWhs.toBigDecimal()
            docLine.Barcode = item.BarCode.toString()
            docLine.managedBy =
                when {
                    item.ManageBatchNumbers == GeneralConsts.YES -> GeneralConsts.MANAGED_BY_BATCH
                    item.ManageSerialNumbers == GeneralConsts.YES -> GeneralConsts.MANAGED_BY_SERIES
                    else -> null
                }
            basketList.value?.add(docLine)

            currentChosenItem.value = item
        }

        return true
    }

    fun changeFromWhsOfBasketList() {
        val newList: ArrayList<InventoryOperationsLines> = arrayListOf()
        if (fromWarehouse.value != null) {
            for (item in basketList.value!!) {
                item.fromWarehouse = fromWarehouse.value
                item.TempBatchNumbers = arrayListOf()
                item.BatchNumbers = arrayListOf()
                newList.add(item)
            }
            basketList.value = newList
        }
    }

    fun changeToWhsOfBasketList() {
        val newList: ArrayList<InventoryOperationsLines> = arrayListOf()
        if (toWarehouse.value != null) {
            for (item in basketList.value!!) {
                item.toWarehouse = toWarehouse.value
                item.TempBatchNumbers = arrayListOf()
                item.BatchNumbers = arrayListOf()
                newList.add(item)
            }
            basketList.value = newList
        }
    }

    fun setBatchNumbersToItem(position: Int, batches: ArrayList<BatchNumbersForPost>) {
        val newList = basketList.value
        newList?.get(position)?.BatchNumbers = batches
        basketList.value = newList

    }

    fun setTempBatchNumbersToItem(position: Int, batches: ArrayList<BatchNumbersVal.BatchNumbers>) {
        val newList = basketList.value
        newList?.get(position)?.TempBatchNumbers = batches
        basketList.value = newList

    }

    fun getItemsList() {
        if (itemsFilterString.value == null)
            itemsFilterString.value = ""
        else {
            itemsList.value = arrayListOf()

            if (itemSearchJob?.isActive == true) {
                itemSearchJob?.cancel()
            }

            itemSearchJob = vmScope.launch {
                itemsLoading.postValue(true)
                val listResult = ArrayList<Any>()
                val items = itemsInteractor.getAllItems(
                    filter = itemsFilterString.value!!,
                    whsCode = fromWarehouse.value,
                    onlyValid = true,

                )
                if (items != null) {
                    listResult.addAll(items)
                    if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
                } else {
                    errorItemsLoading.postValue(true)
                }
                itemsLoading.postValue(false)
                itemsList.postValue(listResult)
            }
        }
    }

    fun getMoreItemsList(skipValue: Int) {
        vmScope.launch {
            val listResult =
                ArrayList<Any>(itemsList.value?.filter { it !is LoadMore } ?: listOf())
            val items =
                itemsInteractor.getMoreItems(
                    filter = itemsFilterString.value!!,
                    skipValue = skipValue,
                    whsCode = fromWarehouse.value,
                    onlyValid = true,

                )

            if (items != null) {
                listResult.addAll(items)

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorItemsLoading.postValue(true)
            }
            itemsList.postValue(listResult)
        }
    }
}