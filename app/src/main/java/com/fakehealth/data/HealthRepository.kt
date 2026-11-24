package com.fakehealth.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.concurrent.Executors

class HealthRepository(private val dao: HealthDao) {

    private val ioExecutor = Executors.newSingleThreadExecutor()

    fun getTodayRecord(): Flow<HealthRecord?> {
        val today = startOfDay(System.currentTimeMillis())
        return dao.observeRecordForDate(today)
    }

    fun getRecordForDate(date: Long): Flow<HealthRecord?> {
        val dayStart = startOfDay(date)
        return dao.observeRecordForDate(dayStart)
    }

    suspend fun getOrCreateTodayRecord(): HealthRecord {
        val today = startOfDay(System.currentTimeMillis())
        return dao.getRecordForDate(today) ?: HealthRecord(date = today).also { dao.insertRecord(it) }
    }

    suspend fun updateTodayRecord(block: HealthRecord.() -> Unit): HealthRecord {
        val record = getOrCreateTodayRecord()
        record.block()
        record.lastModified = System.currentTimeMillis()
        dao.updateRecord(record)
        return record
    }

    suspend fun updateRecordForDate(date: Long, block: HealthRecord.() -> Unit): HealthRecord {
        val dayStart = startOfDay(date)
        val record = dao.getRecordForDate(dayStart) ?: HealthRecord(date = dayStart)
        record.block()
        record.lastModified = System.currentTimeMillis()
        if (record.id == 0L) {
            dao.insertRecord(record)
        } else {
            dao.updateRecord(record)
        }
        return record
    }

    suspend fun getLast7Days(): List<DailySummary> {
        return dao.getLast7Days().map { DailySummary.fromRecord(it) }
    }

    suspend fun getLast30Days(): List<DailySummary> {
        return dao.getLast30Days().map { DailySummary.fromRecord(it) }
    }

    suspend fun getWeeklyStats(): WeeklyStats {
        val records = dao.getLast7Days()
        if (records.isEmpty()) {
            return WeeklyStats(0, 0, 0, 0, 0f, 0f, 0)
        }
        val totalSteps = records.sumOf { it.steps }
        val totalCalories = records.sumOf { it.calories }
        val totalSleepMinutes = records.sumOf { it.sleepMinutes }
        val heartRates = records.filter { it.heartRateAvg > 0 }.map { it.heartRateAvg }
        val avgHeartRate = if (heartRates.isNotEmpty()) heartRates.average().toInt() else 0

        return WeeklyStats(
            totalSteps = totalSteps,
            avgSteps = totalSteps / records.size,
            totalCalories = totalCalories,
            avgCalories = totalCalories / records.size,
            totalSleepHours = totalSleepMinutes / 60f,
            avgSleepHours = (totalSleepMinutes / 60f) / records.size,
            avgHeartRate = avgHeartRate
        )
    }

    private fun startOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    companion object {
        @Volatile private var INSTANCE: HealthRepository? = null

        fun getInstance(application: Application): HealthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HealthRepository(
                    (application as FakeHealthApplication).database.healthDao()
                ).also { INSTANCE = it }
            }
        }
    }
}