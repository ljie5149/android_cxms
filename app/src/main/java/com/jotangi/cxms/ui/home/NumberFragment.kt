package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentNumberBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.constant.CheckType
import com.jotangi.cxms.utils.constant.QrType
import kotlinx.coroutines.launch
import org.json.JSONObject

class NumberFragment : BaseFragment() {

    private lateinit var binding: FragmentNumberBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("報到取號")
    }

    private fun initHandler() {

        binding.apply {

            tvDrawBlood.setOnClickListener {
                goQrCode(CheckType.DrawBlood.value)
            }

            tvEcg.setOnClickListener {
                goQrCode(CheckType.Ecg.value)
            }

            tvService.setOnClickListener {
                checkInSuccess("99")
            }

            tvXRay.setOnClickListener {
                goQrCode(CheckType.XRay.value)
            }

            tvUltra.setOnClickListener {
                goQrCode(CheckType.Ultra.value)
            }

            tvGastroscopy.setOnClickListener {
                goQrCode(CheckType.Gastroscopy.value)
            }
        }
    }

    private fun goQrCode(num: String) {

        findNavController().navigate(
            R.id.scanCouponFragment,
            bundleOf(
                QrType.Fragment.value to QrType.Number.value,
                QrType.Number.value to num
            )
        )
    }

    private fun initCallBack() {

        val jsonValue = arguments?.getString(QrType.JsonValue.value, "") ?: return
        val jsonNumber = arguments?.getString(QrType.Number.value, "") ?: return
        Log.w(TAG, "jsonValue: $jsonValue  jsonNumber: $jsonNumber")

        if (jsonValue.isEmpty() || jsonNumber.isEmpty()) return

        try {

            val id = JSONObject(jsonValue).getString("counter_id")

            id.split(",").forEach {

                if (it == jsonNumber) {
                    checkInSuccess(jsonNumber)
                    return
                }
            }

            showErrorMsgDialog("沒有此檢驗項目")

        } catch (e: Exception) {
            e.printStackTrace()
            showErrorMsgDialog("格式錯誤")
        }
    }

    private fun checkInSuccess(num: String) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                val response = apiBook.hisCheckItem2(num)

                if (response.code == "0x0200" && !response.list.isNullOrBlank()) {

                    if (response.responseMessage == "已取號!") {

                        DialogUtil.instance.singleMessageDialog(
                            requireActivity(),
                            "已取號",
                            "您的號碼為${response.list}號",
                            "確認"
                        ) {}
                    } else {

                        DialogUtil.instance.singleMessageDialog(
                            requireActivity(), "", response.responseMessage.toString()
                        ) {
                            findNavController().navigate(
                                R.id.checkNumberFragment,
                                bundleOf(
                                    QrType.Number.value to num,
                                    QrType.SerialNumber.value to response.list,
                                )
                            )
                        }
                    }
                } else {

                    showErrorMsgDialog(response.responseMessage.toString())
                }

                dialog.dismiss()
            }
        }
    }
}