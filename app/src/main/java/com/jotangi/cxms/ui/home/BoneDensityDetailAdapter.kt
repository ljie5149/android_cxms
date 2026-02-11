package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.databinding.AdapterBoneDensityDetailBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apiresponse.GetBmdDataBean

class BoneDensityDetailAdapter(private val list: List<GetBmdDataBean>) :
    RecyclerView.Adapter<BoneDensityDetailAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterBoneDensityDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: GetBmdDataBean) {

            binding.apply {
                tvNum.text = bean.TScore
                tvDate.text = WatchUtils.instance.clipTimeFormatSecond(bean.startTime)
                tvInputType.text = WatchUtils.instance.inputType(bean.dataType.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterBoneDensityDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}