package com.jotangi.cxms.ui.home

import CustomerServiceBottomSheet
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentMainBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.CommonKtUtils
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.DialogUtils.dialog
import kotlinx.coroutines.launch

class MainFragment : BaseFragment() {

    private lateinit var binding: FragmentMainBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initApi()
        initView()
        initHandler()
        initCallBack()
    }

    private fun initApi() {
        checkBoxCount()
    }

    private fun initView() {

        setToolbar("禾欣骨科診所")
        showBnv()
    }

    private fun initHandler() {

        binding.apply {

            iv01.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                goneBnv()

                // 就醫說明
                findNavController().navigate(R.id.hospitalLegendFragment)
            }

            iv02.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                // 預約掛號

                goneBnv()

                // 就醫說明
                findNavController().navigate(R.id.clinicMenuFragment)
                // 原程式 - start
//                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->
//
//                    lifecycleScope.launch {
//
//                        bookViewModel.getDivisionList {
//
//                            CoroutineScope(Dispatchers.Main).launch {
//                                findNavController().navigate(R.id.reserveRegisteredFragment)
//                                goneBnv()
//                            }
//                        }
//                        dialog.dismiss()
//                    }
//                }
                // 原程式 - end
            }

            iv03.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                // 取消預約
                findNavController().navigate(R.id.appointFragment)

//                findNavController().navigate(R.id.cancelFragment)

                // 查詢 取消 掛號 -> .2025-06-09 門診摘要
//                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->
//
//                    lifecycleScope.launch {
//                        // 原程式 - start
//                        bookViewModel.sleepWellBookingList(
//                            success = {
//                                findNavController().navigate(R.id.myReserveFragment) // 原程式
//                                goneBnv()
//                            },
//                            fail = {
//                                showErrorMsgDialog(it)
//                            }
//                        )
//
//                        dialog.dismiss()
//                        // 原程式 - end
////                        goneBnv()
//
////                        findNavController().navigate(R.id.recordFragment)
////                        dialog.dismiss()
//                    }
//                }
            }

            iv04.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                goneBnv()

                // 檢查/檢驗
                findNavController().navigate(R.id.reserveFragment)
            }

            iv05.setOnClickListener {

//                if (checkLogout()) return@setOnClickListener
//
//                // 看診進度
//                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->
//
//                    lifecycleScope.launch {
//
//                        bookViewModel.hisOplist(
//                            WatchUtils.instance.currentYmd(),
//                            success = {
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    findNavController().navigate(
//                                        R.id.hisOplistFragment
//                                    )
//                                    goneBnv()
//                                }
//                            },
//                            fail = {
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    showErrorMsgDialog(it)
//                                }
//                            }
//                        )
//
//                        dialog.dismiss()
//                    }
//                }
                if (checkLogout()) return@setOnClickListener

                goneBnv()

                // 檢查/檢驗
                findNavController().navigate(R.id.reserveFragment2)
            }

            iv06.setOnClickListener {
                if (checkLogout()) return@setOnClickListener

                goneBnv()

                // 領藥資訊
                                findNavController().navigate(R.id.action_mainFragment_to_rehabilitationFragment)

//                findNavController().navigate(R.id.action_mainFragment_to_appointFragment)
            }

            // 交通資訊
            iv07.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                goneBnv()

                findNavController().navigate(R.id.positionFragment)

                // 付費視訊
//                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->
//
//                    lifecycleScope.launch {
//
//                        bookViewModel.getDivisionList() {
//                            findNavController().navigate(
//                                R.id.divisionListFragment,
//                                bundleOf(
//                                    DivisionType.DIVISION.name to
//                                            DivisionType.VIDEO.name
//                                )
//                            )
//                        }
//                        dialog.dismiss()
//                    }
//                }
            }

            iv08.setOnClickListener {
                if (checkLogout()) return@setOnClickListener

                findNavController().navigate(R.id.recordFragment)
//                dialog.dismiss()
//                goneBnv()
//                findNavController().navigate(R.id.action_mainFragment_to_onlinePaymentFragment)

//                if (checkLogout()) return@setOnClickListener
//
//                // 線上繳費
//
//                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->
//
//                    lifecycleScope.launch {
//
//                        val response = apiBook.memberInfo()
//
//                        if (response.isEmpty()) {
//
//                            showErrorMsgDialog("無回傳資料")
//                        } else {
//
//                            when (response[0].member_pid) {
//                                "" -> showErrorMsgDialog("此帳號尚未設定身分證字號。\n請前往會員中心設定身分證字號。")
//                                else -> {
//                                    val intent = Intent(Intent.ACTION_VIEW)
//                                    val url = Uri.parse(
//                                        "https://pay.digimed.tw/smc/payindex.php?sid=159&member_pid=${
//                                            response[0].member_pid
//                                        }"
//                                    )
//                                    intent.data = url
//                                    startActivity(intent)
//                                }
//                            }
//                        }
//
//                        dialog.dismiss()
//                    }
//                }
            }

            iv09.setOnClickListener {
                val bottomSheet = CustomerServiceBottomSheet()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                // 躍獅線上
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data = Uri.parse("https://www.yesnet.com.tw/")
//                startActivity(intent)
            }

            ivTimeTable.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                lifecycleScope.launch {

                    bookViewModel.bannerList2(

                        success = {
                            goneBnv()
                            findNavController().navigate(R.id.selectMonthFragment)
                        },

                        fail = {

                            showErrorMsgDialog(it)
                        }
                    )
                }
            }
        }
    }

    private fun initCallBack() {

        bookViewModel.playStoreVersion.observe(viewLifecycleOwner) {

            Log.d(TAG, "playStoreVersion: $it")
            CommonKtUtils.instance.checkVersion(it) {

                showUpVersion()
            }
        }
    }
}