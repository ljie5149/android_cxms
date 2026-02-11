package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.book.apiresponse.ComplexRegisterListData
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentMyReserveBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.Const
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MyReserveFragment : BaseFragment() {

    private lateinit var binding: FragmentMyReserveBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var myReserveAdapter: MyReserveAdapter
    private var list = arrayListOf<ComplexRegisterListData>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyReserveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("取消預約")

        myReserveAdapter = MyReserveAdapter(list)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = myReserveAdapter
        }
    }

    private fun initHandler() {

        myReserveAdapter.questionClick = {

            SharedPreferencesUtil.instances.setOrder(
                "sid=${
                    ApiUrl.c_sid
                }&member_pid=${
                    SharedPreferencesUtil.instances.getAccountPid()
                }&division_name=${
                    it.科別
                }&doctor_name=${
                    it.醫師名
                }"
            )

            findNavController().navigate(
                MyReserveFragmentDirections.actionMyReserveFragmentToWebFragment(
                    getString(R.string.pre_back_question)
                )
            )
        }

        myReserveAdapter.cancelClick = {

            if (it.type == Const.dataRegister) {

                DialogUtil.instance.reserveCancel(requireActivity(), it.register!!) {
                    reserveCancel(it.register!!.預掛識別碼.toString())
                }
            } else {

                DialogUtil.instance.cancelTimeDialog(
                    requireActivity(),
                    it.sleepWell!!,
                    okClick = {
                        
                        if (it.sleepWell!!.reserveDate.toString() == LocalDate.now().toString() &&
                            it.sleepWell!!.reserveEndtime.toString() <= LocalTime.now().toString()
                                .substring(0, 5)
                        ) {

                            showErrorMsgDialog("您的預約時段已結束")
                        } else {

                            cancelBookingNo(it.sleepWell!!.bookingNo.toString())
                        }
                    }
                )
            }
        }
    }

    private fun reserveCancel(id: String) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                val response = apiBook.hisCancelRegistration2(id)

                if (response.code == ApiUrl.success) {

                    bookViewModel.sleepWellBookingListEasy()
                } else {

                    showErrorMsgDialog(response.responseMessage.toString())
                }

                dialog.dismiss()
            }
        }
    }

    private fun cancelBookingNo(no: String) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                val response = apiBook.sleepWellBookingCancel(no)

                if (response.code == ApiUrl.success) {

                    bookViewModel.sleepWellBookingListEasy()
                } else {

                    showErrorMsgDialog(response.responseMessage.toString())
                }

                dialog.dismiss()
            }
        }
    }

    private fun initCallBack() {

        bookViewModel.ComplexRegisterListDataLD.observe(viewLifecycleOwner) {

            list.clear()
            list.addAll(it)
            myReserveAdapter.notifyDataSetChanged()
        }
    }
}