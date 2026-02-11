package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.databinding.AdapterDividionNameBinding

class DivisionNameAdapter(private val nameList: ArrayList<String>) :
    RecyclerView.Adapter<DivisionNameAdapter.ViewHolder>() {

    var nameItemClick: (Int) -> Unit = {}

    inner class ViewHolder(private val binding: AdapterDividionNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: String) {
            binding.tvDivisionName.text = group
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDividionNameBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(nameList[position])

        holder.itemView.setOnClickListener {
            nameItemClick.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return nameList.size
    }
}