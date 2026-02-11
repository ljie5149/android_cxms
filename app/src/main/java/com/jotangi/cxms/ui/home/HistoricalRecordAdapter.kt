package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.apiresponse.GetStoreApplyListBeen
import com.jotangi.cxms.databinding.AdapterHistoricalRecordBinding

class HistoricalRecordAdapter(
    private val mData: List<GetStoreApplyListBeen>,
) : RecyclerView.Adapter<HistoricalRecordAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoricalRecordAdapter.ViewHolder {
        return ViewHolder(
            AdapterHistoricalRecordBinding
                .inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
        )
    }

    override fun onBindViewHolder(
        holder: HistoricalRecordAdapter.ViewHolder,
        position: Int
    ) {
        holder.bind(mData[position])

    }

    override fun getItemCount(): Int {
        return mData.size
    }


    inner class ViewHolder(val binding: AdapterHistoricalRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bean: GetStoreApplyListBeen) {
            binding.apply {
                with(bean) {
                    historyTimeTextview.text = apply_date
                    historyNameTextview.text = coupon_name
                    historyPointTextview.text = coupon_point
                }
            }

        }

    }
}