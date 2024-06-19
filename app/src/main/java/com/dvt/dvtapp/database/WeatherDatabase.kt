package com.dvt.dvtapp.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FavouriteTable::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao() : WeatherDao

    companion object{
        @Volatile
        private var instance : WeatherDatabase?= null

        @Synchronized
        fun getInstance(application : Application) : WeatherDatabase?{
            if (instance == null) {
                instance = Room.databaseBuilder(application.applicationContext, WeatherDatabase::class.java, "weather_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build()
            }
            return instance
        }

        private val callback : Callback = object : Callback() {
            override fun onCreate(db : SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}