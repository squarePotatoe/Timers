package com.mjdoescode.simpletimerapp

import android.app.Application
import com.mjdoescode.simpletimerapp.database.AppDatabase

class TimerApp: Application() {

    val db by lazy {
        AppDatabase.getInstance(this)
    }
}