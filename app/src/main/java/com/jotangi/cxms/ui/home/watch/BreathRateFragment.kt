package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentBreathRateBinding
import com.jotangi.cxms.ui.home.bar.ColorBarChartView
import com.jotangi.cxms.utils.smartwatch.WatchUtils

class BreathRateFragment : BaseFragment() {

    private lateinit var binding: FragmentBreathRateBinding
    override fun getToolBar() = binding.toolbar

    private val args by navArgs<BreathRateFragmentArgs>()

    var month: Int = 0
    var year: Int = 0
    var minute: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBreathRateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarDetail(
            getString(R.string.circle_breath_rate),
            BreathRateFragmentDirections.actionBreathRateFragmentToBreathRateDetailFragment(
                args.tel
            )
        )

        binding.breathRateChartView.setType(ColorBarChartView.Type.BREATHRATE)

//        watchViewModel.lastRespiratoryRateData.observe(viewLifecycleOwner) { it ->
        watchViewModel.lastBreathRateData.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {

                val last7Data = it.take(7)
                binding.breathRateValueTextView.text = last7Data[0].respiratoryrate
                binding.breathRateDateTextView.text =
                    WatchUtils.instance.clipTimeFormatSecond(last7Data[0].startTime)
                binding.breathRateChartView.setDataValue(last7Data.map { it.respiratoryrate?.toInt() })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.lastBreathRateData.postValue(listOf())
    }
}