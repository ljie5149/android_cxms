package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentWriteDataBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DateTimeUtil
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import kotlinx.coroutines.launch

class WriteDataFragment : BaseFragment() {

    private lateinit var binding: FragmentWriteDataBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<WriteDataFragmentArgs>()

    //    private lateinit var familyNameList: ArrayList<String>
    private var spItemNum = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("填寫資料")

        binding.apply {

            val avror = args.addVideoReserveOrderRequest
            val ms = DateTimeUtil.instance.clipHmsTohm(avror.reserve_time)

            tvWrdRiDivisionContent.text = avror.price

            tvWrdRiPhysicianContent.text = avror.doctorName

            tvWrdRiTimeContent.text = "${avror.reserve_date} $ms"

            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_wdf_item,
                listOf("E-mail", "地址")
            )

            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_wdf_item)
            spAddressEmail.adapter = arrayAdapter
            spAddressEmail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    spItemNum = p2 + 1
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

//            familyNameList = ArrayList()
//            val member = bookViewModel.memberInfoDataList.value!![0]
//            familyNameList.add(member.member_name!!)
//
//            val familyList = bookViewModel.familyListLiveData.value!!
//            for (i in familyList.indices) {
//                familyNameList.add(familyList[i].family_name!!)
//            }
//
//            val arrayAdapter = ArrayAdapter(
//                requireContext(),
//                R.layout.spinner_wdf_item,
//                familyNameList
//            )
//
//            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_wdf_item)
//            spName.adapter = arrayAdapter
//
//            spName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//                    spItemNum = p2
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {}
//            }
        }
    }

    private fun initHandler() {

        binding.apply {

            btRtNext.setOnClickListener {

                val name = etName.text.toString().trim()
                val address = etWrdUiEmail.text.toString().trim()

                if (name.isNotEmpty()) {

                    val wd = args.addVideoReserveOrderRequest

                    wd.member_phone = SharedPreferencesUtil.instances.getAccountId().toString()
                    wd.member_name = name
                    wd.invoice_type = spItemNum.toString()
                    wd.invoice_address = address

                    DialogUtil.instance.loadingShow(requireActivity()) {

                        lifecycleScope.launch {

                            bookViewModel.addVideoReserveOrder(wd) { order ->

                                Log.d(TAG, "sid: ${args.addVideoReserveOrderRequest.sid}")
                                Log.d(TAG, "預約單號: $order")
                                // 上傳診前資訊
                                findNavController().navigate(
                                    WriteDataFragmentDirections.actionWriteDataFragmentToUploadDataFragment(
                                        args.addVideoReserveOrderRequest.sid,
                                        order
                                    )
                                )
                            }

                            it.dismiss()
                        }
                    }

                } else {

                    showErrorMsgDialog("請填妥姓名")
                }
            }
        }
    }

    private fun initCallBack() {

        bookViewModel.memberInfoDataList.observe(viewLifecycleOwner) {

            binding.etWrdUiEmail.setText(it[0].member_email)
        }
    }
}