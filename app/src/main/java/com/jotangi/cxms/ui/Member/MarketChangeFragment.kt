package com.jotangi.cxms.ui.Member

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentMarketChangeBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

/**
 * A simple [Fragment] subclass.
 * Use the [MarketGetPointFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class MarketChangeFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private lateinit var previewView: DecoratedBarcodeView
    private lateinit var barcodeScanner: BarcodeScanner
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentMarketChangeBinding? = null
    override fun getToolBar(): ToolbarBinding = binding!!.toolbar
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMarketChangeBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMarketChangePointTitle("點數折抵")
        previewView = view.findViewById(R.id.previewView)
        barcodeScanner = BarcodeScanning.getClient()
        binding!!.toolbar.ivToolBack.setOnClickListener {
            findNavController().navigate(R.id.memberFragment)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera() // Permission is already granted
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) { // Match the requestCode used in requestPermissions
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MarketGetPointFragment", "Camera permission granted")
                startCamera() // Start the camera immediately
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun startCamera() {
        previewView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                result.text?.let { barcode ->
                    Log.i("BarcodeScannerFragment", "Scanned Barcode: $barcode")
                    barcode?.let {
                        requireActivity().runOnUiThread {
                            showQRCodeContent(it)
                        }
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {
                // Handle possible result points if needed
            }
        })
    }

    private fun showQRCodeContent(content: String?) {
        // Handle or display QR code content here
        content?.let {
            Log.d("micCheckBV", it)
            storeId = it
            val parts = storeId.split(",")

// Assign the parts to separate variables
            try {
                storeNumber = parts[0] // "1229102"
                storeName = parts[1]
            }catch(e:Exception) {
                Toast.makeText(requireContext(), "QRCode 不正確", Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    companion object {
var storeName = ""
        var storeNumber = ""
        var storeId = "0"

    }
}