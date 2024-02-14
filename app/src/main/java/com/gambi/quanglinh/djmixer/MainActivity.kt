package com.gambi.quanglinh.djmixer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gambi.quanglinh.djmixer.custom.AudioPlayer
import com.gambi.quanglinh.djmixer.databinding.ActivityMainBinding
import com.gambi.quanglinh.djmixer.listener.IDiscoHandleBack
import com.gambi.quanglinh.djmixer.listener.IDiscoRotate
import com.gambi.quanglinh.djmixer.model.AudiObject
import com.gambi.quanglinh.djmixer.ui.activity.AddAudioToDiscoActivity
import com.gambi.quanglinh.djmixer.ui.fragment.BassFragment
import com.gambi.quanglinh.djmixer.ui.fragment.CuesFragment
import com.gambi.quanglinh.djmixer.ui.fragment.EqualizerFragment
import com.gambi.quanglinh.djmixer.ui.fragment.LoopFragment
import com.gambi.quanglinh.djmixer.ui.fragment.SampleFragment
import com.gambi.quanglinh.djmixer.ui.fragment.ViewModelFragment1
import com.gambi.quanglinh.djmixer.utils.Constant
import com.gambi.quanglinh.djmixer.utils.MyUtils
import com.gambi.quanglinh.djmixer.utils.PermissionManager
import com.gambi.quanglinh.djmixer.utils.Settings
import com.gambi.quanglinh.djmixer.view.WaveFormJob
import kotlinx.coroutines.Runnable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), Handler.Callback {
    protected var recorder: MediaRecorder? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var player: AudioPlayer
   public var mediaPlayer1: MediaPlayer? = null
    public var mediaPlayer2: MediaPlayer? = null
    public var mediaPlayer3: MediaPlayer? = null
    private var duration1: Int = -1
    private var duration2: Int = -1
    private val colorSchemeLeft = R.color.primary
    private val colorSchemeRight = R.color.primary_main
    public val DISCO = "disco"
    public val EQ = "eq"
    public val BASS = "bass"
    public val LOOP = "loop"
    public val CUES = "cues"
    public val SAMPLE = "sample"
    public var functionChoosed = DISCO
    public var functionChoosed2 = DISCO
    private var uiUpdateHandler: Handler? = null
    private var handler: Handler? = null
    private var handlerRecord: Handler? = null
    private var audio: AudiObject? = null
    private var pathMusic: String? = null // "/storage/emulated/0/Download/Seven-Jung-Kook.mp3"
    private var pathMusic2: String? =
        null // "/storage/emulated/0/Music/wz video voice changer/Slow down_1694396796888_helium_Hang động.mp3"
    private var isBoost1: Boolean = false
    private var isBoost2: Boolean = false
    lateinit var viewModel: ViewModelFragment1
    var isReset = false
    var isReset2 = true
   var isRecording = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ViewModelFragment1::class.java]
        setContentView(binding.root)
        MyUtils.setStatusBar3(this)
        if (PermissionManager.hasReadAudioPermission(this@MainActivity) && PermissionManager.hasRecordAudioPermission(
                this@MainActivity
            )
        ) {
            initComponents()
            initViews()
            initEvents()
        } else {
            val permissions =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_AUDIO
                )
                else arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun initComponents() {
        handler = Handler(Looper.getMainLooper())
        handlerRecord = Handler(Looper.getMainLooper())
        mediaPlayer3 = MediaPlayer.create(this@MainActivity, R.raw.sample)
        mediaPlayer1?.audioSessionId?.let { binding.visualizer1.setPlayer(it) }
        mediaPlayer2?.audioSessionId?.let { binding.visualizer2.setPlayer(it) }
        uiUpdateHandler = Handler(this)
        viewModel.isResetEnable1.observe(this, Observer {
            isReset = it
            binding.reset1.isEnabled = it
            if (it) {
                binding.reset1.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this,
                            colorSchemeLeft
                        )
                    )
                )
                binding.reset1.setBackgroundResource(R.drawable.bg_gradient_eq_left)
            } else {
                binding.reset1.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                )
                binding.reset1.setBackgroundResource(R.drawable.bg_gradient_eq)

            }

        })
        viewModel.isResetEnable2.observe(this, Observer {
            isReset2 = it
            binding.reset2.isEnabled = it
            if (it) {
                binding.reset2.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this,
                            colorSchemeRight
                        )
                    )
                )
                binding.reset2.setBackgroundResource(R.drawable.bg_gradient_eq_right)
            } else {
                binding.reset2.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                )
                binding.reset2.setBackgroundResource(R.drawable.bg_gradient_eq)

            }

        })
    }

    private fun initViews() {
        showDisco1()
        showDisco2()
        binding.disco1.mediaPlayer = mediaPlayer1
        Log.d("linhd", "disco1")
        binding.imgDisco1.setImageResource(R.drawable.disc)
//        binding.disco1.setBackgroundResource(R.drawable.disc)
        binding.disco2.mediaPlayer = mediaPlayer2
        Log.d("linhd", "disco1")
//        binding.disco2.setImageResource(R.drawable.disc_2)
        if (mediaPlayer1 == null) {
            binding.play1.isEnabled = false
            binding.play1.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        }
        if (mediaPlayer2 == null) {
            binding.play2.isEnabled = false
            binding.play2.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))

        }
//        App.instance.getJobManager()!!.addJobInBackground(
//            WaveFormJob(
//                "/storage/emulated/0/Download/Seven-Jung-Kook.mp3"
//            ), {
//
//            })
//        DisplayWaveForm("/storage/emulated/0/Download/Seven-Jung-Kook.mp3".hashCode(), 540)
    }

    private fun DisplayWaveForm(HashCode: Int, waveWidth: Int) {

        val ChuckWidth = binding.thumbWave.chunkSpacing + binding.thumbWave.chunkWidth
        val ChuckRatio = waveWidth / ChuckWidth
        val SamplingRate = (540 / ChuckRatio).toFloat()
        binding.thumbWave.scaledData = ByteArray(0)
        binding.thumbWave.progress = 0f
        val WaveFormFile = File(
            App.instance.cacheDir,
            HashCode.toString()
        )
        MyUtils.ReadWaveFormFile(WaveFormFile, binding.thumbWave, SamplingRate)
    }

    private fun DisplayWaveForm2(HashCode: Int, waveWidth: Int) {

        val ChuckWidth = binding.thumbWave2.chunkSpacing + binding.thumbWave2.chunkWidth
        val ChuckRatio = waveWidth / ChuckWidth
        val SamplingRate = (540 / ChuckRatio).toFloat()
        binding.thumbWave2.scaledData = ByteArray(0)
        binding.thumbWave2.progress = 0f
        val WaveFormFile = File(
            App.instance.cacheDir,
            HashCode.toString()
        )

        MyUtils.ReadWaveFormFile(WaveFormFile, binding.thumbWave2, SamplingRate)
    }

    private fun initEvents() {
        binding.eq1.setOnClickListener {
            if (functionChoosed != EQ) {
                functionChoosed = EQ
                binding.eq1.setBackgroundResource(R.drawable.bg_gradient_eq_left)
                binding.loop1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.bass1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample1.setBackgroundResource(R.drawable.bg_gradient_eq)
                val equalizerFragment =
                    EqualizerFragment.newInstance(colorSchemeLeft, mediaPlayer1?.audioSessionId)
                replaceFragment(equalizerFragment)
            } else {
                showDisco1()
            }

        }
        binding.bass1.setOnClickListener {
            if (functionChoosed != BASS) {
                functionChoosed = BASS
                binding.bass1.setBackgroundResource(R.drawable.bg_gradient_eq_left)
                binding.loop1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.eq1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample1.setBackgroundResource(R.drawable.bg_gradient_eq)
                val bassFragment =
                    BassFragment.newInstance(colorSchemeLeft, mediaPlayer1?.audioSessionId)
                replaceFragment(bassFragment)
            } else {
                showDisco1()
            }
        }
        binding.loop1.setOnClickListener {
            if (functionChoosed != LOOP) {
                functionChoosed = LOOP
                binding.loop1.setBackgroundResource(R.drawable.bg_gradient_eq_left)
                binding.eq1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.bass1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample1.setBackgroundResource(R.drawable.bg_gradient_eq)
                val loopFragment = LoopFragment.newInstance(colorSchemeLeft)
                replaceFragment(loopFragment)
            } else {
                showDisco1()
            }
        }
        binding.cues1.setOnClickListener {
            if (functionChoosed != CUES) {
                functionChoosed = CUES
                binding.cues1.setBackgroundResource(R.drawable.bg_gradient_eq_left)
                binding.loop1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.bass1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.eq1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample1.setBackgroundResource(R.drawable.bg_gradient_eq)
                val cuesFragment = CuesFragment.newInstance(colorSchemeLeft)
                replaceFragment(cuesFragment)
            } else {
                showDisco1()
            }
        }
        binding.sample1.setOnClickListener {
            if (functionChoosed != SAMPLE) {
                functionChoosed = SAMPLE
                binding.sample1.setBackgroundResource(R.drawable.bg_gradient_eq_left)
                binding.loop1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.bass1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.eq1.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues1.setBackgroundResource(R.drawable.bg_gradient_eq)
                val sampleFragment = SampleFragment.newInstance(colorSchemeLeft)
                replaceFragment(sampleFragment)
            } else {
                showDisco1()
            }
        }
        binding.eq2.setOnClickListener {
            if (functionChoosed2 != EQ) {
                functionChoosed2 = EQ
                binding.eq2.setBackgroundResource(R.drawable.bg_gradient_eq_right)
                binding.loop2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.bass2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample2.setBackgroundResource(R.drawable.bg_gradient_eq)
                val equalizerFragment =
                    EqualizerFragment.newInstance(colorSchemeRight, mediaPlayer2?.audioSessionId)
//                val bundle = Bundle()
//                bundle.putInt(Constant.AUDIO_SESSION_ID, mediaPlayer2?.audioSessionId)
//                equalizerFragment.arguments = bundle
                replaceFragment2(equalizerFragment)
            } else {
                showDisco2()
            }
        }

        binding.bass2.setOnClickListener {
            if (functionChoosed2 != BASS) {
                functionChoosed2 = BASS
                binding.bass2.setBackgroundResource(R.drawable.bg_gradient_eq_right)
                binding.loop2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.eq2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample2.setBackgroundResource(R.drawable.bg_gradient_eq)
                val bassFragment =
                    BassFragment.newInstance(colorSchemeRight, mediaPlayer2?.audioSessionId)
                replaceFragment2(bassFragment)
            } else {
                showDisco2()
            }
        }
        binding.loop2.setOnClickListener {
            Log.d("linhd", "looop2")
            if (functionChoosed2 != LOOP) {
                functionChoosed2 = LOOP
                binding.loop2.setBackgroundResource(R.drawable.bg_gradient_eq_right)
                binding.bass2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.eq2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample2.setBackgroundResource(R.drawable.bg_gradient_eq)
                val loopFragment = LoopFragment.newInstance(colorSchemeRight)
                replaceFragment2(loopFragment)
            } else {
                showDisco2()
            }
        }
        binding.cues2.setOnClickListener {
            if (functionChoosed2 != CUES) {
                functionChoosed2 = CUES
                binding.cues2.setBackgroundResource(R.drawable.bg_gradient_eq_right)
                binding.bass2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.eq2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.loop2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.sample2.setBackgroundResource(R.drawable.bg_gradient_eq)
                val cuesFragment = CuesFragment.newInstance(colorSchemeRight)
                replaceFragment2(cuesFragment)
            } else {
                showDisco2()
            }
        }
        binding.sample2.setOnClickListener {
            Log.d("linhd", "sample2")

            if (functionChoosed2 != SAMPLE) {
                functionChoosed2 = SAMPLE
                binding.sample2.setBackgroundResource(R.drawable.bg_gradient_eq_right)
                binding.bass2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.eq2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.loop2.setBackgroundResource(R.drawable.bg_gradient_eq)
                binding.cues2.setBackgroundResource(R.drawable.bg_gradient_eq)
                val sampleFragment = SampleFragment.newInstance(colorSchemeRight)
                replaceFragment2(sampleFragment)
            } else {
                showDisco2()
            }
        }
        binding.play1.setOnClickListener {
            if (mediaPlayer1?.isPlaying == true) {
                binding.play1.setImageResource(R.drawable.play)
                binding.disco1.setPlayPause(false)
                binding.disco1.isPlaying(false)
            } else {
//                mediaPlayer1?.start()
                binding.disco1.isPlaying(true)
                binding.visualizer1.setPlayer(mediaPlayer1!!.audioSessionId)
                binding.disco1.setPlayPause(true)
//                binding.disco1.startStop()
                binding.play1.setImageResource(R.drawable.pause_1)
                binding.play1.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeLeft))

            }
            updatePlayingView()

        }
        binding.play2.setOnClickListener {
            if (mediaPlayer2?.isPlaying == true) {
                binding.play2.setImageResource(R.drawable.play)
//                mediaPlayer2?.pause()
                binding.disco2.setPlayPause(false)
                binding.disco2.isPlaying(false)

            } else {
                binding.disco2.isPlaying(true)
//                mediaPlayer2?.start()
                binding.visualizer2.setPlayer(mediaPlayer2!!.audioSessionId)
//                binding.disco1.setPlayPause(false)
                binding.disco2.setPlayPause(true)
                binding.play2.setImageResource(R.drawable.pause_1)
//                binding.disco2.startStop()
                binding.play2.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeRight))

            }
            updatePlayingView2()

        }
        binding.listAudio1.setOnClickListener {
            requestPermision(Constant.LIST_FILE_1)
        }
        binding.listAudio2.setOnClickListener {
            requestPermision(Constant.LIST_FILE_2)
        }
        binding.btnRec.setOnClickListener {
            if(!isRecording) {
                if (PermissionManager.hasReadAudioPermission(this@MainActivity) && PermissionManager.hasRecordAudioPermission(
                        this@MainActivity
                    )
                ) {
                    MyUtils.deleteFile("/storage/emulated/0/Music/test2.mp3")
                    startRecording("/storage/emulated/0/Music/test2.mp3")
                } else {
                    val permissions =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_AUDIO
                        )
                        else arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    requestPermissionLauncher.launch(permissions)
                }
            }else{
                stopRecording()
            }
        }

        binding.sbVolume1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val vol: Float = (p0!!.progress).toFloat() / 10
                Log.d("linhd", vol.toString())
                mediaPlayer1?.setVolume(vol, vol)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
//                val vol : Float = (p0!!.progress/10).toFloat()
//                mediaPlayer1?.setVolume(vol,vol)
            }

        })
        binding.sbVolume2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val vol: Float = (p0!!.progress).toFloat() / 10
                Log.d("linhd", vol.toString())
                mediaPlayer2?.setVolume(vol, vol)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
//                val vol : Float = (p0!!.progress/10).toFloat()
//                mediaPlayer1?.setVolume(vol,vol)
            }

        })

        binding.boostVl1.setOnClickListener {
            if (isBoost1) {
                isBoost1 = false
                binding.boostVl1.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.white))
            } else {
                isBoost1 = true
                binding.boostVl1.imageTintList =
                    ColorStateList.valueOf(resources.getColor(colorSchemeLeft))
                boostLoudness(13)
                val audioPct = 1.5
                val gainmB = (kotlin.math.log10(audioPct) * 2000).roundToInt()
                val enhancer = mediaPlayer1?.audioSessionId?.let { it1 -> LoudnessEnhancer(it1) }
                if (enhancer != null) {
                    enhancer.setTargetGain(gainmB)
                }
            }
        }
        binding.boostVl2.setOnClickListener {
            if (isBoost2) {
                isBoost2 = false
                binding.boostVl2.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.white))
            } else {
                isBoost2 = true
                binding.boostVl2.imageTintList =
                    ColorStateList.valueOf(resources.getColor(colorSchemeRight))
                boostLoudness(13)
                val audioPct = 1.5
                val gainmB = (kotlin.math.log10(audioPct) * 2000).roundToInt()
                val enhancer = mediaPlayer2?.audioSessionId?.let { it1 -> LoudnessEnhancer(it1) }
                if (enhancer != null) {
                    enhancer.setTargetGain(gainmB)
                }
            }
        }
        binding.btnMenu.setOnClickListener {
            MyUtils.playBottomSheetMenu(this@MainActivity)
        }
        binding.reset1.setOnClickListener {
            viewModel.setResetEnable(false)
            Settings.reset1()
            viewModel.resetAll1()
        }
        binding.reset2.setOnClickListener {
            viewModel.setReset2Enable(false)
            Settings.reset2()
            viewModel.resetAll2()
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val b = (progress / seekBar!!.max).toFloat()+0.5F
                mediaPlayer1?.setVolume(b, b)
                mediaPlayer2?.setVolume(1.5F - b, 1.5F - b)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.playback1.setOnClickListener {
        if(binding.seekbar.progress>=1)
            binding.seekbar.progress--
        }
        binding.playback2.setOnClickListener {
            if(binding.seekbar.progress<binding.seekbar.max)
                binding.seekbar.progress++
        }
        binding.pitch1.setOnClickListener {
            binding.cstPitch1.visibility=View.VISIBLE
            binding.cstWave1.visibility=View.GONE
            binding.pitch1.visibility=View.GONE

        }
        binding.okPitch1.setOnClickListener {
            binding.cstPitch1.visibility=View.GONE
            binding.cstWave1.visibility=View.VISIBLE
            binding.pitch1.visibility=View.VISIBLE

        }
        binding.pitch2.setOnClickListener {
            binding.cstPitch2.visibility=View.VISIBLE
            binding.cstWave2.visibility=View.GONE
            binding.pitch2.visibility=View.GONE

        }
        binding.okPitch2.setOnClickListener {
            binding.cstPitch2.visibility=View.GONE
            binding.cstWave2.visibility=View.VISIBLE
            binding.pitch2.visibility=View.VISIBLE

        }
        binding.sbPitch1.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mediaPlayer1?.playbackParams?.speed = ((progress/seekBar!!.max).toFloat()*12).toFloat()
                Log.d("linhd","speed1"+((progress/seekBar.max).toFloat()*12).toFloat())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.resetPitch1.setOnClickListener {
            binding.sbPitch1.progress= 0
        }
        binding.resetPitch2.setOnClickListener {
            binding.sbPitch2.progress=0
        }
        binding.sbPitch2.setColor(colorSchemeRight)
        binding.sbPitch2.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mediaPlayer2?.playbackParams?.speed = (((progress/seekBar!!.max).toFloat())*12).toFloat()
                Log.d("linhd","speed2"+((progress/seekBar.max).toFloat()*12).toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    private fun updatePlayingView() {

//        val currentduration = (mediaPlayer1!!.duration - mediaPlayer1!!.currentPosition).toLong()
        if (mediaPlayer1!!.currentPosition != 0) {
            Log.d("linhd", "${mediaPlayer1!!.currentPosition} :" + mediaPlayer1!!.duration)
            val progress = mediaPlayer1!!.currentPosition * 100 / duration1
            binding.thumbWave.progress = progress.toFloat()
        }
        if (mediaPlayer1!!.isPlaying) {
            uiUpdateHandler!!.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 50)
            binding.play1.setImageResource(R.drawable.pause_1)
        } else {
            uiUpdateHandler!!.removeMessages(MSG_UPDATE_SEEK_BAR)
            binding.play1.setImageResource(R.drawable.play)
        }
    }

    private fun updatePlayingView2() {
//        val currentduration = (mediaPlayer2!!.duration - mediaPlayer2!!.currentPosition).toLong()
        if (mediaPlayer2?.currentPosition != 0) {
            val progress = mediaPlayer2!!.currentPosition * 100 / duration2
            binding.thumbWave2.progress = progress.toFloat()
        }
        if (mediaPlayer2!!.isPlaying) {
            uiUpdateHandler!!.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR2, 50)
            binding.play2.setImageResource(R.drawable.pause_1)
        } else {
            uiUpdateHandler!!.removeMessages(MSG_UPDATE_SEEK_BAR2)
            binding.play2.setImageResource(R.drawable.play)
        }
    }

    private fun showDisco2() {
        binding.disco2.visibility = View.VISIBLE
        binding.disco2.setListener(object : IDiscoHandleBack {
            override fun onDiscoHandleBack() {
                requestPermision(Constant.LIST_FILE_2)
            }

        })
        binding.disco2.setListenerRotateDisco(object : IDiscoRotate {
            override fun onDiscoRotate() {
                updatePlayingView2()
            }
        })
        binding.fr2.visibility = View.GONE
        functionChoosed2 = DISCO
        binding.sample2.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.bass2.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.eq2.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.loop2.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.cues2.setBackgroundResource(R.drawable.bg_gradient_eq)
//        val discoFragment = DiscoFragment.newInstance(colorSchemeRight)
//        replaceFragment2(discoFragment)
    }

    private fun showDisco1() {
        binding.disco1.visibility = View.VISIBLE
        Log.d("linhd", "onShow")
        binding.disco1.setListener(object : IDiscoHandleBack {
            override fun onDiscoHandleBack() {
                requestPermision(Constant.LIST_FILE_1)
            }

        })
        binding.disco1.setListenerRotateDisco(object : IDiscoRotate {
            override fun onDiscoRotate() {
                updatePlayingView()
            }
        })

        binding.fr1.visibility = View.GONE
        functionChoosed = DISCO
        binding.sample1.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.bass1.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.eq1.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.loop1.setBackgroundResource(R.drawable.bg_gradient_eq)
        binding.cues1.setBackgroundResource(R.drawable.bg_gradient_eq)
//        val discoFragment = DiscoFragment.newInstance(colorSchemeLeft)
//        replaceFragment(discoFragment)
    }

    override fun handleMessage(message: Message): Boolean {
        when (message.what) {
            MSG_UPDATE_SEEK_BAR -> {
                if (mediaPlayer1?.currentPosition != 0) {
                    val progress = mediaPlayer1!!.currentPosition * 100 / mediaPlayer1!!.duration
                    binding.thumbWave.progress = progress.toFloat()
                }
                uiUpdateHandler!!.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 50)
                return true
            }

            MSG_UPDATE_SEEK_BAR2 -> {
                if (mediaPlayer2?.currentPosition != 0) {
                    val progress = mediaPlayer2!!.currentPosition * 100 / mediaPlayer2!!.duration
                    binding.thumbWave2.progress = progress.toFloat()
                }
                uiUpdateHandler!!.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR2, 50)
                return true
            }
        }
        return false
    }

    private fun requestPermision(idList: Int) {
        if (PermissionManager.hasReadAudioPermission(this@MainActivity)) {
            val intent = Intent(this@MainActivity, AddAudioToDiscoActivity::class.java)
            intent.putExtra("idDisc",idList)
            startActivityForResult(intent, idList)
        } else {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO
            )
            else arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissionLauncher.launch(permissions)
        }

    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fr1, fragment)
        fragmentTransaction.commit()
        Log.d("linhd", "${binding.disco1.visibility}")
        binding.fr1.visibility = View.VISIBLE
        binding.disco1.visibility = View.GONE
        binding.disco1.clearAnimated()
    }

    private fun replaceFragment2(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fr2, fragment)
        fragmentTransaction.commit()
        binding.fr2.visibility = View.VISIBLE
        binding.disco2.visibility = View.GONE
        binding.disco2.clearAnimated()

    }

    private fun startRecording(fileName: String) {
        isRecording=true
        binding.imgRec.setImageResource(R.drawable.pause_1)
        var duration = 0
        handlerRecord!!.postDelayed(object : Runnable {
            override fun run() {
                val minutes =
                    TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                            TimeUnit.MINUTES.toSeconds(minutes)
                binding.tvtRec.text = String.format("%02d:%02d", minutes, seconds)
                duration+=1000
                handlerRecord!!.postDelayed(this, 1000)
            }
        }, 0)
        // initialize and configure MediaRecorder

        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFile(fileName)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {
            // handle error
            Log.e("linhd", "recorder:$e")
        } catch (e: IllegalStateException) {
            // handle error
            Log.e("linhd", "recorder2:$e")
        }
        recorder!!.start()
    }

    private fun stopRecording() {
        isRecording=false
        binding.imgRec.setImageResource(R.drawable.ellipse_29)
        binding.tvtRec.text= getString(R.string.rec)
        handlerRecord!!.removeCallbacksAndMessages(null)
        try {
            recorder!!.stop()
            recorder!!.release()
            recorder = null
        } catch (e: IllegalStateException) {
            e.printStackTrace();
        }
        Toast.makeText(this@MainActivity, getString(R.string.saved),Toast.LENGTH_SHORT).show()
        // stop recording and free up resources

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        if (granted) {
            initComponents()
            initViews()
            initEvents()
        } else MyUtils.showPermissionRequestDialog(
            this, getString(R.string.setting_permission_record_audio)
        )
    }

    @SuppressLint("NewApi")
    fun boostLoudness(boost: Int) {
        val level = boost.toFloat() / 100.0f * 8000.0f
        var loudness: LoudnessEnhancer? = null
        loudness = getLoudnessEnhancer()
        if (loudness == null) {
            loudness = getLoudnessEnhancer()
        }
        if (loudness != null) {
            try {
                loudness.setTargetGain(level.toInt())
                loudness.enabled = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("NewApi")
    fun getLoudnessEnhancer(): LoudnessEnhancer? {
        return try {
            LoudnessEnhancer(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === Constant.LIST_FILE_1 && resultCode === RESULT_OK) {
            audio = data!!.getSerializableExtra(Constant.AUDIO_SESSION_ID) as AudiObject?
            setPlayerFragment1(audio!!)
            if(audio!!.thumbnail){
                Glide.with(this).load(MyUtils.getAlbumImage(audio!!.filePath))
                    .into(binding.imgDisco1)
            }
            binding.play1.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeLeft))

            mediaPlayer1?.setOnPreparedListener {
                // Update the TextView every second
                duration1 = it.duration
                handler!!.postDelayed(object : Runnable {
                    override fun run() {
                        val minutes =
                            TimeUnit.MILLISECONDS.toMinutes(mediaPlayer1!!.currentPosition.toLong())
                        val seconds =
                            TimeUnit.MILLISECONDS.toSeconds(mediaPlayer1!!.currentPosition.toLong()) -
                                    TimeUnit.MINUTES.toSeconds(minutes)
                        binding.timeMusic.text = String.format("%02d:%02d", minutes, seconds)
                        handler!!.postDelayed(this, 1000)
                    }
                }, 0)
            }
            // do something with B's return values
        } else if (requestCode === Constant.LIST_FILE_2 && resultCode === RESULT_OK) {
            audio = data!!.getSerializableExtra(Constant.AUDIO_SESSION_ID) as AudiObject?
            setPlayerFragment2(audio!!)
            Log.d("linhd", "frag2")
            if(audio!!.thumbnail){
                Glide.with(this).load(MyUtils.getAlbumImage(audio!!.filePath))
                    .into(binding.imgDisco2)
                }
            binding.play2.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeRight))
            mediaPlayer2?.setOnPreparedListener {
                // Update the TextView every second
                duration2 = it.duration
                handler!!.postDelayed(object : Runnable {
                    override fun run() {
                        val minutes =
                            TimeUnit.MILLISECONDS.toMinutes(mediaPlayer2!!.currentPosition.toLong())
                        val seconds =
                            TimeUnit.MILLISECONDS.toSeconds(mediaPlayer2!!.currentPosition.toLong()) -
                                    TimeUnit.MINUTES.toSeconds(minutes)
                        binding.timeMusic2.text = String.format("%02d:%02d", minutes, seconds)
                        handler!!.postDelayed(this, 1000)
                    }
                }, 0)
            }
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setPlayerFragment1(audio: AudiObject) {
        mediaPlayer1?.stop()
        mediaPlayer1?.reset()
        player.setFile(File(audio.filePath))
        listenOnPlayerStates()
        initUI()
        mediaPlayer1 = MediaPlayer.create(
            this@MainActivity, Uri.parse(audio.filePath)
        )
        binding.visualizer1.setDensity(1F)
        binding.visualizer1.setPlayer(mediaPlayer1!!.audioSessionId)
        viewModel.setMediaPlayer1(mediaPlayer1!!)
        binding.disco1.mediaPlayer = mediaPlayer1
        binding.play1.isEnabled = true
        binding.play1.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeLeft))
        binding.arrowSet1.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeLeft))
        binding.dotSet1.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
        binding.lnSet1.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.disco1.setPlayPause(true)
                    updatePlayingView()
                }

                MotionEvent.ACTION_UP -> {
                    binding.disco1.setPlayPause(false)
                    updatePlayingView()

                }
            }
            true
        }
        App.instance.getJobManager()!!.addJobInBackground(
            WaveFormJob(
                audio.filePath
            )
        ) {
            DisplayWaveForm(audio.filePath.hashCode(), 540)
        }
        binding.nameMusic.text = audio.nameSong
        pathMusic = audio.filePath
    }

    private fun setPlayerFragment2(audio: AudiObject) {
        mediaPlayer2?.stop()
        mediaPlayer2?.reset()
        mediaPlayer2 = MediaPlayer.create(
            this@MainActivity, Uri.parse(audio.filePath)
        )
        player.setFile(File(audio.filePath))
        listenOnPlayerStates()
        initUI()
        binding.visualizer2.setDensity(1F)
        binding.visualizer2.setPlayer(mediaPlayer2!!.audioSessionId)

        viewModel.setMediaPlayer2(mediaPlayer2!!)
        binding.disco2.mediaPlayer = mediaPlayer2
        binding.arrowSet2.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeRight))
        binding.dotSet2.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
        binding.lnSet2.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.disco2.setPlayPause(true)
                    updatePlayingView2()
                }

                MotionEvent.ACTION_UP -> {
                    binding.disco2.setPlayPause(false)
                    updatePlayingView2()

                }
            }
            true
        }
        binding.play2.isEnabled = true
        binding.play2.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, colorSchemeRight))

        App.instance.getJobManager()!!.addJobInBackground(
            WaveFormJob(
                audio.filePath
            )
        ) {
            DisplayWaveForm2(audio.filePath.hashCode(), 540)
        }
        binding.nameMusic2.text = audio.nameSong
        pathMusic2 = audio.filePath
    }
    private fun initUI() = with(binding) {
        binding.visualizerPlay.apply {
            ampNormalizer = { sqrt(it.toFloat()).toInt() }
            onStartSeeking = {
                player.pause()
            }
            onSeeking = {
//                binding.timelineTextView.text = it.formatAsTime()
            }
            onFinishedSeeking = { time, isPlayingBefore ->
                player.seekTo(time)
                if (isPlayingBefore) {
                    player.resume()
                }
            }
            onAnimateToPositionFinished = { time, isPlaying ->
                updateTime(time, isPlaying)
                player.seekTo(time)
            }
        }
        binding.visualizerPlay2.apply {
            ampNormalizer = { sqrt(it.toFloat()).toInt() }
            onStartSeeking = {
                player.pause()
            }
            onSeeking = {
//                binding.timelineTextView.text = it.formatAsTime()
            }
            onFinishedSeeking = { time, isPlayingBefore ->
                player.seekTo(time)
                if (isPlayingBefore) {
                    player.resume()
                }
            }
            onAnimateToPositionFinished = { time, isPlaying ->
                updateTime(time, isPlaying)
                player.seekTo(time)
            }
            setHalfWave()
        }
        binding.set1.setOnClickListener {
            Log.d(Constant.TAG,"click set")

            player.togglePlay() }
//        seekForwardButton.setOnClickListener { visualizer.seekOver(SEEK_OVER_AMOUNT) }
//        seekBackwardButton.setOnClickListener { visualizer.seekOver(-SEEK_OVER_AMOUNT) }

        lifecycleScope.launchWhenCreated {
            val amps = player.loadAmps()
            visualizerPlay.setWaveForm(amps, player.tickDuration)
            visualizerPlay2.setWaveForm(amps, player.tickDuration)
        }
    }

    private fun listenOnPlayerStates() = with(binding) {
        player = AudioPlayer.getInstance(applicationContext).init().apply {
//            onStart = { playButton.icon = getDrawableCompat(R.drawable.ic_pause_24) }
//            onStop = { playButton.icon = getDrawableCompat(R.drawable.ic_play_arrow_24) }
//            onPause = { playButton.icon = getDrawableCompat(R.drawable.ic_play_arrow_24) }
//            onResume = { playButton.icon = getDrawableCompat(R.drawable.ic_pause_24) }
            onProgress = { time, isPlaying -> updateTime(time, isPlaying) }
        }
    }

    private fun updateTime(time: Long, isPlaying: Boolean) = with(binding) {
//        timelineTextView.text = time.formatAsTime()
        visualizerPlay.updateTime(time, isPlaying)
        visualizerPlay2.updateTime(time, isPlaying)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer2?.release()
        mediaPlayer1?.release()
        stopRecording()
    }

    companion object {
        private val MSG_UPDATE_SEEK_BAR = 1845
        private val MSG_UPDATE_SEEK_BAR2 = 2222
        const val SEEK_OVER_AMOUNT = 5000


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: WaveFormCompletionEvent) {
        if (pathMusic.equals(event.message)) {
            DisplayWaveForm(pathMusic.hashCode(), 540)
        } else if (pathMusic2.equals(event.message))
            DisplayWaveForm2(pathMusic2.hashCode(), 540)
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        listenOnPlayerStates()
        initUI()
    }


    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
        player.release()
    }
}

class WaveFormCompletionEvent(val message: String)
