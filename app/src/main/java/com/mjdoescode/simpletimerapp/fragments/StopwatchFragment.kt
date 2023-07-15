package com.mjdoescode.simpletimerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjdoescode.simpletimerapp.R
import com.mjdoescode.simpletimerapp.adapters.TimerAdapter
import com.mjdoescode.simpletimerapp.dao.AppDao
import com.mjdoescode.simpletimerapp.database.AppDatabase
import com.mjdoescode.simpletimerapp.databinding.FragmentStopwatchBinding
import com.mjdoescode.simpletimerapp.entities.TimerEntity
import com.mjdoescode.simpletimerapp.utils.MyTimer
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class StopwatchFragment : Fragment() {
    private lateinit var binding: FragmentStopwatchBinding

    private lateinit var mainTimer: MyTimer
    private lateinit var secondaryTimer: MyTimer

    private lateinit var adapter: TimerAdapter

    private lateinit var appDao: AppDao

    private var mainTimerValue = ""
    private var secondaryTimerValue = ""

    private var isFirstStart = true

    private var isMainRunning = false
    private var isSecondaryRunning = false

    private var stampId = 1

    private var minutes = 0L
    private var seconds = 0L
    private var millis = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appDatabase = AppDatabase.getInstance(requireContext())
        // Initiate appDao here as it's reused in multiple places
        appDao = appDatabase.appDao()
        adapter = TimerAdapter(ArrayList())


        // Call delete here to clear database before user initiates new timers
        deleteTimeStamps()

        setupMainTimer()
        setupSecondaryTimer()

        setupClickListeners()
    }

    private fun setupMainTimer() {
        mainTimer = MyTimer { timerText ->
            requireActivity().runOnUiThread {

                minutes = TimeUnit.MILLISECONDS.toMinutes(timerText)
                seconds = TimeUnit.MILLISECONDS.toSeconds(timerText) % 60
                millis = timerText % 1000

                mainTimerValue = String.format("%02d:%02d:%02d", minutes, seconds, millis / 10)

                binding.mainTimer.text = mainTimerValue
            }
        }
    }

    private fun setupSecondaryTimer() {
        secondaryTimer = MyTimer { timerText ->
            requireActivity().runOnUiThread {

                minutes = TimeUnit.MILLISECONDS.toMinutes(timerText)
                seconds = TimeUnit.MILLISECONDS.toSeconds(timerText) % 60
                millis = timerText % 1000

                secondaryTimerValue = String.format("%02d:%02d:%02d", minutes, seconds, millis / 10)

                binding.secondaryTimer.isVisible = true
                binding.secondaryTimer.text = secondaryTimerValue
            }
        }
    }

    private fun setupClickListeners() {
        binding.timerControls.setOnClickListener { timerControls() }

        binding.timeStamp.setOnClickListener {
            getTimeStamp()
            setTimeStamp()
            // I found that this is a good spot to increment the stampID without running into issues
            stampId++
            restartSecondaryTimer()
        }

        binding.timerReset.setOnClickListener {
            deleteTimeStamps()
            resetAllValues()
        }
    }

    private fun timerControls() {
        if (isMainRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun getTimeStamp() {
        lifecycleScope.launch {
            appDao.getTimer().collect() { timeStamp ->
                val listOfStamps = ArrayList(timeStamp)
                setupRecycler(listOfStamps)
            }
        }
    }

    private fun setTimeStamp() {
        lifecycleScope.launch {
            // When user starts the timer, secondary timer is inactive
            // If user wants to save intervals, which starts the secondary timer
            // Pass the mainTimerValue as the first interval. Makes UX more concise
            if (isFirstStart) {
                appDao.insert(
                    TimerEntity(
                        stampId,
                        mainTimerValue,
                        "+$mainTimerValue"
                    )
                )
                isFirstStart = false
            } else {
                appDao.insert(
                    TimerEntity(
                        stampId,
                        mainTimerValue,
                        "+$secondaryTimerValue"
                    )
                )
            }
        }
    }

    private fun startTimer() {
        isMainRunning = true
        mainTimer.start()

        binding.timerReset.setImageResource(R.drawable.timer_reset_disabled)
        binding.timerReset.isClickable = false

        binding.timeStamp.isClickable = true
        binding.timeStamp.setImageResource(R.drawable.time_stamp)

        binding.timerControls.setImageResource(R.drawable.image_pause)

        // Needed to resume secondary timer, if it was already running
        // Before main timer was paused
        if (!isFirstStart) {
            isSecondaryRunning = true
            secondaryTimer.start()
        }
    }

    private fun stopTimer() {
        isMainRunning = false
        mainTimer.pause()
        secondaryTimer.pause()

        binding.timeStamp.isClickable = false
        binding.timeStamp.setImageResource(R.drawable.time_stamp_disabled)

        binding.timerReset.isClickable = true
        binding.timerReset.setImageResource(R.drawable.timer_reset)

        binding.timerControls.setImageResource(R.drawable.image_play)
        // Pause secondary timer here,
        // to be able to resume it when startTimer is clicked again
        // without restarting it
        if (isSecondaryRunning) {
            isSecondaryRunning = false
            secondaryTimer.pause()
        }
    }

    private fun restartSecondaryTimer() {
        if (isMainRunning) {
            secondaryTimer.reset()
            secondaryTimer.start()
        }
    }

    private fun setupRecycler(listOfTimes: ArrayList<TimerEntity>) {
        if (listOfTimes.isNotEmpty()) {
            adapter = TimerAdapter(listOfTimes)

            val layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerTimer.layoutManager = layoutManager
            binding.recyclerTimer.adapter = adapter

            binding.recyclerTimer.isVisible = true
            binding.cardTimerStamps.isVisible = true
        }
    }

    private fun resetAllValues() {
        isFirstStart = true
        isMainRunning = false
        isSecondaryRunning = false

        binding.secondaryTimer.isVisible = false
        binding.recyclerTimer.isVisible = false
        binding.cardTimerStamps.isVisible = false

        stampId = 1

        mainTimer.reset()
        secondaryTimer.reset()

        binding.mainTimer.text = getString(R.string.reset_timer_text)
        binding.timerReset.setImageResource(R.drawable.timer_reset_disabled)
    }

    private fun deleteTimeStamps() {
        lifecycleScope.launch {
            appDao.deleteAll()
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }
}