package com.jotangi.cxms.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentMacularPigmentBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apirequest.UploadMpodRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.WatchCommonRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MacularPigmentFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentMacularPigmentBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<MacularPigmentFragmentArgs>()
    private val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }

    private lateinit var timeET: EditText
    private lateinit var date: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMacularPigmentBinding.inflate(inflater, container, false)
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
            getString(R.string.circle_macular_pigment),
            R.id.macularPigmentDetailFragment
        )

        SharedPreferencesUtil.instances.getAccountId().let {

            if (it.isNullOrBlank() || it != args.tel) {
                binding.btInsertData.visibility = View.GONE
                return
            }
        }
    }

    private fun initHandler() {

        binding.apply {

            btInsertData.setOnClickListener {

                val dialog = AlertDialog.Builder(requireContext()).create()

                val view = LayoutInflater.from(requireContext()).inflate(
                    R.layout.dialog_insert_data_mpod, null
                )

                dialog.apply {

                    setTitle("")
                    setView(view)

                    timeET = view.findViewById(R.id.et_mpod_time)
                    val leftEyeET = view.findViewById<EditText>(R.id.et_left_eye)
                    val rightEyeET = view.findViewById<EditText>(R.id.et_right_eye)
                    val insertBT = view.findViewById<Button>(R.id.btn_insert_value)

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    date = sdf.format(Date())
                    timeET.setText(date)

                    leftEyeET.inputType = InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL
                    rightEyeET.inputType = InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL

                    timeET.setOnClickListener {

                        val calendar: Calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(
                            context,
                            this@MacularPigmentFragment,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        datePickerDialog.show()
                    }

                    insertBT.setOnClickListener {

                        val leftEyeValue = leftEyeET.text.toString()
                        val rightEyeValue = rightEyeET.text.toString()
                        val eyeList = arrayOf(leftEyeValue, rightEyeValue)

                        when {

                            SharedPreferencesUtil.instances.getAccountId().isNullOrBlank() ->
                                Toast.makeText(
                                    requireContext(), "請先登入", Toast.LENGTH_SHORT
                                ).show()

                            leftEyeValue.isEmpty() ->
                                Toast.makeText(
                                    requireContext(), "請輸入左眼數值", Toast.LENGTH_SHORT
                                ).show()

                            rightEyeValue.isEmpty() ->
                                Toast.makeText(
                                    requireContext(), "請輸入右眼數值", Toast.LENGTH_SHORT
                                ).show()

                            processInput(*eyeList) ->
                                Toast.makeText(
                                    requireContext(), "請輸入正確範圍(0 ~ 1)", Toast.LENGTH_SHORT
                                ).show()

                            checkDate() ->
                                Toast.makeText(
                                    requireContext(), "此時間已重複，請重新選擇時間", Toast.LENGTH_SHORT
                                ).show()

                            else -> {
                                uploadMpod(leftEyeValue, rightEyeValue)
                                dialog.dismiss()
                            }
                        }
                    }
                    show()
                }
            }
        }
    }

    private fun processInput(vararg values: String): Boolean {

        for (v in values) {

            try {
                val f = v.toFloat()
                if (f > 1 || f < 0) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return true
            }
        }
        return false
    }

    private fun checkDate(): Boolean {

        watchViewModel.getMpodList.value?.let {

            for (i in it.indices) {

                if (it[i].mpodStartTime.toString().contains(date)) {
                    return true
                }
            }
        }
        return false
    }

    private fun uploadMpod(leftEyeValue: String, rightEyeValue: String) {

        lifecycleScope.launch {

            val response = apiRepository.uploadMpod(
                UploadMpodRequest(
                    SharedPreferencesUtil.instances.getAccountId()!!,
                    date,
                    leftEyeValue,
                    rightEyeValue,
                    "1"
                )
            )

            if (response.code == "0x0200") {

                SharedPreferencesUtil.instances.getAccountId()?.let {

                    watchViewModel.getMPOD(
                        WatchCommonRequest(
                            it,
                            WatchUtils.instance.ago3MonthYmdHms(),
                            WatchUtils.instance.currentYmdHms()
                        )
                    )
                }
            }
        }
    }

    private fun initCallBack() {

        watchViewModel.getMpodList.observe(viewLifecycleOwner) { beanList ->

            if (beanList.isNotEmpty()) {

                binding.apply {

                    tvMacularPigmentTime.text = WatchUtils.instance.clipTimeFormatSecond(
                        beanList[0].mpodStartTime
                    )
                    tvMacularPigment.text = "${beanList[0].lefteye}(L)/${beanList[0].righteye}(R)"
                    cbBpodLeftEye.initLegend()
                    cbBpodLeftEye.setDataValue(beanList[0].lefteye)
                    cbBpodRightEye.setDataValue(beanList[0].righteye)

                    val dateList = arrayListOf<String>()

                    val leftList = arrayListOf<Int>()
                    val rightList = arrayListOf<Int>()
                    var leftValue: Int
                    var rightValue: Int
                    var max = -1
                    var isLeft = true

                    for (i in beanList.indices) {

                        dateList.add(WatchUtils.instance.clipTimeToMd(beanList[i].mpodStartTime))

                        leftValue = processValue(beanList[i].lefteye)
                        max = if (leftValue > max) {
                            isLeft = true
                            leftValue
                        } else {
                            max
                        }
                        leftList.add(processValue(beanList[i].lefteye))

                        rightValue = processValue(beanList[i].righteye)
                        max = if (rightValue > max) {
                            isLeft = false
                            rightValue
                        } else {
                            max
                        }
                        rightList.add(processValue(beanList[i].righteye))

                        if (i == 6) {
                            break
                        }
                    }

                    if (isLeft) {

                        // 大的要在上面
                        lineChartView.setDataValue(
                            leftList.toIntArray(),
                            rightList.toIntArray()
                        )

                    } else {

                        lineChartView.rightColor()
                        lineChartView.setDataValue(
                            rightList.toIntArray(),
                            leftList.toIntArray()
                        )
                    }

//                    lineChartView.setDateLabel(dateList.toTypedArray())
                }
            }
        }
    }

    private fun processValue(value: String?): Int {

        var f: Float
        try {

            f = value.toString().toFloat()
            f = if (f < 0) 0f else f
            f = if (f > 1) 1f else f

        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }

        return (f * 100).toInt()
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

        date = "${p1}-${
            WatchUtils.instance.fixTimeLength((p2 + 1).toString())
        }-${
            WatchUtils.instance.fixTimeLength(p3.toString())
        } "
        Log.d(TAG, "onDateSet: $date")

        val calendar: Calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            this,
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(context)
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {

        date += "${
            WatchUtils.instance.fixTimeLength(p1.toString())
        }:${
            WatchUtils.instance.fixTimeLength(p2.toString())
        }"
        Log.d(TAG, "onTimeSet: $date")

        timeET.setText(date)
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.getMpodList.postValue(listOf())
    }
}