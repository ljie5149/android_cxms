package com.jotangi.cxms.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentUploadDataBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.SharedPreferencesUtil
import kotlinx.coroutines.launch

class UploadDataFragment : BaseFragment() {

    private lateinit var binding: FragmentUploadDataBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<UploadDataFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadDataBinding.inflate(inflater, container, false)
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
            bookViewModel.bookingDataCount(args.order)
        }
    }

    private fun initView() {
        setToolbarArrow("上傳診前資訊")
    }

    private fun initHandler() {

        binding.apply {

            tvUploadPicture.setOnClickListener {
                findNavController().navigate(
                    UploadDataFragmentDirections.actionUploadDataFragmentToPictureListFragment(
                        args.order
                    )
                )
            }

            tvUploadVoice.setOnClickListener {
                findNavController().navigate(
                    UploadDataFragmentDirections.actionUploadDataFragmentToUploadMicFragment(
                        args.order
                    )
                )
            }

            tvUploadQuestion.setOnClickListener {

                SharedPreferencesUtil.instances.setOrder(args.order)
                findNavController().navigate(
                    UploadDataFragmentDirections.actionUploadDataFragmentToWebFragment(
                        getString(R.string.pre_video_question)
                    )
                )
            }

            btUdNext.setOnClickListener {

                val intent = Intent(Intent.ACTION_VIEW)
                val url = Uri.parse(
                    "${ApiConstant.PAY_URL}sid=${args.sid}&bookingno=${args.order}"
                )
                intent.data = url
                startActivity(intent)
            }
        }
    }

    private fun initCallBack() {

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