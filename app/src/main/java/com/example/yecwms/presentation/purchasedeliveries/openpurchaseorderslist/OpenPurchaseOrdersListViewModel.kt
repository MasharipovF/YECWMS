package com.example.yecwms.presentation.purchasedeliveries.openpurchaseorderslist

import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.domain.interactor.PurchaseOrdersInteractor
import com.example.yecwms.domain.interactor.PurchaseOrdersInteractorImpl
import com.example.yecwms.util.LoadMore
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OpenPurchaseOrdersListViewModel : BaseViewModel() {
    private val interactor: PurchaseOrdersInteractor by lazy { PurchaseOrdersInteractorImpl() }
    var listToDraw: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoading: MutableLiveData<String> = MutableLiveData()
    var filterString: MutableLiveData<String> = MutableLiveData()

    var dateFrom: MutableLiveData<String> = MutableLiveData()
    var dateTo: MutableLiveData<String> = MutableLiveData()

    var purchaseOrdersListJob: Job? = null


    init {
        filterString.value = ""
    }


    fun getPurchaseOrdersList() {
        if (purchaseOrdersListJob?.isActive == true) {
            purchaseOrdersListJob?.cancel()
        }

        listToDraw.value = arrayListOf()
        purchaseOrdersListJob = vmScope.launch {
            loading.postValue(true)
            val listResult = ArrayList<Any>()

            val purchaseOrders = interactor.getPurchaseOrders(
                filter = filterString.value!!,
                dateFrom = dateFrom.value,
                dateTo = dateTo.value,
                docStatus = GeneralConsts.DOC_STATUS_OPEN
            )

            if (purchaseOrders != null) {

                listResult.addAll(purchaseOrders)

                if (purchaseOrders.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
            } else {
                errorLoading.postValue(interactor.errorMessage)
            }
            loading.postValue(false)
            listToDraw.postValue(listResult)
        }
    }

    fun getMorePurchaseOrdersList(skipValue: Int) {
        vmScope.launch {
            val listResult =
                ArrayList<Any>(listToDraw.value?.filter { it !is LoadMore } ?: listOf())

            val openOrders =
                interactor.getPurchaseOrders(
                    filter = filterString.value!!,
                    dateFrom = dateFrom.value,
                    dateTo = dateTo.value,
                    docStatus = GeneralConsts.DOC_STATUS_OPEN,
                    skipValue = skipValue
                )

            if (openOrders != null) {
                listResult.addAll(openOrders)

                if (openOrders.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorLoading.postValue(interactor.errorMessage)
            }
            listToDraw.postValue(listResult)
        }
    }
}