package com.jotangi.cxms.ui.Member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentEditeDiagneInfoBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DateTimeUtil
import com.jotangi.cxms.utils.SharedPreferencesUtil
import kotlinx.coroutines.launch

class EditeDiagnosisInfoFragment : BaseFragment() {

    private lateinit var binding: FragmentEditeDiagneInfoBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditeDiagneInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initApi()
        initView()
        initHandler()
        initCallBack()
    }

    private fun initApi() {
        lifecycleScope.launch {
            bookViewModel.bookingDataCount(
                bookViewModel.orderDataLiveData.value!!.booking_no!!
            )
        }
    }

    private fun initView() {
        setToolbarArrow("診前資訊編輯")
    }

    private fun initHandler() {

        binding.apply {

            tvUploadPicture.setOnClickListener {
                findNavController().navigate(
                    EditeDiagnosisInfoFragmentDirections.actionEditeDiagnosisInfoFragmentToPictureListFragment(
                        bookViewModel.orderDataLiveData.value!!.booking_no!!
                    )
                )
            }

            tvUploadVoice.setOnClickListener {
                findNavController().navigate(
                    EditeDiagnosisInfoFragmentDirections.actionEditeDiagnosisInfoFragmentToUploadMicFragment(
                        bookViewModel.orderDataLiveData.value!!.booking_no!!
                    )
                )
            }

            tvUploadQuestion.setOnClickListener {

                SharedPreferencesUtil.instances.setOrder(
                    bookViewModel.orderDataLiveData.value!!.booking_no!!
                )
                findNavController().navigate(
                    EditeDiagnosisInfoFragmentDirections.actionEditeDiagnosisInfoFragmentToWebFragment(
                        getString(R.string.pre_video_question)
                    )
                )
            }

            btRtNext.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initCallBack() {

        bookViewModel.orderDataLiveData.observe(viewLifecycleOwner) {

            binding.apply {

                tvDivisionContent.text = it.price
                tvPhysicianContent.text = it.doctor_name
                tvTimeContent.text =
                    "${it.reserve_date} ${DateTimeUtil.instance.clipHmsTohm(it.reserve_time!!)}"
                tvName.text = it.member_name
            }
        }

        bookViewModel.bookingDataCountLiveData.observe(viewLifecycleOwner) { list ->

            binding.apply {

                if (list.isEmpty()) {

                    tvUploadPictureLegend.visibility = View.GONE
                    ivCheckPicture.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_off)
                    )

                    tvSoundLegend.visibility = View.GONE
                    ivCheckSound.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_off)
                    )

                    ivCheckQuestion.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_off)
                    )

                } else {

                    if (list[0].outpatient_picture!!.toInt() > 0) {

                        tvUploadPictureLegend.text = "${list[0].outpatient_picture}個檔案"
                        tvUploadPictureLegend.visibility = View.VISIBLE
                        ivCheckPicture.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_on)
                        )

                    } else {

                        tvUploadPictureLegend.visibility = View.GONE
                        ivCheckPicture.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_off)
                        )
                    }

                    if (list[0].outpatient_audio!!.toInt() > 0) {

                        tvSoundLegend.text = "${list[0].outpatient_audio}個檔案"
                        tvSoundLegend.visibility = View.VISIBLE
                        ivCheckSound.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_on)
                        )
                    } else {

                        tvSoundLegend.visibility = View.GONE
                        ivCheckSound.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_off)
                        )
                    }

                    if (list[0].questionnaire!!.toInt() > 0) {
                        ivCheckQuestion.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_on)
                        )
                    } else {
                        ivCheckQuestion.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_data_off)
                        )
                    }
                }
            }
        }
    }
}