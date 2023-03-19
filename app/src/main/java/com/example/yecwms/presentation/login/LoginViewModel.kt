package com.example.yecwms.presentation.login

import androidx.lifecycle.MutableLiveData
import com.example.yecwms.domain.interactor.LoginInteractor
import com.example.yecwms.domain.interactor.LoginInteractorImpl
import kotlinx.coroutines.launch
import com.example.yecwms.core.BaseViewModel

class LoginViewModel : BaseViewModel() {
    private val interactor: LoginInteractor by lazy { LoginInteractorImpl() }
    var logged: MutableLiveData<Boolean> = MutableLiveData()
    var loginerror: MutableLiveData<String> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()

    fun requestLogin(companyDB: String, username: String, password: String) {
        vmScope.launch {
            loading.postValue(true)
            val isLogged = interactor.requestLogin(companyDB, username, password)
            if (isLogged) {
                logged.postValue(true)
            } else {
                loginerror.postValue(interactor.errorMessage)
            }
            loading.postValue(false)
        }
    }
}