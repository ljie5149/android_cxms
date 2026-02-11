package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.ItemListBean
import com.jotangi.cxms.databinding.AdapterWaitCheckBinding

class WaitCheckAdapter(private val list: List<ItemListBean>) :
    RecyclerView.Adapter<WaitCheckAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterWaitCheckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: ItemListBean) {

            binding.apply {

                tvDate.text = group.日期.toString()
                tvCount.text = group.項次.toString()
                tvRoom.text = group.科室.toString()
                tvContent.text = group.內容.toString()
                tvState.text = group.狀態.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterWaitCheckBinding.inflate(
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