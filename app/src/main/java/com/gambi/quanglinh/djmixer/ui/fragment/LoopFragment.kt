package com.gambi.quanglinh.djmixer.ui.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gambi.quanglinh.djmixer.MainActivity
import com.gambi.quanglinh.djmixer.R
import com.gambi.quanglinh.djmixer.databinding.FragmentLoopBinding
import com.gambi.quanglinh.djmixer.utils.MyUtils
import com.gambi.quanglinh.djmixer.utils.Settings
import kotlin.math.pow
import kotlin.math.roundToInt

class LoopFragment : Fragment() {
    private var _binding: FragmentLoopBinding? = null
    private val binding get() = _binding!!
    private var parent: MainActivity? = null
    private var colorScheme: Int? = null
    private var mediaPlayer: MediaPlayer? = null
    val handler: Handler = Handler(Looper.getMainLooper())
    var targetPosition = 0 // Vị trí mục tiêu trong mili giây
    var anotherPosition = 0 // Vị trí mục tiêu trong mili giây
    val arrayLoop = Settings.arrayLoop
    var positionLoop = -1
    val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer!!.currentPosition >= targetPosition) {
                mediaPlayer!!.seekTo(anotherPosition) // Chuyển đến vị trí khác
            } else {
                handler.postDelayed(this, 1000) // Kiểm tra lại sau 1 giây
            }
        }
    }
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
        _binding = FragmentLoopBinding.inflate(inflater, container, false)
        initComponents()
        initViews()
        initEvents()

        return binding.root
    }

    private fun initComponents() {


    }

    private fun initViews() {
        binding.loop8.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loop4.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loop2.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loop1.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loop12.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loop14.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loop1.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loopMain.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loopIn.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loopOut.setTextColor(ContextCompat.getColor(requireContext(), colorScheme!!))
        ImageViewCompat.setImageTintList(
            binding.loopNext,
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
        )
        ImageViewCompat.setImageTintList(
            binding.loopPrevious,
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
        )
        if (colorScheme == R.color.primary) {
//            if(parent!!.functionChoosed==parent!!.LOOP)
//            viewModel.dataLoopPosition1.observe(viewLifecycleOwner, Observer {
//                if(!parent!!.isReset) {
//                    positionLoop = -1
//                    normalArrayloopLayout()
//                    normalLayoutMainAndOut()
//                }
////                checktoBackNormal()
//            })
            if (Settings.loopedPosition1 != -1) {
                positionLoop = Settings.loopedPosition1
                checktoBackNormal(Settings.loopIn1)
            }
        } else if (colorScheme == R.color.primary_main) {
//            if(parent!!.functionChoosed2==parent!!.LOOP){
//            viewModel.dataLoopPosition2.observe(viewLifecycleOwner, Observer {
//                if(!parent!!.isReset2) {
//                    positionLoop = -1
//                    normalArrayloopLayout()
//                    normalLayoutMainAndOut()
//                }
////                checktoBackNormal()
//            })}
            if (Settings.loopedPosition2 != -1) {
                positionLoop = Settings.loopedPosition2
                checktoBackNormal(Settings.loopIn2)
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initEvents() {
        binding.loop8.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedLayout(binding.loop8)
                }

                MotionEvent.ACTION_UP -> {
                    if (positionLoop != 9 && mediaPlayer != null) {

                        val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
                        normalArrayloopLayout(binding.loop8)
                        positionLoop = 9
                        setLoop(
                            arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                            (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                        )

                    } else {
                        normalArrayloopLayout()
                        normalLayoutMainAndOut()
                        positionLoop = -1
                    }


                }
            }
            true
        }
        binding.loop4.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedLayout(binding.loop4)
                }

                MotionEvent.ACTION_UP -> {
                    if (positionLoop != 8 && mediaPlayer != null) {
                        normalArrayloopLayout(binding.loop4)
                        positionLoop = 8
                        val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
                        setLoop(
                            arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                            (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                        )

                    } else {
                        normalArrayloopLayout()
                        normalLayoutMainAndOut()
                        positionLoop = -1

                    }

                }
            }
            true
        }
        binding.loop2.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedLayout(binding.loop2)
                }

                MotionEvent.ACTION_UP -> {
                    if (positionLoop != 7 && mediaPlayer != null) {
                        normalArrayloopLayout(binding.loop2)
                        positionLoop = 7
                        val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
                        setLoop(
                            arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                            (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                        )
                    } else {
                        normalArrayloopLayout()
                        normalLayoutMainAndOut()
                        positionLoop = -1

                    }

                }
            }
            true
        }
        binding.loop1.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedLayout(binding.loop1)
                }

                MotionEvent.ACTION_UP -> {
                    if (positionLoop != 6 && mediaPlayer != null) {
                        normalArrayloopLayout(binding.loop1)
                        positionLoop = 6
                        val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
                        setLoop(
                            arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                            (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                        )
                    } else {
                        normalArrayloopLayout()
                        normalLayoutMainAndOut()
                        positionLoop = -1

                    }

                }
            }
            true
        }
        binding.loop12.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedLayout(binding.loop12)
                }

                MotionEvent.ACTION_UP -> {
                    if (positionLoop != 5 && mediaPlayer != null) {
                        normalArrayloopLayout(binding.loop12)
                        positionLoop = 5
                        val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
                        setLoop(
                            arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                            (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                        )
                    } else {
                        normalArrayloopLayout()
                        normalLayoutMainAndOut()
                        positionLoop = -1

                    }
                }
            }
            true
        }
        binding.loop14.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedLayout(binding.loop14)
                }

                MotionEvent.ACTION_UP -> {
                    if (positionLoop != 4 && mediaPlayer != null) {
                        normalArrayloopLayout(binding.loop14)
                        positionLoop = 4
                        val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
                        setLoop(
                            arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                            (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                        )
                    } else {
                        normalArrayloopLayout()
                        normalLayoutMainAndOut()
                        positionLoop = -1

                    }
                }
            }
            true
        }

        binding.loopMain.setOnClickListener {
            backtoNormalLayout(binding.loopMain)
            backtoNormalLayout(binding.loopOut)
            normalArrayloopLayout()
            handler.removeCallbacks(runnable)
        }
        binding.loopPrevious.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.loopPrevious.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                colorScheme!!
                            )
                        )//Do Something
                    binding.loopPrevious.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )//Do Something
                }

                MotionEvent.ACTION_UP -> {
                    binding.loopPrevious.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.background2
                            )
                        )//Do Something
                    if (positionLoop >= 1 && mediaPlayer != null) {
                        checktoBackNormal()
                        positionLoop--
                        binding.loopMain.text = "Loop:" + arrayLoop[positionLoop]
                    }
                    binding.loopPrevious.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            colorScheme!!
                        )
                    )//Do Something
                }
            }
            true
        }
        binding.loopNext.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.loopNext.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )//Do Something
                    binding.loopNext.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                colorScheme!!
                            )
                        )//Do Something
                }

                MotionEvent.ACTION_UP -> {
                    binding.loopNext.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            colorScheme!!
                        )
                    )//Do Something
                    binding.loopNext.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.background2
                            )
                        )//Do Something
                    if (positionLoop < arrayLoop.size - 1 && mediaPlayer != null) {
                        positionLoop++
                        binding.loopMain.text = "Loop:" + arrayLoop[positionLoop]
                        checktoBackNormal()

                    }

                }
            }
            true
        }
    }

    private fun checktoBackNormal(inLoop: Int? = null) {
        if (positionLoop > 9 || positionLoop < 4) {
            normalArrayloopLayout()
            val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
            if (inLoop != null) {
                setLoop(
                    arrayLoop[positionLoop], inLoop,
                    (inLoop + durationCount).roundToInt()
                )
            } else
                setLoop(
                    arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                    (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                )
        } else {
            when (positionLoop) {
                4 -> {
                    touchedLayout(binding.loop14)
                    normalArrayloopLayout(binding.loop14)
                }

                5 -> {
                    touchedLayout(binding.loop12)
                    normalArrayloopLayout(binding.loop12)
                }

                6 -> {
                    touchedLayout(binding.loop1)
                    normalArrayloopLayout(binding.loop1)
                }

                7 -> {
                    touchedLayout(binding.loop2)
                    normalArrayloopLayout(binding.loop2)
                }

                8 -> {
                    touchedLayout(binding.loop4)
                    normalArrayloopLayout(binding.loop4)
                }

                9 -> {
                    touchedLayout(binding.loop8)
                    normalArrayloopLayout(binding.loop8)
                }

                else -> {
                    normalArrayloopLayout()

                }
            }
            val durationCount = 4000 * (2.0.pow((positionLoop - 8).toDouble()))
            if (inLoop != null) {
                setLoop(
                    arrayLoop[positionLoop], inLoop,
                    (inLoop + durationCount).roundToInt()
                )
            } else
                setLoop(
                    arrayLoop[positionLoop], mediaPlayer!!.currentPosition,
                    (mediaPlayer!!.currentPosition + durationCount).roundToInt()
                )
        }
    }

    private fun normalArrayloopLayout(tvt: TextView? = null) {
        if (tvt != null) {
            if (tvt != binding.loop8) {
                backtoNormalLayout(binding.loop8)
            }
            if (tvt != binding.loop4) {
                backtoNormalLayout(binding.loop4)
            }
            if (tvt != binding.loop2) {
                backtoNormalLayout(binding.loop2)
            }
            if (tvt != binding.loop1) {
                backtoNormalLayout(binding.loop1)
            }
            if (tvt != binding.loop12) {
                backtoNormalLayout(binding.loop12)
            }
            if (tvt != binding.loop14) {
                backtoNormalLayout(binding.loop14)
            }
        } else {
            backtoNormalLayout(binding.loop8)
            backtoNormalLayout(binding.loop4)
            backtoNormalLayout(binding.loop2)
            backtoNormalLayout(binding.loop1)
            backtoNormalLayout(binding.loop12)
            backtoNormalLayout(binding.loop14)

        }
    }

    private fun normalLayoutMainAndOut() {
        backtoNormalLayout(binding.loopMain)
        backtoNormalLayout(binding.loopOut)
    }

    private fun touchedLayout(tvt: TextView) {
        tvt.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        )
        tvt.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))

    }

    private fun backtoNormalLayout(tvt: TextView) {
        tvt.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    colorScheme!!
                )
            )
        )
        tvt.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.background2))
    }

    private fun setLoop(mainLoop: String, inLoop: Int, outLoop: Int) {
        if (colorScheme == R.color.primary) {
            Settings.loopedPosition1 = positionLoop
            Settings.loopIn1 = inLoop
        } else if (colorScheme == R.color.primary_main) {
            Settings.loopedPosition2 = positionLoop
            Settings.loopIn2 = inLoop

        }
        val inStringLoop = MyUtils.convertDuration(inLoop)
        val outStringLoop = MyUtils.convertDuration(outLoop)
        binding.loopMain.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loopOut.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorScheme!!))
        binding.loopMain.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        )
        binding.loopOut.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        )
        binding.loopMain.text = "Loop:$mainLoop"
        binding.loopIn.text = "IN $inStringLoop "
        binding.loopOut.text = "OUT $outStringLoop"
        targetPosition = outLoop // Vị trí mục tiêu trong mili giây
        anotherPosition = inLoop
        handler.post(runnable) // Bắt đầu kiểm tra
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    companion object {
        fun newInstance(colorScheme: Int) = LoopFragment().apply {
            arguments = Bundle().apply {
                putInt("colorScheme", colorScheme)
            }
        }
    }
}