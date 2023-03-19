package com.example.yecwms.presentation.main

import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.data.Preferences
import com.example.yecwms.data.entity.masterdatas.CompanyInfo
import com.example.yecwms.data.entity.masterdatas.Currencies
import com.example.yecwms.domain.interactor.*
import com.example.yecwms.util.Utils
import kotlinx.coroutines.launch

class MainActivityViewModel : BaseViewModel() {

    private val masterDataInteractor: MasterDataInteractor by lazy { MasterDataInteractorImpl() }


    var currencies: MutableLiveData<List<Currencies>> = MutableLiveData()
    var loading: MutableLiveData<Int> = MutableLiveData(0)
    var errorLoading: MutableLiveData<String> = MutableLiveData()

    var companyInfo: MutableLiveData<CompanyInfo> = MutableLiveData()

    init {
        getCompanyInfo()
        getCurrencies()
    }


    fun getCurrencies() {
        vmScope.launch {
            loading.postValue(loading.value!! + 1)
            val response = masterDataInteractor.getCurrencies(Utils.getCurrentDateinUSAFormat())
            if (response != null) {
                Preferences.putCurrenciesIntoPref(response, Utils.getCurrentDateinUSAFormat())
                currencies.postValue(response)
            } else
                errorItem.postValue("Ошибка при загрузке: ${masterDataInteractor.errorMessage}")
            loading.postValue(loading.value!! - 1)
        }
    }

    fun getCompanyInfo() {
        vmScope.launch {
            loading.postValue(loading.value!! + 1)

            val response = masterDataInteractor.getCompanyInfo()
            if (response != null) {
                Preferences.totalsAccuracy = response.totalsAccuracy
                Preferences.pricesAccuracy = response.priceAccuracy
                Preferences.localCurrency = response.localCurrency
                Preferences.systemCurrency = response.systemCurrency
                Preferences.isDirectRateCalculation = response.isDirectRateCalculation
                companyInfo.postValue(response)
            } else {
                errorItem.postValue("Ошибка при загрузке данных компании: ${masterDataInteractor.errorMessage}")
            }
            loading.postValue(loading.value!! - 1)

        }
    }


}