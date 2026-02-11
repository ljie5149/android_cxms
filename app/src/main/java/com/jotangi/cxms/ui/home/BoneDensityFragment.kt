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
import com.jotangi.cxms.databinding.FragmentBoneDensityBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apirequest.BmdUploadRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.GetBmdRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BoneDensityFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentBoneDensityBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<BoneDensityFragmentArgs>()
    private val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }

    private lateinit var timeET: EditText
    private lateinit var date: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoneDensityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarDetail(getString(R.string.circle_bone_density), R.id.boneDensityDetailFragment)

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
                    R.layout.dialog_insert_data_bmd, null
                )

                dialog.apply {

                    setTitle("")
                    setView(view)
                    timeET = view.findViewById(R.id.et_bmd_time) as EditText
                    val valueET = view.findViewById(R.id.et_value) as EditText
                    val unitTV = view.findViewById<TextView>(R.id.tv_unit)
                    val okBT = view.findViewById(R.id.btn_insert_value) as Button

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    date = sdf.format(Date())
                    timeET.setText(date)
                    valueET.inputType = InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL or
                            InputType.TYPE_NUMBER_FLAG_SIGNED
                    unitTV.text = getString(R.string.unit_t_score)

                    timeET.setOnClickListener {
                        val calendar: Calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(
                            context,
                            this@BoneDensityFragment,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        datePickerDialog.show()
                    }

                    okBT.setOnClickListener {

                        val value = valueET.text.toString()

                        when {

                            SharedPreferencesUtil.instances.getAccountId().isNullOrBlank() ->
                                Toast.makeText(
                                    requireContext(), "請先登入", Toast.LENGTH_SHORT
                                ).show()

                            value.isEmpty() ->
                                Toast.makeText(
                                    requireContext(), "請輸入骨質密度", Toast.LENGTH_SHORT
                                ).show()

                            processInput(value) ->
                                Toast.makeText(
                                    requireContext(), "請輸入正確範圍", Toast.LENGTH_SHORT
                                ).show()

                            checkDate() ->
                                Toast.makeText(
                                    requireContext(), "此時間已重複請，重新選擇時間", Toast.LENGTH_SHORT
                                ).show()

                            else -> {
                                bmdUpload(value.toFloat().toString())
                                dialog.dismiss()
                            }
                        }
                    }
                    show()
                }
            }
        }
    }

    private fun processInput(str: String): Boolean {

        try {
            val f = str.toFloat()
            if (f < -4 || f > 2) {
                return true
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
        return false
    }

    private fun checkDate(): Boolean {

        watchViewModel.getBmdList.value?.let {

            for (i in it.indices) {

                if (it[i].startTime.toString().contains(date)) {
                    return true
                }
            }
        }
        return false
    }

    private fun bmdUpload(tScore: String) {

        lifecycleScope.launch {

            val response = apiRepository.bmdUpload(
                BmdUploadRequest(
                    SharedPreferencesUtil.instances.getAccountId()!!,
                    date,
                    tScore,
                    "1"
                )
            )

            if (response.code == "0x0200") {

                SharedPreferencesUtil.instances.getAccountId()?.let {

                    watchViewModel.getBMD(
                        GetBmdRequest(
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

        watchViewModel.getBmdList.observe(viewLifecycleOwner) { beanList ->

            if (beanList.isNotEmpty()) {

                binding.apply {

                    tvBoneDensityTime.text = WatchUtils.instance.clipTimeFormatSecond(
                        beanList[0].startTime
                    )
                    // {117, 137, 156, 176}
                    tvBoneDensity.text = beanList[0].TScore
                    cbBoneDensity.setDataValue(beanList[0].TScore)

                    val sList = mutableListOf("", "", "", "", "", "", "")

                    for (i in beanList.indices) {

                        sList[i] = beanList[i].TScore.toString()

                        if (i == (sList.size - 1)) {
                            break
                        }
                    }
                    cbbdBoneDensity.setDataValue(sList)
                }
            }
        }
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
        watchViewModel.getBmdList.postValue(listOf())
    }
}