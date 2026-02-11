package com.jotangi.cxms.ui.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.HisRecordListData
import com.jotangi.cxms.databinding.ItemRecordBinding

class RecordAdapter(
    private val itemList: MutableList<HisRecordListData>,
    private val itemClickListener: (Int, HisRecordListData) -> Unit // Click listener as a lambda
) : RecyclerView.Adapter<RecordAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HisRecordListData, clickListener: (Int, HisRecordListData) -> Unit) {
            binding.apply {
                tvDate.text = "${item.日期} 門診摘要"
                tvDoctor.text = item.counter.toString()
                tvDoctor.visibility = View.GONE
                clItem.setOnClickListener {
                    clickListener(position, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}