package com.jotangi.cxms.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.BuildConfig
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.Api.book.OppicListData
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentPictureListBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.AppUtils
import com.jotangi.cxms.utils.DialogUtil
import com.jotangi.cxms.utils.DialogUtils
import com.jotangi.cxms.utils.FileUtils
import kotlinx.coroutines.launch
import java.io.File

class PictureListFragment : BaseFragment() {

    private lateinit var binding: FragmentPictureListBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }
    private val args by navArgs<PictureListFragmentArgs>()
    private lateinit var oppicListAdapter: OppicListAdapter
    private var list = ArrayList<OppicListData>()

    /**
     * registerForActivityResult
     */
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { result ->

        if (!result) return@registerForActivityResult

        DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

            lifecycleScope.launch {

                try {
                    val imgfile = FileUtils.getCacheImageFile(
                        requireContext(),
                        FileUtils.USER_HEAD_PHOTO_FILE
                    )
                    if (imgfile.exists()) {
                        Log.w(TAG, "存在")
                        AppUtils().pictureCutDown(requireContext(), imgfile.path)
                    }

                    val response = apiRepository.addOppic(
                        args.order,
                        "${requireContext().filesDir}/picture.jpeg"
                    )

                    if (response.code == "0x0200") {

                        lifecycleScope.launch {

                            bookViewModel.oppicList(args.order)
                            dialog.dismiss()
                        }

                    } else {
                        dialog.dismiss()
                        showErrorMsgDialog(response.responseMessage!!)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val pickLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        it?.let { uri ->

            DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                lifecycleScope.launch {

                    val path = AppUtils.getPath(requireContext(), uri)
                    Log.d(TAG, "path: $path")
                    val file = File(path)
                    if (file.exists()) {
                        AppUtils().pictureCutDown(requireContext(), path)
                    }

                    val response = apiRepository.addOppic(
                        args.order,
                        "${requireContext().filesDir}/picture.jpeg"
                    )

                    if (response.code == "0x0200") {

                        lifecycleScope.launch {

                            bookViewModel.oppicList(args.order)
                            dialog.dismiss()
                        }

                    } else {
                        dialog.dismiss()
                        showErrorMsgDialog(response.responseMessage!!)
                    }
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPictureListBinding.inflate(inflater, container, false)
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
            bookViewModel.oppicList(args.order)
        }
    }

    private fun initView() {

        setToolbarArrow("圖片上傳")

        oppicListAdapter = OppicListAdapter(list)
        binding.rvPl.apply {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = oppicListAdapter
        }

        oppicListAdapter.delClick = { i ->

            Log.d(TAG, "click del: $i")
            val oid = bookViewModel.oppicListLiveData.value!![i].oid!!

            DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                lifecycleScope.launch {
                    val response = apiRepository.delOppic(args.order, oid)

                    when (response.code) {
                        "0x0200" -> {
                            lifecycleScope.launch {
                                bookViewModel.oppicList(args.order)
                                dialog.dismiss()
                            }
                        }
                        else -> {
                            dialog.dismiss()
                            showErrorMsgDialog(response.responseMessage!!)
                        }
                    }
                }
            }
        }
    }

    private fun initHandler() {

        binding.apply {

            btSelectPicture.setOnClickListener {

                DialogUtils().showMultiple(
                    requireActivity(),
                    "",
                    "請選擇圖片來源",
                    "相機",
                    "圖片",
                    object : DialogUtils.OnMultipleClickListener {
                        override fun onOk() {

                            val uri = FileUtils.getCacheImageUri(
                                requireContext(),
                                FileUtils.USER_HEAD_PHOTO_FILE,
                                BuildConfig.APPLICATION_ID
                            )
                            cameraLauncher.launch(uri)
                        }

                        override fun onCancel() {
                            pickLauncher.launch("image/*")
                        }

                    }
                )
            }

            btOkSend.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initCallBack() {

        bookViewModel.oppicListLiveData.observe(viewLifecycleOwner) {

            list.clear()
            list.addAll(it)
            oppicListAdapter.notifyDataSetChanged()
        }
    }
}