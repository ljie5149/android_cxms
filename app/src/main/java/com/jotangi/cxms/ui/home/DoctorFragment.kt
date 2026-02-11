package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.book.BookViewModel
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentDoctorBinding
import com.jotangi.cxms.utils.DateTimeUtil
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DoctorFragment : Fragment() {

    companion object {
        fun newInstance() = DoctorFragment()
    }

    private lateinit var binding: FragmentDoctorBinding

    private val TAG = "${javaClass.simpleName}(TAG)"
    private val bookViewModel: BookViewModel by viewModel()

    private lateinit var doctorAdapter: DoctorAdapter
    private var list = arrayListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        doctorAdapter = DoctorAdapter(list)

        binding.rv.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = doctorAdapter
        }
    }

    private fun initHandler() {

        doctorAdapter.itemClick = { name ->

//            if (name == ApiUrl.sleepWell) sleepWellApi() else docApi(name)
            docApi(name)
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

    private fun docApi(name: String) {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                bookViewModel.physicianScheduleDoc(
                    name,
                    success = {
                        CoroutineScope(Dispatchers.Main).launch {
                            findNavController().navigate(R.id.reserveListFragment)
                        }
                    },
                    fail = {
                        CoroutineScope(Dispatchers.Main).launch {
                            DialogUtil.instance.singleMessageDialog(
                                requireActivity(),
                                "溫馨提醒",
                                it
                            ) {}
                        }
                    }
                )

                dialog.dismiss()
            }
        }
    }


    private fun initCallBack() {

        bookViewModel.divisionDoctorLD.observe(viewLifecycleOwner) {

            val set = LinkedHashSet<String>()
            var name: String?
            for (i in it.indices) {

                name = it[i].醫師名
                if (name.isNullOrBlank()) {
                    continue
                } else {
                    set.add(name)
                }
            }

//            set.add(ApiUrl.sleepWell)

            list.clear()
            list.addAll(set.toList())
            doctorAdapter.notifyDataSetChanged()
        }
    }
}