package com.mjdoescode.simpletimerapp.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mjdoescode.simpletimerapp.R
import com.mjdoescode.simpletimerapp.databinding.FragmentExerciseBinding
import com.mjdoescode.simpletimerapp.interfaces.CountDownListener
import com.mjdoescode.simpletimerapp.utils.MyCountdown

class ExerciseFragment: Fragment() {

    private lateinit var binding: FragmentExerciseBinding

    private lateinit var mainTimer: MyCountdown
    private lateinit var secondaryTimer: MyCountdown

    private var mediaPlayer: MediaPlayer? = null

    private var exerciseTime = 45L
    private var restTime = 10L

    private var isExercise = false
    private var isRest = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bell)

        binding.timerControls.setOnClickListener {
            isExercise = if (isExercise) {
                mainTimer.pause()
                false
            } else {
                mainTimer.start()
                true
            }
        }
        setupMainTimer()
        setupRestTimer()
    }

    private fun setupMainTimer() {
        mainTimer = MyCountdown(exerciseTime * 1000, 1000, object : CountDownListener {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60

                if (seconds <= 0){
                    mediaPlayer?.start()
                    mediaPlayer?.isLooping = false
                }

                binding.mainTimer.text = String.format("%02d", seconds)
            }

            override fun onFinish() {
                exerciseTime = 45L
                binding.secondaryTimer.text = String.format("%02d", exerciseTime)

                isExercise = false
                isRest = true
                secondaryTimer.start()
            }

        })
    }

    private fun setupRestTimer() {

        secondaryTimer = MyCountdown(restTime * 1000, 1000, object : CountDownListener {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60

                if (seconds <= 0){
                    mediaPlayer?.start()
                    mediaPlayer?.isLooping = false
                }

                binding.secondaryTimer.text = String.format("%02d", seconds)
            }

            override fun onFinish() {
                restTime = 10L
                binding.mainTimer.text = String.format("%02d", restTime)
                isExercise = true
                isExercise = false
                mainTimer.start()
            }

        })
    }
}