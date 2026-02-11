package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentCheckNumberBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.constant.CheckType
import com.jotangi.cxms.utils.constant.QrType

class CheckNumberFragment : BaseFragment() {

    private lateinit var binding: FragmentCheckNumberBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
    }

    private fun initView() {

        setToolbarArrow("檢驗號碼")

        binding.apply {

            val item = titleContent()
            tvTitle.text = "您的${item}號碼為"
            tvMyNum.text =
                arguments?.getString(QrType.SerialNumber.value, "") ?: ""
        }
    }

    private fun titleContent(): String {

        return when (arguments?.getString(QrType.Number.value) ?: "") {
            CheckType.DrawBlood.value -> "抽血"
            CheckType.Ecg.value -> "心電圖"
            CheckType.Service.value -> "其他服務"
            CheckType.XRay.value -> "X光"
            CheckType.Ultra.value -> "超音波"
            CheckType.Gastroscopy.value -> "腸胃鏡"
            else -> "- - -"
        }
    }

    private fun initHandler() {

        binding.tvBack.setOnClickListener {

            findNavController().navigate(
                R.id.action_checkNumberFragment_to_checkFragment
            )
        }
    }
}