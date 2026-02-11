package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.databinding.AdapterDrugDateBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils

class DrugDateAdapter(private val list: List<String>) :
    RecyclerView.Adapter<DrugDateAdapter.ViewHolder>() {

    var itemClick: (Int) -> Unit = {}

    inner class ViewHolder(val binding: AdapterDrugDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            binding.tv.text = WatchUtils.instance.changeTimeChina(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDrugDateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

        holder.binding.tv.setOnClickListener {
            itemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}