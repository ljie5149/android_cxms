package com.jotangi.cxms.ui.mylittlemin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.BookingInfoData
import com.jotangi.cxms.R

class MesageAdapter(var data: List<BookingInfoData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_hb, parent, false)
        return MesageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MesageViewHolder)
            holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MesageViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var txt: TextView = v.findViewById(R.id.ibt1)
        fun bind(model: BookingInfoData) {
            val otherStrings = ArrayList<String>()
            val strs = model.message_log?.split(">>")?.toTypedArray()//找"/"來分割他
            Log.i("Values", "value=$strs.")
            val lstValues: List<String> =//存取list
                model.message_log?.substring(8)?.split(">>")?.toTypedArray()!!
                    .map { it -> it.trim() }
            lstValues.forEach { it ->

                val itResult = it
                Log.i("Values", itResult)
                otherStrings.add(it)
            }
            for (i in otherStrings.indices) {
                txt.text = otherStrings[i].toString()
            }


        }
    }
}