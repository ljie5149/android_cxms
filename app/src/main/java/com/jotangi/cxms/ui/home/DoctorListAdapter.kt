package com.jotangi.cxms.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.Api.book.PhysicianListData
import com.jotangi.cxms.databinding.AdapterDoctorListBinding

class DoctorListAdapter(private val list: List<PhysicianListData>) :
    RecyclerView.Adapter<DoctorListAdapter.ViewHolder>() {

    var doctorClick: (Int) -> Unit = {}

    inner class ViewHolder(val binding: AdapterDoctorListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhysicianListData, context: Context) {
            binding.apply {

                Glide.with(context)
                    .load("${ApiConstant.IMAGE_URL}${item.physician_picture}")
                    .into(binding.ivDoctorMugShot)
                tvDoctorName.text = "${item.doctor_name} ${item.job_title}"
                tvDoctorSkill.text = "經歷：${item.education}\n"
                tvDoctorSpecialty.text = "專長：${item.expertise}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDoctorListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(list[position], holder.itemView.context)

        holder.binding.tvReserveDoctor.setOnClickListener {
            doctorClick.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}