package com.jotangi.cxms

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.zxing.client.android.BuildConfig
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.databinding.ActivityMainBinding
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchViewModel
import com.jotangi.NumberHealthy.utils.Beacon.MediaFile
import com.jotangi.cxms.Api.book.BookViewModel
import com.jotangi.cxms.utils.DialogUtil
import com.ptv.ibeacon.receiver.logger.FirebaseLoggingTree
import com.ptv.ibeacon.receiver.vast.VASTDataHandler
import com.ptv.ibeacon.receiver.vast.VASTModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {
    val currentVersion = com.jotangi.cxms.BuildConfig.VERSION_NAME
    var latestVersion = ""
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var iBeaconReceiver: BroadcastReceiver
    private var counterCount = 0
    private lateinit var vastDataHandler: VASTDataHandler
    private lateinit var vastModel: VASTModel
    private lateinit var serviceIntent: Intent


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { states ->
        if ((states[Manifest.permission.BLUETOOTH_CONNECT] == true && states[Manifest.permission.BLUETOOTH_SCAN] == true) ||
            states[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
        } else {
            finish()
        }
    }

    private var myThread: Thread? = null
    private var currentVideoIndex: Int = 0
    private var mediaFiles: MutableList<MediaFile>? = null
    private var complete: Boolean = false

    private val TAG: String = "${javaClass.simpleName}(TAG)"

    private lateinit var binding: ActivityMainBinding
    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    var camera: Camera? = null
    var isShowCamera = false

    val watchViewModel: WatchViewModel by viewModel()
    val bookViewModel: BookViewModel by viewModel()

    private lateinit var outputDirectory: File
    var mMediaPlayer: MediaPlayer? = null

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // api 35 需做的調整 - 系統字色不被app影響
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        // xml 系統列不被app 蓋住 activity_main.xml加上 android:fitsSystemWindows="true"

        supportActionBar?.hide()
        setContentView(binding.root)
        val appVersion = SharedPreferencesUtil.instances.getAppVersion()
        val versionCode = BuildConfig.VERSION_CODE
        Log.d(TAG, "BuildConfig.VERSION_CODE: ${BuildConfig.VERSION_CODE}")
        if (appVersion.isNullOrBlank() || appVersion.toInt() < BuildConfig.VERSION_CODE) {
            SharedPreferencesUtil.instances.setAppVersion(versionCode.toString())
        }

        initNavigation()

        checkPermission()
        remoteConfig()
    }
    private fun isLogout(): Boolean {
        return SharedPreferencesUtil.instances.getAccountId().isNullOrBlank()
    }

    fun showErrorMsgDialog(content: String) {
        CoroutineScope(Dispatchers.Main).launch {
            DialogUtil.instance.singleMessageDialog(
                this@MainActivity,
                "溫馨提醒",
                content
            ) {}
        }
    }

    fun checkLogout(): Boolean {
        return isLogout().let {
            if (it) showErrorMsgDialog("請先登入後繼續")
            it
        }
    }
    @SuppressLint("RestrictedApi")
    private fun initNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val navView: BottomNavigationView = binding.navView
        navView.setOnItemSelectedListener {
            if (it.itemId != navController.currentDestination?.id) {
                while (navController.popBackStack()) { }

                when (it.itemId) {
                    R.id.mainFragment -> navController.navigate(R.id.mainFragment)
                    R.id.navigation_home -> navController.navigate(R.id.navigation_home)
                    R.id.loginFragment -> {
                        if (SharedPreferencesUtil.instances.getAccountPid().isNullOrEmpty()) {
                            navController.navigate(R.id.loginFragment)
                        } else {
                            navController.navigate(R.id.memberFragment)
                        }
                    }
                    R.id.appointFragment-> {
                        if (!checkLogout()) {
                            navController.navigate(R.id.appointFragment)
                        }

                    }
                }
            }
            true
        }
    }


    private fun login() {
        lifecycleScope.launch {
            // Logic for user login
        }
    }

    private fun findLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val client = LocationServices.getFusedLocationProviderClient(this)
            client.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.let { location ->
                        searchAddress(location)
                    }
                }
            }
        }
    }

    private fun searchAddress(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            Log.d(TAG, "latitude: ${location.latitude}")
            Log.d(TAG, "longitude: ${location.longitude}")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                Log.d(TAG, "address(定位地址): $address")
                SharedPreferencesUtil.instances.setAdminArea(addresses[0].adminArea)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermission() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.CAMERA,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.CALL_PHONE
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.CAMERA,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE
            )
            else -> arrayOf(
                Manifest.permission.CAMERA,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE
            )
        }

        if (hasPermissions(*permissions)) {
        } else {
            ActivityCompat.requestPermissions(this, permissions, 200)
        }
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION) continue
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Log.d(TAG, "Permissions granted. Proceeding with initialization.")
        } else {
            Log.w(TAG, "Permissions denied. Closing app.")
//            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }






    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initTimber() {
        Timber.plant(FirebaseLoggingTree())
    }

    private fun debug() {
        if (vastDataHandler != null) {
            vastDataHandler.handleIBeaconData("E7A7B60A-3BA0-43DB-8B3D-1C2B74A6D050", null, null, null, null)
        }
    }

    private fun remoteConfig() {
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // For production, set it to a higher value
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Fetch and activate Remote Config
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Fetch and activate succeeded
                    val updated = task.result

                    latestVersion = remoteConfig.getString("latest_app_version")
                    Log.d("micCheckQQ1", latestVersion)
                    Log.d("micCheckQQ2", currentVersion)
                    if (isNewVersionAvailable(currentVersion, latestVersion)) {
                        // Prompt the user to update the app
                        promptUpdateDialog()
                    } else {
                        // No update needed
                        Log.d("AppVersionCheck", "No update required.")
                    }
                } else {
                    // Fetch failed
                }
            }

    }
    private fun isNewVersionAvailable(current: String, latest: String): Boolean {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(currentParts.size, latestParts.size)

        for (i in 0 until maxLength) {
            val c = currentParts.getOrElse(i) { 0 }
            val l = latestParts.getOrElse(i) { 0 }

            if (l > c) return true
            if (l < c) return false
        }

        return false // same version
    }


    // Function to show a dialog or prompt to the user to update the app
    private fun promptUpdateDialog() {
        // Show your update dialog here (for example, AlertDialog)
        AlertDialog.Builder(this)
            .setTitle("版本更新提示")
            .setMessage("親愛的使用者您好，禾欣骨科診所功能已進行優化，請前往商店進行程式更新，以維護您的權益，謝謝。")
            .setPositiveButton("更新") { _, _ ->
                // Redirect to Play Store for update
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.jotangi.smart_doctor"))
                startActivity(intent)
            }
            .show()
    }
}
