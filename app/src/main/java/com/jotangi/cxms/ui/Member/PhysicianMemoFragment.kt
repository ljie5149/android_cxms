package com.jotangi.cxms.ui.Member

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentPhysicianMemoBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class PhysicianMemoFragment : BaseFragment() {

    private lateinit var binding: FragmentPhysicianMemoBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<PhysicianMemoFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhysicianMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {

        setToolbarArrow("諮詢備忘錄")

        binding.apply {

            tvMemoPersonContent.text = args.videoRecordListData.doctor_name
//            tvMemoDivisionContent.text = args.videoRecordListData.division_name
            tvMemoDateContent.text = args.videoRecordOutpatientData.outpatient_date
            tvMemoContent.text = args.videoRecordOutpatientData.memo
            tvMemoContent.movementMethod = ScrollingMovementMethod()
        }
    }
}