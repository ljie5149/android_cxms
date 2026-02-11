package com.jotangi.cxms.ui.Member

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.Api.book.apirequest.UserLoginRequest
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentLoginBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.jackyVariant.Common
import com.jotangi.cxms.utils.CommonKtUtils
import com.jotangi.cxms.utils.CoverPassword
import com.jotangi.cxms.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHandler()
        initView()
        initCallBack()
    }

    private fun initView() {

        setToolbar("登入")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initHandler() {

        binding.apply {

            // 會員使用條款
            LoRule.setOnClickListener { findNavController().navigate(R.id.usertermsFragment) }
            // 重設密碼
            loForget.setOnClickListener { findNavController().navigate(R.id.forgetpwdFragment) }
            // 註冊
            toRrgister.setOnClickListener { findNavController().navigate(R.id.registerFragment) }

            LoPwd.transformationMethod = CoverPassword()

            ivEye.setOnTouchListener { view, motionEvent ->

                when (motionEvent.action) {

                    MotionEvent.ACTION_DOWN -> {
                        LoPwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    }

                    MotionEvent.ACTION_UP -> {
                        LoPwd.transformationMethod = CoverPassword()
                    }
                }

                return@setOnTouchListener true
            }

            // 登入
            toLogin.setOnClickListener {

                val pid: String = binding.LoPhpne.text.toString()
                val pw: String = binding.LoPwd.text.toString()

                if (checkAccPwd(pid, pw)) {

                    showProgress()

                    lifecycleScope.launch {

                        FirebaseApp.initializeApp(requireContext())
                        var token: String?
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->

                                if (!task.isSuccessful) {
                                    closeProgress()
                                    return@OnCompleteListener
                                }

                                token = task.result
                                Log.w(TAG, "FCM_token: $token")

                                lifecycleScope.launch {

                                    CoroutineScope(Dispatchers.IO).launch {

                                        val adId = try {
                                            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(
                                                requireContext()
                                            )
                                            adInfo.id
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            null
                                        }

                                        Log.w(TAG, "adId: $adId")
                                        val response = apiRepository.userLogin2(
                                            UserLoginRequest(
                                                Common.getToken(),
                                                pid,
                                                pw,
                                                token,
                                                adId
                                            )
                                        )

                                        if (response.code == "0x0200") {

                                            SharedPreferencesUtil.instances.setAccountPid(pid)
                                            SharedPreferencesUtil.instances.setAccountPwd(pw)

                                            val res = apiRepository.memberInfo()

                                            if (res.isNotEmpty()) {

                                                SharedPreferencesUtil.instances.setAccountId(res[0].member_id)
                                                SharedPreferencesUtil.instances.setAccountName(res[0].member_name)

                                                CoroutineScope(Dispatchers.Main).launch {
                                                    findNavController().navigate(
                                                        LoginFragmentDirections.actionLoginFragmentToMemberFragment()
                                                    )
                                                }
                                            }

                                            closeProgress()

                                        } else {

                                            closeProgress()
                                            if(response.code.equals("0x0201")) {
                                                showErrorMsgDialog("帳號錯誤")
                                            } else {
                                                showErrorMsgDialog("密碼錯誤")

                                            }

//                                            showErrorMsgDialog(response.responseMessage.toString())

                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // 檢查 帳、密
    private fun checkAccPwd(tel: String, pw: String): Boolean {

        var isSafe = false

        when {
            tel.isEmpty() -> showErrorMsgDialog("帳號不能為空白。")
            tel.length < 9 -> showErrorMsgDialog("帳號長度錯誤。")
            pw.isEmpty() -> showErrorMsgDialog("密碼不能為空白。")
            pw.length < 6 -> showErrorMsgDialog("請輸入 6～12碼 英數字混合")
            pw.length > 12 -> showErrorMsgDialog("請輸入 6～12碼 英數字混合")
            else -> isSafe = true
        }

        return isSafe
    }

    private fun initCallBack() {

        bookViewModel.playStoreVersion.observe(viewLifecycleOwner) {

            Log.d(TAG, "playStoreVersion: $it")
            CommonKtUtils.instance.checkVersion(it) {

                showUpVersion()
            }
        }
    }
}