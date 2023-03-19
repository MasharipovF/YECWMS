package com.example.yecwms.presentation.businesspartners.bpadress

import androidx.lifecycle.MutableLiveData
import com.example.yecwms.data.entity.businesspartners.BPAddresses
import com.example.yecwms.core.BaseViewModel

class BpAddressViewModel : BaseViewModel() {

    var listToDraw: MutableLiveData<ArrayList<BPAddresses>> = MutableLiveData()


    fun setAddresses(bpAddresses: List<BPAddresses>?) {
        listToDraw.value = bpAddresses as ArrayList<BPAddresses>
    }
}