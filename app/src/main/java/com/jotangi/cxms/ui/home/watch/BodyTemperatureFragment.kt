package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentBodyTemperatureBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils

class BodyTemperatureFragment : BaseFragment() {

    private lateinit var binding: FragmentBodyTemperatureBinding
    override fun getToolBar() = binding.toolbar

    private val args by navArgs<BodyTemperatureFragmentArgs>()

    var month: Int = 0
    var year: Int = 0
    var minute: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBodyTemperatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initChart()
        initCallBack()
    }

    private fun init() {

        setToolbarDetail(
            getString(R.string.circle_body_temperature),
            BodyTemperatureFragmentDirections.actionBodyTemperatureFragmentToBodyTemperatureDetailFragment(
                args.tel
            )
        )
    }

    private fun initChart() {

        binding.temperatureBarChart.apply {

            setTouchEnabled(false)
            legend.isEnabled = false
            description.isEnabled = false

            with(xAxis) {
                setDrawGridLines(false)
            }
            with(axisLeft) {
                isEnabled = false
            }
        }
    }

    private fun initCallBack() {

        watchViewModel.lastTemperatureData.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {

                binding.temperatureValueTextView.text =
                    WatchUtils.instance.fixDecimalOne(it[0].temperature.toFloat())
                binding.temperatureDateTextView.text =
                    WatchUtils.instance.clipTimeFormatSecond(it[0].startTime)

                val last7Data = it.take(7)
                val entries: ArrayList<BarEntry> = ArrayList()
                for (i in last7Data.indices) {
                    entries.add(BarEntry(i.toFloat(), it[i].temperature.toFloat()))
                }

                binding.temperatureBarChart.apply {

                    val barDataSet = BarDataSet(entries, "")
                    with(barDataSet) {
                        valueTextSize = 10f
                        color = ContextCompat.getColor(requireContext(), R.color.c_CC6600_100)
                    }

                    val barData = BarData(barDataSet)
                    with(barData) {
                        setValueFormatter(object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return "$value"
                            }
                        })
                    }
                    data = barData
                    invalidate()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.lastTemperatureData.postValue(listOf())
    }
}