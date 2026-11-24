package com.fakehealth.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // Unix timestamp (start of day)
    val steps: Int = 0,
    val calories: Int = 0,
    val sleepMinutes: Int = 0,
    val heartRateAvg: Int = 0,
    val heartRateMin: Int = 0,
    val heartRateMax: Int = 0,
    val lastModified: Long = System.currentTimeMillis()
)

@Serializable
data class DailySummary(
    val date: Long,
    val steps: Int,
    val calories: Int,
    val sleepHours: Float,
    val heartRateAvg: Int,
    val heartRateMin: Int,
    val heartRateMax: Int
) {
    companion object {
        fun fromRecord(record: HealthRecord): DailySummary {
            return DailySummary(
                date = record.date,
                steps = record.steps,
                calories = record.calories,
                sleepHours = record.sleepMinutes / 60f,
                heartRateAvg = record.heartRateAvg,
                heartRateMin = record.heartRateMin,
                heartRateMax = record.heartRateMax
            )
        }
    }
}

@Serializable
data class WeeklyStats(
    val totalSteps: Int,
    val avgSteps: Int,
    val totalCalories: Int,
    val avgCalories: Int,
    val totalSleepHours: Float,
    val avgSleepHours: Float,
    val avgHeartRate: Int
)