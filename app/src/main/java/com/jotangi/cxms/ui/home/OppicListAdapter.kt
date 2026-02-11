package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.Api.book.OppicListData
import com.jotangi.cxms.databinding.AdapterOppicListBinding

class OppicListAdapter(private val list: List<OppicListData>) :
    RecyclerView.Adapter<OppicListAdapter.ViewHolder>() {

    var delClick: (Int) -> Unit = {}

    inner class ViewHolder(val binding: AdapterOppicListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OppicListData) {
            Glide.with(binding.root.context)
                .load("${ApiConstant.IMAGE_URL}${item.op_pic}")
                .into(binding.ivOppic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterOppicListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(list[position])

        holder.binding.tvDel.setOnClickListener {
            delClick.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}