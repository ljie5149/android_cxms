package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentSleepBinding
import com.jotangi.cxms.ui.home.bar.HorizontalColorBar
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import java.util.*


class SleepFragment : BaseFragment() {

    private lateinit var binding: FragmentSleepBinding
    override fun getToolBar() = binding.toolbar

    private val args by navArgs<SleepFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarDetail(
            getString(R.string.circle_sleep),
            SleepFragmentDirections.actionSleepFragmentToSleepDetailFragment(args.tel)
        )

        binding.hcbvSleep.setType(HorizontalColorBar.Type.SLEEP)
    }

    private fun initCallBack() {

        // 最近一天 開始睡眠時間
        watchViewModel.lastSleepStartTime.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                binding.tvSleepTime.text = WatchUtils.instance.clipTimeFormatSecond(data)
            }
        }

        // 最近一天 實際總睡眠時、分
        watchViewModel.totalSleepHour.observe(viewLifecycleOwner) { data ->
            if (data != -1) {
                binding.tvSleepHour.text = data.toString()
            }
        }
        watchViewModel.totalSleepMinute.observe(viewLifecycleOwner) { data ->
            if (data != -1) {
                binding.tvSleepMinute.text = data.toString()
            }
        }

        // 最近一天 睡眠圖
        watchViewModel.daySleepDetailData.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                val start = watchViewModel.lastSleepStartTime.value
                val stop = watchViewModel.lastSleepEndTime.value
                binding.shcbvSleep.setDatalist(list, start, stop)
            }
        }

        // 最近一天 睡眠品質
        watchViewModel.lastSleepQuality.observe(viewLifecycleOwner) { data ->
            if (data != -1) {
                binding.tvSleepQuality.text = data.toString()
                binding.hcbvSleep.setDataValue(data)
            }
        }

        // 近7天趨勢圖
        watchViewModel.day7TrendDate.observe(viewLifecycleOwner) {
            if (it.size > 0) {
                binding.sleepBar7Date.setDateLabel(it.toTypedArray())
            }
        }
        watchViewModel.day7TrendSleepTotal.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.sleepBar7Date.setDataValue(it, watchViewModel.day7TrendSleepDeep.value)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.lastSleepStartTime.postValue("")
        watchViewModel.totalSleepHour.postValue(-1)
        watchViewModel.totalSleepMinute.postValue(-1)
        watchViewModel.daySleepDetailData.postValue(listOf())
        watchViewModel.lastSleepQuality.postValue(-1)
        watchViewModel.day7TrendDate.postValue(LinkedList<String>())
        watchViewModel.day7TrendSleepTotal.postValue(IntArray(0))
        watchViewModel.daySleepData.postValue(listOf())
    }
}