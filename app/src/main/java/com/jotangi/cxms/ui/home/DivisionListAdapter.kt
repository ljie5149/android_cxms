package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.DivisionListData
import com.jotangi.cxms.databinding.AdapterDivisionListBinding


class DivisionListAdapter(private val list: List<DivisionListData>) :
    RecyclerView.Adapter<DivisionListAdapter.ViewHolder>() {

    var divisionClick: (DivisionListData) -> Unit = {}

    inner class ViewHolder(val binding: AdapterDivisionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DivisionListData) {
            binding.apply {
                tvDivision.text = item.division_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDivisionListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            divisionClick.invoke(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}