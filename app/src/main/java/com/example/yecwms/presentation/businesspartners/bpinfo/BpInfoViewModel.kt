package com.example.yecwms.presentation.businesspartners.bpinfo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yecwms.data.entity.businesspartners.BPAddresses
import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.data.entity.items.ItemWarehouseInfo
import com.example.yecwms.domain.interactor.BpInteractor
import com.example.yecwms.domain.interactor.BpInteractorImpl
import kotlinx.coroutines.launch
import com.example.yecwms.core.BaseViewModel

class BpInfoViewModel : BaseViewModel() {

    private val interactor: BpInteractor by lazy { BpInteractorImpl() }
    var bpInfo: MutableLiveData<BusinessPartners> = MutableLiveData()
    var bpDebtByShop: MutableLiveData<Double> = MutableLiveData()
    var listToDraw: MutableLiveData<ArrayList<ItemWarehouseInfo>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoading: MutableLiveData<String> = MutableLiveData()

    var currentWhsCode: MutableLiveData<String> = MutableLiveData()
    var warehousesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorWarehousesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var warehousesList: MutableLiveData<ArrayList<Any>> = MutableLiveData()


    fun getBpInfo(bpCode: String) {
        vmScope.launch {
            loading.postValue(true)
            val item = interactor.getBpInfo(bpCode)
            Log.d("ITEMINFO", "Passed string $bpCode \n ${item.toString()}")
            if (item != null) {
                bpInfo.postValue(item)
            } else errorLoading.postValue(interactor.errorMessage)
            loading.postValue(false)
        }
    }


    fun getBpAddresses(): List<BPAddresses>? {
        return bpInfo.value?.BPAddresses
    }

}