package com.mjdoescode.simpletimerapp.utils

import java.util.*

class MyTimer(
    private val tickListener: (Long) -> Unit
) {
    private var timerTask: TimerTask? = null
    private var timer = Timer()
    private var timeElapsed = 0L

    fun start() {
        timerTask = object : TimerTask() {
            override fun run() {
                timeElapsed += 10
                tickListener(timeElapsed)
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 10)
    }

    fun pause() {
        timerTask?.cancel()
        timer.purge()
    }

    fun reset() {
        timerTask?.cancel()
        timer.purge()
        timeElapsed = 0L
    }
}