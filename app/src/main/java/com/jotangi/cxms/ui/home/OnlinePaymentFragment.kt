package com.jotangi.cxms.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentOnlinePaymentBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import kotlinx.coroutines.launch


class OnlinePaymentFragment : BaseFragment() {
    private lateinit var binding: FragmentOnlinePaymentBinding
var call = false
    override fun getToolBar(): ToolbarBinding = binding.toolbar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlinePaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnlinePyament()
        val btnHealthPayment = view.findViewById<TextView>(/* id = */ R.id.btnHealthPayment)
        val btnSelfPaidPayment = view.findViewById<TextView>(R.id.btnSelfPaidPayment)
        bookViewModel.bookingRecordLiveData5.observe(viewLifecycleOwner) { apiData ->

if(call) {
            Log.d("micCheckUG", apiData.toString())

            val hasUnfinished = apiData.any { it.reserveStatus.toString() == "1" }

            val message = if (hasUnfinished) "您的療程尚未完成" else "查無資料"

            android.app.AlertDialog.Builder(requireContext())
                .setTitle("提醒")
                .setMessage(message)
                .setPositiveButton("確定") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_onlinePaymentFragment_to_main)
                    Log.d("micCheckUG", "User confirmed the dialog")
                }
                .show() // ← Don't forget this!
call = false
        }
            }


        btnHealthPayment.setOnClickListener {

            lifecycleScope.launch {
                val intent = Intent(Intent.ACTION_VIEW)
                val response = apiBook.memberInfo()
Log.d("micCheckLJ", response.toString())
                val url = Uri.parse(
                    "https://pay.digimed.tw/smc/payindex.php?sid=159&member_pid=${
                        response[0].member_pid
                    }"
                )
                intent.data = url
                startActivity(intent)
            }
        }

        btnSelfPaidPayment.setOnClickListener {

            AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("請洽詢櫃檯")
                .setPositiveButton("確定") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

//            lifecycleScope.launch {
//                val reservations = apiBook.getPayList() // Assume this returns List<Reservation>
//                Log.d("micCheckJJJ2", reservations.toString())
//                if(reservations.isNullOrEmpty()) {
//                    call = true
//                    bookViewModel.getBookingRecord5All(ApiUrl.c_sid.toInt())
//                } else {
//                    findNavController().navigate(R.id.action_onlinePaymentFragment_to_billingFragment)
//
//                }
//            }


        }
    }
}
