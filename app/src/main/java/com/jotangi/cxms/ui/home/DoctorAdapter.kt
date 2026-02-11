package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.databinding.AdapterDoctorBinding


class DoctorAdapter(private val list: List<String>) :
    RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {

    var itemClick: (String) -> Unit = {}

    inner class ViewHolder(val binding: AdapterDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: String) {

            binding.tvDivisionName.text =
                if (group == ApiUrl.sleepWell) group else "$group 醫師"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDoctorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            itemClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}