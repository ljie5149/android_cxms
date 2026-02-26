package com.jotangi.cxms.ui.home

data class WorkingDayResponse(
    val status: String,
    val code: String,
    val responseMessage: List<WorkingDay>
)

data class TimePeriod(
    val starttime: String,
    val endtime: String,
    val reservation_limit: String,
    val count: Int
)

data class WorkingDay(
    val workingdate: String,
    val workingtype: String,
    val timeperiod: List<TimePeriod>?
)
