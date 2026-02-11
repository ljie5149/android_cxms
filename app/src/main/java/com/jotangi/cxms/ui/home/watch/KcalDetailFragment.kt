package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentKcalDetailBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.smartwatch.apiresponse.GetKcalDataBean

class KcalDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentKcalDetailBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private var arrayList = ArrayList<GetKcalDataBean>()
    private lateinit var kcalDetailAdapter: KcalDetailAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKcalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("卡路里紀錄")

        kcalDetailAdapter = KcalDetailAdapter(arrayList)
        binding.rv.apply {

            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = kcalDetailAdapter
        }
    }

    private fun initCallBack() {
        watchViewModel.getKcalList.observe(viewLifecycleOwner) {
            it?.let { list ->
                arrayList.clear()
                arrayList.addAll(list)
                kcalDetailAdapter.notifyDataSetChanged()
            }
        }
    }
}