package com.example.yecwms.presentation.businesspartners.bpadd

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yecwms.core.BaseViewModel
import com.example.yecwms.core.GeneralConsts
import com.example.yecwms.core.ValidationStatusEnum
import com.example.yecwms.data.entity.businesspartners.BPAddresses
import com.example.yecwms.data.entity.businesspartners.BusinessPartners
import com.example.yecwms.data.entity.businesspartners.BusinessPartnersForPost
import com.example.yecwms.data.entity.masterdatas.BusinessPartnerGroups
import com.example.yecwms.data.entity.masterdatas.PriceLists
import com.example.yecwms.data.entity.series.Series
import com.example.yecwms.data.entity.series.SeriesForPost
import com.example.yecwms.domain.interactor.BpInteractor
import com.example.yecwms.domain.interactor.BpInteractorImpl
import com.example.yecwms.domain.interactor.MasterDataInteractor
import com.example.yecwms.domain.interactor.MasterDataInteractorImpl
import kotlinx.coroutines.launch

class BpAddViewModel() : BaseViewModel() {

    private val interactor: BpInteractor by lazy { BpInteractorImpl() }
    private val masterDataInteractor: MasterDataInteractor by lazy { MasterDataInteractorImpl() }

    var newBp: BusinessPartnersForPost =
        BusinessPartnersForPost(Series = GeneralConsts.SERIES_CUSTOMER_ADD)
    var insertedBp: MutableLiveData<BusinessPartners> = MutableLiveData()

    var listBpGroups: MutableLiveData<ArrayList<BusinessPartnerGroups>> = MutableLiveData()
    var listPricelists: MutableLiveData<ArrayList<PriceLists>> = MutableLiveData()
    var listSeries: MutableLiveData<ArrayList<Series>> = MutableLiveData()
    var chosenBpGroupType: MutableLiveData<String> = MutableLiveData()

    var loadingPage: MutableLiveData<Boolean> = MutableLiveData()
    var errorLoadingPage: MutableLiveData<Boolean> = MutableLiveData()

    var loadingPhoneCheck: MutableLiveData<Boolean> = MutableLiveData()
    var phoneCheckLoadingStatus: MutableLiveData<ValidationStatusEnum> = MutableLiveData()
    var phoneNumberExists: MutableLiveData<String> = MutableLiveData()

    var errorAddNewBP: MutableLiveData<String> = MutableLiveData()


    init {
        loadPage()
    }

    fun addNewBp() {
        vmScope.launch {
            loadingPage.postValue(true)
            val bp = interactor.addNewBp(newBp)

            if (bp != null) {
                insertItem.postValue(bp.CardCode)
                insertedBp.postValue(bp)
            } else errorAddNewBP.postValue(interactor.errorMessage)
            loadingPage.postValue(false)
        }
    }


    fun checkIfPhoneExists(phone: String?, bpType: String?) {
        vmScope.launch {
            loadingPhoneCheck.postValue(true)
            val phoneCheck = interactor.checkIfPhoneExists(phone, bpType)
            if (phoneCheck != null) {
                if (phoneCheck.isEmpty())
                    phoneCheckLoadingStatus.postValue(ValidationStatusEnum.VALIDATION_SUCCESS)
                else {
                    phoneCheckLoadingStatus.postValue(ValidationStatusEnum.VALIDATION_FAIL)
                    phoneNumberExists.postValue(phoneCheck)
                }
            } else {
                phoneCheckLoadingStatus.postValue(ValidationStatusEnum.ERROR_FROM_API)
            }
            loadingPhoneCheck.postValue(false)
        }
    }

    fun setNewBpData(
        cardName: String,
        phone: String,
        address: String,
        seriesCodePosition: Int,
        groupCodePosition: Int,
        priceListCodePosition: Int,
        limit: Double
    ) {
        val bpAddress = listOf<BPAddresses>(
            BPAddresses(
                addressName = address,
                addressType = GeneralConsts.BP_ADDRESS_TYPE_SHIPPING
            )
        )

        newBp.CardName = cardName
        newBp.CardType = GeneralConsts.BP_TYPE_CUSTOMER
        newBp.Series = listSeries.value?.get(seriesCodePosition)?.series
        newBp.Phone1 = phone
        newBp.CreditLimit = limit
        newBp.GroupCode = listBpGroups.value?.get(groupCodePosition)?.Code
        newBp.DefaultPriceListCode = listPricelists.value?.get(priceListCodePosition)?.priceListNo
        newBp.BPAddresses = bpAddress
        newBp.Currency = GeneralConsts.ALL_CURRENCY
        addNewBp()
    }

    fun loadPage() {
        vmScope.launch {
            loadingPage.postValue(true)
            Log.d("LOADPAGE", "loading")

            val series = masterDataInteractor.getSeries(
                SeriesForPost(
                    documentTypeParams = SeriesForPost.DocumentTypeParams(
                        document = GeneralConsts.SERIES_DOCUMENT_BP,
                        documentSubType = if (chosenBpGroupType.value == GeneralConsts.BP_TYPE_SUPPLIER) GeneralConsts.SERIES_DOCUMENTSUBTYPE_SUPPLIER
                        else GeneralConsts.SERIES_DOCUMENTSUBTYPE_CUSTOMER
                    )
                )
            )


            val groups =
                masterDataInteractor.getBpGroups(GeneralConsts.BP_GROUP_TYPE_CUSTOMER) // in case we would like to add purchse orders facility we can use BP_TYPE_SUPPLIER
            val priceLists = masterDataInteractor.getPriceLists()
            if (groups != null && priceLists != null && series != null) {
                listBpGroups.postValue(groups as ArrayList<BusinessPartnerGroups>)
                listPricelists.postValue(priceLists as ArrayList<PriceLists>)
                listSeries.postValue(series as ArrayList<Series>)

                errorLoadingPage.postValue(false)
            } else {
                errorLoadingPage.postValue(true)
            }
            loadingPage.postValue(false)
        }
    }

}