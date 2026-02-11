package com.jotangi.cxms.ui.record

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.jotangi.cxms.Api.book.HisRecordInfoData
import com.jotangi.cxms.Api.book.HisRecordListData
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentRecordDetailBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordDetailFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentRecordDetailBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    var lst_data: List<HisRecordInfoData> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val record = arguments?.getParcelable<HisRecordListData>("record")
        idx = RecordFragment.idx
        record?.let { fetchData(it.counter) }

        binding.apply {

            tvContent.text = ""

            llPrevRecordset.setOnClickListener {
                if (idx > 0) idx--
                fetchData(RecordFragment.lst_data[idx].counter)
            }

            llNextRecordset.setOnClickListener {
                if (idx < RecordFragment.lst_data.size - 1) idx++
                fetchData(RecordFragment.lst_data[idx].counter)
            }
        }
    }
    private fun updateData(position: Int) {
        binding.apply {
            val cur_data = lst_data[position]
            setToolbarArrow("${cur_data.日期} 門診摘要")
            tvContent.text = "門診日期：${cur_data.日期}\n門診摘要：\n${cur_data.病摘內容}"
        }
    }

    private fun fetchData(counter: Int) {
        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {
                try {
                    bookViewModel.fetchHisRecordInfo(counter)
                } finally {
                    dialog.dismiss()
                }
            }
        }
        bookViewModel.hisRecordDataLiveInfo.observe(viewLifecycleOwner) { result ->
            if (result != null && result.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    lst_data = result
                    updateData(0)
                }
            } else {
                Log.d("recordData", "No records found or result is null.")
                // 顯示空資料 UI 或提示訊息
            }
        }
    }
    companion object {
        var idx = -1
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}