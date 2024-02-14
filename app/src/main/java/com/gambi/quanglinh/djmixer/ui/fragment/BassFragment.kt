package com.gambi.quanglinh.djmixer.ui.fragment

import android.media.audiofx.BassBoost
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gambi.quanglinh.djmixer.MainActivity
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.databinding.FragmentBassBinding
import com.gambi.quanglinh.djmixer.utils.Constant
import com.gambi.quanglinh.djmixer.utils.Settings

class BassFragment : Fragment() {
    private var _binding: FragmentBassBinding? = null
    private val binding get() = _binding!!
    private var parent: MainActivity? = null
    private var audioSesionId: Int = 0
    var bassBoost: BassBoost? = null
    private var colorScheme: Int? = null
    var viewModel: ViewModelFragment1? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parent = requireActivity() as MainActivity
        viewModel = parent!!.viewModel
        Log.d("linhd", "create viewmodel")

        if (arguments != null) {
            audioSesionId = requireArguments().getInt(Constant.AUDIO_SESSION_ID)
            colorScheme = arguments?.getInt("colorScheme")
        }
    }

    private fun observeViewModel() {
        if (colorScheme == R.color.primary) {
            if (parent!!.functionChoosed == parent!!.BASS) {
                viewModel?.dataBass1!!.observe(viewLifecycleOwner, Observer {
                    if (!parent!!.isReset)
                        binding.controllerBass.progress = it?.toInt()!!
                    Log.d("linhd", "observer")
                })
            }
        } else if (colorScheme == R.color.primary_main) {
            if (parent!!.functionChoosed2 == parent!!.BASS) {
                viewModel?.dataBass2!!.observe(viewLifecycleOwner, Observer {
                    if (!parent!!.isReset2)
                        binding.controllerBass.progress = it?.toInt()!!
                })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBassBinding.inflate(inflater, container, false)
        observeViewModel()
        initComponents()
        initViews()
        initEvents()
        return binding.root
    }

    private fun initComponents() {
        //volume
        try {
            bassBoost = BassBoost(1, audioSesionId)
            bassBoost!!.enabled = Settings.isEqualizerEnabled
            val bassBoostSettingTemp = bassBoost!!.properties
            val bassBoostSetting = BassBoost.Settings(bassBoostSettingTemp.toString())
            bassBoostSetting.strength = (1000 / 19).toShort()
            bassBoost!!.properties = bassBoostSetting
        } catch (e: Exception) {
//            Toast.makeText(
//                requireContext(),
//                "Please turn off other equalizer app",
//                Toast.LENGTH_LONG
//            ).show()
        }

    }

    private fun initViews() {
        if (colorScheme == R.color.primary_main)
            binding.controllerBass.setColorListForProgress()
    }

    private fun initEvents() {
        if (!Settings.isEqualizerReloaded) {
            var x = 0
            if (bassBoost != null) {
                try {
                    x = bassBoost!!.roundedStrength * 19 / 1000
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            if (x == 0) {
                binding.controllerBass.setProgress(1)
            } else {
                binding.controllerBass.setProgress(x)
            }

        } else {
            var x = 0
            if (colorScheme == R.color.primary)
                x = Settings.bassStrength1 * 19 / 1000
            else if (colorScheme == R.color.primary_main)
                x = Settings.bassStrength2 * 19 / 1000

            if (x == 0) {
                binding.controllerBass.setProgress(1)
            } else {
                binding.controllerBass.setProgress(x)
            }
        }

        binding.controllerBass.setOnProgressChangedListener { progress ->
            if (colorScheme == R.color.primary) {
                Settings.bassStrength1 = (1000f / 19 * progress).toInt().toShort()
                if (Settings.bassStrength1.toInt() >= 1) {
                    viewModel?.setResetEnable(true)
                }
//                viewModel?.saveBass1(Settings.bassStrength1)
            } else if (colorScheme == R.color.primary_main) {
                Settings.bassStrength2 = (1000f / 19 * progress).toInt().toShort()
//                viewModel?.saveBass2(Settings.bassStrength2)
                if (Settings.bassStrength2.toInt() >= 1) {
                    viewModel?.setReset2Enable(true)
                }
            }

            try {
                if (colorScheme == R.color.primary)
                    bassBoost!!.setStrength(Settings.bassStrength1)
                else if (colorScheme == R.color.primary_main)
                    bassBoost!!.setStrength(Settings.bassStrength2)

                //                    Settings.equalizerModel.setBassStrength(Settings.bassStrength)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            //                Log.d("volume", "" + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        }
    }

    companion object {
        fun newInstance(colorScheme: Int, audioSsId: Int?) = BassFragment().apply {
            arguments = Bundle().apply {
                putInt("colorScheme", colorScheme)
                if (audioSsId != null) {
                    putInt(Constant.AUDIO_SESSION_ID, audioSsId)
                }

            }
        }
    }

    override fun onPause() {
        super.onPause()

//        if (colorScheme == R.color.primary_main)
//            viewModel2.saveBass2(binding.controllerBass.progress)
//        else
//            viewModel2.saveBass1(binding.controllerBass.progress)
    }
}