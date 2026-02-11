package com.jotangi.cxms.ui.Member

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentRewardsBinding
import com.jotangi.cxms.databinding.ItemRewardBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import kotlinx.coroutines.launch

class RewardsFragment : BaseFragment() {

    private lateinit var binding: FragmentRewardsBinding
    private lateinit var adapter: RewardsAdapter
    override fun getToolBar(): ToolbarBinding = binding!!.toolbar

    private var isShowingReceived = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMarketChangePointTitle("點數紀錄")
        initObserve()

        // Initially, load received points
        isShowingReceived = true
//        updateButtonStyles()
        fetchData(isShowingReceived)

        binding.toolbar.ivToolBack.setOnClickListener {
            findNavController().navigate(R.id.memberFragment)
        }
        lifecycleScope.launch {
            val pointResponse = apiBook.getPoint()
            val pointData = pointResponse.data.firstOrNull()
            var totalPointShow = pointData?.totalPoint ?: "0"
            binding.tvTotalPoints.text = totalPointShow + "點"
        }

        adapter = RewardsAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

//        binding.btnReceived.setOnClickListener {
//            isShowingReceived = true
//            updateButtonStyles()
//            fetchData(true)
//        }
//
//        binding.btnRedeemed.setOnClickListener {
//            isShowingReceived = false
//            updateButtonStyles()
//            fetchData(false)
//        }
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("獲得"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("核銷"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isShowingReceived = tab.position == 0
                fetchData(isShowingReceived)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initObserve() {
        bookViewModel.memberPointList.observe(viewLifecycleOwner) { result ->
            Log.d("micCheckUUI", result.toString())

            // Set RecyclerView data with filter: only show items with non-zero points
            val rewardItems = result.map { it.toRewardItem() }.filter { it.points != 0 }
            adapter.submitList(rewardItems)
        }
    }


    private fun fetchData(isReceived: Boolean) {
        lifecycleScope.launch {
            bookViewModel.getMemberPointList(if (isReceived) 1 else 0)
        }
    }

//   0 private fun updateButtonStyles() {
//        binding.btnReceived.setTextColor(if (isShowingReceived) Color.parseColor("#63C5DA") else Color.parseColor("#000000"))
//        binding.btnRedeemed.setTextColor(if (!isShowingReceived) Color.parseColor("#63C5DA") else Color.parseColor("#000000"))
//        binding.btnReceived.setBackgroundResource(if (isShowingReceived) R.drawable.button_selected else R.drawable.button_unselected)
//        binding.btnRedeemed.setBackgroundResource(if (!isShowingReceived) R.drawable.button_selected else R.drawable.button_unselected)
//    }
}


data class RewardItem(val time: String, val points: Int, val source: String)

class RewardsAdapter : ListAdapter<RewardItem, RewardsAdapter.RewardViewHolder>(
    object : DiffUtil.ItemCallback<RewardItem>() {
        override fun areItemsTheSame(oldItem: RewardItem, newItem: RewardItem): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: RewardItem, newItem: RewardItem): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class RewardViewHolder(val binding: ItemRewardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RewardItem) {
            binding.txtTime.text = "時間：${item.time}"
            binding.txtPoints.text = "點數：${item.points}"
            binding.txtSource.text = "來源：${item.source}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val binding = ItemRewardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RewardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
