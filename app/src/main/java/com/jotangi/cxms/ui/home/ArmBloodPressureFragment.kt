package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentArmBloodPressureBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.ui.home.bar.HorizontalColorBar
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apirequest.ArmUploadRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.WatchCommonRequest
import kotlinx.coroutines.launch

class ArmBloodPressureFragment : BaseFragment() {

    private lateinit var binding: FragmentArmBloodPressureBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<ArmBloodPressureFragmentArgs>()
    private val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArmBloodPressureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarDetail(
            getString(R.string.circle_arm_blood_pressure),
            R.id.armBloodPressureDetailFragment
        )

        SharedPreferencesUtil.instances.getAccountId().let {

            if (it.isNullOrBlank() || it != args.tel) {
                binding.btInsertData.visibility = View.GONE
                return
            }
        }
    }

    private fun initHandler() {

        binding.btInsertData.setOnClickListener {

            DialogUtil.instance.arm(requireActivity(),
                watchViewModel.getBp2List.value,
                object : DialogUtil.OnArmDataListener {
                    override fun onData(request: ArmUploadRequest) {

                        Log.d(TAG, "request: $request")

                        request.memberId = args.tel

                        lifecycleScope.launch {

                            val response = apiRepository.uploadBp2(request)
                            if (response.code == "0x0200") {
                                watchViewModel.getBp2(
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

        watchViewModel.getBp2List.observe(viewLifecycleOwner) { list ->

            if (list.isNotEmpty()) {
                binding.apply {

                    with(list[0]) {
                        tvArmTime.text = WatchUtils.instance.clipTimeFormatSecond(bloodStartTime!!)
                        tvHeartRate.text = heartValue

                        hcbSbp.setType(HorizontalColorBar.Type.DBP)
                        hcbDbp.setType(HorizontalColorBar.Type.SBP)

                        // 90 - 140
                        hcbSbp.setDataValue(LbloodSBP!!.toInt())
                        // 60 -90
                        hcbDbp.setDataValue(LbloodDBP!!.toInt())

                        hcbSbpR.setType(HorizontalColorBar.Type.DBP)
                        hcbDbpR.setType(HorizontalColorBar.Type.SBP)

                        // 90 - 140
                        hcbSbpR.setDataValue(RbloodSBP!!.toInt())
                        // 60 -90
                        hcbDbpR.setDataValue(RbloodDBP!!.toInt())


                        etLSys.text = LbloodSBP
                        etRSys.text = RbloodSBP
                        etLDia.text = LbloodDBP
                        etRDia.text = RbloodDBP
                        etLPp.text = LbloodPP
                        etRPp.text = RbloodPP
                        etLMap.text = LbloodMAP
                        etRMap.text = RbloodMAP
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.getBp2List.postValue(listOf())
    }
}