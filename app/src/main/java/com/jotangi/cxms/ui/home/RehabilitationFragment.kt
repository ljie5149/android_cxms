package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentRehabilitationBinding
import com.jotangi.cxms.databinding.ToolbarBinding

class RehabilitationFragment : BaseFragment() {

    private var _binding: FragmentRehabilitationBinding? = null
    private val binding get() = _binding!!
    override fun getToolBar(): ToolbarBinding = binding.toolbar
    private val rehabOptions = listOf(
        "請選擇",
        "震波療程",
        "射頻療程",
        "紅繩運動療程",
        "徒手復健療程"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRehabilitationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
setupRehabiliation()
        // Setup Spinner with mock data
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, rehabOptions)
        binding.spinnerRehab.adapter = adapter

        // Handle item selection
        binding.spinnerRehab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Do nothing when the first item is selected
                    return
                }
                val selectedItem = rehabOptions[position]
//                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()

                // Perform specific actions based on selected item
                when (selectedItem) {
                    "震波療程" -> ReserveFragment.did = 3
                    "射頻療程" -> ReserveFragment.did = 4
                    "紅繩運動療程" -> ReserveFragment.did = 5
                    "徒手復健療程" -> ReserveFragment.did = 6
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Handle button click
        binding.buttonBook.setOnClickListener {
            findNavController().navigate(R.id.action_rehabilitationFragment_to_calendarFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
