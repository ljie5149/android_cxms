package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.DrugItemList
import com.jotangi.cxms.databinding.AdapterDrugDetailsBinding

class DrugDetailsAdapter(private val list: List<DrugItemList>) :
    RecyclerView.Adapter<DrugDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterDrugDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: DrugItemList) {

            binding.apply {

                tvDrugName.text = group.藥品簡稱 ?: ""
                tvDrugFrequency.text = group.頻率 ?: ""
                tvDeal.text = group.日份 ?: ""
                tvOutward.text = group.外觀 ?: ""
                tvDosage.text = group.劑量 ?: ""
                tvTotal.text = group.總量 ?: ""
                tvKnowledge.text = group.用藥須知 ?: ""
                tvByEffect.text = group.副作用 ?: ""
                tvSymptom.text = group.適應症 ?: ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDrugDetailsBinding.inflate(
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