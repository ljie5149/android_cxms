package com.jotangi.cxms.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.PhysicianTimeperiod
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.AdapterTimeListBinding
import com.jotangi.cxms.utils.DateTimeUtil

class TimeListAdapter(private val list: List<PhysicianTimeperiod>) :
    RecyclerView.Adapter<TimeListAdapter.ViewHolder>() {

    var timeClick: (Int) -> Unit = {}
    var changeNum = -1
    var reserveTime = ""
    var reserveEndtime = ""
    var price = ""

    inner class ViewHolder(val binding: AdapterTimeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhysicianTimeperiod, position: Int) {

            binding.apply {

                tvAtlTime.text = DateTimeUtil.instance.clipHmsTohm(item.starttime!!)

                if (changeNum == position) {
                    itemView.setBackgroundColor(binding.root.context.getColor(R.color.blue))
                    tvAtlTime.setTextColor(Color.WHITE)
                }else {
                    itemView.setBackgroundColor(Color.WHITE)
                    tvAtlTime.setTextColor(Color.BLACK)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterTimeListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(list[position], position)

        holder.itemView.setOnClickListener {

            changeNum = position
            reserveTime = list[position].starttime!!
            reserveEndtime = list[position].endtime!!
            price = list[position].price!!
            timeClick.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}