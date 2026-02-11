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
import com.jotangi.cxms.databinding.FragmentBloodPressureDetailBinding
import com.jotangi.cxms.utils.smartwatch.model.BPData


class BloodPressureDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentBloodPressureDetailBinding
    override fun getToolBar() = binding.toolbar

    private val args by navArgs<BloodPressureFragmentArgs>()

    private lateinit var bpAdapter: BPAdapter
    private val dataList = arrayListOf<BPData>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBloodPressureDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrowTrend(
            getString(R.string.circle_blood_pressure),
            args.tel,
            BloodPressureDetailFragmentDirections.actionBloodPressureDetailFragmentToWebFragment(
                getString(R.string.circle_blood_pressure)
            )
        )

        bpAdapter = BPAdapter(dataList)
        binding.rvBP.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = bpAdapter
        }
    }

    private fun initCallBack() {

        watchViewModel.lastBpData.observe(viewLifecycleOwner) {
            dataList.clear()
            dataList.addAll(it)
            bpAdapter.notifyDataSetChanged()
        }
    }
}