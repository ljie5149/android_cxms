package com.jotangi.cxms.ui.home.watch

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentHeartRateBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.ui.home.bar.ColorBarChartView
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchApiRepository
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.model.HeartRateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HeartRateFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentHeartRateBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<HeartRateFragmentArgs>()
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
        binding = FragmentHeartRateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarDetail(
            getString(R.string.circle_heart_rate),
            HeartRateFragmentDirections.actionHeartRateFragmentToHeartRateDetailFragment(args.tel)
        )

        binding.chartView.setType(ColorBarChartView.Type.HR)
        updateHeartRateData()

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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun insertData() {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_button, null)
        val bpmView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_bpm, null)
        val qrView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_insert_data_qrcode, null)
        dialog.apply {
            //setCancelable(false)
            setTitle("")
            setView(view)

            val insert = view.findViewById(R.id.btn_dialog_insert_data) as Button
            insert.setOnClickListener {
                val dialog2 = AlertDialog.Builder(requireContext()).create()
                dialog2.apply {
                    val vp = bpmView.parent
                    if (vp is ViewGroup) {
                        vp.removeView(bpmView)
                    }
                    setTitle("")
                    setView(bpmView)
                    val insertValue = bpmView.findViewById(R.id.btn_insert_value) as Button
                    inputDate = bpmView.findViewById(R.id.input_data_time) as EditText
                    val etValue = bpmView.findViewById(R.id.et_value) as EditText
                    inputDate.setText(sdf.format(Date()))
                    inputDate.setOnClickListener {
                        val calendar: Calendar = Calendar.getInstance()
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                        month = calendar.get(Calendar.MONTH)
                        year = calendar.get(Calendar.YEAR)
                        val datePickerDialog =
                            DatePickerDialog(context, this@HeartRateFragment, year, month, day)
                        datePickerDialog.show()
                    }
                    insertValue.setOnClickListener {
                        val value = etValue.text.toString()
                        if (value.isEmpty()) {
                            Toast.makeText(context, "請輸入心率", Toast.LENGTH_SHORT).show()
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

    private fun uploadData(time: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val resp = apiRepository.heartRateUpload(
                HeartRateRequest(
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

            watchViewModel.getHeartRate(
                HeartRateRequest(
                    memberId!!,
                    startstr,
                    endstr
                )
            )
        }
    }

    private fun updateHeartRateData() {

        watchViewModel.lastHeartRateData.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                val last7Data = it.take(7)
                binding.tvHeartRate.text = last7Data[0].heartValue
                binding.heartRateColorBar.setDataValue(last7Data[0].heartValue!!.toInt())
                binding.tvHeartRateTime.text =
                    WatchUtils.instance.clipTimeFormatSecond(last7Data[0].heartStartTime)
                binding.chartView.setDataValue(last7Data.map { it.heartValue?.toInt() })
            }
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

    fun setBitmapCompat(view: View, bitmap: Bitmap?) {
        val bd = BitmapDrawable(view.context.resources, bitmap)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = bd
        } else {
            view.setBackgroundDrawable(bd)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        watchViewModel.lastHeartRateData.postValue(listOf())
    }
}