package com.mjdoescode.simpletimerapp.utils

import android.os.CountDownTimer
import com.mjdoescode.simpletimerapp.interfaces.CountDownListener

class MyCountdown(
    private val totalTime: Long,
    private val tickInterval: Long,
    private val countDownListener: CountDownListener
)
{

    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false

    fun start() {
        if (isRunning) return

        countDownTimer = object : CountDownTimer(totalTime, tickInterval){
            override fun onTick(millisUntilFinished: Long) {
                countDownListener.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                countDownListener.onFinish()
            }
        }

        countDownTimer?.start()
        isRunning = false
    }

    fun pause() {
        countDownTimer?.cancel()
        isRunning = false
    }

    fun resume() {
        if (isRunning) return

        countDownTimer?.start()
        isRunning = true
    }
}