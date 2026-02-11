package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.databinding.AdapterMacularPigmentDetailBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apiresponse.GetMpodDataBean

class MacularPigmentDetailAdapter(private val list: List<GetMpodDataBean>) :
    RecyclerView.Adapter<MacularPigmentDetailAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterMacularPigmentDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: GetMpodDataBean) {

            binding.apply {

                tvNum.text = "${bean.lefteye}(L)/${bean.righteye}(R)"
                tvDate.text = WatchUtils.instance.clipTimeFormatSecond(bean.mpodStartTime)
                tvInputType.text = WatchUtils.instance.inputType(bean.dataType.toString())
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MacularPigmentDetailAdapter.ViewHolder {
        return ViewHolder(
            AdapterMacularPigmentDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MacularPigmentDetailAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}