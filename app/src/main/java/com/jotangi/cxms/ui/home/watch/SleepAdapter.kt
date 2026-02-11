package com.jotangi.cxms.ui.home.watch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.R
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.model.SleepData

class SleepAdapter(private val mData: List<SleepData>) :
    RecyclerView.Adapter<SleepAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sleep, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvSleepHr: TextView = v.findViewById(R.id.tv_sleep_hr)
        var tvSleepMin: TextView = v.findViewById(R.id.tv_sleep_min)
        var tvSleepDate: TextView = v.findViewById(R.id.tv_sleep_date)

        fun bind(model: SleepData) {

            val minTotal =
                model.lightSleepTotal.toString().toInt() + model.deepSleepTotal.toString().toInt()

            tvSleepHr.text = (minTotal / 60).toString()
            tvSleepMin.text = (minTotal % 60).toString()
            tvSleepDate.text = WatchUtils.instance.clipTimeFormatSecond(model.startTime)
        }
    }
}