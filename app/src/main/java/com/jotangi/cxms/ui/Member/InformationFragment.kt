package com.jotangi.cxms.ui.Member

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.Api.book.apirequest.UserEditRequest
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentInformationBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.jackyVariant.Common
import com.jotangi.cxms.utils.CoverEye
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.SpinnerDatePickerDialog
import com.jotangi.cxms.utils.ViewUtils
import kotlinx.coroutines.launch
import java.util.*


class InformationFragment : BaseFragment() {

    private lateinit var binding: FragmentInformationBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var userEditRequest: UserEditRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarArrow("會員資料")

        initView()
        initHandler()
    }

    private fun initView() {


        bookViewModel.memberInfoDataList.observe(viewLifecycleOwner) {

            with(it[0]) {
                userEditRequest = UserEditRequest(
                    Common.getToken(),
                    member_name.toString().trim(),
                    member_gender.toString(),
                    member_email.toString().trim(),
                    member_birthday.toString().trim(),
                    "null",
                    null,
                    member_id.toString().trim()
                )
            }

            binding.apply {

                tvName.text = it[0].member_name
                rgSex.check(if (it[0].member_gender == "0") R.id.rWomen else R.id.rMan)
                tvEmail.text = it[0].member_email
                tvBirth.text = it[0].member_birthday
                tvPhone.text = it[0].member_id
                tvPid.text = it[0].member_pid

                tvPhone.transformationMethod = CoverEye()
                tvPid.transformationMethod = CoverEye()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initHandler() {

        binding.apply {

            // 姓名
            tvNameEdit.setOnClickListener {

                DialogUtil.instance.editName(
                    requireActivity(),
                    userEditRequest,
                    okClick = { editUserData(it) }
                )
            }

            // 性別
            rMan.setOnClickListener {
                val request = userEditRequest
                request.gender = "1"
                editUserData(request)
            }

            rWomen.setOnClickListener {
                val request = userEditRequest
                request.gender = "0"
                editUserData(request)
            }

            // 生日
            tvBirthEdit.setOnClickListener { showDatePickerDialog() }

            // 電話
            tvPhoneEdit.setOnClickListener {

                DialogUtil.instance.editPhone(
                    requireActivity(),
                    userEditRequest,
                    okClick = { editUserData(it, true) }
                )
            }

            // email
            tvEmailEdit.setOnClickListener {

                DialogUtil.instance.editEmail(
                    requireActivity(),
                    userEditRequest,
                    okClick = { editUserData(it) }
                )
            }

            // eye
            ivPhoneEye.setOnTouchListener { p0, p1 ->

                when (p1!!.action) {

                    MotionEvent.ACTION_DOWN -> {
                        tvPhone.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                    }

                    MotionEvent.ACTION_UP -> {
                        tvPhone.transformationMethod = CoverEye()
                    }
                }

                true
            }

            ivPidEye.setOnTouchListener { p0, p1 ->

                when (p1!!.action) {

                    MotionEvent.ACTION_DOWN -> {
                        tvPid.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                    }

                    MotionEvent.ACTION_UP -> {
                        tvPid.transformationMethod = CoverEye()
                    }
                }

                true
            }

            // 修改密碼
            btFixPwd.setOnClickListener {
                findNavController().navigate(R.id.fixPasswordFragment)
            }
        }
    }

    private fun editUserData(request: UserEditRequest, isChange: Boolean = false) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.userEdit2AndLoading(
                    request,
                    fail = { showErrorMsgDialog(it) }
                )

                dialog.dismiss()
            }
        }

        if (isChange)
            SharedPreferencesUtil.instances.setAccountId(request.member_id)
    }

    private fun showDatePickerDialog() {

        val calendar = Calendar.getInstance()
        val datePickerDialog: DatePickerDialog

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.N) {
            datePickerDialog = SpinnerDatePickerDialog(
                requireActivity(),
                R.style.DatePickerTheme,
                { datePicker, i, i1, i2 ->

                    Log.d(TAG, "SpinnerDate: $i $i1 $i2")
                    var month = (i1 + 1).toString()
                    if (month.length < 2) {
                        month = "0$month"
                    }
                    var day = i2.toString()
                    if (day.length < 2) {
                        day = "0$day"
                    }

                    val request = userEditRequest
                    request.birthday = "${i}-${month}-${day}"
                    editUserData(request)
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
        } else {
            datePickerDialog = DatePickerDialog(
                requireActivity(),
                R.style.DatePickerTheme,
                { datePicker, i, i1, i2 ->

                    Log.d(TAG, "date: $i $i1 $i2")
                    var month = (i1 + 1).toString()
                    if (month.length < 2) {
                        month = "0$month"
                    }
                    var day = i2.toString()
                    if (day.length < 2) {
                        day = "0$day"
                    }

                    val request = userEditRequest
                    request.birthday = "${i}-${month}-${day}"
                    editUserData(request)
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
        }
        ViewUtils.colorizeDatePicker(datePickerDialog.datePicker)
        datePickerDialog.datePicker.spinnersShown = true
        datePickerDialog.datePicker.calendarViewShown = false
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}