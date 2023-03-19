package com.example.yecwms.presentation.businesspartners.bplist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.domain.interactor.BpInteractor
import com.example.yecwms.domain.interactor.BpInteractorImpl
import com.example.yecwms.util.LoadMore
import kotlinx.coroutines.launch
import com.example.yecwms.core.BaseViewModel

class BpListViewModel : BaseViewModel() {

    private val interactor: BpInteractor by lazy { BpInteractorImpl() }
    var listToDraw: MutableLiveData<ArrayList<Any>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoading: MutableLiveData<String> = MutableLiveData()
    var filterString: MutableLiveData<String> = MutableLiveData()
    var onlyWithDebts: MutableLiveData<Boolean> = MutableLiveData()

    var errorDebtLoading: MutableLiveData<String> = MutableLiveData()
    var totalDebtByShop: MutableLiveData<Double> = MutableLiveData()


    init {
        filterString.value = ""
        onlyWithDebts.value = true
    }

    fun setFilter(filter: String) {
        filterString.value = filter
    }

    fun getBp() {
        vmScope.launch {
            loading.postValue(true)
            Log.d("BPLIST1", "items.toString()")
            val listResult = ArrayList<Any>()

            val items = interactor.getAllBp(filter = filterString.value!!, bpType = GeneralConsts.BP_TYPE_CUSTOMER, onlyWithDebts = onlyWithDebts.value!!)

            if (items != null) {

                listResult.addAll(items)
                Log.d("BPLIST", items.toString())

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())
            } else {
                errorLoading.postValue(interactor.errorMessage)
                Log.d("BPLIST", "ERROR")
            }
            loading.postValue(false)
            listToDraw.postValue(listResult)
        }
    }

    fun getMoreBp(skipValue: Int) {
        vmScope.launch {
            val listResult =
                ArrayList<Any>(listToDraw.value?.filter { it !is LoadMore } ?: listOf())

            val items =
                interactor.getMoreBps(filter = filterString.value!!, skipValue = skipValue, bpType = GeneralConsts.BP_TYPE_CUSTOMER, onlyWithDebts = onlyWithDebts.value!!)

            if (items != null) {
                listResult.addAll(items)

                if (items.size >= GeneralConsts.MAX_PAGE_SIZE) listResult.add(LoadMore())

            } else {
                errorLoading.postValue(interactor.errorMessage)
            }
            listToDraw.postValue(listResult)
        }
    }




}