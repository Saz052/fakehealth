package com.fakehealth.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HealthRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthDao(): HealthDao
}