package com.jotangi.cxms.ui.home.watch

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentBloodPressureBinding
import com.jotangi.cxms.ui.home.BoneDensityFragmentArgs
import com.jotangi.cxms.ui.home.bar.HorizontalColorBar
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.model.BPRequest
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class BloodPressureFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentBloodPressureBinding
    override fun getToolBar() = binding.toolbar

    private val args by navArgs<BoneDensityFragmentArgs>()
    protected val apiRepository: WatchApiRepository by lazy { WatchApiRepository() }

    private val memberId = SharedPreferencesUtil.instances.getAccountId()
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private lateinit var inputDate: EditText

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var pickDayResult = sdf.format(Date())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBloodPressureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {

        setToolbarDetail(
            getString(R.string.circle_blood_pressure),
            BloodPressureFragmentDirections.actionBloodPressureFragmentToBloodPressureDetailFragment(
                args.tel
            )
        )

        binding.apply {

            tvSbpLegend.text =
                "90MMHG ${getString(R.string.less_than)} 收縮壓 ${getString(R.string.less_than)} 140MMHG"
            tvDbpLegend.text =
                "60MMHG ${getString(R.string.less_than)} 舒張壓 ${getString(R.string.less_than)} 90MMHG"

            heartRateColorBarDBP.setType(HorizontalColorBar.Type.SBP)
            heartRateColorBarSBP.setType(HorizontalColorBar.Type.DBP)

            watchViewModel.lastBpData.observe(viewLifecycleOwner) {

                if (it.isNotEmpty()) {
                    var last7Data = it.take(7)

                    tvBpDate.text =
                        WatchUtils.instance.clipTimeFormatSecond(last7Data[0].bloodStartTime)

                    tvBpValue.text = "${last7Data[0].bloodSBP} / ${last7Data[0].bloodDBP}"
                    heartRateColorBarSBP.setDataValue(last7Data[0].bloodSBP!!.toInt())
                    heartRateColorBarDBP.setDataValue(last7Data[0].bloodDBP!!.toInt())
                    val sbps = last7Data.map { it.bloodSBP?.toInt() }.filterNotNull().toIntArray()
                    val dbps = last7Data.map { it.bloodDBP?.toInt() }.filterNotNull().toIntArray()
                    cvBP.setDataValue(sbps, dbps)
                }
            }

            SharedPreferencesUtil.instances.getAccountId().let {

                if (it.isNullOrBlank() || it != args.tel) {

                    binding.llBottomButtons.visibility = View.GONE

                } else {

                    button.setOnClickListener {
                        if (SharedPreferencesUtil.instances.getWatchMac() != null) {
                            pickerDialog()
                        } else {
                            Toast.makeText(context, "請先綁定手錶", Toast.LENGTH_SHORT).show()
                        }
                    }

                    btnInsertData.setOnClickListener {
                        insertData()
                    }

                }
            }
        }
    }

    private fun insertData() {
        var dialog = AlertDialog.Builder(requireContext()).create()
        var view =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_button, null)
        var bpView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_bp, null)
        var qrView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_qrcode, null)
        dialog.apply {
            //setCancelable(false)
            setTitle("")
            setView(view)

            var insert = view.findViewById(R.id.btn_dialog_insert_data) as Button
            insert.setOnClickListener {
                var dialog2 = AlertDialog.Builder(requireContext()).create()
                dialog2.apply {
                    var vp = bpView.parent
                    if (vp is ViewGroup) {
                        vp.removeView(bpView)
                    }
                    setTitle("")
                    setView(bpView)
                    var insertValue = bpView.findViewById(R.id.btn_insert_value) as Button
                    var sbp = bpView.findViewById(R.id.et_sbp) as EditText
                    var dbp = bpView.findViewById(R.id.et_dbp) as EditText
                    inputDate = bpView.findViewById(R.id.input_data_time) as EditText
                    inputDate.setText(sdf.format(Date()))
                    inputDate.setOnClickListener {
                        val calendar: Calendar = Calendar.getInstance()
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                        month = calendar.get(Calendar.MONTH)
                        year = calendar.get(Calendar.YEAR)
                        val datePickerDialog =
                            DatePickerDialog(context, this@BloodPressureFragment, year, month, day)
                        datePickerDialog.show()
                    }

                    insertValue.setOnClickListener {
                        var sbpValue = sbp.text.toString()
                        var dbpValue = dbp.text.toString()
                        if (sbpValue.isEmpty() || dbpValue.isEmpty()) {
                            Toast.makeText(context, "請輸入資料", Toast.LENGTH_SHORT).show()
                        } else {
                            uploadData(pickDayResult, sbpValue, dbpValue)
                            dialog.dismiss()
                            dialog2.dismiss()
                        }
                    }
                    show()
                }
            }
            val qrcode = view.findViewById(R.id.btn_dialog_qrcode) as Button
            qrcode.setOnClickListener {
                val dialog2 = AlertDialog.Builder(requireContext()).create()
                dialog2.apply {
                    val qr = qrView.findViewById(R.id.imageView10) as ImageView

                    val vp = qrView.parent
                    if (vp is ViewGroup) {
                        vp.removeView(qrView)
                    }
                    setTitle("")
                    setView(qrView)
                    qr.setImageBitmap(
                        textToImage(
                            "member_id=${SharedPreferencesUtil.instances.getAccountId()}",
                            1280,
                            1280
                        )
                    )
                    val close = qrView.findViewById(R.id.btn_dialog_close) as Button
                    close.setOnClickListener {
                        dialog.dismiss()
                        dialog2.dismiss()
                    }
                    show()
                }
            }

            show()
        }
    }

    private fun uploadData(time: String, sbpValue: String, dbpValue: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val resp = apiRepository.bpUpload(
                BPRequest(
                    memberId!!,
                    time,
                    "",
                    dbpValue.toInt(),
                    sbpValue.toInt(),
                    1
                )
            )
            if (resp.code.equals("0x0200")) {
                refreshData()
            }
        }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            val endTime = Date()
            val endstr = sdf.format(endTime)
            val calendar = Calendar.getInstance()
            calendar.setTime(endTime)
            calendar.add(Calendar.MONTH, -3)
            val startTime = calendar.time
            val startstr = sdf.format(startTime)

            watchViewModel.getBp(
                BPRequest(
                    memberId!!,
                    startstr,
                    endstr
                )
            )
        }
    }

    private fun pickerDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bp_picker, null)
        dialog.apply {
            setCancelable(false)
            setTitle("")
            setView(view)
            val picker1 = view.findViewById(R.id.picker1) as NumberPicker
            picker1.apply {
                minValue = 60
                maxValue = 250
                wrapSelectorWheel = false
            }
            val picker2 = view.findViewById(R.id.picker2) as NumberPicker
            picker2.apply {
                minValue = 40
                maxValue = 150
                wrapSelectorWheel = false
            }
            setPositiveButton("確認") { dialogInterface, i ->

                YCBTClient.appBloodCalibration(
                    picker1.value,
                    picker2.value
                ) { i, fl, hashMap ->

                    if (i == 0 && hashMap != null) {
                        GlobalScope.launch(Dispatchers.Main) {
                            Log.d(TAG, "$i , $fl , ${hashMap.get("data").toString()}")
                            Toast.makeText(requireContext(), "校正完成", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            setNegativeButton("取消") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            show()
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar: Calendar = Calendar.getInstance()
        //Log.d("heartdate","$year-$month-$dayOfMonth")
        this.year = year
        this.month = month
        this.day = dayOfMonth
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog =
            TimePickerDialog(context, this, hour, minute, DateFormat.is24HourFormat(context))
        timePickerDialog.show()
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        if (inputDate != null) {
            //Log.d("heartdate","$p1:$p2")
            this.hour = hour
            this.minute = minute
            month += 1
            pickDayResult = "$year-$month-$day $hour:$minute"
            inputDate.setText(pickDayResult)
        }
    }

    @Throws(WriterException::class, NullPointerException::class)
    private fun textToImage(text: String, width: Int, height: Int): Bitmap? {
        val bitMatrix: BitMatrix
        bitMatrix = try {
            MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE,
                width, height, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        val colorWhite = -0x1
        val colorBlack = -0x1000000
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) colorBlack else colorWhite
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.lastBpData.postValue(listOf())
    }
}