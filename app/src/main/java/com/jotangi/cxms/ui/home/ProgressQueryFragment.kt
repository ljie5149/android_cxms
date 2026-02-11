package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.book.CheckBean
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentProgressQueryBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import kotlinx.coroutines.launch

class ProgressQueryFragment : BaseFragment() {

    private lateinit var binding: FragmentProgressQueryBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var progressQueryAdapter: ProgressQueryAdapter
    private var list = ArrayList<CheckBean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProgressQueryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
        initApi()
    }

    private fun initView() {

        setToolbarArrow("進度查詢")

        progressQueryAdapter = ProgressQueryAdapter(list)

        binding.rv.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = progressQueryAdapter
        }
    }

    private fun initCallBack() {

        bookViewModel.CheckBeanLD.observe(viewLifecycleOwner) {

            if (it.isNullOrEmpty()) return@observe

            list.clear()
            list.addAll(it)
            progressQueryAdapter.notifyDataSetChanged()

            countDown()
        }
    }

    private fun initApi() {
        lifecycleScope.launch { bookViewModel.hisCheckItemStatus2() }
    }

    private fun countDown() {

        var count = 20
        binding.tvCountdown.apply {

            object : CountDownTimer(count * 1000L, 1000) {
                override fun onTick(p0: Long) {
                    post {
                        text = "${count}秒後更新"
                        count -= 1
                    }
                }

                override fun onFinish() {
                    post {
                        text = "更新中"
                        initApi()
                    }
                }

            }.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        bookViewModel.CheckBeanLD.value = listOf()
    }
}