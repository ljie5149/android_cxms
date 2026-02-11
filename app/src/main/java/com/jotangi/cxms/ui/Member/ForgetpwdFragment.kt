package com.jotangi.cxms.ui.Member

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.R
import com.jotangi.cxms.databinding.FragmentForgetpwdBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgetpwdFragment : BaseFragment() {

    private lateinit var binding: FragmentForgetpwdBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgetpwdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {

        setToolbarArrow("忘記密碼")

        binding.apply {

            fgGet.setOnClickListener { getCode() }

            fgSend.setOnClickListener { send() }
        }
    }

    private fun getCode() {

        val account: String = binding.etPid.text.trim().toString()

        if (account.isBlank()) {
            showErrorMsgDialog("請輸入證號。")
            return
        }

        if (account.length < 9) {
            showErrorMsgDialog("請輸入正確的證號。")
            return
        }

        showProgress()

        lifecycleScope.launch {

            val response = apiRepository.userCode2(account)

            closeProgress()

            when (response.code) {

                "0x0200" -> {
                    showErrorMsgDialog("驗證碼已傳送。")
                    return@launch
                }

                "0x0201" -> {
                    showErrorMsgDialog("證號錯誤。")
                    return@launch
                }
            }
        }
    }

    private fun send() {

        val pid: String = binding.etPid.text.toString().trim()
        val code: String = binding.fgOtp.text.toString().trim()
        val pwd: String = binding.fgNewPwd.text.toString().trim()
        Log.d("micCheckUY", pwd)
        val again: String = binding.fgAgain.text.toString().trim()

        if (pid.isEmpty() || code.isEmpty() || pwd.isEmpty() || again.isEmpty()) {

            showErrorMsgDialog("欄位不得為空。")
            return
        }

        // 身分證號規則：1英文+9數字
        // 居留證號規則：2英文+8數字
        // 護照號碼規則：9位數，沒有英文
        if (pid.length < 9) {
            showErrorMsgDialog("請輸入正確的證號。")
            return
        }

        if (again != pwd) {

            showErrorMsgDialog("請再次檢查密碼。")

            binding.fgNewPwd.text.clear()
            binding.fgAgain.text.clear()

            return
        }

        showProgress()

        lifecycleScope.launch {
            Log.d("micCheckUY1", code)
            Log.d("micCheckUY2", pid)
            Log.d("micCheckUY3", pwd)
            val response = apiRepository.userResetPwd2(
                    pid,
                    pwd,
                    code
            )

            closeProgress()

            when (response.code) {

                "0x0200" -> {

                    CoroutineScope(Dispatchers.Main).launch {
                        DialogUtils.showSingle(
                            requireActivity(),
                            "重設密碼成功",
                            "請再次重新登入",
                        ) {
                            findNavController().navigate(R.id.loginFragment)
                        }
                    }
                }

                else -> {

                    CoroutineScope(Dispatchers.Main).launch {
                        DialogUtils.showSingle(
                            requireActivity(),
                            "重設密碼失敗",
                            response.responseMessage,
                        ) {
                            binding.fgNewPwd.text.clear()
                            binding.fgOtp.text.clear()
                            binding.fgAgain.text.clear()
                        }
                    }
                }
            }
        }
    }
}