package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.book.apiresponse.HisOplistBean
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentHisOplistBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HisOplistFragment : BaseFragment() {

    private lateinit var binding: FragmentHisOplistBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private lateinit var hisOplistAdapter: HisOplistAdapter
    private var list = ArrayList<HisOplistBean>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHisOplistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("看診進度")

        hisOplistAdapter = HisOplistAdapter(list)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = hisOplistAdapter
        }
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

                        lifecycleScope.launch {
                            bookViewModel.hisOplist(
                                WatchUtils.instance.currentYmd(),
                                success = { },
                                fail = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        showErrorMsgBack(it)
                                    }
                                }
                            )
                        }
                    }
                }

            }.start()
        }
    }

    private fun initCallBack() {

        bookViewModel.hisOplistLD.observe(viewLifecycleOwner) {

            if (it.isNullOrEmpty()) return@observe

            Log.d(TAG, "list: $it")
            list.clear()
            list.addAll(it)
            hisOplistAdapter.notifyDataSetChanged()
            countDown()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        bookViewModel.hisOplistLD.postValue(listOf())
    }
}