package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.apiresponse.HisOplistBean
import com.jotangi.cxms.databinding.AdapterHisOplistBinding

class HisOplistAdapter(private val list: ArrayList<HisOplistBean>) :
    RecyclerView.Adapter<HisOplistAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterHisOplistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: HisOplistBean) {
            binding.apply {
                with(bean) {
                    tvDivisionName.text = divisionname
                    tvDoctorName.text = doctorname
                    tvShiftName.text = shiftname
                    tvNowNo.text = nowno.toString()
                    tvNextNo.text = "/$nextno"

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterHisOplistBinding.inflate(
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