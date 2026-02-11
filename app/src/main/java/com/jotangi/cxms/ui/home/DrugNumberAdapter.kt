package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.DrugInfoBean
import com.jotangi.cxms.databinding.AdapterDrugNumberBinding

class DrugNumberAdapter(private val list: List<DrugInfoBean>) :
    RecyclerView.Adapter<DrugNumberAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterDrugNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: DrugInfoBean) {
            binding.tvId.text = group.領藥號.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDrugNumberBinding.inflate(
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