package com.example.yecwms.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yecwms.util.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

abstract class BaseViewModel : ViewModel() {

    val handler = CoroutineExceptionHandler { _, exception ->
        onException(exception)
    }

    val vmScope = viewModelScope + handler + Dispatchers.IO
    val updateList: LiveEvent<Int> = LiveEvent()
    val updateItem: LiveEvent<String> = LiveEvent()
    val removeItem: LiveEvent<String> = LiveEvent()
    val insertItem: LiveEvent<String> = LiveEvent()
    val errorItem: LiveEvent<String> = LiveEvent()
    val moveItem: LiveEvent<Pair<Int, Int>> = LiveEvent()
    var errorString: String = ""

    val connectionError: LiveEvent<Boolean> = LiveEvent()

    open fun onException(ex: Throwable) {
        connectionError.call()
        errorString = ex.message.toString()
        ex.printStackTrace()
    }



}