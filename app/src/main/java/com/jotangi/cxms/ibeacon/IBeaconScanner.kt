package com.ptv.ibeacon.receiver.ibeacon

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.IOException

import android.os.Handler
import android.os.Looper

class IBeaconScanner(private val context: Context) {
    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    val handler = Handler(Looper.getMainLooper())

    // This will store the scan results until processed
    val scanResults = mutableListOf<ScanResult>()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (result != null) {
                scanResults.add(result)
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            scanResults.addAll(results)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("IBeaconScanner", "Scan failed with error: $errorCode")
        }
    }

    private fun handleResult(result: ScanResult?) {
        result?.let {
            val scanRecord = it.scanRecord
            val rssi = it.rssi
            Log.i("IBeaconScanner", "result available")

            scanRecord?.let { record ->
                // use random unused Manufacturer id https://gist.github.com/angorb/f92f76108b98bb0d81c74f60671e9c67
                val manufacturerData = record.getManufacturerSpecificData(0x0478)

                if (manufacturerData == null) {
                    Log.e("IBeaconScanner", "Manufacturer-specific data is null for ID 0x0478")
                    return
                }

                manufacturerData?.let { data ->
                    try {
                        val result = IBeaconParser.parseIBeaconData(data)

                        if (result != null) {
                            Log.i("IBeaconScanner", "uuid ${result.uuid}")
                            Log.i("IBeaconScanner", "major ${result.major.toString()}")
                            Log.i("IBeaconScanner", "minor ${result.minor.toString()}")

                            sendDataToActivity(result, rssi);
                        } else {
                            Log.e("IBeaconScanner", "data not valid")
                        }
                    } catch (e:IOException) {
                        Log.e("IBeaconScanner", e.toString())
                    }
                }
            }
        }
    }

    val processResultsTask = object : Runnable {
        private var isHandling = false // Flag to track if we're currently handling a result

        override fun run() {
            if (scanResults.isNotEmpty()) {
                // Find the last scan result with manufacturer ID 0x0478
                val lastMatchingResult = scanResults
                    .lastOrNull { result ->
                        result.scanRecord?.getManufacturerSpecificData(0x0478) != null
                    }

                // If a matching result is found, handle it
                if (lastMatchingResult != null && !isHandling) {
                    // Set the flag to true, indicating we're handling the result
                    isHandling = true

                    Log.i("IBeaconScanner", "Handling last result with manufacturer ID 0x0478")
                    handleResult(lastMatchingResult)

                    // Schedule the next handling after 30 seconds
                    handler.postDelayed({
                        Log.i("IBeaconScanner", "Resuming handling after 30 seconds")
                        isHandling = false // Reset the flag after the delay
                    }, 30000) // 30 seconds
                } else if (lastMatchingResult == null) {
                    Log.i("IBeaconScanner", "No matching results found with manufacturer ID 0x0478")
                }

                // Clear the results after processing
                scanResults.clear()
            }
            // Schedule the task again after 5 seconds
            handler.postDelayed(this, 5000)
        }
    }

    private fun sendDataToActivity(data: IBeaconData, rssi: Int) {
        val intent = Intent("com.ptv.ibeacon.receiver.IBEACON_DATA_RECEIVED")
        intent.putExtra("uuid", data.uuid)
        intent.putExtra("major", data.major)
        intent.putExtra("minor", data.minor)
        intent.putExtra("txPower", data.txPower)
        intent.putExtra("rssi", rssi)
        context.sendBroadcast(intent)
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        //  permission handled by main activity
        if (bluetoothAdapter?.isEnabled == true) {
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // Adjust scan mode as needed
//                .setReportDelay(5000)// 10 seconds delay report
                .build()
            val scanFilters = listOf(ScanFilter.Builder()
                .setManufacturerData(0x0478, null)
                .build())
            bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)

            // Start the periodic task
            handler.post(processResultsTask)
        } else {
            Log.e("IBeaconScanner", "Bluetooth is not enabled")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        //  permission handled by main activity
        bluetoothLeScanner?.stopScan(scanCallback)
        handler.removeCallbacks(processResultsTask) // Stop the periodic task
    }
}

