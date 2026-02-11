package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.book.BookViewModel
import com.jotangi.cxms.Api.book.DivisionListData
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentDepartmentBinding
import com.jotangi.cxms.utils.DateTimeUtil
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DepartmentFragment : Fragment() {

    companion object {
        fun newInstance() = DepartmentFragment()
    }

    private val TAG = "${javaClass.simpleName}(TAG)"
    private lateinit var binding: FragmentDepartmentBinding
    private val bookViewModel: BookViewModel by viewModel()

    private lateinit var groupAdapter: DivisionGroupAdapter
    private lateinit var nameAdapter: DivisionNameAdapter

    private var groupList = ArrayList<String>()
    private var nameList = ArrayList<String>()
    private var dataList = ArrayList<DivisionListData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepartmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        groupAdapter = DivisionGroupAdapter(groupList)
        nameAdapter = DivisionNameAdapter(nameList)

        binding.rvGroup.apply {

            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL
                )
            )
            adapter = groupAdapter
        }

        binding.rvName.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = nameAdapter
        }
    }

    private fun initHandler() {

        binding.apply {

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {

                    ivClose.visibility = if (p0.toString().isEmpty())
                        View.INVISIBLE else View.VISIBLE

                    searchNames(p0.toString(), true)
                }

            })

            ivClose.setOnClickListener {

                ivClose.visibility = View.INVISIBLE
                etSearch.text.clear()
            }
        }

        groupAdapter.groupItemClick = { groupName ->
            searchNames(groupName, false)
        }

        nameAdapter.nameItemClick = { index ->

            if (nameList[index] == ApiUrl.subSleepWell)
                sleepWellApi() else divApi(index)
        }
    }

    private fun sleepWellApi() {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.sleepwellWorkingday(
                    DateTimeUtil.instance.getNowYMD(),
                    WatchUtils.instance.after6MonthYmd(),
                    success = {
                        findNavController().navigate(R.id.sleepWellFragment)
                    },
                    fail = { error ->

                        DialogUtil.instance.singleMessageDialog(
                            requireActivity(),
                            "溫馨提醒",
                            error
                        ) {}
                    }
                )

                dialog.dismiss()
            }
        }
    }

    private fun divApi(index: Int) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.physicianScheduleDiv(
                    dataList[index].division_name.toString(),
                    fail = {
                        CoroutineScope(Dispatchers.Main).launch {
                            DialogUtil.instance.singleMessageDialog(
                                requireActivity(),
                                "溫馨提醒",
                                it
                            ) {}
                        }
                    },
                    success = {
                        CoroutineScope(Dispatchers.Main).launch {
                            findNavController().navigate(R.id.reserveListFragment)
                        }
                    }
                )

                dialog.dismiss()
            }
        }
    }

    private fun initCallBack() {

        bookViewModel.hospitalDivisionData.observe(viewLifecycleOwner) {

            val groups = LinkedHashSet<String>()
            val names = mutableListOf<String>()

            it.forEach { data ->

                data.group_division?.also { div ->
                    if (div.isNotBlank()) groups.add(div)
                }

                data.division_name?.also { name ->
                    if (name.isNotBlank()) names.add(name)
                }
            }

//            groups.add(ApiUrl.sleepWell)
//            names.add(ApiUrl.subSleepWell)

            groupList.clear()
            groupList.addAll(groups)
            groupAdapter.notifyDataSetChanged()

            nameList.clear()
            nameList.addAll(names)
            nameAdapter.notifyDataSetChanged()

            dataList.clear()
            dataList.addAll(it)
        }
    }

    private fun searchNames(name: String, isName: Boolean) {

        if (name.isBlank()) return

        val names = mutableListOf<String>()
        val dataMutable = mutableListOf<DivisionListData>()

        bookViewModel.hospitalDivisionData.value?.forEach {

            if (isName) {

                it.division_name?.also { divisionName ->

                    if (divisionName.contains(name)) {

                        names.add(divisionName)
                        dataMutable.add(it)
                    }
                }
            } else {

                it.group_division?.also { group ->

                    if (name == group) {

                        names.add(it.division_name ?: "")
                        dataMutable.add(it)
                    }
                }
            }
        }

        if (isName) {

            if (ApiUrl.subSleepWell.contains(name)) {
                names.add(ApiUrl.subSleepWell)
            }
        } else {

            if (name == ApiUrl.sleepWell) {
                names.add(ApiUrl.subSleepWell)
            }
        }

        if (names.isNotEmpty()) {

            nameList.clear()
            nameList.addAll(names)
            nameAdapter.notifyDataSetChanged()

            dataList.clear()
            dataList.addAll(dataMutable)
        }
    }
}