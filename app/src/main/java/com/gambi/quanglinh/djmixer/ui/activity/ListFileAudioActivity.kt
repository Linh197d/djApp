package com.gambi.quanglinh.djmixer.ui.activity

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.adapter.ChooseAudioAdapter
import com.gambi.quanglinh.djmixer.databinding.ActivityListFileAudioBinding
import com.gambi.quanglinh.djmixer.listener.ChooseAudioListener
import com.gambi.quanglinh.djmixer.model.AudiObject
import com.gambi.quanglinh.djmixer.utils.MyUtils
import com.gambi.quanglinh.djmixer.utils.PermissionManager


class ListFileAudioActivity : AppCompatActivity(), ChooseAudioListener {
    var audioAdapter: ChooseAudioAdapter? = null
    var audioList = mutableListOf<AudiObject>()
    private lateinit var binding: ActivityListFileAudioBinding
    private var mWasGetContentIntent = false
    private val REQUEST_CODE_EDIT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListFileAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MyUtils.setStatusBar3(this)


//        getAudio()
        audioAdapter =
            ChooseAudioAdapter(this, this)
        val intent = intent
        mWasGetContentIntent = intent.action == Intent.ACTION_GET_CONTENT
        initComponents()
        initViews()
        initEvents()
    }


    private fun initComponents() {

    }

    private fun initViews() {
        getData(0)
    }

    private fun initEvents() {
        binding.backAudio.setOnClickListener {
            finish()
        }
    }

    private fun getData(type: Int) {//0 is Audio, other are Video

        if (PermissionManager.hasReadAudioPermission(this)) {
            getAudios()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requestAudioPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO
                    )
                )
            else {
                requestAudioPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }


    }


    private fun getAudios() {
        audioAdapter?.clearData()
        binding.rcvAudio.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rcvAudio.adapter = audioAdapter
        val selection = StringBuilder("is_music != 0 AND title != ''")
        // Display audios in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            selection.toString(),
            null,
            sortOrder
        )



        if (cursor != null && cursor.moveToFirst()) {
            do {
                val filePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val filePath = cursor.getString(filePathColumn)
                val title: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val duration: Int =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                if (duration > 1000) {
                    Log.d(
                        "audio",
                        "filePath:$filePath,title:$title,duration:$duration,artist:$artist"
                    )
                    audioAdapter?.addAudio(AudiObject(filePath, title, artist, duration))
                }
//                }
            } while (cursor.moveToNext())
        }
        cursor?.close()

    }

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        if (granted) {
            getAudios()
        } else {
            MyUtils.showPermissionRequestDialog(
                this@ListFileAudioActivity,
                getString(R.string.setting_permission_audio), true
            )
        }
    }


    override fun onAudioChoosed(audio: AudiObject) {
//        setResult(
//            RESULT_OK,
////            Intent().putExtra(Constant.AUDIO_SESSION_ID, audio.filePath).putExtra("longitude", longitude)
//            Intent().putExtra(Constant.AUDIO_SESSION_ID, audio.filePath)
//        )
//        finish()
        try {
            val intent = Intent(Intent.ACTION_EDIT, Uri.parse(audio.filePath))
            intent.putExtra("was_get_content_intent", mWasGetContentIntent)
            intent.setClassName("com.gambi.quanglinh.djmixer", "com.gambi.quanglinh.djmixer.RingdroidEditActivity")
            startActivityForResult(intent, REQUEST_CODE_EDIT)
        } catch (e: Exception) {
            Log.e("Ringdroid", "Couldn't start editor")
        }
//        val intent = Intent(this, RingdroidEditActivity2::class.java)
//        intent.putExtra(Constant.AUDIO_SESSION_ID, audio.filePath)
//        startActivity(intent)
    }

    override fun onMenuDotAudio(audio: AudiObject, position: Int) {

    }
}