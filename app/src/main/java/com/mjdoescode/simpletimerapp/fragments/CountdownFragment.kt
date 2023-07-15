package com.mjdoescode.simpletimerapp.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mjdoescode.simpletimerapp.R
import com.mjdoescode.simpletimerapp.databinding.FragmentCountdownBinding
import com.mjdoescode.simpletimerapp.interfaces.CountDownListener
import com.mjdoescode.simpletimerapp.utils.MyCountdown
import com.mjdoescode.simpletimerapp.utils.NumberPickerHelper

class CountdownFragment : Fragment(), NumberPickerHelper.ViewToUpdate {
    private lateinit var binding: FragmentCountdownBinding

    private lateinit var myCountDownTimer: MyCountdown

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var secondsPicker: NumberPicker

    private lateinit var hourPickerHelper: NumberPickerHelper
    private lateinit var minutePickerHelper: NumberPickerHelper
    private lateinit var secondsPickerHelper: NumberPickerHelper

    // Declared here, because the variables get reused throughout the fragment
    private var hours = 0L
    private var minutes = 0L
    private var seconds = 0L
    private var totalTime = 0L

    private var isTimeSet = false

    private var isAlarmOn = false

    // Set remaining time to be used for storing paused time value
    private var remainingTime = 0L
    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPickers()
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm_ring)
        setupClickListeners()
        disableControls()
    }

    private fun setupClickListeners() {

        binding.timerControls.setOnClickListener {
            timerControls()
        }

        binding.timerReset.setOnClickListener {
            resetTimer()
        }

        binding.alarm.setOnClickListener {
            getAlarm()
        }
    }

    private fun getAlarm() {
        // Simple toggle for the alarm.
        // Goes off once timer finishes
        // Currently plays for 3 seconds before stopping on its own
        isAlarmOn = if (!isAlarmOn) {
            binding.alarmImage.setImageResource(R.drawable.alarm_on)
            Toast.makeText(requireContext(), "Alarm on!", Toast.LENGTH_SHORT).show()
            true
        } else {
            binding.alarmImage.setImageResource(R.drawable.alarm_off)
            false
        }
    }

    private fun disableControls() {
        binding.timerControls.setImageResource(R.drawable.image_play_disabled)
        binding.timerControls.isClickable = false
    }

    private fun disableReset() {
        binding.timerReset.setImageResource(R.drawable.timer_reset_disabled)
        binding.timerReset.isClickable = false
    }

    private fun resetTimer() {
        // Reset doesn't instantly resets the timer value.
        // Rather it just gives the option for user to set a new time value.
        // Which simply pausing the timer does not do
        binding.layoutPickers.visibility = VISIBLE
        pauseTimer()
        disableReset()

        // Check to see if the timer is set,
        // to know whether to only change the controls resource or call disableControls
        // When the timer runs out, isTimerSet becomes false, thus calling resetTimer()
        // disables the controls, rather than just changing the value or resources
        if (!isTimeSet) {
            disableControls()
        }
        isTimeSet = false
    }

    private fun timerControls() {
        // To not reset the timer every time the play/pause is clicked,
        // because setupTime() function gets invoked again.
        // Get the total time, that user already set and save it in remainingTime
        // That way we save the time remaining and don't need to invoke setupTime()
        // Which we can the also use to update the number clickers with, to keep a more fluid UX
        if (isTimeSet) {
            totalTime = remainingTime
        } else {
            setupTime()
            isTimeSet = true
        }

        if (isTimerRunning) {
            pauseTimer()
        } else {
            resumeCountDown()
        }
    }

    private fun resumeCountDown() {
        startMyCountdown()
        isTimerRunning = true
        myCountDownTimer.start()
    }

    private fun pauseTimer() {
        myCountDownTimer.pause()
        isTimerRunning = false

        // Set number picker values with remaining time value
        // To make the UX a bit more fluid
        hourPicker.value = ((remainingTime / 3_600_000) % 24).toInt()
        minutePicker.value = ((remainingTime / 60_000) % 60).toInt()
        secondsPicker.value = ((remainingTime / 1_000) % 60).toInt()

        // Because ResetTimer calls PauseTimer. We check if the reset was called
        // After the timer had ran out, in which case we would want to disableControls
        // Otherwise if there is still time,
        // The play button just needs to change resource and stay clickable to resume
        if (isTimeSet) {
            binding.timerControls.setImageResource(R.drawable.image_play)
        }
    }

    private fun setupTime(): Long {
        isTimeSet = true
        // Convert the number picker values to milliseconds
        // Add them up to one total value and pass it on to MyCountdown timer
        hours = (hourPicker.value * 3_600_000).toLong()
        minutes = (minutePicker.value * 60_000).toLong()
        seconds = (secondsPicker.value * 1000).toLong()

        totalTime = (hours + minutes + seconds)

        return totalTime
    }

    private fun startMyCountdown() {
        myCountDownTimer = MyCountdown(totalTime, 1000, object : CountDownListener {
            override fun onTick(millisUntilFinished: Long) {
                val hour = (millisUntilFinished / 3_600_000) % 24
                val minutes = (millisUntilFinished / 60_000) % 60
                val seconds = (millisUntilFinished / 1000) % 60

                binding.timerText.text = String.format("%02d:%02d:%02d", hour, minutes, seconds)
                binding.timerReset.setImageResource(R.drawable.timer_reset)
                binding.timerReset.isClickable = true

                // Update remaining time, to be reused when user pauses timer or reset
                // Use the value to populate Number Picker values with
                remainingTime = millisUntilFinished
            }

            override fun onFinish() {
                updatesOfOnFinish()
                disableControls()

                if (isAlarmOn) {
                    mediaPlayer?.start()
                    Handler(Looper.getMainLooper()).postDelayed({
                        mediaPlayer?.pause()
                        binding.timerReset.setImageResource(R.drawable.timer_reset_prompt)
                    }, 3000)
                }
            }
        })

        binding.timerControls.setImageResource(R.drawable.image_pause)
        binding.layoutPickers.visibility = INVISIBLE
    }

    private fun updatesOfOnFinish() {
        isTimeSet = false
        binding.timerReset.setImageResource(R.drawable.timer_reset_prompt)
        binding.timerText.text = getString(R.string.reset_timer_text)
    }

    private fun setupPickers() {

        hourPicker = binding.pickerHours
        minutePicker = binding.pickerMinutes
        secondsPicker = binding.pickerSeconds

        hourPickerHelper = NumberPickerHelper(hourPicker, this)
        minutePickerHelper = NumberPickerHelper(minutePicker, this)
        secondsPickerHelper = NumberPickerHelper(secondsPicker, this)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23

        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        secondsPicker.minValue = 0
        secondsPicker.maxValue = 59
    }

    override fun updateValue(newValue: Int) {
        if (newValue == 0) {
            disableReset()
            disableControls()
        } else {
            binding.timerControls.isClickable = true
            binding.timerControls.setImageResource(R.drawable.image_play)
        }
    }

}