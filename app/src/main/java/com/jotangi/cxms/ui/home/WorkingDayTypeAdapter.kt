package com.jotangi.cxms.ui.home

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class WorkingDayTypeAdapter : JsonDeserializer<WorkingDay> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): WorkingDay {
        val jsonObject = json.asJsonObject

        val workingdate = jsonObject.get("workingdate").asString
        val workingtype = jsonObject.get("workingtype").asString
        val timeperiodElement = jsonObject.get("timeperiod")

        val timeperiod: List<TimePeriod>? = when {
            timeperiodElement.isJsonArray -> {
                context.deserialize(timeperiodElement, object : TypeToken<List<TimePeriod>>() {}.type)
            }
            timeperiodElement.isJsonPrimitive && timeperiodElement.asString.isEmpty() -> {
                emptyList() // Treat empty string as an empty list
            }
            else -> null // Default for unexpected cases
        }

        return WorkingDay(workingdate, workingtype, timeperiod)
    }
}
