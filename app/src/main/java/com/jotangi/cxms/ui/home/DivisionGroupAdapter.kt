package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.databinding.AdapterDividionGroupBinding

class DivisionGroupAdapter(private val groupList: ArrayList<String>) :
    RecyclerView.Adapter<DivisionGroupAdapter.ViewHolder>() {

    var groupItemClick: (String) -> Unit = {}

    inner class ViewHolder(private val binding: AdapterDividionGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: String) {
            binding.tvDivisionGroup.text = group
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDividionGroupBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(groupList[position])

        holder.itemView.setOnClickListener {
            groupItemClick.invoke(groupList[position])
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}