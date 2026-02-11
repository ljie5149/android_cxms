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
import com.jotangi.cxms.databinding.FragmentStepCountDetailBinding
import com.jotangi.cxms.utils.smartwatch.model.GetStepsDataBean

class StepCountDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentStepCountDetailBinding
    override fun getToolBar() = binding.toolbar

    private val args by navArgs<StepCountDetailFragmentArgs>()

    private lateinit var stepAdapter: StepAdapter
    private val dataList = arrayListOf<GetStepsDataBean>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepCountDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrowTrend(
            getString(R.string.circle_step_count),
            args.tel,
            StepCountDetailFragmentDirections.actionStepCountDetailFragmentToWebFragment(
                getString(R.string.circle_step_count)
            )
        )

        stepAdapter = StepAdapter(dataList)
        binding.rvStep.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = stepAdapter
        }
    }

    private fun initCallBack() {

        watchViewModel.dayFootStepsData.observe(viewLifecycleOwner) { datas ->
            dataList.clear()
            dataList.addAll(datas)
            stepAdapter.notifyDataSetChanged()
        }
    }
}