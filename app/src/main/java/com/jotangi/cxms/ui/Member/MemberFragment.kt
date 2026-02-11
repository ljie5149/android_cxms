package com.jotangi.cxms.ui.Member


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.Api.ApiUrl
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.BuildConfig
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentMemberBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.*
import com.jotangi.cxms.utils.smartwatch.WatchUtils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MemberFragment : BaseFragment() {

    private lateinit var binding: FragmentMemberBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemberBinding.inflate(inflater, container, false)
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
            bookViewModel.memberInfo {}
        }

        checkBoxCount()
    }

    private fun initView() {

        setToolbar("會員中心")

        binding.tvVersionName.text = "版本號：${BuildConfig.VERSION_NAME}"
    }

    private fun initHandler() {

        binding.apply {

            sivPhoto.setOnClickListener {
                val intent = Intent(requireActivity(), CropHeadImageActivity::class.java)
                getUserHeadLauncher.launch(intent)
            }

            // 智慧通關 QR Code
            tvQrcode.setOnClickListener { getQrcode() }

            // 會員資料
            tvMemberInfo.setOnClickListener {
                findNavController().navigate(R.id.informationFragment)
            }

            // 裝置設定
            tvWatch.setOnClickListener {

                if (SharedPreferencesUtil.instances.getWatchMac().isNullOrBlank()) {
                    findNavController().navigate(R.id.watchListFragment)
                } else {
                    findNavController().navigate(R.id.watchSettingFragment)
                }
            }

            tvPointChange.setOnClickListener {
                findNavController().navigate(R.id.action_MemberFragment_to_marketChangeFragment)
            }

            tvPointRecord.setOnClickListener {
                findNavController().navigate(R.id.action_MemberFragment_to_rewradFragment)

            }
            // 使用者條款
            tvUserterms.setOnClickListener { findNavController().navigate(R.id.usertermsFragment) }

            // 登出
            tvLogout.setOnClickListener {

                lifecycleScope.launch {
                    showProgress()
                    val response = apiRepository.userLogout2()
                    closeProgress()
                    if (!response.code.isNullOrBlank()) {
                        clearAccount()
                        CoroutineScope(Dispatchers.Main).launch {
                            findNavController().navigate(
                                MemberFragmentDirections.actionMemberFragmentToLoginFragment()
                            )
                        }
                    }
                }
            }

            // 授權設定
            tvOtherAuthorize.setOnClickListener {
                findNavController().navigate(R.id.otherAuthorizeFragment)
            }

            // 刪除帳號
            tvDel.setOnClickListener {

                DialogUtil.instance.multiMessageDialog(
                    requireActivity(),
                    "帳號刪除",
                    "確定要將帳號刪除嗎？",
                    "確定",
                    "取消",
                    okClick = { delAccount() }
                )
            }
        }
    }

    private fun clearAccount() {

        SharedPreferencesUtil.instances.setAccountId(null)
        SharedPreferencesUtil.instances.setAccountPid(null)
        SharedPreferencesUtil.instances.setAccountPwd(null)
        SharedPreferencesUtil.instances.setAccountMid(null)
        SharedPreferencesUtil.instances.setWatchMac(null)
        SharedPreferencesUtil.instances.setWatchName(null)
        WatchUtils.instance.disconnectBle()
        watchViewModel.setIsBleConnect(false)
        bookViewModel.clearData()
        FirebaseMessaging.getInstance().deleteToken()
    }

    private fun delAccount() {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                val response = apiBook.userUnregister2()

                if (response.code == ApiUrl.success) {

                    clearAccount()
                    findNavController().navigate(
                        MemberFragmentDirections.actionMemberFragmentToLoginFragment()
                    )
                } else {

                    showErrorMsgDialog(response.responseMessage.toString())
                }

                dialog.dismiss()
            }
        }
    }

    private fun getQrcode() {

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                var name = ""
                var email: String? = null
                bookViewModel.memberInfoDataList.value?.let {
//                    if (it[0].member_email.isNullOrBlank()) null else it[0].member_email
                    name = it[0].member_name.toString()
                    email = it[0].member_email
                }

                qrCodeViewModel.universalQrCode(
                    name,
                    email,
                    success = { findNavController().navigate(R.id.qrCodeListFragment) },
                    fail = { showErrorMsgDialog(it) }
                )

                dialog.dismiss()
            }
        }
    }

    private var getUserHeadLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val path = data.getStringExtra("path")
                SharedPreferencesUtil.instances.setAccountHeadShot(path)
                Timber.d("大頭照路徑: $path")
                watchViewModel.refreshHeadShotPath()
                if (path != null) {
                    reloadUserHeadShot(path)
                }
            }
        }
    }

    private fun reloadUserHeadShot(path: String?) {
        //Glide.with(requireContext()).load(path?:R.drawable.ic_img_user).into(binding.sivPhoto)
        if (path != null) {
            binding.sivPhoto.setImageBitmap(AppUtils.loadAndRotateBitmap(path))
            userUploadPicProcess(path)

        } else {
            binding.sivPhoto.setImageResource(R.drawable.ic_img_user)
        }
    }

    // 上傳 大頭照到後台
    private fun userUploadPicProcess(path: String) {
        lifecycleScope.launch {
            apiRepository.userUploadpic2(path)
        }
    }

    private fun initCallBack() {

        bookViewModel.playStoreVersion.observe(viewLifecycleOwner) {

            Log.d(TAG, "playStoreVersion: $it")
            CommonKtUtils.instance.checkVersion(it) {

                showUpVersion()
            }
        }

        bookViewModel.memberInfoDataList.observe(viewLifecycleOwner) {

            Picasso.get().load(ApiConstant.MUG_SHOT_URL + it[0].member_picture)
                .into(binding.sivPhoto)

            SharedPreferencesUtil.instances.setAccountMid(it[0].mid)
        }
    }
}