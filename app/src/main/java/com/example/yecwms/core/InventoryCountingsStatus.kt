package com.example.yecwms.core

enum class InventoryCountingsStatus(val status: String) {
    NOT_PROCEEDED("N"),
    PROCEEDED_WITH_TSD("Y"),
    DOCUMENT_POSTED("F")
}