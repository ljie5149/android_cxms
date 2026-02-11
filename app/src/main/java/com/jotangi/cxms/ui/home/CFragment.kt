package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentCBinding
import com.jotangi.cxms.utils.DialogUtils
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.JiugonggeEnum
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apirequest.EcgListRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.GetBmdRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.TemperatureListRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.WatchCommonRequest
import com.jotangi.cxms.utils.smartwatch.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CFragment : BaseFragment() {

    private lateinit var binding: FragmentCBinding
    override fun getToolBar() = binding.toolbar

    private var statusList = arrayListOf<String>()
    private var memberIdList = arrayListOf<String>()

    private lateinit var legendTV: ArrayList<TextView>
    private lateinit var backgroundV: ArrayList<View>
    private lateinit var iconIV: ArrayList<ImageView>
    private lateinit var clickCL: ArrayList<ConstraintLayout>

    private var legendStr = ""
    private var backgroundNum = 0
    private var iconNum = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initView()
    }

    private fun initData() {

        binding.apply {

            legendTV = arrayListOf(
                tvCfC1, tvCfC2, tvCfC3, tvCfC4, tvCfC5, tvCfC6, tvCfC7, tvCfC8, tvCfC9
            )

            backgroundV = arrayListOf(
                vCfC1, vCfC2, vCfC3, vCfC4, vCfC5, vCfC6, vCfC7, vCfC8, vCfC9
            )

            iconIV = arrayListOf(
                ivCfC1, ivCfC2, ivCfC3, ivCfC4, ivCfC5, ivCfC6, ivCfC7, ivCfC8, ivCfC9
            )

            clickCL = arrayListOf(
                clCfC1, clCfC2, clCfC3, clCfC4, clCfC5, clCfC6, clCfC7, clCfC8, clCfC9
            )
        }

        arguments?.getString(JiugonggeEnum.LEGEND.name, "")?.let {
            if (it.isNotBlank()) {
                legendStr = it
            }
        }

        arguments?.getInt(JiugonggeEnum.BACKGROUND.name, 0)?.let {
            if (it != 0) {
                backgroundNum = it
            }
        }

        arguments?.getInt(JiugonggeEnum.ICON.name, 0)?.let {
            if (it != 0) {
                iconNum = it
            }
        }
    }

    private fun initView() {

        setToolbarArrow("數體健")

        legendTV[0].text = "自己"

        if (backgroundNum != 0) {
            backgroundV[0].background = ResourcesCompat.getDrawable(
                resources, backgroundNum, null
            )
        }

//        if (iconNum != 0) {
//            iconIV[0].setImageResource(iconNum)
//        }

        clickCL[0].setOnClickListener {

            val tel = SharedPreferencesUtil.instances.getAccountId()

            if (tel.isNullOrBlank()) {
                toFragment("")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    getElementData(tel)
                }
                toFragment(tel.toString())
            }
        }


        bookViewModel.careList.observe(viewLifecycleOwner) {

            it?.let {

                var index: Int
                statusList.clear()
                memberIdList.clear()

                for (i in it.indices) {

                    index = i + 1
                    legendTV[index].text = it[i].nick_name.toString()

                    if (backgroundNum != 0) {
                        backgroundV[index].background = ResourcesCompat.getDrawable(
                            resources, backgroundNum, null
                        )
                    }

//                    if (iconNum != 0) {
//                        iconIV[index].setImageResource(iconNum)
//                    }

                    statusList.add(it[i].status.toString())
                    memberIdList.add(it[i].cmember_id.toString())

                    // 讀取指定帳號
                    clickCL[index].setOnClickListener {

                        // 有授權
                        if (statusList[i] == "1") {

                            CoroutineScope(Dispatchers.IO).launch {
                                getElementData(memberIdList[i])
                            }

                            toFragment(memberIdList[i])

                        } else {

                            DialogUtils.showSingle(requireActivity(), "", "對方未授權") {}
                        }
                    }

                    if (i == 7) {
                        return@observe
                    }
                }
            }
        }
    }

    private suspend fun getElementData(memberId: String) {

        val startTime = WatchUtils.instance.ago3MonthYmdHms()
        val endTime = WatchUtils.instance.currentYmdHms()

        when (legendStr) {

            getString(R.string.circle_heart_rate) -> watchViewModel.getHeartRate(
                HeartRateRequest(memberId, startTime, endTime)
            )

            getString(R.string.circle_blood_pressure) -> watchViewModel.getBp(
                BPRequest(memberId, startTime, endTime)
            )

            getString(R.string.circle_blood_oxygen) -> watchViewModel.getOxygen(
                OxygenRequest(memberId, startTime, endTime)
            )

            getString(R.string.circle_breath_rate) -> {
                watchViewModel.getBreathRate(
                    BreathRateRequest(memberId, startTime, endTime)
                )

//                    watchViewModel.getRespiratoryRate(
//                        RespiratoryRateListRequest(accountid, startstr, endstr)
//                    )
            }

            getString(R.string.circle_body_temperature) -> watchViewModel.getTemperature(
                TemperatureListRequest(memberId, startTime, endTime)
            )

            getString(R.string.circle_sleep) -> {
                watchViewModel.getSleep(
                    SleepRequest(memberId, startTime, endTime)
                )

                // 睡眠細節
                watchViewModel.getSleepDetail(
                    SleepRequest(memberId, startTime, endTime)
                )
            }

            getString(R.string.circle_step_count) -> watchViewModel.getGetSteps(
                WatchCommonRequest(memberId, startTime, endTime)
            )

            getString(R.string.circle_ecg) -> watchViewModel.getECG(
                EcgListRequest(memberId, startTime, endTime)
            )
            getString(R.string.circle_kcal) -> {
                watchViewModel.getStepsKcal(
                    WatchCommonRequest(memberId, startTime, endTime)
                )

                watchViewModel.getKcal(
                    WatchCommonRequest(memberId, startTime, endTime)
                )
            }


            getString(R.string.circle_arm_blood_pressure) -> watchViewModel.getBp2(
                WatchCommonRequest(memberId, startTime, endTime)
            )

            getString(R.string.circle_macular_pigment) -> watchViewModel.getMPOD(
                WatchCommonRequest(memberId, startTime, endTime)
            )

            getString(R.string.circle_bone_density) -> watchViewModel.getBMD(
                GetBmdRequest(memberId, startTime, endTime)
            )
        }
    }

    private fun toFragment(tel: String) {

        when (legendStr) {

            getString(R.string.circle_heart_rate) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToHeartRateFragment(tel)
                )
            getString(R.string.circle_blood_pressure) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToBloodPressureFragment(tel)
                )
            getString(R.string.circle_blood_oxygen) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToBloodOxygenFragment(tel)
                )

            getString(R.string.circle_breath_rate) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToBreathRateFragment(tel)
                )
            getString(R.string.circle_body_temperature) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToBodyTemperatureFragment(tel)
                )
            getString(R.string.circle_sleep) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToSleepFragment(tel)
                )

            getString(R.string.circle_step_count) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToStepCountFragment(tel)
                )
            getString(R.string.circle_ecg) ->
                findNavController().navigate(R.id.ecgFragment)
            getString(R.string.circle_kcal) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToKcalFragment(tel)
                )

            getString(R.string.circle_arm_blood_pressure) -> {
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToArmBloodPressureFragment(tel)
                )
            }
            getString(R.string.circle_macular_pigment) -> {
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToMacularPigmentFragment(tel)
                )
            }
            getString(R.string.circle_bone_density) ->
                findNavController().navigate(
                    CFragmentDirections.actionCFragmentToBoneDensityFragment(tel)
                )
        }
    }
}