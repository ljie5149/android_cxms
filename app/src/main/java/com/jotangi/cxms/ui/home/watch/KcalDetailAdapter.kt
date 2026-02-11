package com.jotangi.cxms.ui.home.watch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.databinding.AdapterKcalDetailBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apiresponse.GetKcalDataBean

class KcalDetailAdapter(private val list: List<GetKcalDataBean>) :
    RecyclerView.Adapter<KcalDetailAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterKcalDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: GetKcalDataBean) {

            binding.apply {

                with(bean) {

                    tvValue.text = KCAL
                    tvDate.text = WatchUtils.instance.clipTimeToYMD(startTime)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterKcalDetailBinding.inflate(
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