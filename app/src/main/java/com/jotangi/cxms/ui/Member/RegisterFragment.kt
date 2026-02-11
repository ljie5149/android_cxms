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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.Api.book.apirequest.UserRegister
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentRegisterBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.jackyVariant.Common
import com.jotangi.cxms.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RegisterFragment : BaseFragment() {

    private lateinit var binding: FragmentRegisterBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private var selectItem = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
    }

    private fun initView() {

        setToolbarArrow("註冊")

        val adapterSelectDocuments = ArrayAdapter(
            requireContext(),
            R.layout.spinner_register_item,
            listOf("身分證字號", "居留證號碼", "護照號碼")
        )

        adapterSelectDocuments.setDropDownViewResource(
            R.layout.spinner_register_down_item
        )

        binding.apply {

            spSelectDocuments.adapter = adapterSelectDocuments
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initHandler() {
        binding.apply {

            tvBirthday.setOnClickListener {
                showDatePickerDialog()
            }

            spSelectDocuments.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        sel: Int,
                        p3: Long
                    ) {
                        selectItem = sel

                        if (sel == 0)
                            tvIdLegend.visible() else tvIdLegend.invisible()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

            ePwd.transformationMethod = CoverPassword()

            ivEye.setOnTouchListener { view, motionEvent ->

                when (motionEvent.action) {

                    MotionEvent.ACTION_DOWN -> {
                        ePwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    }

                    MotionEvent.ACTION_UP -> {
                        ePwd.transformationMethod = CoverPassword()
                    }
                }

                return@setOnTouchListener true
            }

            ePwdAgain.transformationMethod = CoverPassword()

            ivEyeAgain.setOnTouchListener { view, motionEvent ->

                when (motionEvent.action) {

                    MotionEvent.ACTION_DOWN -> {
                        ePwdAgain.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                    }

                    MotionEvent.ACTION_UP -> {
                        ePwdAgain.transformationMethod = CoverPassword()
                    }
                }

                return@setOnTouchListener true
            }

            // 條款
            tvPrivacy.setOnClickListener {
                findNavController().navigate(R.id.usertermsFragment)
            }

            // 註冊會員
            btRe.setOnClickListener {
                memberRegister()
            }
        }
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
                    binding.tvBirthday.text = "${i}-${month}-${day}"
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
                    binding.tvBirthday.text = "${i}-${month}-${day}"
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

    private fun memberRegister() {

        val name: String = binding.eName.text.toString()
        val phone: String = binding.ePhone.text.toString()
        val pwd: String = binding.ePwd.text.toString()
        val pwdAgain = binding.ePwdAgain.text.toString()
        val id = binding.etId.text.toString()
        val birthday = binding.tvBirthday.text.toString()

        when {

            selectItem == 0 && !binding.etId.text.toString().checkId() ->
                showErrorMsgDialog("身分證字號錯誤，請檢查修正後繼續。")

            selectItem == 1 && !binding.etId.text.toString().checkRid() ->
                showErrorMsgDialog("居留證號碼錯誤，請檢查修正後繼續。")

            pwd.isBlank() || pwdAgain.isBlank() || pwd != pwdAgain ->
                showErrorMsgDialog("請確認密碼是否輸入正確")

            name.isBlank() || phone.isBlank() || pwd.isBlank() || id.isBlank()
                    || birthday.isBlank() || !binding.eR.isChecked ->
                showErrorMsgDialog("請檢查欄位是否填上，並勾選同意聲明")

            // 身分證號規則：1英文+9數字
            // 居留證號規則：2英文+8數字
            // 護照號碼規則：9位數，沒有英文
            id.length < 9 -> showErrorMsgDialog("證號長度錯誤")

            else -> register(
                UserRegister(
                    Common.getToken(),
                    phone,
                    pwd,
                    name,
                    id,
                    birthday
                )
            )
        }
    }

    private fun register(register: UserRegister) {

        showProgress()

        lifecycleScope.launch {

            val response = apiBook.userRegister2(register)

            closeProgress()

            if (response.code == "0x0200") {

                CoroutineScope(Dispatchers.Main).launch {
                    DialogUtils.showSingle(
                        requireActivity(),
                        "註冊成功",
                        "請再次重新登入",
                    ) {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }

            } else {

                CoroutineScope(Dispatchers.Main).launch {
                    DialogUtils.showSingle(
                        requireActivity(),
                        "註冊失敗",
                        response.responseMessage,
                    ) {

                    }
                }
            }
        }
    }
}