package com.mjdoescode.simpletimerapp.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mjdoescode.simpletimerapp.dao.AppDao
import com.mjdoescode.simpletimerapp.entities.TimerEntity

@androidx.room.Database(entities = [TimerEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao() : AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this){
                 var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app-database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}