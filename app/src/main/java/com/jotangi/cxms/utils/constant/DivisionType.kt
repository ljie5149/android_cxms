package com.jotangi.cxms.utils.constant

enum class DivisionType {
    DIVISION, VIDEO, RESERVE, PROGRESS, CODE
}

enum class QrType(val value: String) {
    Fragment("Fragment"), JsonValue("JsonValue"),
    StoreManger("StoreManger"), Number("Number"),
    SerialNumber("SerialNumber")
}

enum class CheckType(val value: String) {
    DrawBlood("-1"),
    Ecg("2"),
    Service("99"),
    XRay("0"),
    Ultra("1"),
    Gastroscopy("3"),
}