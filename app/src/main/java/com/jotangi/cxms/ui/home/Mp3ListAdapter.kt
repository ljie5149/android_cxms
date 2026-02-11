package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.Opmp3ListData
import com.jotangi.cxms.databinding.AdapterMp3ListBinding

class Mp3ListAdapter(private val list: List<Opmp3ListData>) :
    RecyclerView.Adapter<Mp3ListAdapter.ViewHolder>() {

    var delClick: (Int) -> Unit = {}
    var playClick: (Int) -> Unit = {}

    inner class ViewHolder(val binding: AdapterMp3ListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Opmp3ListData) {

            binding.apply {
                tvSid.text = item.oid
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterMp3ListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(list[position])

        holder.binding.tvDel.setOnClickListener {
            delClick.invoke(position)
        }

        holder.binding.ivPlay.setOnClickListener {
            playClick.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}