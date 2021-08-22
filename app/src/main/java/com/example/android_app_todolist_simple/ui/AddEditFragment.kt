package com.example.android_app_todolist_simple.ui

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android_app_todolist_simple.R
import com.example.android_app_todolist_simple.alarm.AlarmService
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
class AddEditFragment : Fragment() {
    val viewModel: MainViewModel by viewModels()
    val navigationArgs: AddEditFragmentArgs by navArgs()
    lateinit var _binding: FragmentAddEditBinding
    val binding get() = _binding
    private val recorder = MediaRecorder()
    private val player = MediaPlayer()
    private lateinit var folder: File
    private var fileName:String? = null
    private var todoBundle: Bundle = Bundle()
    private var createdTime: Long? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)

        checkAndGetRecordingPermission()
        return _binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val id = navigationArgs.id

        // edit existing to-do case
        if (id != -1) {
            lifecycle.coroutineScope.launch {

                viewModel.getSpecificTodo(id).collect { it ->

                    binding.apply {
                        editTitle.editText?.setText(it.todoTitle)
                        editDetail.editText?.setText(it.todoDetail)
                    }
                    //show alarm time
                    if(it.alarmTime != null){
                        // check the alarm time is after current time
                        if(Date(it.alarmTime).after(Date())) {

                            binding.alarmText.text = resources.getString(R.string.alarm_time_hint,
                                SimpleDateFormat("h:mm a").format(Date(it.alarmTime)))
                            binding.cancelAlarm.isEnabled = true
                        }
                    }
                    // already had recording file
                    if (it.audioRecord != null) {
                        fileName = it.audioRecord.toString()

                        binding.recordFileCardView.visibility = View.VISIBLE
                        binding.recordingTime.text = SimpleDateFormat("MM/dd h:mm a").format(Date(it.audioRecord.toLong()))
                    }

                    createdTime = it.createdTime

                }
            }

            _binding.saveTodo.setOnClickListener {

                updateItem(id)
                goBackToList()
            }
        }
        // add new to-do case
        if (id == -1) {
            viewModel.alarmTime = null
            _binding.saveTodo.setOnClickListener {

                addItem()
                goBackToList()
            }

        }


        //click to pick time and start the alarm
        _binding.picktime.setOnClickListener {
            val todoContent = _binding.editTitle.editText?.text

            todoBundle.putString("TODO_CONTENT", todoContent.toString())
            todoBundle.putInt("TODO_ID", id)
            fun showTimePickerDialog(v: View) {

                TimePickerFragment(::updateAlarmTimeText, todoBundle).show(childFragmentManager, "timePicker")
            }
            showTimePickerDialog(it)
        }

        // cancel the alarm
        _binding.cancelAlarm.setOnClickListener {

            viewModel.alarmTime = null
            updateItem(id)
            binding.alarmText.text = "No Alarm Set"
            AlarmService(requireContext()).cancelAlarm(todoBundle)
            Snackbar.make(view, "Canceled Alarm Notification", Snackbar.LENGTH_LONG).show()
        }



    }

    private fun updateItem(id: Int) {
        val newTodoTitle = _binding.editTitle.editText?.text.toString()
        val newTodoDetail = _binding.editDetail.editText?.text.toString()
        if (viewModel.isEntryValid(newTodoTitle)){
            viewModel.updateTodo(
                Todo(
                    id = id,
                    todoTitle = newTodoTitle,
                    todoDetail = newTodoDetail,
                    isChecked = false,
                    alarmTime = viewModel.alarmTime,
                    audioRecord = fileName,
                    createdTime = createdTime
                )
            )

        }else{
            Toast.makeText(context, "Please do not leave blank",Toast.LENGTH_SHORT).show()
        }

    }
    private fun goBackToList(){
        val action = AddEditFragmentDirections.actionAddEditFragmentToListFragment()
        findNavController().navigate(action)
    }

    private fun addItem() {
        val newTodoTitle = _binding.editTitle.editText?.text.toString()
        val newTodoDetail = _binding.editDetail.editText?.text.toString()
        if(viewModel.isEntryValid(newTodoTitle)){
            viewModel.insertTodo(
                Todo(
                    id = 0,
                    todoTitle = newTodoTitle,
                    todoDetail = newTodoDetail,
                    isChecked = false,
                    alarmTime = viewModel.alarmTime,
                    audioRecord = fileName,
                    createdTime = Calendar.getInstance().timeInMillis
                )
            )

        }else{
            Toast.makeText(context, "Please do not leave blank",Toast.LENGTH_SHORT).show()
        }

    }
    private fun updateAlarmTimeText(c: Calendar){
        _binding.alarmText.text = resources.getString(R.string.alarm_time_hint, SimpleDateFormat("h:mm a").format(c?.time))
        viewModel.alarmTime = c.timeInMillis
        _binding.cancelAlarm.isEnabled = true
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

        var isRecording:Boolean = false
        var isPlaying:Boolean = false

        val textView = _binding.textRecord
        val btnDelRecording = _binding.btnDelRecord
        val btnStartRecording = _binding.btnRecord
        val btnPlayRecording = _binding.btnPlayRecord


        btnDelRecording.setOnClickListener {
            delRecording()
            textView.text = "請開始錄音"

        }
        btnStartRecording.setOnClickListener {
            isRecording = when(isRecording){
                false -> {
                    btnStartRecording.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_stop, null))
                    fileName = "${Calendar.getInstance().time.time}" //定義檔案名稱為目前時間
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC) //聲音來源為麥克風
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //設定輸出格式
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //設定編碼器
                    recorder.setOutputFile(File(folder, fileName).absolutePath) //設定輸出路徑
                    recorder.prepare() //準備錄音
                    recorder.start() //開始錄音
                    textView.text = "錄音中..."
                    btnPlayRecording.isEnabled = false

                    true
                }
                else -> {
                    btnStartRecording.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_mic, null))
                    try {
                        //val file = File(folder, fileName) //定義錄音檔案
                        recorder.stop() //結束錄音
                        textView.text = "請開始錄音"
                        btnPlayRecording.isEnabled = true

                        binding.recordFileCardView.visibility = View.VISIBLE
                        binding.recordingTime.text = SimpleDateFormat("MM/dd h:mm a").format(Date())
                        Snackbar.make(requireView(), "錄音完成", Snackbar.LENGTH_LONG).show()


                    } catch (e: Exception) {
                        e.printStackTrace()
                        recorder.reset() //重置錄音器
                        textView.text = "錄音失敗"
                        btnPlayRecording.isEnabled = true

                    }
                    false
                }
            }

        }
        btnPlayRecording.setOnClickListener {
            isPlaying = when(isPlaying){
                false -> {
                    btnPlayRecording.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_pause, null)
                    val file = File(folder, fileName!!) //定義播放檔案
                    player.setDataSource(requireContext(),Uri.fromFile(file)) //設定音訊來源
                    player.setVolume(1f, 1f) //設定左右聲道音量
                    player.prepare() //準備播放
                    player.start() //開始播放
                    Snackbar.make(requireView(), "播放錄音中...", Snackbar.LENGTH_LONG).show()
                    btnStartRecording.isEnabled = false

                    true
                }
                else -> {
                    btnPlayRecording.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_arrow, null)
                    player.stop() //停止播放
                    player.reset() //重置播放器
                    Snackbar.make(requireView(), "播放結束", Snackbar.LENGTH_LONG).show()
                    btnStartRecording.isEnabled = true

                    false
                }
            }
        }
        player.setOnCompletionListener { //設定播放器播放完畢的監聽器
            it.reset() //重置播放器
            //textView.text = "播放結束"
            Snackbar.make(requireView(), "播放結束", Snackbar.LENGTH_LONG).show()
            btnPlayRecording.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_arrow, null)
            btnStartRecording.isEnabled = true

        }
    }

    private fun delRecording() {
        val id = navigationArgs.id
        val newTodoTitle = _binding.editTitle.editText?.text.toString()
        viewModel.updateTodo(
            Todo(id = id,
                todoTitle = newTodoTitle,
                isChecked = false,
                alarmTime = viewModel.alarmTime,
                audioRecord = null,
                createdTime = createdTime
            )
        )
        fileName = null
        binding.recordFileCardView.visibility = View.INVISIBLE
        Snackbar.make(requireView(), "錄音已刪除", Snackbar.LENGTH_LONG).show()
    }
}