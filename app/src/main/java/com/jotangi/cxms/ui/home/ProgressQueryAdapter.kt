package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.CheckBean
import com.jotangi.cxms.databinding.AdapterProgressQueryBinding

class ProgressQueryAdapter(private val list: List<CheckBean>) :
    RecyclerView.Adapter<ProgressQueryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterProgressQueryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: CheckBean) {

            binding.apply {
                tvTitle.text = group.檢驗項目
                tvNum.text = group.我的號碼
                tvNow.text = group.目前進度
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterProgressQueryBinding.inflate(
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