package com.fakehealth

import android.app.Application
import androidx.datastore.preferences.PreferencesDataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room

class FakeHealthApplication : Application() {

    val dataStore: PreferencesDataStore by lazy {
        preferencesDataStore(name = "secret_prefs")
    }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "fake_health_db")
            .fallbackToDestructiveMigration()
            .build()
    }
}