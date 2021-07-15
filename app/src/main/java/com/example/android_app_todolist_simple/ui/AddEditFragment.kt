package com.example.android_app_todolist_simple.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android_app_todolist_simple.R
import com.example.android_app_todolist_simple.databinding.FragmentAddEditBinding
import com.example.android_app_todolist_simple.db.Todo
import com.example.android_app_todolist_simple.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {
    val viewModel: MainViewModel by viewModels()
    val navigationArgs: AddEditFragmentArgs by navArgs()
    lateinit var _binding: FragmentAddEditBinding
    val binding get() = _binding
    private val recorder = MediaRecorder()
    private val player = MediaPlayer()
    private lateinit var folder: File
    private var fileName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val binding = FragmentAddEditBinding.bind(view)
        _binding = FragmentAddEditBinding.bind(view)

        val id = navigationArgs.id

        // edit existing to-do case
        if (id != -1) {
            lifecycle.coroutineScope.launch {

                viewModel.getSpecificTodo(id).collect { it ->
                    binding.editText.setText(it.todoTitle)
                    //show alarm time
                    if(it.alarmTime!=null){
                        binding.alarmText.text = SimpleDateFormat( "h:mm a").format(Date(it.alarmTime!!))
                    }
                    //
                    if (it.audioRecord != null) {
                        fileName = it.audioRecord.toString()
                        binding.textRecord.text="you have a audio recording"
                    }

                }
            }

            _binding.saveTodo.setOnClickListener {
                val newTodoTitle = _binding.editText.text.toString()
                updateItem(id, newTodoTitle)
            }
        }
        // add new to-do case
        if (id == -1) {
            viewModel.alarmTime = null
            _binding.saveTodo.setOnClickListener {
                val newTodoTitle = _binding.editText.text.toString()
                addItem(newTodoTitle)
            }
        }


        //click to pick time and start the alarm
        _binding.picktime.setOnClickListener {
            val todoContent = _binding.editText.text
            fun showTimePickerDialog(v: View) {

                TimePickerFragment(::updateAlarmTimeText, todoContent.toString()).show(childFragmentManager, "timePicker")
            }
            showTimePickerDialog(it)
        }

        checkAndGetRecordingPermission()

    }

    private fun updateItem(id: Int, newTodoTitle: String) {
        if (viewModel.isEntryValid(newTodoTitle)){
            viewModel.updateTodo(Todo(id = id, todoTitle = newTodoTitle, isChecked = false, alarmTime = viewModel.alarmTime, audioRecord = fileName))
            val action = AddEditFragmentDirections.actionAddEditFragmentToListFragment()
            findNavController().navigate(action)
        }else{
            Toast.makeText(context, "Please do not leave blank",Toast.LENGTH_SHORT).show()
        }

    }

    private fun addItem(newTodoTitle: String) {
        if(viewModel.isEntryValid(newTodoTitle)){
            viewModel.insertTodo(Todo(id = 0, todoTitle = newTodoTitle, isChecked = false,alarmTime = viewModel.alarmTime, audioRecord = fileName ))
            val action = AddEditFragmentDirections.actionAddEditFragmentToListFragment()
            findNavController().navigate(action)
        }else{
            Toast.makeText(context, "Please do not leave blank",Toast.LENGTH_SHORT).show()
        }

    }
    private fun updateAlarmTimeText(c: Calendar?){
        _binding.alarmText.text = SimpleDateFormat("h:mm a").format(c?.time)
        viewModel.alarmTime = c?.timeInMillis

    }

    private fun checkAndGetRecordingPermission(){
        //宣告錄音權限
        val permission = android.Manifest.permission.RECORD_AUDIO
        val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted) {
                setRecordingFolder()
                setRecordingListener()
            }else{
                Snackbar.make(requireView(), "您無法使用錄音功能", Snackbar.LENGTH_LONG).show()
            }
        }
        //判斷使用者是否已允許錄音權限
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED) {
            //尚未允許，向使用者要求權限
            requestPermissions.launch(permission)
        } else {
            //因已允許錄音權限，所以正常執行應用程式
            setRecordingFolder()
            setRecordingListener()
        }
    }


    private fun setRecordingFolder() { //設定資料夾
        folder = File(context?.filesDir?.absolutePath  +"/record") //定義資料夾名稱
        if (!folder.exists()) {
            folder.mkdirs() //建立存放錄音檔的資料夾
        }
    }
    private fun setRecordingListener() { //設定監聽器
        //將變數與 XML 元件綁定
        var isRecording:Boolean = false
        var isPlaying:Boolean = false

        val textView = _binding.textRecord
        val btn_del_record = _binding.btnDelRecord
        val btn_float_record = _binding.btnRecord
        val btn_float_play = _binding.btnPlayRecord


        btn_del_record.setOnClickListener {
            delRecording()
            textView.text = "請開始錄音"
        }
        btn_float_record.setOnClickListener {
            isRecording = when(isRecording){
                false -> {
                    btn_float_record.setImageDrawable(resources.getDrawable(R.drawable.ic_stop,requireContext().theme))
                    fileName = "${Calendar.getInstance().time.time}" //定義檔案名稱為目前時間
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC) //聲音來源為麥克風
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //設定輸出格式
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //設定編碼器
                    recorder.setOutputFile(File(folder, fileName).absolutePath) //設定輸出路徑
                    recorder.prepare() //準備錄音
                    recorder.start() //開始錄音
                    textView.text = "錄音中..."
                    btn_float_play.isEnabled = false

                    true
                }
                else -> {
                    btn_float_record.setImageDrawable(resources.getDrawable(R.drawable.ic_recording,requireContext().theme))
                    try { //若使用模擬器停止錄音容易產生例外，所以使用 try-catch 處理
                        val file = File(folder, fileName) //定義錄音檔案
                        recorder.stop() //結束錄音
                        textView.text = "已儲存至${file.absolutePath}"
                        btn_float_play.isEnabled = true


                    } catch (e: Exception) {
                        e.printStackTrace()
                        recorder.reset() //重置錄音器
                        textView.text = "錄音失敗"
                        btn_float_play.isEnabled = true

                    }
                    false
                }
            }

        }
        btn_float_play.setOnClickListener {
            isPlaying = when(isPlaying){
                false -> {
                    btn_float_play.setImageDrawable(resources.getDrawable(R.drawable.ic_pause,requireContext().theme))
                    val file = File(folder, fileName) //定義播放檔案
                    player.setDataSource(requireContext(),Uri.fromFile(file)) //設定音訊來源
                    player.setVolume(1f, 1f) //設定左右聲道音量
                    player.prepare() //準備播放
                    player.start() //開始播放
                    textView.text = "播放中..."
                    btn_float_record.isEnabled = false

                    true
                }
                else -> {
                    btn_float_play.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow,requireContext().theme))
                    player.stop() //停止播放
                    player.reset() //重置播放器
                    textView.text = "播放結束"
                    btn_float_record.isEnabled = true

                    false
                }
            }
        }
        player.setOnCompletionListener { //設定播放器播放完畢的監聽器
            it.reset() //重置播放器
            textView.text = "播放結束"
            btn_float_play.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow,requireContext().theme))
            btn_float_record.isEnabled = true

        }
    }

    private fun delRecording() {
        val id = navigationArgs.id
        val newTodoTitle = _binding.editText.text.toString()
        viewModel.updateTodo(
            Todo(id = id,
                todoTitle = newTodoTitle,
                isChecked = false,
                alarmTime = viewModel.alarmTime,
                audioRecord = null)
        )
    }
}