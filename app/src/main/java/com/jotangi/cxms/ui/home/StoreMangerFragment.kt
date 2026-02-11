package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentStoreMangerBinding
import com.jotangi.cxms.utils.DialogUtils
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.constant.QrType
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class StoreMangerFragment : BaseFragment() {

    private lateinit var binding: FragmentStoreMangerBinding
    override fun getToolBar() = binding.toolbar

    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreMangerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCallBack()
    }

    private fun initView() {
        // Set the toolbar title and configure the logout button visibility
        setOutToolbarArrow("數體健店長")
        binding.storeLogout.visibility = View.VISIBLE
        activity?.findViewById<View>(R.id.nav_view)?.visibility = View.INVISIBLE // Hide BottomNavigationView

        val startTime = WatchUtils.instance.ago3MonthYmdHms() // Helper function to get the date 3 months ago
        val endTime = WatchUtils.instance.currentYmdHms() // Current date and time

        // Setting up click listeners
        binding.apply {
            mangerQrImageview.setOnClickListener {
                val bundle = bundleOf(QrType.Fragment.value to QrType.StoreManger.value)
                findNavController().navigate(R.id.scanCouponFragment, bundle)
            }

            documentalImageview.setOnClickListener {
                arguments?.clear() // Clear arguments

                // If the user is logged in, fetch history list
                if (SharedPreferencesUtil.instances.getAccountId().isNullOrBlank()) {
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    showProgress()
                    bookViewModel.getHistoryList(startTime, endTime)
                    closeProgress()
                    findNavController().navigate(R.id.historicalRecordFragment)
                }
            }
        }

        // Logout functionality
        binding.storeLogout.setOnClickListener {
            lifecycleScope.launch {
                showProgress()
                val response = apiRepository.userLogout2() // API call for logout
                closeProgress()

                if (!response.code.isNullOrBlank()) {
                    // Clear stored user information after logout
                    SharedPreferencesUtil.instances.setAccountId(null)
                    SharedPreferencesUtil.instances.setAccountPid(null)
                    SharedPreferencesUtil.instances.setAccountPwd(null)
                    SharedPreferencesUtil.instances.setAccountMid(null)
                    bookViewModel.clearData()

                    CoroutineScope(Dispatchers.Main).launch {
                        binding.storeLogout.visibility = View.INVISIBLE
                        activity?.findViewById<View>(R.id.nav_view)?.visibility = View.VISIBLE
                        findNavController().navigate(
                            StoreMangerFragmentDirections.actionStoreMangerFragmentToLoginFragment()
                        )
                    }
                }
            }
        }
    }

    // Handle callback for coupon QR code scanning
    private fun initCallBack() {
        val jsonValue = arguments?.getString(QrType.JsonValue.value, "")
        if (!jsonValue.isNullOrBlank()) {
            try {
                // Parse the JSON object
                val jsonObject = JSONObject(jsonValue)
                val id = jsonObject.getString("customer_id").deAes()
                val coupon = jsonObject.getString("coupon_id").deAes()
                val count = jsonObject.getString("coupon_count").deAes()

                // Show progress dialog
                showProgress()
                lifecycleScope.launch {
                    // API call to scan the coupon
                    val response = apiRepository.getScanCoupon(id, coupon, count)
                    closeProgress()

                    // Handle response from API
                    if (response.code != "0x0200") {
                        showErrorMsgDialog(response.responseMessage.toString())
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            DialogUtils.showSingle(
                                requireActivity(),
                                "訊息",
                                "兌換成功"
                            ) {
                                // Callback after showing the dialog
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showErrorMsgDialog("格式錯誤")
            }
        }
    }
}
