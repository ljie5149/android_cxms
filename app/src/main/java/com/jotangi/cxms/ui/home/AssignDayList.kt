package com.jotangi.cxms.ui.home

import com.jotangi.cxms.Api.book.apiresponse.PhysicianScheduleData

sealed class AssignDayList(val item_type: Int) {

    companion object {
        const val type_title = 0
        const val type_item = 1

        const val value_register ="可掛號"
        const val value_thing_day = "醫師請假"
        const val value_rest_day = "醫師休診"
        const val value_call = "限電話預約"
    }

    data class Title(val text: String) : AssignDayList(type_title)
    data class Item(val bean: PhysicianScheduleData) : AssignDayList(type_item)
}
