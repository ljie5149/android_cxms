package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentStepCountBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.model.GetStepsDataBean
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StepCountFragment : BaseFragment() {

    private lateinit var binding: FragmentStepCountBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<StepCountFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepCountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarDetail(
            getString(R.string.circle_step_count),
            StepCountFragmentDirections.actionStepCountFragmentToStepCountDetailFragment(
                args.tel
            )
        )
    }

    private fun initCallBack() {
        watchViewModel.dayTotalSteps.observe(viewLifecycleOwner) {
            if (it != -1) {
                binding.tvStepTotal.text = it.toString()
            }
        }
        watchViewModel.dayTotalCalories.observe(viewLifecycleOwner) {
            if (it != -1) {
                binding.tvSportCalorie.text = it.toString()
            }
        }
        watchViewModel.dayTotalMeters.observe(viewLifecycleOwner) {
            if (it != -1) {
                binding.tvWalkDistance.text = it.toString()
            }
        }
        watchViewModel.lastFootStepsData.observe(viewLifecycleOwner) {
            if (!it.sportStep.isNullOrBlank()) {
                it.sportStartTime?.let { sportstarttime ->
                    it.sportEndTime?.let { sportendtime ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val t1 = sdf.parse(sportstarttime)
                        val t2 = sdf.parse(sportendtime)
                        val diff = t2.time - t1.time
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
                        var minutes = seconds / 60f
                        binding.tvSportTime.text =
                            String.format(Locale.getDefault(), "%.1f", minutes)
                    }
                    binding.tvSportStepTime.text =
                        WatchUtils.instance.clipTimeToYMD(sportstarttime)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        watchViewModel.dayTotalSteps.postValue(-1)
        watchViewModel.dayTotalCalories.postValue(-1)
        watchViewModel.dayTotalMeters.postValue(-1)
        watchViewModel.lastFootStepsData.postValue(GetStepsDataBean())
        watchViewModel.dayFootStepsData.postValue(listOf())
    }
}