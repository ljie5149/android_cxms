package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.book.ItemListBean
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentWaitCheckBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.jackyVariant.ConvertText
import com.jotangi.cxms.utils.constant.CheckType
import org.simpleframework.xml.convert.Convert

class WaitCheckFragment : BaseFragment() {

    private lateinit var binding: FragmentWaitCheckBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var waitCheckAdapter: WaitCheckAdapter
    private var list = ArrayList<ItemListBean>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaitCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("待檢項目")

        waitCheckAdapter = WaitCheckAdapter(list)

        binding.rv.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = waitCheckAdapter
        }
    }

    private fun initCallBack() {

        bookViewModel.ItemListBeanLD.observe(viewLifecycleOwner) {

            list.clear()
            var date = ""
            var count = 0

            for (i in it.indices) {
                if (date != ConvertText.changeChinaDate(it[i].日期.toString())) {

                    count = 1
                    date = ConvertText.changeChinaDate(it[i].日期.toString())
                } else {

                    count++
                }

//                if (WatchUtils.instance.chinaTimeToLong(date) <
//                    WatchUtils.instance.beforeOneDayTime()
//                ) {
//                    continue
//                }

                list.add(
                    ItemListBean(
                        it[i].內容, date, it[i].狀態,
                        selectRoom(it[i].科室.toString()), count.toString()
                    )
                )
            }

            waitCheckAdapter.notifyDataSetChanged()

            binding.apply {

                if (list.isEmpty()) cl.visibility = View.GONE
                else cl.visibility = View.VISIBLE
            }
        }
    }

    private fun selectRoom(str: String): String {

        return when (str) {
            CheckType.DrawBlood.value -> "抽血"
            CheckType.Ecg.value -> "心電圖"
            CheckType.Service.value -> "其他服務"
            CheckType.XRay.value -> "X光"
            CheckType.Ultra.value -> "超音波"
            CheckType.Gastroscopy.value -> "腸胃鏡"
            else -> "- - -"
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        bookViewModel.ItemListBeanLD.postValue(listOf())
    }
}