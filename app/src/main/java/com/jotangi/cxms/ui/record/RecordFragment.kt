package com.jotangi.cxms.ui.record

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.ApiConnect
import com.jotangi.cxms.Api.book.HisRecordListData
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentRecordBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.jackyVariant.ConvertText
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordFragment : BaseFragment() {

    private lateinit var binding: FragmentRecordBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private var dataRecord = mutableListOf<HisRecordListData>()
    private lateinit var recordAdapter: RecordAdapter

    private var allRecords: List<HisRecordListData> = emptyList()
    private var afterFilterRecords: List<HisRecordListData> = emptyList()
    private var currentPage = 0
    private var pageSize = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarArrow("門診摘要")
        initObject()
        val mytoday = ConvertText.getFormattedDate("")
        binding.apply {
//            clDateSelection.visibility = View.GONE
            val dft_date = ConvertText.getTaiwanDate(ConvertText.getDateBefore(mytoday, -365))
            val now_date = ConvertText.getTaiwanDate(mytoday)
            etStartDate.setText(dft_date)
            etEndDate.setText(now_date)
            fetchData(etStartDate.text.toString(), etEndDate.text.toString())
        }
    }

    companion object {
        var lst_data = mutableListOf<HisRecordListData>()
        var idx = -1
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initObject() {
        binding.apply {
            // 防止手動輸入 - start
            etStartDate.inputType = InputType.TYPE_NULL
            etStartDate.isFocusable = false

            etEndDate.inputType = InputType.TYPE_NULL
            etEndDate.isFocusable = false
            // 防止手動輸入 - end

            // 日曆選擇器 - start
            val calendar = Calendar.getInstance()

            val dateSetListener = { editText: EditText ->
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        val selectedDate =
                            String.format("%03d-%02d-%02d", year - 1911, month + 1, dayOfMonth)
                        editText.setText(selectedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            // 日曆選擇器 - end

            // 元件觸發日期選擇器
            etStartDate.setOnClickListener {
                dateSetListener(etStartDate)
            }

            etEndDate.setOnClickListener {
                dateSetListener(etEndDate)
            }

            btnSearch.setOnClickListener {
                fetchData(etStartDate.text.toString(), etEndDate.text.toString())
            }

            llPrev.setOnClickListener {
                if ((currentPage + 1) * pageSize < afterFilterRecords.size) {
                    currentPage++
                    initRecyclerView(getPagedData(currentPage))
                }
            }

            llNext.setOnClickListener {
                if (currentPage > 0) {
                    currentPage--
                    initRecyclerView(getPagedData(currentPage))
                }
            }
        }
    }

    private fun getPagedData(page: Int): List<HisRecordListData> {
        val fromIndex = page * pageSize
        val toIndex = minOf(fromIndex + pageSize, afterFilterRecords.size)
        return if (fromIndex < toIndex) afterFilterRecords.subList(fromIndex, toIndex) else emptyList()
    }

    private fun fetchData(start: String, end: String) {
        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {
                try {
                    bookViewModel.fetchHisRecordList()
//                    bookViewModel.fetchRecordData(start, end)
//                    bookViewModel.fetchRecordDataII(start, end)

                } finally {
                    dialog.dismiss()
                }
            }
        }
        bookViewModel.hisRecordDataLiveData.observe(viewLifecycleOwner) { result ->
            if (result != null && result.isNotEmpty()) {
                val start = ConvertText.rocDashToLocalDateSymbolDesh(binding.etStartDate.text.toString())
                val end = ConvertText.rocDashToLocalDateSymbolDesh(binding.etEndDate.text.toString())
                allRecords = result // 這邊已經是 List<DataRecord>
                afterFilterRecords = allRecords.filter { record ->
                    val date = ConvertText.rocToLocalDate(record.日期)
                    !date.isBefore(start) && !date.isAfter(end)
                }
//                currentPage = 0
//                setupPageControls()
                CoroutineScope(Dispatchers.Main).launch {
                    initRecyclerView(afterFilterRecords)
//                    initRecyclerView(getPagedData(currentPage))
                }
            } else {
                Log.d("recordData", "No records found or result is null.")
                // 顯示空資料 UI 或提示訊息
                allRecords = emptyList()
                afterFilterRecords = emptyList()
                initRecyclerView(emptyList())
            }
        }
//        bookViewModel.recordDataLiveData.observe(viewLifecycleOwner) { result ->
//            if (result != null && result.isNotEmpty()) {
//                allRecords = result // 這邊已經是 List<DataRecord>
////                currentPage = 0
////                setupPageControls()
//                CoroutineScope(Dispatchers.Main).launch {
//                    initRecyclerView(allRecords)
////                    initRecyclerView(getPagedData(currentPage))
//                }
//            } else {
//                Log.d("recordData", "No records found or result is null.")
//                // 顯示空資料 UI 或提示訊息
//                allRecords = emptyList()
//                initRecyclerView(emptyList())
//            }
//        }
    }
    private fun fetchData2(start: String, end: String) {
        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {
                var mobile = SharedPreferencesUtil.instances.getAccountId() // "0900000000", "2025-05-01", "2025-06-30"

                mobile = "0900000000"
                val dst_start = "2025-05-01"
                val dst_end = "2025-06-30"
                ApiConnect().getMedicalRecordSummary(mobile, dst_start, dst_end,
                    object : ApiConnect.resultListener {
                        override fun onSuccess(message: String?) {

                            try {

                                val jsonArray = JSONArray(message)

                                if (jsonArray.length() > 0) {
                                    dialog.dismiss()

                                    val dataList = mutableListOf<HisRecordListData>()
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val record = HisRecordListData(
                                            jsonObject.getInt("counter"),
                                            jsonObject.getString("日期")
                                        )
                                        dataList.add(record)
                                    }
                                    allRecords = dataList // 儲存全部資料
//                                    currentPage = 0
//                                    setupPageControls()
                                    CoroutineScope(Dispatchers.Main).launch {
//                                        initRecyclerView(getPagedData(currentPage))
                                        initRecyclerView(allRecords)
                                    }
                                }

                            } catch (e: Exception) {
                                dialog.dismiss()
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(task: String?, message: String?) {
                            dialog.dismiss()
                            Log.e(TAG, "task: $task message: $message")

                            CoroutineScope(Dispatchers.Main).launch {
                                initRecyclerView(emptyList())
                            }
                        }

                    }
                )
            }
        }
    }


    private fun initRecyclerView(result: List<HisRecordListData>) {
        this.afterFilterRecords = result.toMutableList() // 轉成可變列表以供 adapter 使用
        lst_data = this.afterFilterRecords.toMutableList()
        binding?.apply {
            rvData.visibility = if (lst_data.size > 0) View.VISIBLE else View.GONE
            tvNoData.visibility = if (lst_data.size == 0) View.VISIBLE else View.GONE
        }
        binding?.rvData?.apply {

            // Initialize the adapter with the click listener
            recordAdapter = RecordAdapter(lst_data) {position, item ->
                // Handle item click
                val bundle = Bundle().apply {
                    putParcelable("record", item)
                }
                idx = position
                findNavController().navigate(R.id.recordDetailFragment, bundle)
            }

            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = recordAdapter
        }
    }
    private fun setupPageControls() {
        val spinner = binding.spCountPerPage
        val options = listOf(10, 20, 50)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.setSelection(0) // 預設選擇10
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                pageSize = options[position]
                currentPage = 0
                initRecyclerView(getPagedData(currentPage))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.llPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                initRecyclerView(getPagedData(currentPage))
            }
        }
 
        binding.llNext.setOnClickListener {
            val totalPages = (afterFilterRecords.size + pageSize - 1) / pageSize
            if (currentPage < totalPages - 1) {
                currentPage++
                initRecyclerView(getPagedData(currentPage))
            }
        }
    }

}