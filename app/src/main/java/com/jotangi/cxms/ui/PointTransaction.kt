package com.jotangi.cxms.ui

import com.jotangi.cxms.ui.Member.RewardItem

data class PointTransaction(
    val point_created_at: String,
    val point: Int,
    val point_type: Int,
    val store_name: String
) {
    fun toRewardItem(): RewardItem {
        return RewardItem(
            time = point_created_at,
            points = point,
            source = store_name
        )
    }
}

