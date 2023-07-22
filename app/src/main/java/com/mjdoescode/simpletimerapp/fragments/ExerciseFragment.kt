package com.mjdoescode.simpletimerapp.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mjdoescode.simpletimerapp.R
import com.mjdoescode.simpletimerapp.databinding.FragmentExerciseBinding
import com.mjdoescode.simpletimerapp.interfaces.CountDownListener
import com.mjdoescode.simpletimerapp.utils.MyCountdown
import java.util.*

class ExerciseFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var binding: FragmentExerciseBinding
    private var textToSpeech: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var exerciseTimer: MyCountdown
    private lateinit var restTimer: MyCountdown

    private var exerciseTime = 0L
    private var exerciseProgress = 0
    private var countdown = 0L

    private var isExercise = false
    private var isRest = true

    private var isFirstStart = true

    private var isRunning = false

    private var isSpeakOutEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bell)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textToSpeech = TextToSpeech(activity, this)

        binding.mainTimer.text = exerciseTime.toString()
        timerControls()

    }
    private fun timerControls() {
        binding.timerControls.setOnClickListener {
            if (isRunning){
                isRunning = false
                exerciseTimer.pause()
                binding.timerControls.setImageResource(R.drawable.image_play)
            } else {
                isRunning = true
                setupRestView()
                binding.timerControls.setImageResource(R.drawable.image_pause)
            }
        }

        binding.toggleTts.setOnClickListener {
            isSpeakOutEnabled = if (isSpeakOutEnabled){
                binding.toggleTts.setImageResource(R.drawable.speak_out_disabled)
                false
            } else {
                binding.toggleTts.setImageResource(R.drawable.speak_out)
                true
            }
        }
    }

    private fun setupTimer() {

        binding.mainTimer.text = exerciseTime.toString()
        binding.exerciseProgressBar.max = exerciseTime.toInt()

        exerciseTimer = MyCountdown(exerciseTime * 1000, 1000, object : CountDownListener {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                if (countdown < 4L && isSpeakOutEnabled){
                    speakOut((countdown).toString())
                }
                countdown--
                exerciseProgress++

                binding.exerciseProgressBar.progress = (exerciseTime.toInt() + 1) - exerciseProgress

                binding.mainTimer.text = ((exerciseTime.toInt() + 1)- exerciseProgress).toString()
            }

            override fun onFinish() {

                if (isRest){
                    setupExerciseView()
                } else {
                    setupRestView()
                }
            }
        })
    }

    private fun setupRestView() {
        isExercise = false
        isRest = true

        exerciseTime = 10L
        exerciseProgress =0
        countdown = exerciseTime
        setupTimer()

        if (!isFirstStart){
            playSound()
        }

        isFirstStart = false

        exerciseTimer.start()
    }
    private fun setupExerciseView() {

        isExercise = true
        isRest = false

        exerciseTime = 15L
        exerciseProgress = 0
        countdown = exerciseTime
        setupTimer()
        playSound()

        exerciseTimer.start()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech!!.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language is not supported")
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }

    private fun playSound(){
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                it.isLooping = false
            }
        }
    }

    private fun speakOut(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}