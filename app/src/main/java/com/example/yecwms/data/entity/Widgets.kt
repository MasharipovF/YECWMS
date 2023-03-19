package com.example.yecwms.data.entity

import com.example.yecwms.core.WidgetsEnum

data class Widgets(
    val widget: WidgetsEnum,
    var isVisible: Boolean = true
)
