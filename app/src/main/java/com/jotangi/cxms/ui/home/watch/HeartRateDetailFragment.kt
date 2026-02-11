package com.jotangi.cxms.ui.home.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentHeartRateDetailBinding
import com.jotangi.cxms.utils.smartwatch.model.HeartRateData


class HeartRateDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentHeartRateDetailBinding
    override fun getToolBar() = binding.toolbar

    private val args by navArgs<HeartRateDetailFragmentArgs>()

    private lateinit var hrAdapter: HeartRateAdapter
    private val dataList = arrayListOf<HeartRateData>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeartRateDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrowTrend(
            getString(R.string.circle_heart_rate),
            args.tel,
            HeartRateDetailFragmentDirections.actionHeartRateDetailFragmentToWebFragment(
                getString(R.string.circle_heart_rate)
            )
        )

        hrAdapter = HeartRateAdapter(dataList)
        binding.rvHR.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = hrAdapter
        }
    }

    private fun initCallBack() {

        watchViewModel.lastHeartRateData.observe(viewLifecycleOwner) {
            dataList.clear()
            dataList.addAll(it)
            hrAdapter.notifyDataSetChanged()
        }
    }
}