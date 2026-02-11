package com.jotangi.cxms.ui.Member

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.Api.book.VideoRecordListData
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentVideoRecordBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.DialogUtils
import kotlinx.coroutines.launch

class VideoRecordFragment : BaseFragment() {

    private lateinit var binding: FragmentVideoRecordBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<VideoRecordFragmentArgs>()
    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }

    private lateinit var videoRecordAdapter: VideoRecordAdapter
    private var videoRecordList = ArrayList<VideoRecordListData>()
    private lateinit var vrlData: VideoRecordListData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("取消視訊掛號")

        videoRecordAdapter = VideoRecordAdapter(requireContext(), videoRecordList)

        binding.rvVideoRecord.apply {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = videoRecordAdapter
        }
    }

    private fun initHandler() {

        videoRecordAdapter.apply {

            dataItemClick = { bookingNo ->

                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                    lifecycleScope.launch {

                        bookViewModel.orderData(bookingNo) {
                            findNavController().navigate(R.id.editeDiagnosisInfoFragment)
                        }

                        dialog.dismiss()
                    }
                }
            }

            enterItemClick = { enterI ->

                vrlData = bookViewModel.videoRecordListData.value!![enterI]
                val bookingNo = videoRecordList[enterI].booking_no
                Log.d(TAG, "enterI booking_no: $bookingNo")

                if (!bookingNo.isNullOrBlank()) {
                    enter(bookingNo)
                }
            }

            cancelItemClick = { cancelI ->

                val booking_no = videoRecordList[cancelI].booking_no
                Log.d(TAG, "cancelI booking_no: $booking_no")

                lifecycleScope.launch {

                    booking_no?.let { it1 ->

                        val response = apiRepository.videoRecordCancel(it1)

                        if (response.code == "0x0200") {

                            if (args.cancel == "y") {
                                lifecycleScope.launch {
                                    bookViewModel.videoRecordListCancel(ApiUrl.c_sid){}
                                }
                            }else {
                                lifecycleScope.launch {
                                    bookViewModel.videoRecordList(ApiUrl.c_sid){}
                                }
                            }

                        } else {

                            DialogUtils.showSingle(requireActivity(), "取消失敗", response.responseMessage) {}
                        }
                    }
                }
            }
        }
    }

    private fun enter(no : String) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                val response = apiRepository.videoRecordOutpatient(no)

                if (response.isEmpty()) {
                    dialog.dismiss()
                    return@launch
                }

                when (response[0].reserve_status) {

                    "1","4","6" -> {

                        val url = response[0].meetingUrl2
                        Log.d(TAG, "meetingUrl2: $url")

                        if (url.isNullOrBlank()) {

                            dialog.dismiss()

                            DialogUtils.showSingle(
                                requireActivity(),
                                "注意",
                                "尚未到看診時間，請於約定時間點擊查看候診資訊，謝謝！"
                            ) {}

                        } else {

                            val response = apiRepository.updatebookingStatus2(no)
                            dialog.dismiss()

                            if (response.code == "0x0200") {
                                findNavController().navigate(
                                    VideoRecordFragmentDirections.actionVideoRecordFragmentToPhysicianMeetingFragment(url!!)
                                )
                            }
                        }
                    }

                    "8" -> {

                        val memo = response[0].memo
                        Log.d(TAG, "memo: $memo")

                        dialog.dismiss()

                        findNavController().navigate(
                            VideoRecordFragmentDirections.actionVideoRecordFragmentToPhysicianMemoFragment(
                                vrlData,
                                response[0]
                            )
                        )
                    }
                }
            }
        }
    }

    private fun initCallBack() {

        bookViewModel.apply {

            videoRecordListData.observe(viewLifecycleOwner) {

                videoRecordList.clear()
                videoRecordList.addAll(it)
                videoRecordAdapter.notifyDataSetChanged()
            }
        }
    }
}