package com.mjdoescode.simpletimerapp.interfaces

interface CountDownListener {
    fun onTick(millisUntilFinished: Long)
    fun onFinish()
}