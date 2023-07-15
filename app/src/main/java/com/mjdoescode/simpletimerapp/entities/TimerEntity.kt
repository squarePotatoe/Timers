package com.mjdoescode.simpletimerapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimerEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val time: String,
    val timeDiff: String
) {
}