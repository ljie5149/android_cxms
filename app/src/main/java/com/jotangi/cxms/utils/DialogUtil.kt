package com.jotangi.cxms.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.jotangi.cxms.Api.book.apirequest.UserEditRequest
import com.jotangi.cxms.Api.book.apiresponse.PhysicianScheduleData
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationBean
import com.jotangi.cxms.Api.book.apiresponse.HisRegistrationListBean
import com.jotangi.cxms.Api.book.apiresponse.SleepWellBookingListBean
import com.jotangi.cxms.Api.book.apiresponse.SleepWellWorkingDayBean
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.DialogActivityBinding
import com.jotangi.cxms.databinding.DialogCheckTimeBinding
import com.jotangi.cxms.databinding.DialogConvertBinding
import com.jotangi.cxms.databinding.DialogEditEmailBinding
import com.jotangi.cxms.databinding.DialogEditNameBinding
import com.jotangi.cxms.databinding.DialogEditPhoneBinding
import com.jotangi.cxms.databinding.DialogInsertDataArmBinding
import com.jotangi.cxms.databinding.DialogInsetDataKcalBinding
import com.jotangi.cxms.databinding.DialogMessageSingleBinding
import com.jotangi.cxms.databinding.DialogMultiMessageBinding
import com.jotangi.cxms.databinding.DialogReserveAgreeBinding
import com.jotangi.cxms.databinding.DialogReserveCancelBinding
import com.jotangi.cxms.databinding.DialogReserveHintBinding
import com.jotangi.cxms.databinding.DialogReserveListBinding
import com.jotangi.cxms.databinding.DialogSelectTimeBinding
import com.jotangi.cxms.databinding.ProgressLoadingBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.jotangi.cxms.utils.smartwatch.apirequest.ArmUploadRequest
import com.jotangi.cxms.utils.smartwatch.apirequest.KcalUploadRequest
import com.jotangi.cxms.utils.smartwatch.apiresponse.GetBp2DataBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class DialogUtil private constructor() {

    private val TAG = "DialogUtil"

    companion object {
        val instance: DialogUtil = DialogUtil()
    }

    interface OnArmDataListener {
        fun onData(request: ArmUploadRequest)
    }

    interface OnKcalDataListener {
        fun onData(request: KcalUploadRequest)
    }

    fun loadingShow(activity: Activity, pd: (dialog: Dialog) -> Unit) {

        val viewGroup = activity.findViewById<ViewGroup>(R.id.cl_pg)
        val binding = ProgressLoadingBinding.inflate(
            LayoutInflater.from(activity), viewGroup, false
        )
        val dialog = Dialog(activity, R.style.NewDialog)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.show()
        pd(dialog)
    }

    fun singleMessageDialog(
        activity: Activity,
        title: String,
        content: String,
        cancelClick: () -> Unit
    ) {
        singleMessageDialog(activity, title, content, "關閉", cancelClick)
    }

    fun singleMessageDialog(
        activity: Activity,
        title: String,
        content: String,
        cancel: String,
        cancelClick: () -> Unit
    ) {
        val viewGroup = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogMessageSingleBinding.inflate(
            LayoutInflater.from(activity), viewGroup, false
        )

        val alertDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {

            if (title.isEmpty()) {
                tvDmsTitle.visibility = View.GONE
            } else {
                tvDmsTitle.text = title
            }
            tvDmsContent.text = content
            tvDmsCancel.text = cancel

            tvDmsCancel.setOnClickListener {
                alertDialog.dismiss()
                cancelClick.invoke()
            }
        }

        alertDialog.show()
    }

    fun multiMessageDialog(
        activity: Activity,
        title: String,
        content: String,
        okTxt: String,
        cancelTxt: String,
        okClick: () -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogMultiMessageBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            if (title.isEmpty()) {
                tvTitle.visibility = View.GONE
            } else {
                tvTitle.text = title
            }

            tvContent.text = content

            if (okTxt.isNotBlank()) {
                tvOk.text = okTxt
            }

            if (cancelTxt.isNotBlank()) {
                tvCancel.text = cancelTxt
            }

            tvOk.setOnClickListener {
                ad.dismiss()
                okClick()
            }

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun selectTimeDialog(
        activity: Activity,
        time: String,
        list: List<SleepWellWorkingDayBean>,
        okClick: (SleepWellWorkingDayBean) -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogSelectTimeBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvTitle.text = "日期：${
                DateTimeUtil.instance.chinaYmdDate(list[0].workingdate.toString())
            }"
            tvContent.text = "預約時段：$time"

            var radioButton: RadioButton
            var selectNum = -1

            for (i in list.indices) {

                radioButton = RadioButton(activity)
                with(list[i]) {
                    radioButton.text = "${starttime}-${endtime}(${roomno})"
                }
                radioButton.id = i
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60F)

                rgSingleton.addView(radioButton)
            }

            rgSingleton.setOnCheckedChangeListener { _, checkedId ->
                selectNum = checkedId
            }

            tvOk.setOnClickListener {

                if (selectNum == -1) {
                    Toast.makeText(activity, "請選擇時段。", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                ad.dismiss()
                okClick(list[selectNum])
            }

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun checkTimeDialog(
        activity: Activity,
        time: String,
        item: SleepWellWorkingDayBean,
        okClick: () -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogCheckTimeBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvTitle.text = "日期：${
                DateTimeUtil.instance.chinaYmdDate(item.workingdate.toString())
            }"
            tvContent.text = "預約時段：$time"
            tvPew.text = "座位：${item.roomno}"
            tvCheckInTime.text = "預約時間：${item.starttime}-${item.endtime}"
            tvRunTime.text = "體驗時段：${
                DateTimeUtil.instance.about20Minute(
                    item.starttime.toString(),
                    item.endtime.toString()
                )
            }"

            tvOk.setOnClickListener {
                ad.dismiss()
                okClick()
            }

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun cancelTimeDialog(
        activity: Activity,
        item: SleepWellBookingListBean,
        okClick: () -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogCheckTimeBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvTitle.text = "日期：${
                DateTimeUtil.instance.chinaYmdDate(item.reserveDate.toString())
            }"
            tvContent.text = "預約時段：${
                if (item.shiftCode == "1") "上午" else "下午"
            }"
            tvPew.text = "座位：${item.roomNo}"
            tvCheckInTime.text = "預約時間：${item.reserveStarttime}-${item.reserveEndtime}"
            tvRunTime.text = "體驗時段：${
                DateTimeUtil.instance.about20Minute(
                    item.reserveStarttime.toString(),
                    item.reserveEndtime.toString()
                )
            }"
            tvOk.text = "取消預約"
            tvCancel.text = "離開"

            tvOk.setOnClickListener {
                ad.dismiss()
                okClick()
            }

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun editName(
        activity: Activity,
        request: UserEditRequest,
        okClick: (UserEditRequest) -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogEditNameBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvOk.setOnClickListener {

                if (etContent.text.toString().isBlank()) {
                    Toast.makeText(ad.context, "姓名不能為空白", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                request.name = etContent.text.toString().trim()

                ad.dismiss()
                okClick(request)
            }

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun editPhone(
        activity: Activity,
        request: UserEditRequest,
        okClick: (UserEditRequest) -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogEditPhoneBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvOk.setOnClickListener {

                if (etContent.text.toString().isBlank()) {
                    Toast.makeText(ad.context, "電話不能為空白", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                request.member_id = etContent.text.toString().trim()

                ad.dismiss()
                okClick(request)
            }

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun editEmail(
        activity: Activity,
        request: UserEditRequest,
        okClick: (UserEditRequest) -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogEditEmailBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvOk.setOnClickListener {

                request.email = etContent.text.toString().trim()

                ad.dismiss()
                okClick(request)
            }

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun activity(
        activity: Activity
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogActivityBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            ivCancel.setOnClickListener {
                ad.dismiss()
            }
            ivActivity.setOnClickListener {
                ad.dismiss()
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://forms.gle/HCU2hAKs8yvtQYJR9")
                    )
                )
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(15000)
            ad.dismiss()
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }


    fun reserve(
        activity: Activity,
        day: String,
        data: PhysicianScheduleData,
        okClick: (PhysicianScheduleData) -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogReserveListBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {
            tvDate.text = "日期：$day"
            tvTime.text = "看診時段：${
                when (data.班別) {
                    "上午診" -> "早診"
                    "下午診" -> "午診"
                    "晚診" -> "晚診"
                    else -> data.班別.toString()
                }
            }"
            tvDivision.text = "科別：${data.科別}"
            tvDoctor.text = "醫師：${
                if (data.代班醫師名 == "無")
                    data.醫師名.toString() else data.代班醫師名.toString()
            }"
            tvCancel.setOnClickListener {
                ad.dismiss()
            }
            tvOk.setOnClickListener {
                ad.dismiss()
                okClick(data)
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun reserveHint(
        activity: Activity,
        day: String,
        data: HisRegistrationBean,
        okClick: (HisRegistrationBean) -> Unit,
        cancelClick: () -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogReserveHintBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvDate.text = "日期：$day"
            tvTime.text = "看診時段：${data.班別}"
            tvDivision.text = "科別：${data.科別}"
//            tvDivisionCount.text = "診次：${data.診別}"
            tvDoctor.text = "醫師姓名：${data.醫師}"
            tvReserveId.text = "預約號碼：${data.掛號序號}"

            tvCancel.setOnClickListener {
                ad.dismiss()
                cancelClick()
            }
            tvOk.setOnClickListener {
                ad.dismiss()
                okClick(data)
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun reserveAgree(
        activity: Activity,
        day: String,
        data: HisRegistrationBean,
        okClick: (HisRegistrationBean) -> Unit,
        cancelClick: () -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogReserveAgreeBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvDate.text = "日期：$day"
            tvTime.text = "看診時段：${data.班別}"
            tvDivision.text = "科別：${data.科別}"
//            tvDivisionCount.text = "診次：${data.診別}"
            tvDoctor.text = "醫師姓名：${data.醫師}"
            tvReserveId.text = "預約號碼：${data.掛號序號}"
            xbAgree.visibility = View.VISIBLE
            tvAgreeHint.visibility = View.GONE

            tvCancel.setOnClickListener {
                ad.dismiss()
                cancelClick()
            }
            tvOk.setOnClickListener {
                if (!xbAgree.isChecked) {
                    tvAgreeHint.visibility = View.VISIBLE
                } else {
                    ad.dismiss()
                    okClick(data)
                }
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun reserveCancel(
        activity: Activity,
        data: HisRegistrationListBean,
        okClick: () -> Unit
    ) {
        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogReserveCancelBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        binding.apply {

            tvDate.text = "日期：${WatchUtils.instance.ymdChinaToWestern(data.日期!!)}"
            tvTime.text = "看診時段：${data.班別}"
            tvDivision.text = "科別：${data.科別}"
            tvDoctor.text = "醫師：${data.醫師名}"
            tvId.text = "預約號碼：${data.掛號序號}"

            tvCancel.setOnClickListener {
                ad.dismiss()
            }
            tvOk.setOnClickListener {
                ad.dismiss()
                okClick()
            }
        }

        ad.window?.setBackgroundDrawableResource(android.R.color.transparent)
        ad.show()
    }

    fun kcal(activity: Activity, listener: OnKcalDataListener) {

        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogInsetDataKcalBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity).setView(binding.root).create()

        val date = WatchUtils.instance.currentYmdHm()
        binding.apply {

            etTime.setText(date)
            etTime.setOnClickListener {
                dialogTime(activity, etTime)
            }

            btnInsertValue.setOnClickListener {

                if (etValue.text.toString().isBlank()) {
                    Toast.makeText(activity, "請填妥相關欄位", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                ad.dismiss()
                listener.onData(
                    KcalUploadRequest(
                        "",
                        etTime.text.toString(),
                        etValue.text.toString(),
                        "1",
                    )
                )
            }
        }

        ad.show()
    }

    fun arm(activity: Activity, list: List<GetBp2DataBean>?, listener: OnArmDataListener) {

        val vg = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogInsertDataArmBinding.inflate(
            LayoutInflater.from(activity), vg, false
        )
        val ad = AlertDialog.Builder(activity).setView(binding.root).create()

        val date = WatchUtils.instance.currentYmdHm()
        binding.apply {

            etTime.setText(date)
            etTime.setOnClickListener {
                dialogTime(activity, etTime)
            }

            btnInsertValue.setOnClickListener {

                if (etHeartRate.text.toString().isBlank() ||
                    etLSys.text.toString().isBlank() ||
                    etRSys.text.toString().isBlank() ||
                    etLDia.text.toString().isBlank() ||
                    etRDia.text.toString().isBlank() ||
                    etLPp.text.toString().isBlank() ||
                    etRPp.text.toString().isBlank() ||
                    etLMap.text.toString().isBlank() ||
                    etRMap.text.toString().isBlank()
                ) {
                    Toast.makeText(activity, "請填妥相關欄位", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                list?.let {
                    for (i in it.indices) {
                        if (it[i].bloodStartTime.toString().contains(etTime.text.toString())) {
                            Toast.makeText(
                                activity, "此時間已重複，請重新選擇時間", Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                    }
                }

                ad.dismiss()
                listener.onData(
                    ArmUploadRequest(
                        "",
                        etTime.text.toString(),
                        etLDia.text.toString(),
                        etLSys.text.toString(),
                        etLPp.text.toString(),
                        etLMap.text.toString(),
                        etRDia.text.toString(),
                        etRSys.text.toString(),
                        etRPp.text.toString(),
                        etRMap.text.toString(),
                        etHeartRate.text.toString(),
                        "1"
                    )
                )
            }
        }

        ad.show()
    }

    private fun dialogTime(activity: Activity, etTime: EditText) {

        var dateVale: String

        val calendar: Calendar = Calendar.getInstance()

        DatePickerDialog(
            activity,
            { p0, p1, p2, p3 ->

                dateVale = "${p1}-${
                    WatchUtils.instance.fixTimeLength((p2 + 1).toString())
                }-${
                    WatchUtils.instance.fixTimeLength(p3.toString())
                } "
                Log.d(TAG, "dateVale: $dateVale")

                TimePickerDialog(
                    activity,
                    { timePicker, i, i2 ->

                        dateVale += "${
                            WatchUtils.instance.fixTimeLength(i.toString())
                        }:${
                            WatchUtils.instance.fixTimeLength(i2.toString())
                        }"
                        Log.d(TAG, "dateTimeVale: $dateVale")

                        etTime.setText(dateVale)
                    },
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(activity)
                ).show()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun convert(activity: Activity, numValue: (String) -> Unit) {

        val viewGroup = activity.findViewById<ViewGroup>(android.R.id.content)
        val binding = DialogConvertBinding.inflate(
            LayoutInflater.from(activity), viewGroup, false
        )

        val dialog = AlertDialog.Builder(activity).setView(binding.root).create()
        binding.apply {

            ivAdd.setOnClickListener {

                tvValue.text = (tvValue.text.toString().toInt() + 1).toString()
            }

            ivMinus.setOnClickListener {

                val value = tvValue.text.toString().toInt()
                if (value < 1) {
                    tvValue.text = "0"
                } else {
                    tvValue.text = (value - 1).toString()
                }
            }

            tvCancel.setOnClickListener {
                dialog.dismiss()
            }

            tvOk.setOnClickListener {
                dialog.dismiss()
                numValue(tvValue.text.toString())
            }
        }

        dialog.show()
    }
}