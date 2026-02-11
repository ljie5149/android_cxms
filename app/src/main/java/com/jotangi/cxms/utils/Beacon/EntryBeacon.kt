package com.jotangi.NumberHealthy.utils.Beacon

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

@SuppressLint("StaticFieldLeak")
object EntryBeacon {
    private var TAG = "beacon-receiver"
    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private var btScanner: BluetoothLeScanner? = null
    var UpdateVideo = false
    var PreUUID : String =""
    var PreMajor: Int    =0
    var PreMinor: Int    =0
    var UUID : String =""
    var Major: Int    =0
    var Minor: Int    =0
    lateinit var activity : Activity
    var Device_id: String =""

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val scanRecord = result?.scanRecord
            super.onScanResult(callbackType, result)
            Log.d(TAG, "receiving result")

            if (scanRecord != null) {
                val device = IBeacon(scanRecord?.bytes!!)
                val uuid = device.getUUID()
                val major = device.getMajor()
                val minor = device.getMinor()

                if (minor == 100) {
                    if (PreUUID != uuid) {
                        if (UUID != uuid) UUID = uuid
                        if (Major != major) Major = major
                        if (Minor != minor) Minor = minor
                        PreUUID  = UUID
                        PreMajor = Major
                        PreMinor = Minor
                        UpdateVideo =true
                    }

                    Log.d(TAG, "@receive Broadcast " + "Device UUID: " + uuid + "\n" + "Major: " + major + "\n" + "Minor: " + minor + "\n")
                }
            }
            return
        }

        override fun onScanFailed(errorCode: Int) {
            Log.d(TAG, "@onScanFailed failed: " + errorCode)
        }
    }

    private fun startBLEScan() {
        try {
            btScanner!!.startScan(leScanCallback)
        } catch (e: SecurityException) {
            Log.e(TAG, "@startScan SecurityException: " + e.message)
        }
    }

    private fun setUpBluetoothManager() {
        btManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager!!.adapter
        btScanner = btAdapter?.bluetoothLeScanner
        if (btAdapter != null ) {
            if (btAdapter?.isEnabled == false) {
                Log.d(TAG, "Bluetooth adapter is not enabled")
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startActivityForResult(enableBtIntent, 100)
            } else {
                Log.d(TAG, "Bluetooth adapter is enabled")
                startBLEScan()
            }
        }
    }

    fun init (act: Activity) {
        this.activity  =act
        this.Device_id =getDeviceId(act)
        setUpBluetoothManager()
    }

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}