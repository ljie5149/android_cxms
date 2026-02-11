package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.apiresponse.PhysicianScheduleData
import com.jotangi.cxms.databinding.AdapterAssignDayItemBinding
import com.jotangi.cxms.databinding.AdapterAssignDayTitleBinding

class AssignDayAdapter : ListAdapter<AssignDayList, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<AssignDayList>() {

        override fun areItemsTheSame(
            oldItem: AssignDayList,
            newItem: AssignDayList
        ) = oldItem.item_type == newItem.item_type

        override fun areContentsTheSame(
            oldItem: AssignDayList,
            newItem: AssignDayList
        ) = oldItem == newItem
    }
) {

    var registerClick: (PhysicianScheduleData) -> Unit = {}
    var callClick: () -> Unit = {}

    inner class ViewHolderTitle(
        private val binding: AdapterAssignDayTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AssignDayList.Title) {
            binding.tvTitle.text = item.text
        }
    }

    inner class ViewHolderItem(
        private val binding: AdapterAssignDayItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhysicianScheduleData) {

            binding.apply {

                tvName.text = if (item.代班醫師名 == "無")
                    item.醫師名.toString() else item.代班醫師名.toString()

                tvType.text = if (item.科別 == "特別門診") "特別門診" else ""

                val valueTxt = if (item.可掛號否 == "Y")
                    AssignDayList.value_register
                else
                    when (item.限數) {
                        "-1" -> item.備註.toString()
                        "-2" -> item.就診參考序號.toString()
                        else -> ""
                    }
//                    when (item.就診參考序號) {
//                        AssignDayList.value_thing_day -> AssignDayList.value_thing_day
//                        AssignDayList.value_rest_day -> AssignDayList.value_rest_day
//                        else -> AssignDayList.value_call
//                    }

                tvValue.text = valueTxt

                tvValue.setOnClickListener {

                    when (valueTxt) {
                        AssignDayList.value_register -> registerClick(item)
                        AssignDayList.value_call -> callClick()
                        else -> return@setOnClickListener
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {

        return when (viewType) {

            AssignDayList.type_title -> ViewHolderTitle(
                AdapterAssignDayTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> ViewHolderItem(
                AdapterAssignDayItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when (val item = getItem(position)) {
            is AssignDayList.Title -> (holder as ViewHolderTitle).bind(item)
            is AssignDayList.Item -> (holder as ViewHolderItem).bind(item.bean)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).item_type
    }
}