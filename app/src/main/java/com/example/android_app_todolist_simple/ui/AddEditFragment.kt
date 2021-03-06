package com.example.android_app_todolist_simple.ui

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.android_app_todolist_simple.ui.viewmodels.AddEditViewModel
import com.example.android_app_todolist_simple.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()
    private val addEditViewModel: AddEditViewModel by viewModels()
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


        val id = navigationArgs.todoId
        mainViewModel.todoId.value = id

        // edit existing to-do case
        if (id != -1) {
            mainViewModel.editTodoDetail.observe(viewLifecycleOwner) { todo->
                binding.apply {
                    editTitle.editText?.setText(todo.todoTitle)
                    editDetail.editText?.setText(todo.todoDetail)
                    switchButton.isChecked = todo.isChecked
                }
                //show alarm time
                if(todo.alarmTime != null){
                    // check the alarm time is after current time
                    if(Date(todo.alarmTime).after(Date())) {

                        binding.alarmText.text = resources.getString(R.string.alarm_time_hint,
                            SimpleDateFormat("h:mm a").format(Date(todo.alarmTime)))
                        binding.cancelAlarm.isEnabled = true
                    }
                }
                // already had recording file
                if (todo.audioRecord != null) {
                    fileName = todo.audioRecord.toString()

                    binding.recordFileCardView.visibility = View.VISIBLE
                    binding.recordingTime.text = SimpleDateFormat("MM/dd h:mm a").format(Date(todo.audioRecord.toLong()))
                }

                createdTime = todo.createdTime
            }



            _binding.saveTodo.setOnClickListener {

                updateItem(id)
                goBackToList()
            }
        }
        // add new to-do case
        if (id == -1) {

            addEditViewModel.alarmTime = null
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

            addEditViewModel.alarmTime = null
            updateItem(id)
            binding.alarmText.text = "No Alarm Set"
            AlarmService(requireContext()).cancelAlarm(todoBundle)
            Snackbar.make(view, "Canceled Alarm Notification", Snackbar.LENGTH_LONG).show()
        }



    }

    private fun updateItem(id: Int) {
        val newTodoTitle = _binding.editTitle.editText?.text.toString()
        val newTodoDetail = _binding.editDetail.editText?.text.toString()
        if (addEditViewModel.isEntryValid(newTodoTitle)){
            mainViewModel.updateTodo(
                Todo(
                    id = id,
                    todoTitle = newTodoTitle,
                    todoDetail = newTodoDetail,
                    isChecked = binding.switchButton.isChecked,
                    alarmTime = addEditViewModel.alarmTime,
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
        if(addEditViewModel.isEntryValid(newTodoTitle)){
            mainViewModel.insertTodo(
                Todo(
                    id = 0,
                    todoTitle = newTodoTitle,
                    todoDetail = newTodoDetail,
                    isChecked = binding.switchButton.isChecked,
                    alarmTime = addEditViewModel.alarmTime,
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
        addEditViewModel.alarmTime = c.timeInMillis
        _binding.cancelAlarm.isEnabled = true
    }

    private fun checkAndGetRecordingPermission(){
        //??????????????????
        val permission = android.Manifest.permission.RECORD_AUDIO
        val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted) {
                setRecordingFolder()
                setRecordingListener()
            }else{
                Snackbar.make(requireView(), "???????????????????????????", Snackbar.LENGTH_LONG).show()
            }
        }
        //??????????????????????????????????????????
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED) {
            //???????????????????????????????????????
            requestPermissions.launch(permission)
        } else {
            //?????????????????????????????????????????????????????????
            setRecordingFolder()
            setRecordingListener()
        }
    }


    private fun setRecordingFolder() { //???????????????
        folder = File(context?.filesDir?.absolutePath  +"/record") //?????????????????????
        if (!folder.exists()) {
            folder.mkdirs() //?????????????????????????????????
        }
    }
    private fun setRecordingListener() { //???????????????

        var isRecording:Boolean = false
        var isPlaying:Boolean = false

        val textView = _binding.textRecord
        val btnDelRecording = _binding.btnDelRecord
        val btnStartRecording = _binding.btnRecord
        val btnPlayRecording = _binding.btnPlayRecord


        btnDelRecording.setOnClickListener {
            delRecording()
            textView.text = "???????????????"

        }
        btnStartRecording.setOnClickListener {
            isRecording = when(isRecording){
                false -> {
                    btnStartRecording.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_stop, null))
                    fileName = "${Calendar.getInstance().time.time}" //?????????????????????????????????
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC) //????????????????????????
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //??????????????????
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //???????????????
                    recorder.setOutputFile(File(folder, fileName).absolutePath) //??????????????????
                    recorder.prepare() //????????????
                    recorder.start() //????????????
                    textView.text = "?????????..."
                    btnPlayRecording.isEnabled = false

                    true
                }
                else -> {
                    btnStartRecording.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_mic, null))
                    try {
                        //val file = File(folder, fileName) //??????????????????
                        recorder.stop() //????????????
                        textView.text = "???????????????"
                        btnPlayRecording.isEnabled = true

                        binding.recordFileCardView.visibility = View.VISIBLE
                        binding.recordingTime.text = SimpleDateFormat("MM/dd h:mm a").format(Date())
                        Snackbar.make(requireView(), "????????????", Snackbar.LENGTH_LONG).show()


                    } catch (e: Exception) {
                        e.printStackTrace()
                        recorder.reset() //???????????????
                        textView.text = "????????????"
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
                    val file = File(folder, fileName!!) //??????????????????
                    player.setDataSource(requireContext(),Uri.fromFile(file)) //??????????????????
                    player.setVolume(1f, 1f) //????????????????????????
                    player.prepare() //????????????
                    player.start() //????????????
                    Snackbar.make(requireView(), "???????????????...", Snackbar.LENGTH_LONG).show()
                    btnStartRecording.isEnabled = false

                    true
                }
                else -> {
                    btnPlayRecording.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_arrow, null)
                    player.stop() //????????????
                    player.reset() //???????????????
                    Snackbar.make(requireView(), "????????????", Snackbar.LENGTH_LONG).show()
                    btnStartRecording.isEnabled = true

                    false
                }
            }
        }
        player.setOnCompletionListener { //???????????????????????????????????????
            it.reset() //???????????????
            //textView.text = "????????????"
            Snackbar.make(requireView(), "????????????", Snackbar.LENGTH_LONG).show()
            btnPlayRecording.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_arrow, null)
            btnStartRecording.isEnabled = true

        }
    }

    private fun delRecording() {
        val id = mainViewModel.todoId.value!!
        val newTodoTitle = _binding.editTitle.editText?.text.toString()
        val newTodoDetail = _binding.editDetail.editText?.text.toString()
        mainViewModel.updateTodo(
            Todo(id = id,
                todoTitle = newTodoTitle,
                todoDetail = newTodoDetail,
                isChecked = binding.switchButton.isChecked,
                alarmTime = addEditViewModel.alarmTime,
                audioRecord = null,
                createdTime = createdTime
            )
        )
        fileName = null
        binding.recordFileCardView.visibility = View.INVISIBLE
        Snackbar.make(requireView(), "???????????????", Snackbar.LENGTH_LONG).show()
    }
}