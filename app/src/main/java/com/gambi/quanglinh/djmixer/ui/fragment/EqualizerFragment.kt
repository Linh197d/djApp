package com.gambi.quanglinh.djmixer.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gambi.quanglinh.djmixer.MainActivity
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.custom.VerticalSeekbar
import com.gambi.quanglinh.djmixer.databinding.FragmentEqualizerBinding
import com.gambi.quanglinh.djmixer.utils.Constant
import com.gambi.quanglinh.djmixer.utils.Settings

class EqualizerFragment : Fragment() {
    private var _binding: FragmentEqualizerBinding? = null
    private val binding get() = _binding!!
    private var parent: MainActivity? = null
    private var audioSesionId: Int = 0
    var mEqualizer: Equalizer? = null
    var seekBarFinal: Array<VerticalSeekbar?> = arrayOfNulls<VerticalSeekbar>(5)
    var maxValue = IntArray(5)
    private var colorScheme: Int? = null
    lateinit var viewModel: ViewModelFragment1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parent = requireActivity() as MainActivity
        viewModel = parent!!.viewModel

        if (arguments != null) {
            colorScheme = requireArguments().getInt("colorScheme")
            audioSesionId = requireArguments().getInt(Constant.AUDIO_SESSION_ID)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEqualizerBinding.inflate(inflater, container, false)
        initComponents()
        initViews()
        initEvents()

        return binding.root
    }

    private fun initComponents() {
        //volume
        try {
            mEqualizer = Equalizer(1, audioSesionId)
            //        Visualizer visualizer = new Visualizer(0);

            if (Settings.presetPos == 0) {
                for (bandIdx in 0 until mEqualizer!!.numberOfBands) {
                    if (colorScheme == R.color.primary) {
                        mEqualizer!!.setBandLevel(
                            bandIdx.toShort(),
                            Settings.seekbarpos1[bandIdx] as Short
                        )

//                        viewModel.dataEqualizer1.observe(viewLifecycleOwner, Observer {
//                            if (!parent!!.isReset)
//                            mEqualizer!!.setBandLevel(
//                                bandIdx.toShort(),
//                                it[bandIdx] as Short
//                            )
//                        })
                    } else if (colorScheme == R.color.primary_main) {
                        mEqualizer!!.setBandLevel(
                            bandIdx.toShort(),
                            Settings.seekbarpos2[bandIdx] as Short
                        )
//                        viewModel.dataEqualizer2.observe(viewLifecycleOwner, Observer {
//                            if (!parent!!.isReset2)
//                            mEqualizer!!.setBandLevel(
//                                bandIdx.toShort(),
//                                it[bandIdx] as Short
//                            )
//                        })
                    }

                }
            }
//            else {
//                mEqualizer!!.usePreset((Settings.presetPos - 1) as Short)
//            }
        } catch (e: Exception) {
            Log.e("linhd", "equalizer: $e")
        }
        if (mEqualizer != null)
            mEqualizer!!.enabled = true
    }

    private fun initViews() {

    }

    private fun initEvents() {
        if (mEqualizer != null)
            seekbarEvent()


    }

    private fun seekbarEvent() {

//        TextView equalizerHeading = new TextView(getContext());
//        equalizerHeading.setText(R.string.eq);
//        equalizerHeading.setTextSize(20);
//        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        val numberOfFrequencyBands = 5

        val points = FloatArray(numberOfFrequencyBands)

        val lowerEqualizerBandLevel = mEqualizer!!.bandLevelRange[0]
        val upperEqualizerBandLevel = mEqualizer!!.bandLevelRange[1]

        for (i in 0 until numberOfFrequencyBands) {
            val equalizerBandIndex = i.toShort()
            val frequencyHeaderTextView = TextView(context)
            frequencyHeaderTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            frequencyHeaderTextView.gravity = Gravity.CENTER_HORIZONTAL
            frequencyHeaderTextView.setTextColor(Color.parseColor("#FFFFFF"))
            frequencyHeaderTextView.text =
                (mEqualizer!!.getCenterFreq(equalizerBandIndex) / 1000).toString() + "Hz" //1000
            val seekBarRowLayout = LinearLayout(context)
            seekBarRowLayout.orientation = LinearLayout.VERTICAL
            val lowerEqualizerBandLevelTextView = TextView(context)
            lowerEqualizerBandLevelTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"))
            lowerEqualizerBandLevelTextView.text = (lowerEqualizerBandLevel / 100).toString() + "dB"
            val upperEqualizerBandLevelTextView = TextView(context)
            lowerEqualizerBandLevelTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            upperEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"))
            upperEqualizerBandLevelTextView.text = (upperEqualizerBandLevel / 100).toString() + "dB"
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1f
            var seekBar = VerticalSeekbar(requireActivity())
            var textView = TextView(context)
            var textProgress = TextView(context)
            var cstLayout = ConstraintLayout(requireContext())
            when (i) {
                0 -> {
                    seekBar = binding.seekBar1
                    textView = binding.textView1
                    textProgress = binding.progressSb1
                    cstLayout = binding.rlt1
                }

                1 -> {
                    seekBar = binding.seekBar2
                    textView = binding.textView2
                    textProgress = binding.progressSb2
                    cstLayout = binding.rlt2
                }

                2 -> {
                    seekBar = binding.seekBar3
                    textView = binding.textView3
                    textProgress = binding.progressSb3
                    cstLayout = binding.rlt3
                }

                3 -> {
                    seekBar = binding.seekBar4
                    textView = binding.textView4
                    textProgress = binding.progressSb4
                    cstLayout = binding.rlt4
                }

                4 -> {
                    seekBar = binding.seekBar5
                    textView = binding.textView5
                    textProgress = binding.progressSb5
                    cstLayout = binding.rlt5
                }
            }
            if (colorScheme == R.color.primary) {
                if (parent!!.functionChoosed == parent!!.EQ)
                    viewModel.dataEqualizer1.observe(viewLifecycleOwner, Observer {
                        if (!parent!!.isReset){
                            Log.d(Constant.TAG,"eqa1:${it[1]}")
                            seekBar.progress = it[i]
                            Settings.seekbarpos1=it
                            for (bandIdx in 0 until mEqualizer!!.numberOfBands) {
                                mEqualizer!!.setBandLevel(
                                    bandIdx.toShort(),
                                    it[bandIdx].toShort()
                                )
                            }
                        }

                    })
            } else if (colorScheme == R.color.primary_main) {
                if (parent!!.functionChoosed2 == parent!!.EQ)
                    viewModel.dataEqualizer2.observe(viewLifecycleOwner, Observer {
                        if (!parent!!.isReset2){
                            Log.d(Constant.TAG,"eqa2:${it[1]}")
                            Settings.seekbarpos2 = it
                            seekBar.progress = it[i]
                            for (bandIdx in 0 until mEqualizer!!.numberOfBands) {
                                mEqualizer!!.setBandLevel(
                                    bandIdx.toShort(),
                                    it[bandIdx].toShort()
                                )
                            }
                        }
                    })
            }

            textView.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
            textProgress.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))

            if (colorScheme == R.color.primary) {
                cstLayout.setBackgroundResource(R.drawable.bg_equalizer_seekbar_primarycolor)
                seekBar.progressTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))

            } else if (colorScheme == R.color.primary_main) {
                seekBar.progressTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
                cstLayout.setBackgroundResource(R.drawable.bg_equalizer_seekbar_primarycolor2)

            }

            seekBarFinal[i] = seekBar
            //            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN));
//            seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(themeColor, PorterDuff.Mode.SRC_IN));
            seekBar.setId(i)
            //            seekBar.setLayoutParams(layoutParams)
//            seekBar.max = upperEqualizerBandLevel - lowerEqualizerBandLevel
            seekBar.max = upperEqualizerBandLevel.toInt()- lowerEqualizerBandLevel
            Log.d(Constant.TAG,"max:${seekBar.max}")

            maxValue[i] = upperEqualizerBandLevel - lowerEqualizerBandLevel
            textView.text = frequencyHeaderTextView.text
            //textView.setTextColor(Color.WHITE);
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            if (Settings.isEqualizerReloaded) {
                if (colorScheme == R.color.primary) {
                    points[i] = (Settings.seekbarpos1[i] - lowerEqualizerBandLevel).toFloat()
//                    seekBar.progress = Settings.seekbarpos1[i] - lowerEqualizerBandLevel
                    seekBar.progress = Settings.seekbarpos1[i]
                    Log.d(Constant.TAG,"sb1:${seekBar.progress}")
                } else if (colorScheme == R.color.primary_main) {
                    points[i] = (Settings.seekbarpos2[i] - lowerEqualizerBandLevel).toFloat()
//                    seekBar.progress = Settings.seekbarpos2[i] - lowerEqualizerBandLevel
                    seekBar.progress = Settings.seekbarpos2[i]
                }
            } else {
                points[i] =
                    (mEqualizer!!.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel).toFloat()
//                seekBar.progress =
//                    mEqualizer!!.getBandLevel(equalizerBandIndex)
                seekBar.progress =
                    mEqualizer!!.getBandLevel(equalizerBandIndex).toInt()
                Log.d(Constant.TAG,"${seekBar.progress}")

                if (colorScheme == R.color.primary) {
                    Settings.seekbarpos1[i] = mEqualizer!!.getBandLevel(equalizerBandIndex).toInt()

                } else if (colorScheme == R.color.primary_main) {
                    Settings.seekbarpos2[i] = mEqualizer!!.getBandLevel(equalizerBandIndex).toInt()

                }
                Settings.isEqualizerReloaded = true
            }
            textProgress.text = ((seekBar.progress) * 10 / (seekBar.max)-5).toString()

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (!mEqualizer!!.hasControl()) {
//                        mEqualizer.release();
                        mEqualizer = Equalizer(1, 0)
//                        Toast.makeText(ctx, "Please turn off other equalizer app", Toast.LENGTH_SHORT).show();
                    } else {
                        textProgress.text = ((seekBar!!.progress) * 10 / (seekBar.max)-5).toString()
//                        mEqualizer!!.setBandLevel(
//                            equalizerBandIndex,
//                            (progress + lowerEqualizerBandLevel).toShort()
//                        )
                        mEqualizer!!.setBandLevel(
                            equalizerBandIndex,
                            (progress).toShort()
                        )
                        points[seekBar.id] =
                            (mEqualizer!!.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel).toFloat()
                        if (colorScheme == R.color.primary) {
//                            Settings.seekbarpos1[seekBar.id] = progress + lowerEqualizerBandLevel
                            Settings.seekbarpos1[seekBar.id] = progress
                            if (progress*10/seekBar.max > 0)
                                viewModel.setResetEnable(true)
                        } else if (colorScheme == R.color.primary_main) {
                            Settings.seekbarpos2[seekBar.id] = progress + lowerEqualizerBandLevel
                            if (progress*10/seekBar.max > 0)
                                viewModel.setReset2Enable(true)
                        }
//                        Settings.equalizerModel.getSeekbarpos().get(seekBar.id) =
//                            progress + lowerEqualizerBandLevel
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Settings.presetPos = 0
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mEqualizer?.release()
    }

    companion object {
        fun newInstance(colorScheme: Int, audioSsId: Int?) = EqualizerFragment().apply {
            arguments = Bundle().apply {
                putInt("colorScheme", colorScheme)
                if (audioSsId != null) {
                    putInt(Constant.AUDIO_SESSION_ID, audioSsId)
                }
            }
        }
    }
}