package com.jotangi.cxms.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.apiresponse.SleepWellWorkingDayBean
import com.jotangi.cxms.databinding.AdapterSleepWellBinding
import com.jotangi.cxms.utils.SleepWellAdapterData

class SleepWellAdapter(private val list: List<SleepWellAdapterData>) :
    RecyclerView.Adapter<SleepWellAdapter.ViewHolder>() {

    var itemClick: (String, List<SleepWellWorkingDayBean>) -> Unit = {
            time: String, list: List<SleepWellWorkingDayBean> -> }

    inner class ViewHolder(val binding: AdapterSleepWellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: SleepWellAdapterData) {

            binding.apply {

                tvTitleDay.text = data.日期

                tvTitleAm.apply {

                    if (data.amList.isEmpty()) {

                        text = "不可預約"
                        setTextColor(Color.RED)
                    } else {

                        text = "可預約(${data.amList.size})"
                        setTextColor(Color.BLACK)
                    }

                    setOnClickListener {

                        if (data.amList.isEmpty()) return@setOnClickListener

                        itemClick("上午", data.amList)
                    }
                }

                tvTitlePm.apply {

                    if (data.pmList.isEmpty()) {

                        text = "不可預約"
                        setTextColor(Color.RED)
                    } else {

                        text = "可預約(${data.pmList.size})"
                        setTextColor(Color.BLACK)
                    }

                    setOnClickListener {

                        if (data.pmList.isEmpty()) return@setOnClickListener

                        itemClick("下午", data.pmList)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        AdapterSleepWellBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}