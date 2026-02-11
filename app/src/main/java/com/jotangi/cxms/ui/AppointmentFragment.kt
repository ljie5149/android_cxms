package com.jotangi.cxms.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentAppointmentBinding
import com.jotangi.cxms.databinding.ItemAppointmentBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentFragment : BaseFragment() {

    private lateinit var binding: FragmentAppointmentBinding
    private lateinit var adapter: AppointmentAdapter
    override fun getToolBar(): ToolbarBinding = binding!!.toolbar
    var cancelDid = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMarketChangePointTitle("查詢/取消預約")
        setupSpinner()
        setupRecyclerView()
        initObserve()
    }

    private fun initObserve() {
        val treatmentMap = mapOf(
            "1" to "EECP",
            "2" to "ILIB",
            "3" to "震波療程",
            "4" to "射頻療程",
            "5" to "紅繩運動療程",
            "6" to "徒手復健療程"
        )

        bookViewModel.cancelBooking5LiveData.observe(viewLifecycleOwner) { apiData ->
            Log.d("micCheckUU", apiData.toString())
            lifecycleScope.launch {
                adapter.updateData(emptyList())
                bookViewModel.getBookingRecord5(ApiUrl.c_sid.toInt(), cancelDid)
            }
        }

        bookViewModel.bookingRecordLiveData5.observe(viewLifecycleOwner) { apiData ->
            Log.d("micCheckJJ", apiData.toString())

            if (apiData.isNullOrEmpty()) {
                adapter.updateData(emptyList()) // Clear RecyclerView
                Toast.makeText(requireContext(), "沒有資料", Toast.LENGTH_SHORT).show() // Show toast
                return@observe
            }

            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            val appointments = apiData.map {
                // Ensure reserveTime is formatted correctly
                val rawTime = it.reserveTime ?: "00:00:00"
                val formattedTime = when {
                    rawTime.length == 4 -> rawTime.substring(0, 2) + ":" + rawTime.substring(2) + ":00" // Convert "1300" to "13:00:00"
                    rawTime.length == 5 && rawTime.contains(":") -> "$rawTime:00" // Convert "13:00" to "13:00:00"
                    rawTime.length == 8 -> rawTime // Already in "HH:mm:ss" format
                    else -> "00:00:00"
                }

                val fullDateTime = "${it.reserveDate} $formattedTime"

                val parsedDateTime = LocalDateTime.parse(fullDateTime, inputFormatter)
                val displayDateTime = parsedDateTime.format(outputFormatter) // Convert to "yyyy-MM-dd HH:mm"

                Appointment(
                    project = treatmentMap[it.did] ?: "未知",
                    date = displayDateTime, // Use formatted date-time without seconds
                    name = it.memberName ?: "Unknown",
                    phone = it.memberPhone ?: "Unknown",
                    status = when (it.reserveStatus) {
                        "0" -> "預約訂單"
                        "1" -> "已報到"
                        "2" -> "取消訂單"
                        "3" -> "已完成"
                        else -> "未知"
                    },
                    bookingNo = it.bookingNo,
                    treatmentpackage = it.treatmentpackage
                )
            }.sortedByDescending {
                LocalDateTime.parse(it.date, outputFormatter) // Sort by date and time
            }

            adapter.updateData(appointments)
        }
    }

    private fun setupSpinner() {
        val options = listOf("請選擇", "EECP", "ILIB", "震波療程", "射頻療程", "紅繩運動療程", "徒手復健療程")
        val treatmentMap = mapOf(
            "EECP" to "1",
            "ILIB" to "2",
            "震波療程" to "3",
            "射頻療程" to "4",
            "紅繩運動療程" to "5",
            "徒手復健療程" to "6"
        )

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        binding.spinner.adapter = spinnerAdapter

        binding.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = options[position]
                val selectedDid = treatmentMap[selectedItem] ?: ""

                if (selectedDid.isNotEmpty()) {
                    lifecycleScope.launch {
                        adapter.updateData(emptyList())
                        Log.d("micCheckJJ",ApiUrl.c_sid.toInt().toString()+ selectedDid.toInt().toString())
                        bookViewModel.getBookingRecord5(ApiUrl.c_sid.toInt(), selectedDid.toInt())
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    fun cancelReserve(appointment: Appointment){
        lifecycleScope.launch {
            val treatmentMap = mapOf(
                "EECP" to "1",
                "ILIB" to "2",
                "震波療程" to "3",
                "射頻療程" to "4",
                "紅繩運動療程" to "5",
                "徒手復健療程" to "6"
            )
            cancelDid = treatmentMap[appointment.project]!!.toInt() ?: "".toInt()
            Log.d("micCheckUU", appointment.bookingNo)
            bookViewModel.cancelBooking5(appointment.bookingNo) // Implement the cancel logic in ViewModel
        }
    }

    private fun promptCancelCheckDialog(appointment: Appointment) {
        val title1 = "項目 : " + appointment.project
        val title2 = "時間 : " + appointment.date

        // Show AlertDialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("預約資訊")
            .setMessage("$title1\n$title2") // \n for line break
            .setPositiveButton("確定取消預約") { _, _ ->
                cancelReserve(appointment)
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss() // Explicitly dismiss the dialog
            }
            .show()
    }


    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(emptyList(), object : AppointmentAdapter.ItemClickListener {
            override fun onCancelClick(appointment: Appointment, position: Int) {
                promptCancelCheckDialog(appointment)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

}

// RecyclerView Adapter
class AppointmentAdapter(private var items: List<Appointment>,  private val listener: ItemClickListener) :
    RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvProject.text = item.project
            tvDate.text = item.date
            tvName.text = item.name
            tvPhone.text = item.phone
tvCount.text = item.treatmentpackage
            tvStatus.text = item.status
            tvStatus.visibility = if (item.status == "預約訂單") View.GONE else View.VISIBLE
            btnAction.visibility = if (item.status == "預約訂單") View.VISIBLE else View.GONE
            btnAction.text = "取消預約"

            when (item.status) {
                "已報到" -> {
                    tvStatus.text = "已報到"
                    tvStatus.visibility = View.VISIBLE
                }
                "取消訂單" -> {
                    tvStatus.text = "已取消"
                    tvStatus.visibility = View.VISIBLE
                }
                "已完成" -> {
                    tvStatus.text = "已完成"
                    tvStatus.visibility = View.VISIBLE
                }
                else -> {
                    tvStatus.visibility = View.GONE
                }
            }


            btnAction.setOnClickListener {
                listener.onCancelClick(item, position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Appointment>) {
        items = newItems
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onCancelClick(appointment: Appointment, position: Int)
    }
}

// Data Model
data class Appointment(
    val project: String,
    val date: String,
    val name: String,
    val phone: String,
    val status: String,
    val bookingNo:String,
    val treatmentpackage: String
)
