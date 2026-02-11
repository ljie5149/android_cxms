package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.book.apiresponse.ComplexRegisterListData
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationListBean
import com.jotangi.cxms.databinding.AdapterMyReserveBinding
import com.jotangi.cxms.utils.Const
import com.jotangi.cxms.utils.DateTimeUtil
import com.jotangi.cxms.utils.gone
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.visible

class MyReserveAdapter(
    private val list: List<ComplexRegisterListData>,
) : RecyclerView.Adapter<MyReserveAdapter.ViewHolder>() {

    var questionClick: (HisRegistrationListBean) -> Unit = {}
    var cancelClick: (ComplexRegisterListData) -> Unit = {}

    inner class ViewHolder(val binding: AdapterMyReserveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ComplexRegisterListData) {

            binding.apply {

                if (item.type == Const.dataRegister) {

                    tvDate.text = DateTimeUtil.instance.chinaDate(
                        WatchUtils.instance.ymdChinaToWestern(
                            item.register?.日期.toString()
                        )
                    )
                    tvTime.text = item.register?.班別
                    tvDivision.text = item.register?.科別
                    tvDoctor.text = item.register?.醫師名
                    tvQuestion.visible()
                } else {

                    item.sleepWell?.also {

                        tvDate.text = DateTimeUtil.instance.ymdToChinaYmdw(
                            it.reserveDate.toString()
                        )
                        tvTime.text = "${it.reserveStarttime}-${it.reserveEndtime}"
                        tvDivision.text = "舒眠體驗館"
                        tvDoctor.text = ""
                        tvQuestion.gone()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyReserveAdapter.ViewHolder {
        return ViewHolder(
            AdapterMyReserveBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: MyReserveAdapter.ViewHolder,
        position: Int
    ) {
        holder.bind(list[position])

        holder.binding.tvQuestion.setOnClickListener {
            questionClick(list[position].register!!)
        }
        holder.binding.tvCancel.setOnClickListener {
            cancelClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}