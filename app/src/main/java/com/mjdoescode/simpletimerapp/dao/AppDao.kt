package com.mjdoescode.simpletimerapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.mjdoescode.simpletimerapp.entities.TimerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert
    suspend fun insert(time: TimerEntity)

    @Query("SELECT * FROM `TimerEntity`")
    fun getTimer(): Flow<List<TimerEntity>>

    @Query("SELECT * FROM `TimerEntity` WHERE time = :time")
    fun getLastStamp(time: String): Flow<TimerEntity>

    @Query("DELETE FROM `TimerEntity`")
    suspend fun deleteAll()
}