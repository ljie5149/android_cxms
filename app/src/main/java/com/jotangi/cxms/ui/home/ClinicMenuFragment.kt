package com.jotangi.cxms.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentClinicMenuBinding
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
 * Use the [ClinicMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClinicMenuFragment : BaseFragment() {

    private lateinit var binding: FragmentClinicMenuBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClinicMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarArrow("預約掛號、查詢")
        initHandler()
    }
    private fun initHandler() {

        binding.apply {

            btnReservation.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                // 預約掛號
                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                    lifecycleScope.launch {

                        bookViewModel.getDivisionList {

                            CoroutineScope(Dispatchers.Main).launch {
                                findNavController().navigate(R.id.reserveRegisteredFragment)
                                goneBnv()
                            }
                        }
                        dialog.dismiss()
                    }
                }
            }

            btnQuery.setOnClickListener {

                if (checkLogout()) return@setOnClickListener

                // 取消預約
//                findNavController().navigate(R.id.cancelFragment)

                // 查詢 取消 掛號 -> .2025-06-09 門診摘要
                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                    lifecycleScope.launch {
                        // 原程式 - start
                        try {
                            bookViewModel.sleepWellBookingList(
                                success = {
                                    findNavController().navigate(R.id.action_clinicMenuFragment_to_myReserveFragment)
                                    goneBnv()
                                },
                                fail = {
                                    showErrorMsgDialog(it)
                                }
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
//                            showErrorMsgDialog("查詢失敗，請稍後再試 (${e.message})")
                        } finally {
                            dialog.dismiss()
                        }
                        // 原程式 - end
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClinicMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClinicMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}