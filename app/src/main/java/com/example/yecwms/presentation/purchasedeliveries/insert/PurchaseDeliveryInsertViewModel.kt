package com.example.yecwms.presentation.purchasedeliveries.insert

import android.content.Context
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.dantsu.escposprinter.EscPosPrinter
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.batches.BatchNumbersForPost
import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.data.entity.discount.DiscountByQuantity
import com.example.yecwms.data.entity.documents.Document
import com.example.yecwms.data.entity.documents.DocumentForPost
import com.example.yecwms.data.entity.documents.DocumentLines
import com.example.yecwms.data.entity.documents.DocumentLinesForPost
import com.example.yecwms.data.entity.items.Items
import com.example.yecwms.data.entity.masterdatas.Currencies
import com.example.yecwms.data.entity.masterdatas.SalesManagers
import com.example.yecwms.data.entity.masterdatas.Warehouses
import com.example.yecwms.domain.interactor.*
import com.example.yecwms.domain.mappers.Mappers
import com.example.yecwms.util.LoadMore
import com.example.yecwms.util.Utils
import com.example.yecwms.util.barcodeprinter.PrinterHelper
import com.godex.Godex
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class PurchaseDeliveryInsertViewModel : BaseViewModel() {

    private val itemsInteractor: ItemsInteractor by lazy { ItemsInteractorImpl() }
    private val bpInteractor: BpInteractor by lazy { BpInteractorImpl() }
    private val purchaseDeliveryInteractor: PurchaseDeliveriesInteractor by lazy { PurchaseDeliveriesInteractorImpl() }
    private val purchaseOrdersInteractor: PurchaseOrdersInteractor by lazy { PurchaseOrdersInteractorImpl() }
    private val masterDataInteractor: MasterDataInteractor by lazy { MasterDataInteractorImpl() }

    var itemSearchJob: Job? = null
    var bpSearchJob: Job? = null

    val image: MutableLiveData<Boolean> = MutableLiveData()

    var docEntry: Long? = null
    var loadingDocument: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoadingDocument: MutableLiveData<String> = MutableLiveData()

    var errorLoadingInsert: MutableLiveData<Boolean> = MutableLiveData()
    var loadingInsert: MutableLiveData<Boolean> = MutableLiveData()

    var insertedDocument: MutableLiveData<Document> = MutableLiveData()

    var addBtnText: MutableLiveData<String> = MutableLiveData()

    var document: MutableLiveData<Document> = MutableLiveData()
    var isCopiedFrom: MutableLiveData<Boolean> = MutableLiveData()

    //var exchangeRate: Double? = null

    var itemsList: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    val itemsListForImages: MutableLiveData<List<Items>> = MutableLiveData()
    var itemsFilterString: MutableLiveData<String> = MutableLiveData()
    var itemsLoading: MutableLiveData<Boolean> = MutableLiveData()

    var errorItemsLoading: MutableLiveData<String> = MutableLiveData()
    var currentChosenItem: MutableLiveData<Items> = MutableLiveData()
    var currentQuantity: MutableLiveData<Double> = MutableLiveData()
    var currentPrice: MutableLiveData<Double> = MutableLiveData()

    var barcodeItemsList: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    var barcodeFilterString: MutableLiveData<String> = MutableLiveData()

    var bpList: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    var bpFilterString: MutableLiveData<String> = MutableLiveData()
    var bpLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorBpLoading: MutableLiveData<String> = MutableLiveData()
    var currentChosenBp: MutableLiveData<BusinessPartners> = MutableLiveData()

    var previousChosenBp: MutableLiveData<BusinessPartners> = MutableLiveData()
    var currentBpPhone: MutableLiveData<String> = MutableLiveData()

    var currentWhsCode: MutableLiveData<String> = MutableLiveData()
    var warehousesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorWarehousesLoading: MutableLiveData<String> = MutableLiveData()
    var warehousesList: MutableLiveData<ArrayList<Warehouses>> = MutableLiveData()

    var currentCurrency: MutableLiveData<Currencies> = MutableLiveData()
    var currenciesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorCurrenciesLoading: MutableLiveData<String> = MutableLiveData()
    var currenciesList: MutableLiveData<List<Currencies>> = MutableLiveData()

    var managersLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorManagersLoading: MutableLiveData<String> = MutableLiveData()
    var managersList: MutableLiveData<ArrayList<SalesManagers>> = MutableLiveData()
    var currentChosenManager: MutableLiveData<SalesManagers> = MutableLiveData()

    var comments: MutableLiveData<String> = MutableLiveData()

    val docDueDate: MutableLiveData<String> = MutableLiveData()
    val docDate: MutableLiveData<String> = MutableLiveData()

    var basketList: MutableLiveData<ArrayList<DocumentLines>> = MutableLiveData()
    var itemWithDiscountByQuantityCollection: MutableLiveData<ArrayList<DiscountByQuantity>> =
        MutableLiveData()

    var docTotal: MutableLiveData<Double> = MutableLiveData()
    var discount: MutableLiveData<Int> = MutableLiveData()
    var discountedTotal: MutableLiveData<Double> = MutableLiveData()

    var payByCashSum: MutableLiveData<Double> = MutableLiveData()
    var payByCardSum: MutableLiveData<Double> = MutableLiveData()
    var payByePaymentSum: MutableLiveData<Double> = MutableLiveData()
    var payByBankTransferSum: MutableLiveData<Double> = MutableLiveData()

    var balanceSum: MutableLiveData<Double> = MutableLiveData()

    var paidSum: MutableLiveData<Double> = MutableLiveData()

    var isBpNameSwitchChecked: MutableLiveData<Boolean> = MutableLiveData()
    var isBarcodeSwitchChecked: MutableLiveData<Boolean> = MutableLiveData()

    lateinit var printer: EscPosPrinter
    var printerLoading: MutableLiveData<Boolean> = MutableLiveData()
    var printerError: MutableLiveData<String> = MutableLiveData()
    var printerSuccess: MutableLiveData<Boolean> = MutableLiveData()


    init {
        getCurrencies()

        isCopiedFrom.value = false
        bpFilterString.value = ""

        basketList.value = ArrayList<DocumentLines>()

        docDate.value = Utils.getCurrentDateinUSAFormat()
        docDueDate.value = Utils.getCurrentDateinUSAFormat()

        comments.value = ""
        currentBpPhone.value = ""

        currentWhsCode.value = Preferences.defaultWhs

        docTotal.value = 0.0
        discount.value = 0

        discountedTotal.value = 0.0

        payByCashSum.value = 0.0

        payByCardSum.value = 0.0

        payByePaymentSum.value = 0.0

        payByBankTransferSum.value = 0.0

        balanceSum.value = 0.0

        paidSum.value = 0.0

        currentChosenManager.value = Preferences.getSalesPersonFromPref()

    }

    fun connectAndPrint(
        context: Context,
        documentLines: List<DocumentLines>,
        printerIp: String? = null,
        printerPort: Int? = null
    ) {
        vmScope.launch {
            printerLoading.postValue(true)
            try {

                val wifi =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

                if (!wifi.isWifiEnabled)
                    wifi.isWifiEnabled = true

                Godex.getMainContext(context)

                val isConnected = Godex.openport(printerIp, 1)

                if (!isConnected) {
                    Toast.makeText(
                        context,
                        "WiFi Connect fail",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                var printingResults = true


                printingResults = PrinterHelper.printReceipt(
                    context,
                    documentLines,
                    60,
                    60
                )




                printerSuccess.postValue(printingResults)
                Log.d("PRINTER", "ПРИНТЕР ПОДКЛЮЧЕН")

            } catch (e: Exception) {
                printerError.postValue("Ошибка при подключении ${e.message}")
                e.printStackTrace()
            }
            printerLoading.postValue(false)
        }
    }


    fun getSalesManagersList() {
        vmScope.launch {
            managersLoading.postValue(true)
            var managers = masterDataInteractor.getSalesManagers()
            if (managers != null) {
                managersList.postValue(managers as ArrayList<SalesManagers>?)
            } else {
                errorManagersLoading.postValue(masterDataInteractor.errorMessage)
            }
            managersLoading.postValue(false)
        }
    }

    fun getManager(managerCode: Long) {
        vmScope.launch {
            val managers = masterDataInteractor.getSalesManager(managerCode)
            if (managers != null) {
                currentChosenManager.postValue(managers)
            } else {
                errorManagersLoading.postValue(masterDataInteractor.errorMessage)
            }
        }
    }


    fun loadPurchaseOrder(docEntry: Long) {
        vmScope.launch {
            loadingDocument.postValue(true)
            val purchaseInvoice = purchaseOrdersInteractor.getPurchaseOrder(docEntry)
            if (purchaseInvoice != null) {
                document.postValue(purchaseInvoice)
                loadingDocument.postValue(false)
                getWarehouseList()
            } else
                errorItem.postValue("Ошибка при загрузке: ${purchaseOrdersInteractor.errorMessage}")
        }
    }
/*
    fun updateSalesOrder() {
        val docForUpdate = DocumentForPost(
            BPL_ID = Preferences.branch,
            DocEntry = document.value?.DocEntry,
            DocNum = document.value?.DocNum,
            NumAtCard = document.value?.NumAtCard,
            DocDate = Utils.getCurrentDateinUSAFormat(),
            DocDueDate = Utils.getCurrentDateinUSAFormat(),
            CardCode = currentChosenBp.value?.CardCode,
            CardName = currentChosenBp.value?.CardName,
            U_phone = currentBpPhone.value,
            DocumentLines = Mappers.mapDocLinesToDocLinesForPost(basketList.value!!, false),
            DocTotal = discountedTotalUSD.value,
            DocTotalUZS = discountedTotalUZS.value,
            DocTotalUZSBefDiscount = docTotalUZS.value,
            DiscountPercent = discount.value?.toDouble(),
            DocCurrency = GeneralConsts.PRIMARY_CURRENCY,
            Cancelled = document.value?.Cancelled,
            ShipToCode = document.value?.ShipToCode,
            WhsCode = currentWhsCode.value
        )

        Log.d("INSERTINVOICE", Gson().toJson(docForUpdate).toString())


        vmScope.launch {
            val response =
                salesOrserInteractor.updateSalesOrder(docForUpdate.DocEntry!!, docForUpdate)
            if (response) {
                insertItem.postValue("Отложка №${docForUpdate.DocNum!!} успешно обновлена!")
                isUpdateMode.postValue(false)
                clearAll()
            } else
                errorItem.postValue("Ошибка в отложке: ${salesOrserInteractor.errorMessage}")

        }
    }
*/


    fun changeQuantity(position: Int, quantity: Double): Boolean {
        val currentItem = basketList.value?.get(position)

        if (quantity <= 0) {
            return false
        } // ERROR IF QUANTITY FALLS BEHIND ZERO

        if (isCopiedFrom.value!!) {
            if (quantity > basketList.value?.get(position)?.MaxQuantity!!) return false
        } // IN COPIED FROM INVOICE MODE, WE SHOULD NOT BE ABLE TO RETURN MORE QUANTITY THAN IN INVOICE

        basketList.value?.get(position)?.Quantity = quantity
        basketList.value?.get(position)?.UserQuantity = quantity
        if (currentItem?.DiscountType != null && (currentItem.DiscountType == GeneralConsts.DISCOUNT_TYPE_SPECIAL_PRICES || currentItem.DiscountType == GeneralConsts.DISCOUNT_TYPE_VOLUME_PERIOD)) {
            applyDiscountByQuantity(position, quantity)
        }
        calculateDocTotal()


        if (!isCopiedFrom.value!!) { // IF IS NOT COPIED FROM INVOICE, SET QUANTITY TO MAIN PAGE
            if (currentItem?.ItemCode == currentChosenItem.value?.ItemCode) {
                currentQuantity.value = quantity
                //TODO CHECK IF WE NEED USERQUANTITY
            }
        }

        basketList.value = basketList.value

        return true
    }

    fun changePrice(position: Int? = null, price: Double): Boolean {

        if (price < 0) return false // ERROR IF PRICE FALLS BEHIND ZERO
        var row: DocumentLines? = null

        if (position == null) {
            basketList.value?.forEachIndexed { index, item ->
                if (item.ItemCode == currentChosenItem.value?.ItemCode) {
                    row = item
                }
            }
        } else row = basketList.value?.get(position)!!

        //  val minPrice = row?.PriceAfterVATUZS!!
        //  if (priceUZS < minPrice) return false // IF price is lower than price in pricelist

        row?.UserPriceAfterVAT = price

        //currentPrice.value = row?.UserPriceAfterVATUZS
        calculateDocTotal()

        if (row?.ItemCode == currentChosenItem.value?.ItemCode) {
            currentPrice.value = price
        }
        basketList.value = basketList.value
        return true
    }

    fun removeItemFromBasket(item: Items) {
        val itemCode = item.ItemCode

        basketList.value?.forEachIndexed { index, row ->
            if (row.ItemCode == itemCode) {
                basketList.value!!.removeAt(index)
                basketList.value = basketList.value
                clearItemsSection(true)
                calculateDocTotal()
                return
            }
        }
    }


    fun clearItemsSection(withItemList: Boolean = false) {
        currentChosenItem.value = null
        currentQuantity.value = null
        currentPrice.value = null
        if (!withItemList) {
            itemsFilterString.value = null
            itemsList.value = null
        }
    }

    fun clearBpSelection() {
        currentChosenBp.value = null
    }

    fun clearAll() {
        currentCurrency.postValue(null)

        itemsList.postValue(null)
        itemsFilterString.postValue(null)
        currentChosenItem.postValue(null)
        currentQuantity.postValue(null)
        currentPrice.postValue(null)

        currentChosenBp.postValue(null)

        currentChosenManager.postValue(null)

        bpFilterString.postValue("")

        basketList.postValue(ArrayList<DocumentLines>())

        docDate.postValue(Utils.getCurrentDateinUSAFormat())
        docDueDate.postValue(Utils.getCurrentDateinUSAFormat())

        comments.postValue("")
        previousChosenBp.postValue(null)
        currentBpPhone.postValue("")

        docTotal.postValue(0.0)
        discount.postValue(0)

        discountedTotal.postValue(0.0)

        payByCashSum.postValue(0.0)

        payByCardSum.postValue(0.0)

        payByePaymentSum.postValue(0.0)

        payByBankTransferSum.postValue(0.0)

        balanceSum.postValue(0.0)

        paidSum.postValue(0.0)

        isBpNameSwitchChecked.postValue(null)
        isBarcodeSwitchChecked.postValue(null)

        insertedDocument.postValue(null)
    }


    fun insertPurchaseDelivery() {
        vmScope.launch {
            loadingInsert.postValue(true)

            val response: Document?

            if (insertedDocument.value == null) {
                val docLineForInsert: List<DocumentLinesForPost> =
                    if (isCopiedFrom.value!!) {
                        Mappers.mapDocLinesToDocLinesForPost(
                            basketList.value!!,
                            isCopyFrom = true,
                            areBatchesIssued = false
                        )
                    } else Mappers.mapDocLinesToDocLinesForPost(
                        basketList.value!!,
                        isCopyFrom = false,
                        areBatchesIssued = false
                    )

                val docForInsert = DocumentForPost(
                    CardCode = currentChosenBp.value?.CardCode,
                    DocDate = Utils.getCurrentDateinUSAFormat(),
                    DocDueDate = Utils.getCurrentDateinUSAFormat(),
                    DocTotal = discountedTotal.value,
                    DocCurrency = currentCurrency.value?.code,
                    DocumentLines = docLineForInsert,
                    SalesManagerCode = currentChosenManager.value?.salesEmployeeCode,
                    Comments = comments.value
                )

                Log.wtf("INSERTINVOICE", Gson().toJson(docForInsert).toString())
                response = purchaseDeliveryInteractor.insertPurchaseDelivery(docForInsert)

                if (response != null) {
                    insertedDocument.postValue(response)
                } else {
                    errorLoadingInsert.postValue(true)
                    loadingInsert.postValue(false)
                    errorItem.postValue("Ошибка в поступлении: ${purchaseDeliveryInteractor.errorMessage}")
                    return@launch
                }
            } else {
                response = insertedDocument.value
            }


            loadingInsert.postValue(false)
            insertItem.postValue("Поступление №${response?.DocNum} успешно добавлен!")
            isCopiedFrom.postValue(false)


        }

    }


    fun addToBasket(
        chosenItem: Items? = null,
        quantityToAdd: Double,
        priceUZS: Double? = null
    ): Boolean {
        val item: Items = chosenItem ?: currentChosenItem.value!!


        var itemFound = false
        val docLine = DocumentLines()


        basketList.value!!.forEachIndexed { index, row ->
            if (row.ItemCode == item.ItemCode) {

                val updatedQuantity = row.Quantity?.plus(quantityToAdd)!!
                if (updatedQuantity <= 0) return false // IF QUANTITY OF CHOSEN ITEM IS LESS THAN IN QUANTITY IN BASKET, THEN MAKE TOAST AND DO NOT ADD ITEM TO BASKET

                row.Quantity = updatedQuantity
                row.UserQuantity = updatedQuantity

                if (item.DiscountType != null && (item.DiscountType == GeneralConsts.DISCOUNT_TYPE_SPECIAL_PRICES || item.DiscountType == GeneralConsts.DISCOUNT_TYPE_VOLUME_PERIOD)) {
                    applyDiscountByQuantity(index, updatedQuantity)
                }

                currentPrice.value = row.UserPriceAfterVAT
                currentQuantity.value = updatedQuantity
                currentChosenItem.value = item
                itemFound = true

            }

        }


        if (!itemFound) {
            if (quantityToAdd <= 0) return false  // IF QUANTITY OF CHOSEN ITEM IS 0, THEN MAKE TOAST AND DO NOT ADD ITEM TO BASKET
            docLine.ItemCode = item.ItemCode
            docLine.ItemName = item.ItemName
           /*TODO docLine.Forma = item.Forma
            docLine.Forma2 = item.Forma2
            docLine.Parametr = item.Parametr
            docLine.Volume = item.Volume*/
            docLine.OnHand = item.OnHandCurrentWhs
            docLine.Committed = item.CommittedCurrentWhs
            docLine.Quantity = quantityToAdd
            docLine.UserQuantity = quantityToAdd
            docLine.DiscountPercent = item.DiscountApplied
            docLine.DiscountMain = item.DiscountApplied
            docLine.DiscountType = item.DiscountType
            docLine.PriceAfterVAT = priceUZS
            docLine.UserPriceAfterVAT = priceUZS
            docLine.InitialPrice = item.Price
            docLine.WarehouseCode = currentWhsCode.value.toString()

            docLine.ManageBatchNumbers = item.ManageBatchNumbers
            /*if (item.ManageBatchNumbers == GeneralConsts.T_YES) {
                docLine.BatchNumbers.add(
                    BatchNumbersForPost(
                        BatchNumber = item.BatchNumber,
                        Quantity = quantityToAdd
                    )
                )
            }*/


            if (item.DiscountType != null && (item.DiscountType == GeneralConsts.DISCOUNT_TYPE_SPECIAL_PRICES || item.DiscountType == GeneralConsts.DISCOUNT_TYPE_VOLUME_PERIOD)) {
                // IF THERE IS DISCOUNT BY QUANTITY
                docLine.DiscountByQuantityLoading = true
                getItemWithDiscountByQuantity(item)
            }

            basketList.value?.add(docLine)
            currentChosenItem.value = item
            currentPrice.value = priceUZS
            currentQuantity.value = quantityToAdd
        }
        calculateDocTotal()

        return true
    }


    fun generateBatchesToAllItems() {
        basketList.value?.forEach {
            it.BatchNumbers = arrayListOf<BatchNumbersForPost>(
                BatchNumbersForPost(
                    BatchNumber = Utils.generateBatchNumber(it.ItemCode.toString()),
                    Quantity = it.UserQuantity!!.toDouble()
                )
            )
        }

        basketList.value = basketList.value
    }

    fun calculateDocTotal() {
        var totalUSD = 0.0
        for (row in basketList.value!!) {
            if (row.LineStatus == GeneralConsts.DOC_STATUS_CLOSED)
                continue
            totalUSD += row.Quantity?.times(row.UserPriceAfterVAT!!)!!
        }
        docTotal.value =
            BigDecimal(totalUSD).setScale(Preferences.totalsAccuracy + 1, RoundingMode.HALF_UP)
                .setScale(Preferences.totalsAccuracy, RoundingMode.HALF_UP).toDouble()

        discountedTotal.value =
            BigDecimal(totalUSD * (100 - discount.value!!) / 100).setScale(
                Preferences.totalsAccuracy + 1,
                RoundingMode.HALF_UP
            ).setScale(Preferences.totalsAccuracy, RoundingMode.HALF_UP).toDouble()

    }

    fun calculateDebt() {
        val total =
            payByCashSum.value!! + payByCardSum.value!! + payByePaymentSum.value!! + payByBankTransferSum.value!!

        paidSum.value =
            BigDecimal(total).setScale(Preferences.totalsAccuracy + 1, RoundingMode.HALF_UP)
                .setScale(Preferences.totalsAccuracy, RoundingMode.HALF_UP).toDouble()

        balanceSum.value =
            BigDecimal(discountedTotal.value!! - total).setScale(
                Preferences.totalsAccuracy + 1,
                RoundingMode.HALF_UP
            ).setScale(Preferences.totalsAccuracy, RoundingMode.HALF_UP).toDouble()

    }

    fun setBatchNumbersToItem(position: Int, batches: ArrayList<BatchNumbersForPost>) {
        val newList = basketList.value
        newList?.get(position)?.BatchNumbers = batches
        basketList.value = newList
        Log.d("COPIEDFROMORDER", basketList.value.toString())

    }


    fun getCurrencies() {
        vmScope.launch {
            currenciesLoading.postValue(true)
            val response = masterDataInteractor.getCurrencies(Utils.getCurrentDateinUSAFormat())
            if (response != null) {
                currenciesList.postValue(response)
            } else
                errorCurrenciesLoading.postValue("Ошибка при загрузке: ${masterDataInteractor.errorMessage}")
            currenciesLoading.postValue(false)
        }
    }


    fun getWarehouseList() {
        vmScope.launch {
            warehousesLoading.postValue(true)
            val warehouses = masterDataInteractor.getWarehouses()
            if (warehouses != null) {
                warehousesList.postValue(warehouses as ArrayList<Warehouses>?)
            } else {
                errorWarehousesLoading.postValue(masterDataInteractor.errorMessage)
            }
            warehousesLoading.postValue(false)
        }
    }

    fun getItemWithDiscountByQuantity(item: Items) {
        Log.d("DISCOUNTQUANTITY", "DISCOUNT TYPE ${item.DiscountType}")

        vmScope.launch {
            val itemWithDiscountCollection =
                if (item.DiscountType == GeneralConsts.DISCOUNT_TYPE_VOLUME_PERIOD) {
                    itemsInteractor.getItemWithDiscountByQuantity(
                        itemCode = item.ItemCode!!,
                        lineNum = item.DiscountLineNum
                    )
                } else {
                    itemsInteractor.getItemWithDiscountByQuantity(
                        cardCode = currentChosenBp.value!!.CardCode,
                        itemCode = item.ItemCode!!,
                        lineNum = item.DiscountLineNum
                    )
                }

            if (itemWithDiscountCollection != null) {
                itemWithDiscountByQuantityCollection.postValue(itemWithDiscountCollection as ArrayList<DiscountByQuantity>?)
                Log.d("DISCOUNTQUANTITY", itemWithDiscountCollection.toString())
            } else {
                Log.d("DISCOUNTQUANTITY", itemsInteractor.errorMessage.toString())
            }
        }

    }

    fun applyDiscountByQuantity(positionInBasket: Int, quantity: Double) {
        var discountToApply = basketList.value?.get(positionInBasket)?.DiscountMain!!
        basketList.value?.get(positionInBasket)?.DiscountByQuantityCollection?.forEach {
            if (quantity >= it!!.amount) {
                discountToApply = it.discount
            }
        }

        Log.d("DISCOUNT", discountToApply.toString())
        val updatedBasketList = basketList.value!!

        updatedBasketList[positionInBasket].PriceAfterVAT =
            (updatedBasketList[positionInBasket].InitialPrice!! * (100 - discountToApply)) / 100

        updatedBasketList[positionInBasket].UserPriceAfterVAT =
            (updatedBasketList[positionInBasket].InitialPrice!! * (100 - discountToApply)) / 100


        updatedBasketList[positionInBasket].DiscountPercent = discountToApply

        basketList.postValue(updatedBasketList)
    }


    fun getItemsList() {
        if (itemsFilterString.value == null) return

        if (itemSearchJob?.isActive == true) {
            itemSearchJob?.cancel()
        }

        itemsList.value = arrayListOf()
        itemSearchJob = vmScope.launch {
            itemsLoading.postValue(true)
            val listResult = ArrayList<Any>()
            val items = itemsInteractor.getItemsViaSML(
                cardCode = currentChosenBp.value!!.CardCode!!,
                whsCode = currentWhsCode.value!!,
                date = docDate.value!!,
                priceListCode = currentChosenBp.value!!.PriceListCode!!,
                filter = itemsFilterString.value!!
            )
            /*val items = itemsInteractor.getAllItems(
                whsCode = currentWhsCode.value!!,
                priceListCode = currentChosenBp.value!!.PriceListCode!!,
                filter = itemsFilterString.value!!,
                onlyValid = true
            )*/

            if (items != null) {
                listResult.addAll(items)
                itemsListForImages.postValue(makeItemsListForImages(items))
                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
            } else {
                errorItemsLoading.postValue("Ошибка при загрузке товаров: ${itemsInteractor.errorMessage}")
            }
            itemsLoading.postValue(false)
            itemsList.postValue(listResult)
        }

    }

    fun getMoreItemsList(skipValue: Int) {
        vmScope.launch {
            val listResult =
                ArrayList<Any>(itemsList.value?.filter { it !is LoadMore } ?: listOf())
            val items =
                itemsInteractor.getItemsViaSML(
                    cardCode = currentChosenBp.value!!.CardCode!!,
                    whsCode = currentWhsCode.value!!,
                    date = docDate.value!!,
                    priceListCode = currentChosenBp.value!!.PriceListCode!!,
                    filter = itemsFilterString.value!!,
                    skipValue = skipValue
                )

            if (items != null) {
                listResult.addAll(items)
                itemsListForImages.postValue(makeItemsListForImages(items))

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorItemsLoading.postValue("Ошибка при загрузке товаров: ${itemsInteractor.errorMessage}")
            }
            itemsList.postValue(listResult)
        }
    }


    private fun makeItemsListForImages(items: List<Items>): List<Items> {
        return items.distinctBy { it.ItemCode }.toList() ?: listOf()
    }

    fun getItemImage(itemcode: String) {
        Log.wtf("IMAGE", "ITEMIMAGE IS GETTING")
        vmScope.launch {
            val image = itemsInteractor.getItemImage(itemcode)
            Log.wtf("ITEMINFO", "Passed string FOR GETTING IMAGE $itemcode \n ${image.toString()}")
            if (image != null) {
                setItemImage(image, itemcode)
                Log.wtf("ITEM IMAGE IN VIEWMODEL", image.toString())
            }
        }

    }

    private suspend fun setItemImage(image: Bitmap, itemCode: String) {
        Log.wtf("IMAGE", "ITEMIMAGE IS SET")
        itemsList.value?.forEach {
            if (it is Items) {
                if (it.ItemCode == itemCode) {
                   // it.ItemImage = image
                }
            }
        }

        itemsList.postValue(itemsList.value)

        basketList.value?.forEach {
            if (it.ItemCode == itemCode) {
              //  it.ItemImage = image
            }
        }
        basketList.postValue(basketList.value)
    }

    fun getItemByBarCode() {

        Log.d("ITEMSLIST", "FILTERSTRING BARCODE " + barcodeFilterString.value.toString())

        if (itemSearchJob?.isActive == true) {
            itemSearchJob?.cancel()
        }

        if (barcodeFilterString.value.isNullOrEmpty()) {
            itemsList.value = arrayListOf()
            return
        }

        itemsList.value = arrayListOf()

        itemSearchJob = vmScope.launch {
            itemsLoading.postValue(true)
            val listResult = ArrayList<Any>()
            val items = itemsInteractor.getItemByBarCode(
                barcode = barcodeFilterString.value!!,
                priceListCode = currentChosenBp.value?.PriceListCode!!
            )
            if (items != null) {
                listResult.addAll(items)
            } else {
                errorItemsLoading.postValue("Ошибка при загрузке товаров: ${itemsInteractor.errorMessage}")
            }
            itemsList.postValue(listResult)
            itemsLoading.postValue(false)
        }

    }


    fun getBp(bpCode: String) {
        vmScope.launch {
            //itemsLoading.postValue(true)
            val item = bpInteractor.getBpInfo(bpCode, true)
            if (item != null) {
                currentChosenBp.postValue(item)
            } else {
                errorBpLoading.postValue(bpInteractor.errorMessage)
            }
        }
    }


    fun getBpList() {
        if (bpSearchJob?.isActive == true) {
            bpSearchJob?.cancel()
        }

        bpList.value = arrayListOf()
        bpSearchJob = vmScope.launch {
            bpLoading.postValue(true)
            val listResult = ArrayList<Any>()

            val items = bpInteractor.getAllBp(
                filter = bpFilterString.value!!,
                bpType = GeneralConsts.BP_TYPE_SUPPLIER,
                onlyWithDebts = false
            )

            if (items != null) {
                listResult.addAll(items)
                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
            } else {
                errorBpLoading.postValue(bpInteractor.errorMessage)
            }
            bpLoading.postValue(false)
            bpList.postValue(listResult)
        }
    }

    fun getMoreBpList(skipValue: Int) {
        vmScope.launch {
            val listResult =
                ArrayList<Any>(bpList.value?.filter { it !is LoadMore } ?: listOf())

            val items =
                bpInteractor.getMoreBps(
                    filter = bpFilterString.value!!,
                    skipValue = skipValue,
                    bpType = GeneralConsts.BP_TYPE_SUPPLIER,
                    onlyWithDebts = false
                )

            if (items != null) {
                listResult.addAll(items)

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorBpLoading.postValue(bpInteractor.errorMessage)
            }
            bpList.postValue(listResult)
        }
    }

}