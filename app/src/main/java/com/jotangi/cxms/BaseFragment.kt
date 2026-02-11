package com.jotangi.cxms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.Api.book.BookViewModel
import com.jotangi.cxms.Api.book.QrCodeApiRepository
import com.jotangi.cxms.Api.qrcode.QrCodeViewModel
import com.jotangi.cxms.databinding.ToolbarBinding // Use ViewBinding instead of kotlinx.android.synthetic
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.DialogUtils
import com.jotangi.cxms.utils.SharedPreferencesUtil
import com.jotangi.cxms.utils.smartwatch.WatchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec



abstract class BaseFragment : Fragment() {

    abstract fun getToolBar(): ToolbarBinding?
    val TAG: String = "(TAG)${javaClass.simpleName}"

    // Declare the ViewBinding variable for the toolbar
    private var _binding: ToolbarBinding? = null
    private val binding get() = _binding!!

    var mActivity: MainActivity? = null
    val watchViewModel: WatchViewModel by viewModel()
    val bookViewModel: BookViewModel by viewModel()
    val qrCodeViewModel: QrCodeViewModel by viewModel()
    val apiBook: BookApiRepository by lazy { BookApiRepository() }
    val apiQr: QrCodeApiRepository by lazy { QrCodeApiRepository() }

    private val key = "YcL+NyCRl5FYMWhozdV5V8eu6qv3cLDL".toByteArray(Charsets.UTF_8)
    private val type = "CBC"
    private val size = 256
    private val iv = "53758995@jotangi".toByteArray(Charsets.UTF_8)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            mActivity = (activity as MainActivity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ToolbarBinding.bind(view)  // Initialize ViewBinding for toolbar
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }


    private fun isLogout(): Boolean {
        return SharedPreferencesUtil.instances.getAccountId().isNullOrBlank()
    }


    fun checkLogout(): Boolean {
        return isLogout().let {
            if (it) showErrorMsgDialog("請先登入後繼續")
            it
        }
    }

    fun checkBoxCount() {
        if (isLogout()) return
        lifecycleScope.launch {
            apiBook.getMessageBoxCount().also {
                if (it.responseMessage.isNullOrBlank()) return@launch
                setupToolbarWithCount(it.responseMessage)
            }
        }
    }

    private fun setupToolbarWithCount(count: String) {
        binding.apply {
            val notifyImageId = getImageId(count)
            setupToolbarBtn(ivToolBtn1, notifyImageId) {
                findNavController().navigate(R.id.notification_history_fragment)
            }
        }
    }

    private fun getImageId(count: String): Int {
        return when (count) {
            "0" -> R.drawable.ic_notify
            else -> R.drawable.ic_notify_active
        }
    }

    // ----------------------------------------------------------------------


    fun setFull() {
        getToolBar()?.clTb!!.visibility = View.GONE
        mActivity!!.findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.GONE
    }

    fun setToolbar(title: String) {
        binding.tvToolTitle.text = title
    }

    fun setToolbarWatchStatus(isConnected: Boolean) {
        binding.apply {
            ivWatchConnectStatus.setImageResource(
                if (isConnected) R.drawable.ic_watch_connect_status_change_white
                else R.drawable.ic_watch_connect_status_change_red
            )
            ivWatchConnectStatus.setOnClickListener {
                // Define the actions on watch status click
            }
        }
    }

    fun setToolbarArrow(title: String) {

        getToolBar()?.apply {

            tvToolTitle.text = title

            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setToolbarHome(title: String) {

        getToolBar()?.apply {

            tvToolTitle.text = title

            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                goHome()
            }
        }
    }

    fun setupMarketChangePointTitle(title: String) {

        getToolBar()?.apply {

            tvToolTitle.text = title

            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                goHome()
            }
        }
    }

    fun setOutToolbarArrow(title: String) {
        getToolBar()?.apply {
            tvToolTitle.text = title
        }
    }

    fun setToolbarArrowTrend(title: String, tel: String, nav: NavDirections) {

        getToolBar()?.apply {

            tvToolTitle.text = title + getString(R.string.title_detsil)
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }

            if (SharedPreferencesUtil.instances.getAccountId() == tel) {
                tvRightContent.visibility = View.VISIBLE
                tvRightContent.setOnClickListener {
                    findNavController().navigate(nav)
                }
            } else {
                setToolbarArrow(title + getString(R.string.title_detsil))
            }
        }
    }

    fun setToolbarDetail(title: String, byId: Int) {
        getToolBar()?.apply {
            tvToolTitle.text = title
            setupToolbarBtn(ivToolBack, R.drawable.ic_close_w) {
                onBackPressed()
            }

            setupToolbarBtn(ivToolBtn1, R.drawable.ic_detail) {
                findNavController().navigate(byId)
            }
        }
    }

    fun setToolbarDetail(title: String, nav: NavDirections) {
        getToolBar()?.apply {
            tvToolTitle.text = title
            setupToolbarBtn(ivToolBack, R.drawable.ic_close_w) {
                onBackPressed()
            }

            setupToolbarBtn(ivToolBtn1, R.drawable.ic_detail) {
                findNavController().navigate(nav)
            }
        }
    }

    /**
     * BottomNavigationView
     */
    fun goHome() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)

        // Pop back stack entries until you reach the root fragment
        while (navController.popBackStack()) {
            // Continue popping until popBackStack() returns false
        }

        // Now navigate to the desired destination, like the home fragment
        showBnv()  // Assuming this is a method to show the BottomNavigationView
        navController.navigate(R.id.mainFragment)
    }


    fun goneBnv() {
        val bnv = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bnv.visibility = View.GONE
    }

    fun showBnv() {
        val bnv = requireActivity()
            .findViewById<BottomNavigationView>(R.id.nav_view) ?: return
        bnv.visibility = View.VISIBLE
    }


    // ----------------------------------------------------------------------


    // toolbar 左邊 iv 元件、圖、動作
    private fun setupToolbarBtn(iv: ImageView?, res: Int?, onClick: () -> Unit) {
        iv?.apply {
            visibility = View.VISIBLE
            res?.let { setImageResource(it) }
            setOnClickListener {
                onClick.invoke()
            }
        }
    }


    // 返回箭頭、叉叉
    fun onBackPressed() {
        mActivity?.onBackPressed()
    }

    fun showProgress() {
        CoroutineScope(Dispatchers.Main).launch {
            DialogUtils.showProgress(requireActivity())
        }
    }

    fun closeProgress() {
        CoroutineScope(Dispatchers.Main).launch {
            DialogUtils.closeProgress()
        }
    }

    fun showErrorMsgDialog(content: String) {
        CoroutineScope(Dispatchers.Main).launch {
            DialogUtil.instance.singleMessageDialog(
                requireActivity(),
                "溫馨提醒",
                content
            ) {}
        }
    }

    fun showErrorMsgBack(content: String) {
        CoroutineScope(Dispatchers.Main).launch {
            DialogUtils.showSingle(requireActivity(), "溫馨提醒", content) {
                onBackPressed()
            }
        }
    }

    fun showUpVersion() {

        CoroutineScope(Dispatchers.Main).launch {

            DialogUtils().showMultiple(
                requireActivity(),
                "更新提醒",
                "已有最新禾欣骨科診所版本\n請更新後再次使用",
                "前往",
                "關閉",
                object : DialogUtils.OnMultipleClickListener {
                    override fun onOk() {

                        SharedPreferencesUtil.instances.setAccountId(null)
                        SharedPreferencesUtil.instances.setAccountPwd(null)
                        SharedPreferencesUtil.instances.setAccountPid(null)
                        SharedPreferencesUtil.instances.setAccountMid(null)
                        bookViewModel.clearData()

                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(ApiConstant.STORE_URL + BuildConfig.APPLICATION_ID)
                            )
                        )

                        requireActivity().finishAffinity()
                    }

                    override fun onCancel() {

                        requireActivity().finishAffinity()
                    }
                }
            )
        }
    }


    // ----------------------------------------------------------------------


    fun setupToolBookStep0() {
        getToolBar()?.apply {
            tvToolTitle.text = "服務據點"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setupOnlinePyament() {
        getToolBar()?.apply {
            tvToolTitle.text = "線上繳費"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setupBilling() {
        getToolBar()?.apply {
            tvToolTitle.text = "自費項目繳費"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setupToolBookDetail() {
        getToolBar()?.apply {
            tvToolTitle.text = "預約詳情"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setupToolBarReserveSuccess() {
        getToolBar()?.apply {
            tvToolTitle.text = "智慧健康管理中心"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setupToolBarReserve() {
        getToolBar()?.apply {
            tvToolTitle.text = "預約療程"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setupToolBarCalendar() {
        getToolBar()?.apply {
            tvToolTitle.text = "預約療程"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun setupRehabiliation() {
        getToolBar()?.apply {
            tvToolTitle.text = "復健項目"
            setupToolbarBtn(ivToolBack, R.drawable.ic_left_arrow) {
                onBackPressed()
            }
        }
    }

    fun String.deAes(): String {

        return try {
            val random = SecureRandom.getInstance("SHA1PRNG")
            random.setSeed(key)
            KeyGenerator.getInstance("AES").init(size, random)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(key, type),
                IvParameterSpec(iv)
            )
            String(
                cipher.doFinal(
                    Base64.decode(
                        this.toByteArray(Charsets.UTF_8),
                        Base64.DEFAULT
                    )
                ),
                Charsets.UTF_8
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun String.enAes(): String {

        return try {
            val random = SecureRandom.getInstance("SHA1PRNG")
            random.setSeed(key)
            KeyGenerator.getInstance("AES").init(size, random)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                Cipher.ENCRYPT_MODE,
                SecretKeySpec(key, type),
                IvParameterSpec(iv)
            )

            Base64.encodeToString(
                cipher.doFinal(this.toByteArray(Charsets.UTF_8)), Base64.DEFAULT
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}