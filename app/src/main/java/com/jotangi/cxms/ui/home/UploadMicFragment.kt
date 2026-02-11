package com.jotangi.cxms.ui.home

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.FileObserver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jotangi.cxms.Api.ApiConstant
import com.jotangi.cxms.Api.book.BookApiRepository
import com.jotangi.cxms.Api.book.Opmp3ListData
import com.jotangi.cxms.BaseFragment
import com.jotangi.cxms.databinding.FragmentUploadMicBinding
import com.jotangi.cxms.databinding.ToolbarBinding
import com.jotangi.cxms.utils.DialogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class UploadMicFragment : BaseFragment() {

    private lateinit var binding: FragmentUploadMicBinding
    override fun getToolBar(): ToolbarBinding = binding.toolbar

    private val args by navArgs<UploadMicFragmentArgs>()
    private val apiRepository: BookApiRepository by lazy { BookApiRepository() }

    private lateinit var recorder: MediaRecorder
    private lateinit var file: File

    //    private var isFile = false
    private var isDown = false

    private var list = ArrayList<Opmp3ListData>()
    private lateinit var mp3ListAdapter: Mp3ListAdapter
    private lateinit var player: MediaPlayer


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadMicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initApi()
        initView()
        initHandler()
        initCallBack()
    }

    private fun initData() {
        file = File(requireActivity().filesDir, "raw.mp3")
    }

    private fun initApi() {
        lifecycleScope.launch {
            bookViewModel.opmp3List(args.order)
        }
    }

    private fun initView() {

        setToolbarArrow("上傳語音")

        mp3ListAdapter = Mp3ListAdapter(list)
        binding.rvMp3.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = mp3ListAdapter
        }
    }

    private fun initHandler() {

        binding.apply {

            ivMic.setOnClickListener {

                countdown()
                ivMic.visibility = View.GONE
                ivStop.visibility = View.VISIBLE
                ivPlay.isEnabled = false

                recorder = MediaRecorder()
                recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(file.path)
                    prepare()
                    start()
                }
            }

            ivStop.setOnClickListener {

                isDown = false
                tvSec.text = ""

                stop()

                OverFile(file.path, FileObserver.CLOSE_WRITE).apply {

                    over = {

                        CoroutineScope(Dispatchers.Main).launch {

                            DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                                lifecycleScope.launch {

                                    val response = apiRepository.addOpmp3(args.order, file)

                                    when (response.code) {
                                        "0x0200" -> {
                                            lifecycleScope.launch {
                                                bookViewModel.opmp3List(args.order)
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

                    startWatching()
                }
            }

//            ivPlay.setOnClickListener {
//
//                if (isFile) {
//
//                    ivMic.isEnabled = false
//                    ivPlay.isEnabled = false
//
//                    MediaPlayer.create(requireContext(), Uri.fromFile(file)).apply {
//                        setOnCompletionListener {
//                            ivPlay.isEnabled = true
//                            ivMic.isEnabled = true
//                            release()
//                        }
//                        start()
//                    }
//
//                } else {
//                    showErrorMsgDialog("請先錄製音訊")
//                }
//            }

            btOkSend.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        mp3ListAdapter.apply {

            delClick = { i ->

                val oid = bookViewModel.opmp3ListLiveData.value!![i].oid!!

                DialogUtil.instance.loadingShow(requireActivity()) { dialog ->

                    lifecycleScope.launch {

                        val response = apiRepository.delOpmp3(args.order, oid)

                        when (response.code) {
                            "0x0200" -> {
                                lifecycleScope.launch {
                                    bookViewModel.opmp3List(args.order)
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

            playClick = { i ->

                val op_mp3 = bookViewModel.opmp3ListLiveData.value!![i].op_mp3!!
                val url = "${ApiConstant.IMAGE_URL}$op_mp3"
                Log.d(TAG, "url: $url")

                player = MediaPlayer()

                lifecycleScope.launch {
                    player.apply {

                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(url)
                        prepare()

                        setOnCompletionListener {
                            reset()
                        }

                        start()
                    }
                }
            }
        }
    }

    class OverFile(path: String?, mask: Int) :
        FileObserver(path, mask) {
        var over: () -> Unit = {}
        override fun onEvent(i: Int, s: String?) {
            over.invoke()
        }
    }

    private fun stop() {

        CoroutineScope(Dispatchers.Main).launch {
            binding.apply {
                ivMic.visibility = View.VISIBLE
                ivStop.visibility = View.GONE
                ivPlay.isEnabled = true
            }

            recorder.apply {

                try {
                    stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                reset()
                release()
//                isFile = true
            }
        }
    }

    private fun countdown() {

        var num = 60
        binding.tvSec.text = num.toString()
        isDown = true
        CoroutineScope(Dispatchers.IO).launch {
            while (isDown) {

                Log.d(TAG, "num: $num")
                Thread.sleep(1000)
                num -= 1

                if (!isDown) {
                    return@launch
                }
                context?.let {

                    CoroutineScope(Dispatchers.Main).launch {
                        binding.tvSec.text = num.toString()
                    }
                    if (num == 0) {
                        isDown = false
                        stop()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        isDown = false
    }

    private fun initCallBack() {

        bookViewModel.opmp3ListLiveData.observe(viewLifecycleOwner) {
            list.clear()
            list.addAll(it)
            mp3ListAdapter.notifyDataSetChanged()
        }
    }
}