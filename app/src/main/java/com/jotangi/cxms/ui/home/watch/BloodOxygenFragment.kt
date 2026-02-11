package com.jotangi.cxms.ui.home.watch

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.text.format.DateFormat
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
import com.jotangi.cxms.databinding.FragmentBloodOxygenBinding
import com.jotangi.cxms.ui.home.BoneDensityFragmentArgs
import com.jotangi.cxms.ui.home.bar.ColorBarChartView
import com.jotangi.cxms.ui.home.bar.HorizontalColorBar
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.model.OxygenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class BloodOxygenFragment : BaseFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentBloodOxygenBinding
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
        binding = FragmentBloodOxygenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarDetail(
            getString(R.string.circle_blood_oxygen),
            BloodOxygenFragmentDirections.actionBloodOxygenFragmentToBloodOxygenDetailFragment(
                args.tel
            )
        )

        binding.chartOxy.setType(ColorBarChartView.Type.OXYGEN)
        binding.barOxy.setType(HorizontalColorBar.Type.OXYGEN)

        watchViewModel.lastOxygenData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val last7Data = it.take(7)
                binding.tvOxyValue.text = last7Data[0].OOValue
                binding.tvOxyDate.text =
                    WatchUtils.instance.clipTimeFormatSecond(last7Data[0].startTime)
                binding.barOxy.setDataValue(last7Data[0].OOValue!!.toInt())
                binding.chartOxy.setDataValue(last7Data.map { it.OOValue?.toInt() })
            }
        }

        SharedPreferencesUtil.instances.getAccountId().let {

            if (it.isNullOrBlank() || it != args.tel) {

                binding.btnInsertData.visibility = View.GONE

            } else {

                binding.btnInsertData.setOnClickListener {
                    insertData()
                }

            }
        }
    }

    private fun insertData() {
        var dialog = AlertDialog.Builder(requireContext()).create()
        var view =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_button, null)
        var oxyView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_oxy, null)
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
                    var vp = oxyView.parent
                    if (vp is ViewGroup) {
                        vp.removeView(oxyView)
                    }
                    setTitle("")
                    setView(oxyView)
                    var insertValue = oxyView.findViewById(R.id.btn_insert_value) as Button
                    inputDate = oxyView.findViewById(R.id.input_data_time) as EditText
                    var etValue = oxyView.findViewById(R.id.et_value) as EditText
                    inputDate.setText(sdf.format(Date()))
                    inputDate.setOnClickListener {
                        val calendar: Calendar = Calendar.getInstance()
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                        month = calendar.get(Calendar.MONTH)
                        year = calendar.get(Calendar.YEAR)
                        val datePickerDialog =
                            DatePickerDialog(context, this@BloodOxygenFragment, year, month, day)
                        datePickerDialog.show()
                    }
                    insertValue.setOnClickListener {
                        var value = etValue.text.toString()
                        if (value.isEmpty()) {
                            Toast.makeText(context, "請輸入血氧", Toast.LENGTH_SHORT).show()
                        } else {
                            //Toast.makeText(context,value,Toast.LENGTH_SHORT).show()
                            uploadData(pickDayResult, value)
                            dialog.dismiss()
                            dialog2.dismiss()
                        }
                    }
                    show()
                }
            }
            var qrcode = view.findViewById(R.id.btn_dialog_qrcode) as Button
            qrcode.setOnClickListener {
                var dialog2 = AlertDialog.Builder(requireContext()).create()
                dialog2.apply {
                    var qr = qrView.findViewById(R.id.imageView10) as ImageView

                    var vp = qrView.parent
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
                    var close = qrView.findViewById(R.id.btn_dialog_close) as Button
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

    private fun uploadData(time: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val resp = apiRepository.oxygenUpload(
                OxygenRequest(
                    memberId!!,
                    time,
                    "",
                    value.toInt(),
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

            watchViewModel.getOxygen(
                OxygenRequest(
                    memberId!!,
                    startstr,
                    endstr
                )
            )
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
        watchViewModel.lastOxygenData.postValue(listOf())
    }
}