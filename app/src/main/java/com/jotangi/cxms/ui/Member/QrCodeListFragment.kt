package com.jotangi.cxms.ui.Member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentQrcodeListBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.journeyapps.barcodescanner.BarcodeEncoder

class QrCodeListFragment : BaseFragment() {

    private lateinit var binding: FragmentQrcodeListBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrcodeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initHandler()
        initCallBack()
    }

    private fun initView() {

        setToolbarArrow("會員QR Code")

        qrCodeViewModel.uQrCode.value ?: return

        val encoder = BarcodeEncoder()
        try {

            val bit = encoder.encodeBitmap(
                qrCodeViewModel.uQrCode.value,
                BarcodeFormat.QR_CODE,
                250,
                250
            )
            binding.ivQrcode.setImageBitmap(bit)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun initHandler() {


    }

    private fun initCallBack() {


    }
}