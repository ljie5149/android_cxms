package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentHomeBinding
import com.jotangi.cxms.utils.CommonKtUtils
import com.jotangi.cxms.utils.SharedPreferencesUtil


class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun getToolBar() = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initApi()
        initView()
        initHandler()
        initCallBack()
    }

    private fun initApi() {
        checkBoxCount()
    }

    private fun initView() {

        setToolbar("量測項目")

        binding.apply {
            if (SharedPreferencesUtil.instances.getAccountId() == "53651543") {
                findNavController().navigate(R.id.storeMangerFragment)
            } else {
                viewPager2.adapter = ViewPagerAdapter(this@HomeFragment)
                ciBottomCircle.setViewPager(viewPager2)
            }
        }
    }

    private fun initHandler() {


    }

    private fun initCallBack() {

        bookViewModel.playStoreVersion.observe(viewLifecycleOwner) {

            Log.d(TAG, "playStoreVersion: $it")
            CommonKtUtils.instance.checkVersion(it) {

                showUpVersion()
            }
        }

        SharedPreferencesUtil.instances.getAccountId().let {

            if (!it.isNullOrBlank()) {

                watchViewModel.isBleConnect.observe(viewLifecycleOwner) { boolean ->
                    setToolbarWatchStatus(boolean)
                }
            }
        }
    }
}