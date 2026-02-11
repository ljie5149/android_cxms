package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentKcalBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apirequest.KcalUploadRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.WatchCommonRequest
import kotlinx.coroutines.launch

class KcalFragment : BaseFragment() {

    private lateinit var binding: FragmentKcalBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<KcalFragmentArgs>()
    private val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKcalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarDetail(getString(R.string.circle_kcal), R.id.kcalDetailFragment)

        binding.bc.apply {

            setTouchEnabled(false)
            legend.isEnabled = false
            description.isEnabled = false

            with(xAxis) {
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
            }
            with(axisLeft) {
                axisMinimum = 0f
                isEnabled = false
            }
            with(axisRight) {
                axisMinimum = 0f
            }
        }

        SharedPreferencesUtil.instances.getAccountId().let {

            if (it.isNullOrBlank() || it != args.tel) {
                binding.btInsertData.visibility = View.GONE
                return
            }
        }
    }

    private fun initHandler() {

        binding.btInsertData.setOnClickListener {

            DialogUtil.instance.kcal(
                requireActivity(),
                object : DialogUtil.OnKcalDataListener {
                    override fun onData(request: KcalUploadRequest) {

                        request.memberId = args.tel
                        lifecycleScope.launch {

                            val response = apiRepository.uploadKcal(request)
                            if (response.code == "0x0200") {
                                watchViewModel.getKcal(
                                    WatchCommonRequest(
                                        args.tel,
                                        WatchUtils.instance.ago3MonthYmdHms(),
                                        WatchUtils.instance.currentYmdHms()
                                    )
                                )
                            }
                        }
                    }

                }
            )
        }
    }

    private fun initCallBack() {

        watchViewModel.getKcalList.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {

                binding.apply {

                    tvTime.text = WatchUtils.instance.clipTimeToYMD(it[0].startTime)
                    tvValue.text = it[0].KCAL

                    watchViewModel.kcal7DayValueList.value?.let { valueList ->

                        val entries: ArrayList<BarEntry> = ArrayList()
                        for (i in valueList.indices) {
                            entries.add(BarEntry(i.toFloat(), valueList[i].toFloat()))
                        }

                        binding.bc.apply {

                            val barDataSet = BarDataSet(entries, "")
                            with(barDataSet) {
                                valueTextSize = 10f
                                color = ContextCompat.getColor(requireContext(), R.color.kcal_bar)
                            }

                            val barData = BarData(barDataSet)
                            with(barData) {
                                setValueFormatter(object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        return WatchUtils.instance.fixDecimalZero(value)
                                    }
                                })
                            }

                            with(xAxis) {
                                valueFormatter = object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        return watchViewModel.kcal7DayDateList.value!![value.toInt()]
                                    }
                                }
                            }

                            binding.bc.xAxis
                            data = barData
                            invalidate()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.getKcalList.postValue(listOf())
        watchViewModel.getStepsKcalList.postValue(listOf())
    }
}