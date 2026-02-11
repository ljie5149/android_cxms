package com.jotangi.cxms.ui.home

data class TreatmentItem(
    val title: String,
    val price: Int,
    val appointments: List<Appointment2>
)

data class Appointment2(
    val dateTime: String
)