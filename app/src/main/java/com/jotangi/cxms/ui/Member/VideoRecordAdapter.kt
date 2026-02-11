package com.jotangi.cxms.ui.Member

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.Api.book.VideoRecordListData
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.AdapterVideoRecordBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.squareup.picasso.Picasso

class VideoRecordAdapter(
    private val context: Context,
    private val list: ArrayList<VideoRecordListData>
) : RecyclerView.Adapter<VideoRecordAdapter.ViewHolder>() {

    var dataItemClick: (String) -> Unit = {}
    var enterItemClick: (Int) -> Unit = {}
    var cancelItemClick: (Int) -> Unit = {}

    inner class ViewHolder(val binding: AdapterVideoRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: VideoRecordListData, position: Int) {

            binding.apply {

                Picasso.get().load(ApiConstant.IMAGE_URL + data.physician_picture).into(ivAvrIcon)
                tvClinicContent.text = "${data.doctor_name} 醫師"

                tvTimeContent.text =
                    "${data.reserve_date} ${WatchUtils.instance.clipHmsTohm(data.reserve_time!!)}"
                tvNameContent.text = data.member_name

                when (data.reserve_status) {

                    "0" -> {

                        tvStatusButton.text = "尚未成立"
                        tvStatusButton.isEnabled = false
                        tvStatusButton.setBackgroundColor(context.getColor(R.color.gray_background))
                        tvPersonTitle.visibility = View.GONE
                        tvCancelButton.visibility = View.GONE
                    }

                    "1","4","6" -> {

                        tvStatusButton.text = "進入諮詢室"
                        tvStatusButton.isEnabled = true
                        tvStatusButton.setBackgroundColor(context.getColor(R.color.skin_background))
                        tvStatusButton.setOnClickListener {
                            enterItemClick.invoke(position)
                        }

                        tvCancelButton.visibility = View.VISIBLE
                        tvCancelButton.setOnClickListener {
                            cancelItemClick.invoke(position)
                        }

                        tvPersonTitle.visibility = View.VISIBLE
                        tvPersonTitle.setOnClickListener {
                            dataItemClick.invoke(data.booking_no!!)
                        }
                    }



                    "2" -> {

                        tvStatusButton.text = "預約已取消"
                        tvStatusButton.isEnabled = false
                        tvStatusButton.setBackgroundColor(context.getColor(R.color.gray_background))

                        tvPersonTitle.visibility = View.GONE
                        tvCancelButton.visibility = View.GONE
                    }

                    "8" -> {

                        tvStatusButton.text = "視訊結束，諮詢備忘錄"
                        tvStatusButton.isEnabled = true
                        tvStatusButton.setBackgroundColor(context.getColor(R.color.gray_background))
                        tvStatusButton.setOnClickListener {
                            enterItemClick.invoke(position)
                        }

                        tvPersonTitle.visibility = View.GONE
                        tvCancelButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterVideoRecordBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}