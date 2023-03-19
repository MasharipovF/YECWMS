package com.example.yecwms.core

object GeneralConsts {

    const val TIMER_MS_IN_FUTURE: Long = 700L
    const val TIMER_INTERVAL: Long = 3000L

    const val WAREHOUSE_BARCODE_PREFIX="WHS-"
    const val BINLOCATION_BARCODE_PREFIX="BIN-"

    const val PRINTER_TIMEOUT = 5000
    const val PRINTER_CHARSET = "Cp866"
    const val PRINTER_CHARSET_CODE = 17


    const val MAX_PAGE_SIZE = 20
    const val MAX_PAGE_SIZE_CROSSJOIN = 20


    //TODO need to define all this at first start up, and move necessary to Preferences

    const val NO_DOCUMENT_ENTRY = -1L

    //const val PRIMARY_CURRENCY = "USD"
    //const val SECONDARY_CURRENCY = "UZS"
    const val ALL_CURRENCY = "##"
    const val DEF_WHS_CODE = "DEFAULTWHS"
    const val DEF_CARD_CODE = "*"
    const val DEF_PRICELIST_CODE = 1

    //const val DEFAULT_BP_CODE = "00000"
    const val SERIES_ITEM_ADD = 72
    const val SERIES_CUSTOMER_ADD = 70

    //const val CASH_ACCOUNT_USD = "511001"
    //const val CASH_ACCOUNT_UZS = "511001"
    const val PHONE_NUMBER_LENGTH = 9
    const val BARCODE_LENGTH = 13

    const val T_YES = "tYES"
    const val T_NO = "tNO"
    const val YES = "Y"
    const val NO = "N"


    const val PASSED_ITEM_CODE = "ITEMCODE"
    const val PASSED_CARD_CODE = "CARDCODE"
    const val PASSED_BP_ADDRESSES = "BP_ADDRESSES"
    const val PASSED_BP = "BUSINESSPARTNERS"
    const val PASSED_SALESORDER_DOCENTRY = "SALESORDER_DOCENTRY"
    const val PASSED_PURCHASEINVOICE_DOCENTRY = "PURCHASEINVOICE_DOCENTRY"
    const val PASSED_PURCHASEORDER_DOCENTRY = "PURCHASEORDER_DOCENTRY"
    const val PASSED_RETURN_TO_SUPPLIER_REQUEST_DOCENTRY = "RETURN_TO_SUPPLIER_REQUEST_DOCENTRY"
    const val PASSED_RETURN_TO_CLIENT_REQUEST_DOCENTRY = "RETURN_TO_CLIENT_REQUEST_DOCENTRY"
    const val PASSED_INVOICE_DOCENTRY = "INVOICE_DOCENTRY"
    const val PASSED_INVENTORY_REQUEST_DOCENTRY = "INVENTORY_REQUEST_DOCENTRY"
    const val PASSED_INVENTORY_TRANSFER_DOCENTRY = "INVENTORY_TRANSFER_DOCENTRY"
    const val PASSED_INVENTORY_COUNTINGS_DOCENTRY = "INVENTORY_COUNTINGS_DOCENTRY"
    const val PASSED_INVENTORY_TRANSFER = "INVENTORY_TRANSFER"
    const val PASSED_PAYMENTS_DOCENTRY = "PAYMENTS_DOCENTRY"
    const val PASSED_PAYMENT = "PAYMENT"

    const val FRAGMENT_BACKSTACK = "BACKSTACK"

    const val BP_TYPE_CUSTOMER = "cCustomer"
    //const val BP_TYPE_CUSTOMER_SML = "C"
    const val BP_TYPE_LID = "cLid"
    //const val BP_TYPE_LID_SML = "L"
    const val BP_TYPE_SUPPLIER = "cSupplier"
    //const val BP_TYPE_SUPPLIER_SML = "S"


    const val BP_ADDRESS_TYPE_SHIPPING = "bo_ShipTo"
    const val BP_GROUP_TYPE_CUSTOMER = "bbpgt_CustomerGroup"
    const val BP_GROUP_TYPE_SUPPLIER = "bbpgt_VendorGroup"
    const val BP_DEFAULT_LIMIT = 0

    const val DOC_STATUS_OPEN = "bost_Open"
    const val DOC_STATUS_CLOSED = "bost_Close"
    const val DOC_STATUS_DELIVERED = "bost_Delivered"

    const val INVENTORY_COUNTING_STATUS_OPEN = "cdsOpen"
    const val INVENTORY_COUNTING_CLOSED = "cdsClosed"

    const val DOC_STATUS_OPEN_NAME = "Открыт"
    const val DOC_STATUS_CLOSED_NAME = "Закрыт"

    const val DOC_CURRENCY_UZS = "UZS"
    const val DOC_CURRENCY_USD = "USD"

    const val INVOICE_TYPE_CREDTNOTE = "it_CredItnote"

    const val SERIES_DOCUMENT_BP = "2"
    const val SERIES_DOCUMENTSUBTYPE_CUSTOMER = "C"
    const val SERIES_DOCUMENTSUBTYPE_SUPPLIER = "S"

    const val SERIES_DOCUMENT_ITEMS = "4"
    const val SERIES_DOCUMENTSUBTYPE_ITEMS = "--"

    const val DISCOUNT_NO = "V"
    const val DISCOUNT_TYPE_VOLUME_PERIOD = "V"
    const val DISCOUNT_TYPE_GROUP = "G"
    const val DISCOUNT_TYPE_SPECIAL_PRICES = "S"

    const val BIN_ACTION_TYPE_FROM = "batFromWarehouse"
    const val BIN_ACTION_TYPE_TO = "batToWarehouse"
    const val SYSTEM_BIN_LOCATION = "SYSTEM_BIN_LOCATION"

    const val OBJECT_CODE_INVENTORY_TRANSFER=67
    const val OBJECT_CODE_PURCHASE_DELIVERY="oPurchaseDeliveryNotes"
    const val OBJECT_CODE_DELIVERY="oDeliveryNotes"
    const val OBJECT_CODE_PURCHASE_INVOICE="oPurchaseInvoices"
    const val OBJECT_CODE_PURCHASE_RETURNS="oPurchaseReturns"
    const val OBJECT_CODE_RETURNS="oReturns"

    const val MANAGED_BY_BATCH = "BATCH"
    const val MANAGED_BY_SERIES = "SERIES"

}