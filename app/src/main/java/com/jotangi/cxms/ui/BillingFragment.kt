// BillingFragment.kt (Rewritten to use ConcatAdapter and move point & amount sections into RecyclerView)

package com.jotangi.cxms.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentBillingBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.ui.home.Appointment2
import com.jotangi.cxms.ui.home.TreatmentItem
import com.jotangi.cxms.ui.mylittlemin.BaseBookRequest
import kotlinx.coroutines.launch

class BillingFragment : BaseFragment() {
    private lateinit var binding: FragmentBillingBinding
    private lateinit var recyclerView: RecyclerView
    private var isCheckoutEnabled = true

    private var enteredPoints = 0
    private var availablePoints = 0
    private var totalPoint = ""
    private var totalAmount = 0
    private var finalAmount = 0

    override fun getToolBar(): ToolbarBinding = binding.toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            bookViewModel.getMemberPointList(if (true) 1 else 0)
        }
        bookViewModel.memberPointList.observe(viewLifecycleOwner) { result ->
            totalPoint = result.sumOf { it.point }.toString()
            availablePoints = totalPoint.toIntOrNull() ?: 0
            Log.d("micCheckFH2", "Got totalPoint from ViewModel: $totalPoint")
        }
        val pointsView = layoutInflater.inflate(R.layout.item_points_section, recyclerView, false)
        val amountView = layoutInflater.inflate(R.layout.item_main_container, recyclerView, false)

        val pointsAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(pointsView) {}
            }

            override fun getItemCount() = 1
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val pointsCheckbox =
                    pointsView.findViewById<android.widget.CheckBox>(R.id.usePointsCheckbox)
                val pointsInput = pointsView.findViewById<android.widget.EditText>(R.id.pointsValue)

                pointsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    enteredPoints = pointsInput.text.toString().toIntOrNull() ?: 0
                    availablePoints = totalPoint.toIntOrNull() ?: 0
                    Log.d("micCheckFH1", availablePoints.toString())
                    if (isChecked) {
                        if (enteredPoints > availablePoints) {
                            Toast.makeText(requireContext(), "超過現有點數", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val discountText = amountView.findViewById<TextView>(R.id.discountTotal)
                            discountText.text = "-$enteredPoints"
                            finalAmount = totalAmount - enteredPoints
                            amountView.findViewById<TextView>(R.id.finalAmount).text =
                                "$${finalAmount}"
                        }
                    } else {
                        pointsInput.setText("")
                        amountView.findViewById<TextView>(R.id.discountTotal).text = "-0"
                        amountView.findViewById<TextView>(R.id.finalAmount).text = "$${totalAmount}"
                    }
                }

                pointsInput.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (!pointsCheckbox.isChecked) return
                        enteredPoints = s.toString().toIntOrNull() ?: 0
                        if (enteredPoints > availablePoints) {
                            Toast.makeText(requireContext(), "超過現有點數", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            amountView.findViewById<TextView>(R.id.discountTotal).text =
                                "-$enteredPoints"
                            finalAmount = totalAmount - enteredPoints
                            amountView.findViewById<TextView>(R.id.finalAmount).text =
                                "$${finalAmount}"
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
            }
        }

        val amountAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(amountView) {}
            }

            override fun getItemCount() = 1
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                amountView.findViewById<TextView>(R.id.totalAmount).text = "$${totalAmount}"
                amountView.findViewById<TextView>(R.id.discountTotal).text = "-0"
                amountView.findViewById<TextView>(R.id.finalAmount).text = "$${totalAmount}"
            }
        }




        lifecycleScope.launch {
//            val pointResponse = apiBook.getPoint()
//            val pointData = pointResponse.data.firstOrNull()
//            totalPoint = pointData?.totalPoint ?: "0"
            Log.d("micCheckFH2", totalPoint.toString())
            val reservations = apiBook.getPayList()
            val treatmentItems = groupByTreatment(reservations)
            totalAmount = calculateTotalAmount(treatmentItems)
            finalAmount = totalAmount

            val treatmentAdapter = TreatmentAdapter(treatmentItems)
            val concatAdapter = ConcatAdapter(treatmentAdapter, pointsAdapter, amountAdapter)
            recyclerView.adapter = concatAdapter
        }

        binding.checkoutButton.setOnClickListener {
            if (!isCheckoutEnabled) {
                Toast.makeText(requireContext(), "請勿連續點擊", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredPoints > availablePoints) {
                AlertDialog.Builder(context)
                    .setTitle("點數超過上限")
                    .setPositiveButton("確定") { dialog, _ -> dialog.dismiss() }
                    .setCancelable(false)
                    .show()
                return@setOnClickListener
            }

            val pid = BaseBookRequest().member_pid

            lifecycleScope.launch {
                if (finalAmount < 0) {
                    AlertDialog.Builder(context)
                        .setTitle("錯誤")
                        .setMessage("折抵點數錯誤, 請勿超過總金額$${totalAmount}")
                        .setPositiveButton("確定") { dialog, _ -> dialog.dismiss() }
                        .setCancelable(false)
                        .show()
                    return@launch
                }

                // ⛔ Disable logic for 2 seconds, but keep button enabled
                isCheckoutEnabled = false
                launch {
                    kotlinx.coroutines.delay(2_000L)
                    isCheckoutEnabled = true
                }

                try {
                    val response = apiBook.createOrder(totalAmount, totalPoint.toInt(), finalAmount)
                    val url =
                        "https://pay.digimed.tw/smc2/payregister.php?pid=$pid&payid=${response.responseMessage}&amount=$finalAmount"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "發生錯誤，無法前往付款頁面",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

        private fun groupByTreatment(reservations: List<Reservation>): List<TreatmentItem> {
        return reservations.groupBy { it.treatmentName }.map { (treatmentName, group) ->
            val price = group.firstOrNull()?.treatmentPrice?.toIntOrNull() ?: 0
            val appointments = group.map { Appointment2("${it.reserveDate} ${it.reserveTime}") }
            TreatmentItem(treatmentName, price, appointments)
        }
    }

    private fun calculateTotalAmount(items: List<TreatmentItem>): Int {
        return items.sumOf { it.price * it.appointments.size }
    }



}
