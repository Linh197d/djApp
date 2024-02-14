package com.gambi.quanglinh.djmixer.ui.fragment

import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gambi.quanglinh.djmixer.MainActivity
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.databinding.FragmentCuesBinding
import com.gambi.quanglinh.djmixer.utils.MyUtils
import com.gambi.quanglinh.djmixer.utils.Settings

class CuesFragment : Fragment() {
    private var _binding: FragmentCuesBinding? = null
    private val binding get() = _binding!!
    private var parent: MainActivity? = null
    private var colorScheme: Int? = null
    private var mediaPlayer: MediaPlayer? = null
    private var arrayDuration = arrayOf(0, 0, 0, 0, 0, 0, 0, 0)
    private var deleteCues: Boolean = false
    lateinit var viewModel: ViewModelFragment1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colorScheme = arguments?.getInt("colorScheme")
        parent = requireActivity() as MainActivity
        viewModel = parent!!.viewModel

        if (colorScheme == R.color.primary)
            viewModel.media1.observe(this, Observer {
                mediaPlayer = it
            })
        else if (colorScheme == R.color.primary_main)
            viewModel.media2.observe(this, Observer {
                mediaPlayer = it
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCuesBinding.inflate(inflater, container, false)
        initComponents()
        initViews()
        initEvents()

        return binding.root
    }

    private fun initComponents() {
        if (colorScheme == R.color.primary) {
            deleteCues = Settings.isDeleteOn1
            if (Settings.arrayCues1 != null) {
                for (i in 0..7) {
                    arrayDuration[i] = Settings.arrayCues1[i]
                }
            }
            if (arrayDuration[0] != 0) {
                showCues1()
                if (Settings.isDeleteOn1) {
                    binding.cstDelete1.visibility = View.VISIBLE
                }
            }
            if (arrayDuration[1] != 0) {
                showCues2()
                if (Settings.isDeleteOn1) {
                    binding.cstDelete2.visibility = View.VISIBLE
                }
            }
            if (arrayDuration[2] != 0) {
                showCues3()
                if (Settings.isDeleteOn1) {
                    binding.cstDelete3.visibility = View.VISIBLE
                }
            }
            if (arrayDuration[3] != 0) {
                showCues4()
                if (Settings.isDeleteOn1) {
                    binding.cstDelete4.visibility = View.VISIBLE
                }
            }
            if (Settings.isDeleteOn1) {
                binding.deleteCues.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
                binding.deleteCues.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )

            }
        } else if (colorScheme == R.color.primary_main) {
            deleteCues = Settings.isDeleteOn2

            if (Settings.arrayCues2 != null) {
                for (i in 0..7) {
                    arrayDuration[i] = Settings.arrayCues2[i]
                }
            }
            if (arrayDuration[0] != 0) {
                showCues1()
                if (Settings.isDeleteOn2) {
                    binding.cstDelete1.visibility = View.VISIBLE
                }
            }
            if (arrayDuration[1] != 0) {
                showCues2()
                if (Settings.isDeleteOn2) {
                    binding.cstDelete2.visibility = View.VISIBLE
                }
            }
            if (arrayDuration[2] != 0) {
                showCues3()
                if (Settings.isDeleteOn2) {
                    binding.cstDelete3.visibility = View.VISIBLE
                }
            }
            if (arrayDuration[3] != 0) {
                showCues4()
                if (Settings.isDeleteOn2) {
                    binding.cstDelete4.visibility = View.VISIBLE
                }
            }
            if (Settings.isDeleteOn2) {
                binding.deleteCues.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
                binding.deleteCues.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )

            }
        }
    }

    private fun initViews() {

    }

    private fun initEvents() {
        initCues1()
        initCues2()
        initCues3()
        initCues4()
        initDelete()


    }

    private fun initDelete() {
        binding.deleteCues.setOnClickListener {
            if (!deleteCues) {
                deleteCues = true
                binding.deleteCues.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
                binding.deleteCues.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.cues1.isEnabled = false
                binding.cues2.isEnabled = false
                binding.cues3.isEnabled = false
                binding.cues4.isEnabled = false

                if (binding.cstSelected1.visibility == View.VISIBLE) {
                    binding.cstDelete1.visibility = View.VISIBLE
                }

                if (binding.cstSelected2.visibility == View.VISIBLE) {
                    binding.cstDelete2.visibility = View.VISIBLE

                }
                if (binding.cstSelected3.visibility == View.VISIBLE) {
                    binding.cstDelete3.visibility = View.VISIBLE

                }
                if (binding.cstSelected4.visibility == View.VISIBLE) {
                    binding.cstDelete4.visibility = View.VISIBLE

                }
            } else {
                deleteCues = false
//                binding.deleteCues.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background2))
                binding.deleteCues.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.background2
                        )
                    )
                binding.deleteCues.setBackgroundResource(R.drawable.bg_gradient_playpause)
                binding.deleteCues.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.cues1.isEnabled = true
                binding.cues2.isEnabled = true
                binding.cues3.isEnabled = true
                binding.cues4.isEnabled = true
                if (binding.cstSelected1.visibility == View.VISIBLE) {
                    binding.cstDelete1.visibility = View.GONE
                }
                if (binding.cstSelected2.visibility == View.VISIBLE) {
                    binding.cstDelete2.visibility = View.GONE

                }
                if (binding.cstSelected3.visibility == View.VISIBLE) {
                    binding.cstDelete3.visibility = View.GONE

                }
                if (binding.cstSelected4.visibility == View.VISIBLE) {
                    binding.cstDelete4.visibility = View.GONE

                }
            }
        }
    }

    private fun initCues1() {

        binding.cues1.setOnClickListener {
            if (arrayDuration[0] == 0) {
                arrayDuration[0] = mediaPlayer!!.currentPosition
                showCues1()
            } else {
                mediaPlayer!!.seekTo(arrayDuration[0])
            }

        }
        binding.cancel1.setOnClickListener {
            binding.cstSelected1.visibility = View.GONE
            binding.cstDelete1.visibility = View.GONE
            arrayDuration[0] = 0
        }
    }


    private fun initCues2() {
        binding.cues2.setOnClickListener {
            if (arrayDuration[1] == null || arrayDuration[1] == 0) {
                arrayDuration[1] = mediaPlayer!!.currentPosition
                binding.cstSelected2.visibility = View.VISIBLE
                binding.duration2.text = MyUtils.convertDuration(arrayDuration[1])
            } else {
                mediaPlayer!!.seekTo(arrayDuration[1])

            }

        }
        binding.cancel2.setOnClickListener {
            binding.cstSelected2.visibility = View.GONE
            binding.cstDelete2.visibility = View.GONE
            arrayDuration[1] = 0
        }
    }

    private fun initCues3() {
        binding.cues3.setOnClickListener {
            if (arrayDuration[2] == null || arrayDuration[2] == 0) {
                arrayDuration[2] = mediaPlayer!!.currentPosition
                binding.cstSelected3.visibility = View.VISIBLE
                binding.duration3.text = MyUtils.convertDuration(arrayDuration[2])

            } else {
                mediaPlayer!!.seekTo(arrayDuration[2])

            }

        }
        binding.cancel3.setOnClickListener {
            binding.cstSelected3.visibility = View.GONE
            binding.cstDelete3.visibility = View.GONE
            arrayDuration[2] = 0
        }
    }

    private fun initCues4() {
        binding.cues4.setOnClickListener {
            if (arrayDuration[3] == null || arrayDuration[3] == 0) {
                arrayDuration[3] = mediaPlayer!!.currentPosition
                binding.cstSelected4.visibility = View.VISIBLE
                binding.duration4.text = MyUtils.convertDuration(arrayDuration[3])

            } else {
                mediaPlayer!!.seekTo(arrayDuration[3])

            }

        }
        binding.cancel4.setOnClickListener {
            binding.cstSelected4.visibility = View.GONE
            binding.cstDelete4.visibility = View.GONE
            arrayDuration[3] = 0
        }
    }

    private fun showCues1() {
        binding.cstSelected1.visibility = View.VISIBLE
        binding.duration1.text = MyUtils.convertDuration(arrayDuration[0])
    }

    private fun showCues2() {
        binding.cstSelected2.visibility = View.VISIBLE
        binding.duration2.text = MyUtils.convertDuration(arrayDuration[1])
    }

    private fun showCues3() {
        binding.cstSelected3.visibility = View.VISIBLE
        binding.duration3.text = MyUtils.convertDuration(arrayDuration[2])
    }

    private fun showCues4() {
        binding.cstSelected4.visibility = View.VISIBLE
        binding.duration4.text = MyUtils.convertDuration(arrayDuration[3])
    }

    override fun onPause() {
        super.onPause()
        if (colorScheme == R.color.primary) {
            for (i in 0..7) {
                Settings.arrayCues1[i] = arrayDuration[i]
            }
            Settings.isDeleteOn1 = deleteCues
        } else if (colorScheme == R.color.primary_main) {
            for (i in 0..7) {
                Settings.arrayCues2[i] = arrayDuration[i]
            }
            Settings.isDeleteOn2 = deleteCues
        }
    }


    companion object {
        fun newInstance(colorScheme: Int) = CuesFragment().apply {
            arguments = Bundle().apply {
                putInt("colorScheme", colorScheme)
            }
        }
    }
}