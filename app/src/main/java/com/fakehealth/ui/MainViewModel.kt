package com.fakehealth.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.fakehealth.data.DailySummary
import com.fakehealth.data.HealthRecord
import com.fakehealth.data.HealthRepository
import com.fakehealth.data.SecretPrefs
import com.fakehealth.data.WeeklyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository.getInstance(application)
    private val secretPrefs = SecretPrefs.getInstance(application)

    val todayRecord: Flow<HealthRecord?> = repository.getTodayRecord()
    val weeklyStats: Flow<WeeklyStats> = todayRecord.map { _ -> repository.getWeeklyStats() }.flattenMerge()
    val last7Days: Flow<List<DailySummary>> = todayRecord.map { _ -> repository.getLast7Days() }.flattenMerge()
    val secretModeEnabled: Flow<Boolean> = secretPrefs.secretModeEnabled
    val hasPin: Flow<Boolean> = secretPrefs.hasPin

    fun updateSteps(steps: Int) {
        androidx.lifecycle.viewModelScope.launch {
            repository.updateTodayRecord { this.steps = steps }
        }
    }

    fun updateCalories(calories: Int) {
        androidx.lifecycle.viewModelScope.launch {
            repository.updateTodayRecord { this.calories = calories }
        }
    }

    fun updateSleep(sleepMinutes: Int) {
        androidx.lifecycle.viewModelScope.launch {
            repository.updateTodayRecord { this.sleepMinutes = sleepMinutes }
        }
    }

    fun updateHeartRate(avg: Int, min: Int, max: Int) {
        androidx.lifecycle.viewModelScope.launch {
            repository.updateTodayRecord {
                this.heartRateAvg = avg
                this.heartRateMin = min
                this.heartRateMax = max
            }
        }
    }

    fun updateRecordForDate(date: Long, steps: Int, calories: Int, sleepMinutes: Int, heartRateAvg: Int, heartRateMin: Int, heartRateMax: Int) {
        androidx.lifecycle.viewModelScope.launch {
            repository.updateRecordForDate(date) {
                this.steps = steps
                this.calories = calories
                this.sleepMinutes = sleepMinutes
                this.heartRateAvg = heartRateAvg
                this.heartRateMin = heartRateMin
                this.heartRateMax = heartRateMax
            }
        }
    }

    fun setPin(pin: String) {
        androidx.lifecycle.viewModelScope.launch {
            secretPrefs.setPin(pin)
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        return secretPrefs.verifyPin(pin)
    }

    fun clearPin() {
        androidx.lifecycle.viewModelScope.launch {
            secretPrefs.clearPin()
        }
    }

    fun toggleSecretMode() {
        androidx.lifecycle.viewModelScope.launch {
            val current = secretPrefs.secretModeEnabled.first()
            secretPrefs.setSecretMode(!current)
        }
    }

    fun registerSecretTap(): Boolean {
        return androidx.lifecycle.viewModelScope.launch {
            secretPrefs.registerTap()
        }.getCompleted()
    }

    fun resetTapCount() {
        androidx.lifecycle.viewModelScope.launch {
            secretPrefs.resetTapCount()
        }
    }
}