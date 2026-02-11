package com.jotangi.cxms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.Api.book.apiresponse.GetCouponDataBeen
import com.jotangi.cxms.databinding.AdapterFitnessMirrorCouponListBinding

class FitnessMirrorCouponListAdapter(
    private val mData: List<GetCouponDataBeen>,
) :
    RecyclerView.Adapter<FitnessMirrorCouponListAdapter.ViewHolder>() {

    var watchItemClick: (GetCouponDataBeen) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FitnessMirrorCouponListAdapter.ViewHolder {
        return ViewHolder(
            AdapterFitnessMirrorCouponListBinding
                .inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
        )
    }


    override fun onBindViewHolder(
        holder: FitnessMirrorCouponListAdapter.ViewHolder,
        position: Int
    ) {
        holder.bind(mData[position])

        holder.binding.couponButtonUse.setOnClickListener {
            watchItemClick.invoke(mData[position])
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }


    inner class ViewHolder(val binding: AdapterFitnessMirrorCouponListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: GetCouponDataBeen) {
            binding.apply {
                with(bean) {
                    couponTitleTextview.text = coupon_name
                    couponContentTextview.text = coupon_description
                    couponStarDateTextview.text = coupon_startdate
                    couponEndDateTextview.text = coupon_enddate
                    couponPointTextview.text = "所需點數: ${coupon_point}"
                    Glide.with(root).load(ApiConstant.IMAGE_URL + coupon_picture).into(couponImageview)
                }
            }
        }
    }
}


