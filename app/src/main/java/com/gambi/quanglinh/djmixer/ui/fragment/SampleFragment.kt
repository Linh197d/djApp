package com.gambi.quanglinh.djmixer.ui.fragment

import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gambi.quanglinh.djmixer.MainActivity
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.databinding.FragmentSamplesBinding
import com.gambi.quanglinh.djmixer.listener.ItopDJFxListener
import com.gambi.quanglinh.djmixer.utils.Settings

class SampleFragment : Fragment(), ItopDJFxListener {
    private var _binding: FragmentSamplesBinding? = null
    private val binding get() = _binding!!
    private var parent: MainActivity? = null
    private var colorScheme: Int? = null
    private var mediaPlayer: MediaPlayer? = null
    private var mListenerTopDJ: ItopDJFxListener? = null
    private lateinit var viewModel: ViewModelFragment1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colorScheme = arguments?.getInt("colorScheme")
        parent = requireActivity() as MainActivity
        viewModel = parent!!.viewModel
        mediaPlayer = parent!!.mediaPlayer3


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSamplesBinding.inflate(inflater, container, false)
        initComponents()
        initViews()
        initEvents()

        return binding.root
    }

    private fun initComponents() {
        if (colorScheme == R.color.primary) {
//            if(parent!!.functionChoosed==parent!!.SAMPLE)
//            viewModel.dataSbSample1.observe(viewLifecycleOwner, Observer {
//                binding.seekbar.progress = it
//            })
            if (Settings.sbPositionSample1 != -1) {
                binding.seekbar.progress = Settings.sbPositionSample1
            }
        } else if (colorScheme == R.color.primary_main) {
//            if(parent!!.functionChoosed2==parent!!.SAMPLE)
//            viewModel.dataSbSample1.observe(viewLifecycleOwner, Observer {
//                binding.seekbar.progress = it
//            })
            if (Settings.sbPositionSample2 != -1) {
                binding.seekbar.progress = Settings.sbPositionSample2
            }
        }

    }

    private fun initViews() {
        binding.seekbar.progressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
        if (colorScheme == R.color.primary)
            binding.volumeSample.setBackgroundResource(R.drawable.bg_equalizer_seekbar_primarycolor)
        else if (colorScheme == R.color.primary_main) {
            binding.volumeSample.setBackgroundResource(R.drawable.bg_equalizer_seekbar_primarycolor2)
        }
    }

    private fun initEvents() {
        binding.sampleHorn.setOnClickListener {
            mediaPlayer!!.start()
        }
        binding.sampleDownlifter.setOnClickListener {
            mediaPlayer!!.start()
        }
        binding.sampleSiren.setOnClickListener {
            mediaPlayer!!.start()
        }
        binding.sampleUplifter.setOnClickListener {
            mediaPlayer!!.start()
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val vol: Float = (progress / seekBar!!.max).toFloat()
                mediaPlayer!!.setVolume(vol, vol)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.cstTopdj.setOnClickListener {
            showListTopDJFx()
        }


    }

    private fun showListTopDJFx() {
//        binding.spinnerView.apply {
//            setSpinnerAdapter(IconSpinnerAdapter(requireActivity))
//            setItems(
//                arrayListOf(
//                    IconSpinnerItem(iconRes = R.drawable.tabler_check, text = resources.getStr),
//                    IconSpinnerItem(iconRes = R.drawable.tabler_check, text = "UK"),
//                    IconSpinnerItem(iconRes = R.drawable.tabler_check, text = "France"),
//                    IconSpinnerItem(icon = R.drawable.tabler_check, text = "Canada"),
//                    IconSpinnerItem(
//                        icon = R.drawable.tabler_check,
//                        text = "South Korea"
//                    )
//                )
//            )
//            setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, item ->
//                Toast.makeText(applicationContext, item.text, Toast.LENGTH_SHORT).show()
//            }
//            getSpinnerRecyclerView().layoutManager = GridLayoutManager(baseContext, 2)
//            selectItemByIndex(4)
//            preferenceName = "country"
//        }
    }

    companion object {
        fun newInstance(colorScheme: Int) = SampleFragment().apply {
            arguments = Bundle().apply {
                putInt("colorScheme", colorScheme)
            }
        }
    }

    override fun onTopDJChoosed(s: String) {

    }

    override fun onPause() {
        super.onPause()
        if (colorScheme == R.color.primary) {
            Settings.sbPositionSample1 = binding.seekbar.progress
        } else (colorScheme == R.color.primary_main)
        Settings.sbPositionSample2 = binding.seekbar.progress

    }
}